package com.example.jobland;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.jobland.Job;

import java.util.ArrayList;

public class JobAdapter extends ArrayAdapter<Job> {

    public JobAdapter(@NonNull Context context, ArrayList<Job> dataModalArrayList) {
        super(context, 0, dataModalArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.job_item, parent, false);
        }

        Job dataModal = getItem(position);

        TextView jobTitle = listitemView.findViewById(R.id.job_title);
        TextView jobPayRate = listitemView.findViewById(R.id.job_payRate);
        TextView jobDistance = listitemView.findViewById(R.id.job_distance);


        DecimalFormat df = new DecimalFormat("0.00");

        jobTitle.setText(dataModal.getTitle());
        jobPayRate.setText("£"+ dataModal.getPayRate());
        Double distance = Double.valueOf(df.format(dataModal.getDistance()));
        jobDistance.setText(Double.toString(distance) + " miles");

        String jobId = dataModal.getJobId();

        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "Item clicked is : " + dataModal.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), ApplyNowActivity.class);
                intent.putExtra("jobIdKey", jobId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);

            }
        });
        return listitemView;
    }
}
