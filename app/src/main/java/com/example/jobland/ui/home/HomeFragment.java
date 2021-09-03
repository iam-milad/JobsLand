package com.example.jobland.ui.home;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.jobland.AvailableJobsActivity;
import com.example.jobland.HomeActivity;
import com.example.jobland.Job;
import com.example.jobland.JobDetailsActivity;
import com.example.jobland.R;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private TextView doneButton;
    private TextView currentLocationBtn;
    private Slider slider;
    private TextView textViewMiles;
    private TextInputLayout destination;
    private int miles;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.wannaMakeCashTxt);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        miles = 20;

        destination = root.findViewById(R.id.destinationField);
        textViewMiles = root.findViewById(R.id.text_view_miles);
        slider = root.findViewById(R.id.slider);
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                textViewMiles.setText(value + " miles");
                miles = (int) value;
            }
        });


        doneButton = (Button) root.findViewById(R.id.doneBtn);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postCode = destination.getEditText().getText().toString().trim();
                if(!postCode.isEmpty()){

                    String regex = "^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2}$";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(postCode);
                    if(matcher.matches()){
                        Intent intent = new Intent(getContext(), AvailableJobsActivity.class);
                        intent.putExtra("postCodeKey", postCode);
                        intent.putExtra("milesKey", miles);
                        startActivity(intent);
                    } else {
                        Snackbar snackbar = Snackbar.make(doneButton,"Please enter a valid ZIP or Post Code", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }

                } else {
                    Snackbar snackbar = Snackbar.make(doneButton,"Please enter a ZIP or Post Code", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

            }
        });

        currentLocationBtn = (Button) root.findViewById(R.id.useCurrentLocationBtn);
        currentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AvailableJobsActivity.class);
                intent.putExtra("currentLocationKey", true);
                intent.putExtra("milesKey2", miles);
                startActivity(intent);
            }
        });

        return root;
    }

}