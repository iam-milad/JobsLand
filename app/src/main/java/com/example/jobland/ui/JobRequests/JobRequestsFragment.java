package com.example.jobland.ui.JobRequests;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobland.JobDetailsActivity;
import com.example.jobland.JobPost;
import com.example.jobland.JobPostAdapter;
import com.example.jobland.JobRequest;
import com.example.jobland.JobRequestAdapter;
import com.example.jobland.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class JobRequestsFragment extends Fragment {

    private JobRequestsViewModel jobRequestsViewModel;

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String userId;
    private CollectionReference jobRequestRef;
    private JobRequestAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        jobRequestsViewModel =
                new ViewModelProvider(this).get(JobRequestsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_job_requests, container, false);
//        final TextView textView = root.findViewById(R.id.job_requests_text);
//        jobRequestsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        jobRequestRef = fStore.collection("users").document(userId).collection("AppliedJobs");

        setUpRecyclerView(root);
        return root;
    }

    private void setUpRecyclerView(View root){
        Query query = jobRequestRef.orderBy("applyDate", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<JobRequest> options = new FirestoreRecyclerOptions.Builder<JobRequest>().setQuery(query, JobRequest.class).build();
        adapter = new JobRequestAdapter(options);

        RecyclerView recyclerView = root.findViewById(R.id.job_requests_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.red))
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                        .setActionIconTint(ContextCompat.getColor(getContext(), R.color.white))
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new JobRequestAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                JobPost jobPost = documentSnapshot.toObject(JobPost.class);
                String id = documentSnapshot.getId();
                Intent intent = new Intent(getContext(), JobDetailsActivity.class);
                intent.putExtra("keyId", id);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}