package com.example.jobland;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JobRequestActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;

    private TextView acceptBtn;
    private TextView rejectBtn;
    private TextView messageTextView;
    private TextView responseTextView;

    private String jobTitle;
    private String jobId;
    private String message;
    private String applicantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_request);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Job Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String notificationId = intent.getStringExtra("NotificationKey");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        acceptBtn = (Button) findViewById(R.id.accept_btn);
        rejectBtn = (Button) findViewById(R.id.reject_btn);
        responseTextView = findViewById(R.id.response_text_view);


        Map<String, Object> notificationViewed = new HashMap<>();
        notificationViewed.put("viewed", true);

        fStore.collection("users").document(userId).collection("Notifications").document(notificationId).set(notificationViewed, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(acceptBtn, "Failed to view notification", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        fStore.collection("users").document(userId).collection("Notifications").document(notificationId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("failed_listening", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    jobTitle = snapshot.getString("title");
                    jobId = snapshot.getString("jobId");
                    message = snapshot.getString("message");
                    messageTextView = findViewById(R.id.message_text_view);
                    messageTextView.setText(message + " " + jobTitle);
                    applicantId = snapshot.getString("applicantId");
                    setApplicantImage();
                    if(snapshot.getBoolean("accepted") && snapshot.getString("state").equals("accepted")){
                        acceptBtn.animate().alpha(0.0f);
                        rejectBtn.animate().alpha(0.0f);
                        responseTextView.setVisibility(View.VISIBLE);
                        responseTextView.setText("Job Request Accepted");
                    } else if (!snapshot.getBoolean("accepted") && snapshot.getString("state").equals("rejected")){
                        acceptBtn.animate().alpha(0.0f);
                        rejectBtn.animate().alpha(0.0f);
                        responseTextView.setBackgroundColor(Color.parseColor("#ff0000"));
                        responseTextView.setVisibility(View.VISIBLE);
                        responseTextView.setText("Job Request Rejected");
                    }

                } else {
                    Log.d("null_data", "Current data: null");
                }
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptJobRequest(notificationId);
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectJobRequest(notificationId);
            }
        });
    }

    private void acceptJobRequest(String notificationId){
        Map<String, Object> notificationViewed = new HashMap<>();
        notificationViewed.put("accepted", true);
        notificationViewed.put("state", "accepted");

        fStore.collection("users").document(userId).collection("Notifications").document(notificationId).set(notificationViewed, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    setNotification(true);
                    Snackbar snackbar = Snackbar.make(acceptBtn, "Job Request Accepted", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(acceptBtn, "Failed to accept job request", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    private void rejectJobRequest(String notificationId){
        Map<String, Object> notificationViewed = new HashMap<>();
        notificationViewed.put("accepted", false);
        notificationViewed.put("state", "rejected");

        fStore.collection("users").document(userId).collection("Notifications").document(notificationId).set(notificationViewed, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    setNotification(false);
                    Snackbar snackbar = Snackbar.make(acceptBtn, "Job Request Rejected", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(acceptBtn, "Failed to reject job request", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    private void setNotification(boolean jobRequestAccepted) {

        String newMessage = "Your job request was rejected for ";
        boolean accepted = false;
        String state = "rejected";

        if(jobRequestAccepted){
            newMessage = "Your job request was accepted for ";
            accepted = true;
            state = "accepted";
        }

        Date now = new Date();
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", jobTitle);
        notification.put("message", newMessage);
        notification.put("jobId", jobId);
        notification.put("applicantId", applicantId);
        notification.put("time", now);
        notification.put("state", state);
        notification.put("accepted", accepted);
        notification.put("viewed", false);


        fStore.collection("users/" + applicantId + "/Notifications").add(notification).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (!task.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(acceptBtn, "Failed to add notification to DB", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    private void setApplicantImage(){
        ImageView hostImage = findViewById(R.id.applicant_profile_image);

        fStore.collection("users").document(applicantId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (snapshot != null && snapshot.exists()) {
                    String imageURL = snapshot.getString("uPhoto");
                    Glide.with(getApplicationContext()).load(imageURL).into(hostImage);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}