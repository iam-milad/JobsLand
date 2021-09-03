package com.example.jobland;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.jobland.ui.jobposts.JobpostsFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class JobDetailsActivity extends AppCompatActivity {

    private TextView title;
    private TextView startTime;
    private TextView endTime;
    private TextView date;
    private TextView payRate;
    private TextView phoneNumber;
    private TextView address;
    private TextView city;
    private TextView postCode;
    private TextView description;

    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Job Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.tv_title);
        startTime = findViewById(R.id.tv_startTime);
        endTime = findViewById(R.id.tv_endTime);
        date = findViewById(R.id.tv_date);
        payRate = findViewById(R.id.tv_payRate);
        phoneNumber = findViewById(R.id.tv_phoneNum);
        address = findViewById(R.id.tv_address);
        city = findViewById(R.id.tv_city);
        postCode = findViewById(R.id.tv_postCode);
        description = findViewById(R.id.tv_description);

        fStore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String jobId = intent.getStringExtra("keyId");

//        TextView textView = (TextView) findViewById(R.id.textView);
//        System.out.println("The job id is " + jobId);
//        textView.setText(jobId);

        final DocumentReference docRef = fStore.collection("jobs").document(jobId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.out.println("Listen Failed");
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    title.setText(snapshot.getString("title"));
                    startTime.setText(snapshot.getString("fromTime"));
                    endTime.setText(snapshot.getString("toTime"));
                    date.setText(snapshot.getString("date"));
                    payRate.setText("£" + snapshot.getString("payRate"));
                    phoneNumber.setText(snapshot.getString("phoneNumber"));
                    address.setText(snapshot.getString("address"));
                    city.setText(snapshot.getString("city"));
                    postCode.setText(snapshot.getString("postCode"));
                    description.setText(snapshot.getString("description"));
                } else {
                    System.out.println("Current data: null");
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