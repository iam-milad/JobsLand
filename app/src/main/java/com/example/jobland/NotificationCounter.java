package com.example.jobland;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class NotificationCounter {

    private TextView notificationNumber;
    private final int MAX_NUMBER = 99;
    private final int MIN_NUMBER = 0;
    private int notificationNumberCounter = 0;

    public NotificationCounter(View view){
        notificationNumber = view.findViewById(R.id.notificationNumber);
    }

    public void increaseNumber(int counter){
        notificationNumberCounter = counter;

        if(notificationNumberCounter > MAX_NUMBER){
            Log.d("Counter", "Maximum Number Reached");
        } else if(notificationNumberCounter < MIN_NUMBER) {
            Log.d("Counter", "Minimum Number Reached");
            notificationNumberCounter = 0;
            notificationNumber.setText(String.valueOf(notificationNumberCounter));
        } else {
            notificationNumber.setText(String.valueOf(notificationNumberCounter));
        }
    }
}
