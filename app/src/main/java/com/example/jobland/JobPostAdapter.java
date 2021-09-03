package com.example.jobland;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JobPostAdapter extends FirestoreRecyclerAdapter<JobPost, JobPostAdapter.JobPostHolder> {

    private OnItemClickListener listener;

    public JobPostAdapter(@NonNull FirestoreRecyclerOptions<JobPost> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull JobPostHolder holder, int position, @NonNull JobPost model) {
        holder.textViewTitle.setText(model.getTitle());
        holder.textViewPayRate.setText("£" + model.getPayRate());
        Date cDate = model.getCreatedDate();
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        String postedDate = DateFor.format(cDate);
        holder.textViewData.setText(postedDate);
    }

    @NonNull
    @Override
    public JobPostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_post_item, parent, false);
        return new JobPostHolder(view);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class JobPostHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewPayRate;
        TextView textViewData;

        public JobPostHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.job_post_title);
            textViewPayRate = itemView.findViewById(R.id.job_post_payRate);
            textViewData = itemView.findViewById(R.id.job_post_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

        }

    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public String getId(int position){
        return getSnapshots().getSnapshot(position).getId();
    }
}
