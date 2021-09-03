package com.example.jobland;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewJobPostActivity extends AppCompatActivity {

    private TextInputLayout editTextTitle;
    private TextInputLayout editTextDate;
    private TextInputLayout startTime;
    private TextInputLayout endTime;
    private TextInputLayout editTextPayRate;
    private TextInputLayout editAddress;
    private TextInputLayout editCity;
    private TextInputLayout editPostCode;
    private TextInputLayout editPhoneNum;
    private TextInputLayout editDescription;
    int fHour,fMinute, tHour, tMinute;
    private TextView addJob;

    private FirebaseAuth fAuth;
    private String locationLatLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_job_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add New Job");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fAuth = FirebaseAuth.getInstance();

        editTextTitle = findViewById(R.id.edit_text_title);
        startTime = findViewById(R.id.time_from);
        endTime = findViewById(R.id.time_to);
        editTextPayRate = findViewById(R.id.edit_text_payRate);
        editTextDate = findViewById(R.id.edit_text_date);
        addJob = (Button) findViewById(R.id.add_job_btn);
        editAddress = findViewById(R.id.emp_address);
        editCity = findViewById(R.id.emp_city);
        editPostCode = findViewById(R.id.emp_post_code);
        editPhoneNum = findViewById(R.id.emp_phone_num);
        editDescription = findViewById(R.id.description);

        findViewById(R.id.time_from_picker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        NewJobPostActivity.this,
                        R.style.MyDatePickerStyle,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                fHour = hourOfDay;
                                fMinute = minute;

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0, 0, 0, fHour, fMinute);
                                startTime.getEditText().setText(DateFormat.format("hh:mm aa", calendar));
                            }
                        }, 12, 0, false
                );
                timePickerDialog.updateTime(fHour, fMinute);
                timePickerDialog.show();
            }
        });

        findViewById(R.id.time_to_picker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        NewJobPostActivity.this,
                        R.style.MyDatePickerStyle,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                tHour = hourOfDay;
                                tMinute = minute;

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(0, 0, 0, tHour, tMinute);
                                endTime.getEditText().setText(DateFormat.format("hh:mm aa", calendar));
                            }
                        }, 12, 0, false
                );
                timePickerDialog.updateTime(tHour, tMinute);
                timePickerDialog.show();
            }
        });

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

//        setListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int day) {
//                month = month+1;
//                String date = day + "/" + month + "/" + year;
//                editTextDate.getEditText().setText(date);
//            }
//        };

        findViewById(R.id.date_picker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        NewJobPostActivity.this,
                        R.style.MyDatePickerStyle,
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        String date = day + "/" + month + "/" + year;
                        editTextDate.getEditText().setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        addJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveJobPost();
            }
        });

    }

    private void saveJobPost(){
        String jobTitle = editTextTitle.getEditText().getText().toString().trim();
        String jobStartDate = editTextDate.getEditText().getText().toString().trim();
        String jobStartTime = startTime.getEditText().getText().toString().trim();
        String jobEndTime = endTime.getEditText().getText().toString().trim();
        String jobWage = editTextPayRate.getEditText().getText().toString().trim();
        String jobAddress = editAddress.getEditText().getText().toString().trim();
        String jobCity = editCity.getEditText().getText().toString().trim();
        String jobPostCode = editPostCode.getEditText().getText().toString().trim();
        String jobPhoneNumber = editPhoneNum.getEditText().getText().toString().trim();
        String jobDescription = editDescription.getEditText().getText().toString().trim();

        if(jobTitle.isEmpty()){
            editTextTitle.setError("Job title is required");
            editTextTitle.requestFocus();
            return;
        }else {
            editTextTitle.setErrorEnabled(false);
            editTextTitle.clearFocus();
        }

        if(jobStartDate.isEmpty()){
            editTextDate.setError("Job start date is required");
            editTextDate.requestFocus();
            return;
        }else {
            editTextDate.setErrorEnabled(false);
            editTextDate.clearFocus();
        }

        if(jobStartTime.isEmpty()){
            startTime.setError("Job start time is required");
            startTime.requestFocus();
            return;
        }else {
            startTime.setErrorEnabled(false);
            startTime.clearFocus();
        }

        if(jobEndTime.isEmpty()){
            endTime.setError("End time is required");
            endTime.requestFocus();
            return;
        }else {
            endTime.setErrorEnabled(false);
            endTime.clearFocus();
        }

        if(jobWage.isEmpty()){
            editTextPayRate.setError("Pay rate is required");
            editTextPayRate.requestFocus();
            return;
        }else {
            editTextPayRate.setErrorEnabled(false);
            editTextPayRate.clearFocus();
        }

        if(jobAddress.isEmpty()){
            editAddress.setError("Street and House No is required");
            editAddress.requestFocus();
            return;
        }else {
            editAddress.setErrorEnabled(false);
            editAddress.clearFocus();
        }

        if(jobCity.isEmpty()){
            editCity.setError("City is required");
            editCity.requestFocus();
            return;
        }else {
            editCity.setErrorEnabled(false);
            editCity.clearFocus();
        }

        if(jobPostCode.isEmpty()){
            editPostCode.setError("Post code is required");
            editPostCode.requestFocus();
            return;
        }else {
            editPostCode.setErrorEnabled(false);
            editPostCode.clearFocus();
        }

        String regex = "^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jobPostCode);
        if(!matcher.matches()){
            editPostCode.setError("Post code is required");
            editPostCode.requestFocus();
            return;
        } else {
            editPostCode.setErrorEnabled(false);
            editPostCode.clearFocus();
        }

        if(jobPhoneNumber.isEmpty()){
            editPhoneNum.setError("Phone Number is required");
            editPhoneNum.requestFocus();
            return;
        }else {
            editPhoneNum.setErrorEnabled(false);
            editPhoneNum.clearFocus();
        }

        if(jobDescription.isEmpty()){
            editDescription.setError("Job title is required");
            editDescription.requestFocus();
            return;
        }else {
            editDescription.setErrorEnabled(false);
            editDescription.clearFocus();
        }

        GeoLocation locationAddress = new GeoLocation();
        locationAddress.getAddressFromLocation(jobPostCode, getApplicationContext(), new GeoCoderHandler());

        locationLatLong = locationAddress.getLocationResult();
        String[] splitedLocation = locationLatLong.split(" ");

        double latitude = Double.parseDouble(splitedLocation[splitedLocation.length - 2]);
        double longitude = Double.parseDouble(splitedLocation[splitedLocation.length - 1]);

        GeoPoint mLocation = new GeoPoint(latitude, longitude);

        Date now = new Date();
        String userId = fAuth.getCurrentUser().getUid();


        CollectionReference jobPostRef = FirebaseFirestore.getInstance().collection("jobs");
        jobPostRef.add(new JobPost(jobTitle, jobStartDate, jobStartTime, jobEndTime, jobWage, jobAddress, jobCity, jobPostCode, jobPhoneNumber, jobDescription, mLocation, now, userId))
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            finish();
                        } else {
                            Snackbar snackbar = Snackbar.make(addJob,"Failed to Add Job", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });
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
}