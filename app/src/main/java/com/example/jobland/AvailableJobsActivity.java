package com.example.jobland;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.maps.android.SphericalUtil;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class AvailableJobsActivity extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private CollectionReference jobRef;
    String locationLatLong;

    private ListView jobsListView;
    private ArrayList<Job> dataModalArrayList;
    private GoogleMap mMap;

    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    private int milesChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_jobs);
        ActivityCompat.requestPermissions( this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Available Jobs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fStore = FirebaseFirestore.getInstance();
        jobRef = fStore.collection("jobs");

        jobsListView = findViewById(R.id.available_jobs_list);
        dataModalArrayList = new ArrayList<>();

        Intent intent = getIntent();
        String userDestination = intent.getStringExtra("postCodeKey");
        int chosenMiles = intent.getIntExtra("milesKey", 0);

        if(userDestination == null){
            boolean useCurrentLocation = intent.getBooleanExtra("currentLocationKey", false);
            milesChosen = intent.getIntExtra("milesKey2", 0);

        } else {
            GeoLocation locationAddress = new GeoLocation();
            locationAddress.getAddressFromLocation(userDestination, getApplicationContext(), new GeoCoderHandler());
            locationLatLong = locationAddress.getLocationResult();
            String[] splitedLocation = locationLatLong.split(" ");

            Double latitudeA = Double.parseDouble(splitedLocation[splitedLocation.length - 2]);
            Double longitudeA = Double.parseDouble(splitedLocation[splitedLocation.length - 1]);

            LatLng locationA = new LatLng(latitudeA, longitudeA);

            loadDatainListview(locationA, chosenMiles);
        }
    }

    private void loadDatainListview(LatLng locationA, int miles) {

        fStore.collection("jobs").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.out.println("error here: " + error);
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {

                        String title = (String) dc.getDocument().getData().get("title");
                        String date = (String) dc.getDocument().getData().get("date");
                        String fromTime = (String) dc.getDocument().getData().get("fromTime");
                        String toTime = (String) dc.getDocument().getData().get("toTime");
                        String payRate = (String) dc.getDocument().getData().get("payRate");
                        String address = (String) dc.getDocument().getData().get("address");
                        String city = (String) dc.getDocument().getData().get("city");
                        String postCode = (String) dc.getDocument().getData().get("postCode");
                        String phoneNumber = (String) dc.getDocument().getData().get("phoneNumber");
                        String description = (String) dc.getDocument().getData().get("describe");
                        GeoPoint mLocation = (GeoPoint) dc.getDocument().getData().get("mLocation");
                        Date createdDate = dc.getDocument().getTimestamp("createdDate").toDate();
                        String hostId = (String) dc.getDocument().getData().get("hostId");
                        String jobId = (String) dc.getDocument().getId();

                        LatLng locationB = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                        Double distance = calculateDistance(locationA, locationB);
                        if(distance <= miles){
                            Job job = new Job(title, date, fromTime, toTime, payRate, address, city, postCode, phoneNumber, description, mLocation, createdDate, hostId, distance, jobId);
                            dataModalArrayList.add(job);
                            JobAdapter adapter = new JobAdapter(getApplicationContext(), dataModalArrayList);
                            jobsListView.setAdapter(adapter);
                        }
                    }
                }
            }
        });
    }

    private Double calculateDistance(LatLng locationA, LatLng locationB){

        // on below line we are calculating the distance between sydney and brisbane
        Double distance = SphericalUtil.computeDistanceBetween(locationA, locationB);
        Double km = distance / 1000;
        Double miles = km / 1.609344;

        return miles;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
        }
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void getLocation(int milesChosen) {
        System.out.println("Test 1");
        if (ActivityCompat.checkSelfPermission(
                AvailableJobsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                AvailableJobsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            System.out.println("Test 2");
        } else {
            System.out.println("Test 3");
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                System.out.println("Test 4");
                double latitude = locationGPS.getLatitude();
                double longitude = locationGPS.getLongitude();

                LatLng currentLocation = new LatLng(latitude, longitude);
                loadDatainListview(currentLocation, milesChosen);
            } else {
                System.out.println("Test 5");
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }

        System.out.println("Test 6");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Permission granted");
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        OnGPS();
                    } else {
                        getLocation(milesChosen);
                    }

                } else {
                    System.out.println("Permission denied");
                    finish();
                }
                return;
            }

        }
    }
}