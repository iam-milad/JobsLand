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

public class JobRequestAdapter extends FirestoreRecyclerAdapter<JobRequest, JobRequestAdapter.JobRequestHolder> {

    private OnItemClickListener listener;

    public JobRequestAdapter(@NonNull FirestoreRecyclerOptions<JobRequest> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull JobRequestHolder holder, int position, @NonNull JobRequest model) {
        holder.textViewTitle.setText(model.getTitle());
        Date cDate = model.getApplyDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String applyDate = dateFormat.format(cDate);
        holder.textViewDate.setText(applyDate);
    }

    @NonNull
    @Override
    public JobRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_request_item, parent, false);
        return new JobRequestHolder(view);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class JobRequestHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewDate;

        public JobRequestHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.job_request_title);
            textViewDate = itemView.findViewById(R.id.job_request_date);

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
}

