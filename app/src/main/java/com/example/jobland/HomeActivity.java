package com.example.jobland;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.net.MalformedURLException;
import java.net.URL;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private TextView fullName;
    private TextView email;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;
    NotificationCounter notificationCounter;
    private boolean initialExecution;
    private ImageView imageView;
    private String photoURL;
    private ListenerRegistration registration;

    private int totalItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initialExecution = true;

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_jobposts, R.id.nav_job_requests)
                .setDrawerLayout(drawer)
                .build();

        NavigationView navigationView1 = findViewById(R.id.nav_view);
        View headerView = navigationView1.getHeaderView(0);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView1.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                if (!handled) {
                    if(item.getItemId() == R.id.nav_logout){
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        FirebaseAuth.getInstance().signOut();
                    }
                }
                drawer.closeDrawer(GravityCompat.START);
                return handled;
            }
        });

        findViewById(R.id.notificationIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
            }
        });


        totalItems = 0;
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();


        fullName = headerView.findViewById(R.id.myProfileName);
        email = headerView.findViewById(R.id.myProfileEmail);
        imageView = headerView.findViewById(R.id.profileImage);


        final DocumentReference docRef = fStore.collection("users").document(userId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    fullName.setText(snapshot.getString("uName"));
                    email.setText(snapshot.getString("uEmail"));
                    photoURL = snapshot.getString("uPhoto");
                    setProfileImage(photoURL);
                } else {
                    Log.d("TAG", "Current data: null");
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        notificationCounter = new NotificationCounter(findViewById(R.id.bell));
        CollectionReference collectionReference = fStore.collection("users").document(userId).collection("Notifications");
        registration = collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                int viewedItems = 0;
                int unviewedItems = 0;
                for(DocumentChange dc : snapshots.getDocumentChanges()) {

                    boolean viewed = (boolean) dc.getDocument().getData().get("viewed");
                    if(!viewed){
                        unviewedItems++;
                    } else if(viewed) {
                        viewedItems--;
                    }

                }
                if(initialExecution){
                    viewedItems = Math.abs(viewedItems);
                    unviewedItems = Math.abs(unviewedItems);
                    totalItems = viewedItems + unviewedItems;
                    totalItems = totalItems - viewedItems;
                } else {
                    totalItems = totalItems + unviewedItems + viewedItems;
                }

                if(unviewedItems == 0){
                    findViewById(R.id.notificationCounter).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.notificationCounter).setVisibility(View.VISIBLE);
                }

                notificationCounter.increaseNumber(totalItems);
                initialExecution = false;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        initialExecution = true;
        registration.remove();
    }

    private void setProfileImage(String imageURL){
        Glide.with(this).load(imageURL).into(imageView);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}