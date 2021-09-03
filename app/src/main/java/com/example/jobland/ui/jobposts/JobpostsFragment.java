package com.example.jobland.ui.jobposts;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobland.EditJobActivity;
import com.example.jobland.JobDetailsActivity;
import com.example.jobland.JobPost;
import com.example.jobland.JobPostAdapter;
import com.example.jobland.NewJobPostActivity;
import com.example.jobland.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class JobpostsFragment extends Fragment{

    private JobpostsViewModel jobpostsViewModel;

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String userId;
    private CollectionReference jobPostRef;
    private JobPostAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        jobpostsViewModel =
                new ViewModelProvider(this).get(JobpostsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_jobposts, container, false);
//        final TextView textView = root.findViewById(R.id.text_slideshow);
//        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        jobPostRef = fStore.collection("jobs");

        FloatingActionButton btnAddJob = root.findViewById(R.id.add_job);
        btnAddJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NewJobPostActivity.class));
            }
        });

        setUpRecyclerView(root);

        return root;
    }

    private void setUpRecyclerView(View root){
        Query query = jobPostRef.orderBy("createdDate", Query.Direction.DESCENDING).whereEqualTo("hostId", userId);

        FirestoreRecyclerOptions<JobPost> options = new FirestoreRecyclerOptions.Builder<JobPost>().setQuery(query, JobPost.class).build();
        adapter = new JobPostAdapter(options);

        RecyclerView recyclerView = root.findViewById(R.id.job_posts_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == ItemTouchHelper.LEFT){
                    adapter.deleteItem(viewHolder.getAdapterPosition());
                } else if (direction == ItemTouchHelper.RIGHT){
                    String jobId = adapter.getId(viewHolder.getAdapterPosition());
                    Intent intent = new Intent(getContext(), EditJobActivity.class);
                    intent.putExtra("keyJobId", jobId);
                    startActivity(intent);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.red))
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                        .setActionIconTint(ContextCompat.getColor(getContext(), R.color.white))
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_s))
                        .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new JobPostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
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