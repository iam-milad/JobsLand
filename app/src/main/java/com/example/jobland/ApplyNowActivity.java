package com.example.jobland;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ApplyNowActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private double latitude;
    private double longitude;
    private FirebaseFirestore fStore;
    private String currentUserId;
    private String jobId;

    private ConstraintLayout bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout headerLayout;
    private ImageView headerImage;

    private TextView hostName;
    private TextView title;
    private String hostId;
    private String jobTitle;
    private TextView phoneNumber;
    private TextView date;
    private TextView payRate;
    private TextView startTime;
    private TextView endTime;
    private TextView description;
    private TextView applyButton;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_now);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Apply Now");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        jobId = intent.getStringExtra("jobIdKey");

        fStore = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getUid();

        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        headerLayout = findViewById(R.id.header_layout);
        headerImage = findViewById(R.id.header_arrow);


        hostName = findViewById(R.id.job_host);
        title = findViewById(R.id.job_title);
        phoneNumber = findViewById(R.id.phone_number);
        date = findViewById(R.id.job_date);
        payRate = findViewById(R.id.job_payRate);
        startTime = findViewById(R.id.start_time);
        endTime = findViewById(R.id.end_time);
        description = findViewById(R.id.description);
        applyButton = (Button) findViewById(R.id.applyBtn);

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
                    jobTitle = snapshot.getString("title");
                    startTime.setText(snapshot.getString("fromTime"));
                    endTime.setText(snapshot.getString("toTime"));
                    date.setText(snapshot.getString("date"));
                    payRate.setText("£" + snapshot.getString("payRate"));
                    phoneNumber.setText(snapshot.getString("phoneNumber"));
                    description.setText(snapshot.getString("description"));
                    hostId = snapshot.getString("hostId");

                    if(hostId.equals(currentUserId)){
                        applyButton.setVisibility(View.GONE);
                    }

                    fetchJobHostDetails();

                } else {
                    System.out.println("Current data: null");
                }
            }
        });


        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                headerImage.setRotation(slideOffset * 180);
            }
        });


        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();
                Map<String, Object> appliedJobs = new HashMap<>();
                appliedJobs.put("title", jobTitle);
                appliedJobs.put("hostId", hostId);
                appliedJobs.put("applyDate", now);
                appliedJobs.put("appliedJob", true);
                appliedJobs.put("accepted", false);

                fStore.collection("users").document(currentUserId).collection("AppliedJobs").document(jobId).set(appliedJobs).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            setNotification();
                            Snackbar snackbar = Snackbar.make(applyButton,"Applied Successfully", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            Snackbar snackbar = Snackbar.make(applyButton,"Job Apply Failed", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });
            }
        });
    }

    private void setNotification() {

        fStore.collection("users").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userName = value.getString("uName");

                Date now = new Date();
                Map<String, Object> notification = new HashMap<>();
                notification.put("title", jobTitle);
                notification.put("message", userName + " has applied for:");
                notification.put("jobId", jobId);
                notification.put("applicantId", currentUserId);
                notification.put("time", now);
                notification.put("state", "progress");
                notification.put("accepted", false);
                notification.put("viewed", false);


                fStore.collection("users/" + hostId + "/Notifications").add(notification).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (!task.isSuccessful()) {
                            Snackbar snackbar = Snackbar.make(applyButton, "Failed to add notification to DB", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });
            }
        });
    }

    private void fetchJobHostDetails(){
        final DocumentReference docRef = fStore.collection("users").document(hostId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.out.println("Listen Failed");
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    hostName.setText(snapshot.getString("uName"));
                    setHostPicture(snapshot.getString("uPhoto"));

                    System.out.println("The host Id is " + hostId);
                    System.out.println("The job Id is " + jobId);

                    final DocumentReference docRef2 = fStore.collection("users").document(currentUserId).collection("AppliedJobs").document(jobId);
                    docRef2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (e != null) {
                                System.out.println("Listen Failed");
                                return;
                            }

                            if (value != null && value.exists()) {
                                if(value.getBoolean("appliedJob")){
                                    applyButton.setEnabled(false);
                                    applyButton.setText("Applied Already");
                                    applyButton.setBackgroundColor(Color.parseColor("#ff5789"));
                                }

                            } else {
                                System.out.println("Current data: null");
                            }
                        }
                    });

                } else {
                    System.out.println("Current data: null");
                }
            }
        });
    }

    private void setHostPicture(String imageURL){
        ImageView hostImage = findViewById(R.id.host_image);
        Glide.with(this).load(imageURL).into(hostImage);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        fStore = FirebaseFirestore.getInstance();
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
                    latitude = snapshot.getGeoPoint("mLocation").getLatitude();
                    longitude = snapshot.getGeoPoint("mLocation").getLongitude();
                    String title = snapshot.getString("title");
                    System.out.println("The Lat is " + latitude);
                    System.out.println("The Lat is " + longitude);
                    setMap(map,title,latitude,longitude);

                } else {
                    System.out.println("Current data: null");
                }
            }
        });
    }

    private void setMap(GoogleMap map, String title, double latitude, double longitude){


        LatLng point = new LatLng(latitude, longitude);
        map.moveCamera(CameraUpdateFactory.newLatLng(point));
        float zoomLevel = 16.0f;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoomLevel));

        LatLng jobLocation = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(jobLocation).title(title));
        map.moveCamera(CameraUpdateFactory.newLatLng(jobLocation));
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