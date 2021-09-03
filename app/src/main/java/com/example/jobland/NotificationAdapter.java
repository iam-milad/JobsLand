package com.example.jobland;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.ocpsoft.prettytime.PrettyTime;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class NotificationAdapter extends FirestoreRecyclerAdapter<Notification, NotificationAdapter.NotificationHolder> {

    private OnItemClickListener listener;

    public NotificationAdapter(@NonNull FirestoreRecyclerOptions<Notification> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationHolder holder, int position, @NonNull Notification model) {
        holder.jobTitle.setText(model.getTitle());
        holder.notificationMessage.setText(model.getMessage());
        if(!model.isViewed()){
            holder.card.setCardBackgroundColor(Color.parseColor("#ebebeb"));
        }

        PrettyTime p = new PrettyTime();
        holder.time.setText("" + p.format(model.getTime()));
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationHolder(v);
    }

    public void deleteItem(int position){

        getSnapshots().getSnapshot(position).getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("Success", "Delete Notification");
                } else {
                    Log.d("Success", "Failed to Delete Notification");
                }
            }
        });
    }

    class NotificationHolder extends RecyclerView.ViewHolder{

        TextView notificationMessage;
        TextView jobTitle;
        TextView time;
        CardView card;

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);

            notificationMessage = itemView.findViewById(R.id.notification_txt);
            jobTitle = itemView.findViewById(R.id.job_title);
            time = itemView.findViewById(R.id.notification_time);
            card = itemView.findViewById(R.id.card_view);

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
