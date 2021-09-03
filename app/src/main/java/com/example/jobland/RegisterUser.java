package com.example.jobland;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener{

    private TextInputLayout fullName;
    private TextInputLayout email;
    private TextInputLayout password;
    private TextInputLayout phoneNum;
    private TextInputLayout street;
    private TextInputLayout city;
    private TextInputLayout postalCode;
    private TextInputLayout country;
    private TextView registerUser;
    private ProgressBar progressBar;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fireStore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.emailInput2);
        password = findViewById(R.id.passwordInput2);
        phoneNum = findViewById(R.id.phoneNumber);
        street = findViewById(R.id.street);
        city = findViewById(R.id.city);
        postalCode = findViewById(R.id.postCode);
        country = findViewById(R.id.country);

        registerUser = (Button) findViewById(R.id.registerBtn2);
        registerUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.registerBtn2:
                registerUser();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void registerUser(){
        String userFullName = fullName.getEditText().getText().toString().trim();
        String userEmail = email.getEditText().getText().toString().trim();
        String userPassword = password.getEditText().getText().toString().trim();
        String userPhoneNum = phoneNum.getEditText().getText().toString().trim();
        String userStreet = street.getEditText().getText().toString().trim();
        String userCity = city.getEditText().getText().toString().trim();
        String userPostCode = postalCode.getEditText().getText().toString().trim();
        String userCountry = country.getEditText().getText().toString().trim();

        if(userFullName.isEmpty()){
            fullName.setError("Full name is required");
            fullName.requestFocus();
            return;
        }else {
            fullName.setErrorEnabled(false);
        }

        if(userEmail.isEmpty()){
            email.setError("Email is required");
            email.requestFocus();
            return;
        }else {
            email.setErrorEnabled(false);
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            email.setError("Please enter a valid email");
            email.requestFocus();
            return;
        } else {
            email.setErrorEnabled(false);
        }

        if(userPassword.isEmpty()){
            password.setError("Password is required");
            password.requestFocus();
            return;
        }else {
            password.setErrorEnabled(false);
        }

        if(userPassword.length() < 6){
            password.setError("Password should min 6 characters!");
            password.requestFocus();
            return;
        } else {
            password.setErrorEnabled(false);
        }


        if(userPhoneNum.isEmpty()){
            phoneNum.setError("Phone Number is required");
            phoneNum.requestFocus();
            return;
        }else {
            phoneNum.setErrorEnabled(false);
        }

        if(userStreet.isEmpty()){
            street.setError("Street is required");
            street.requestFocus();
            return;
        }else {
            street.setErrorEnabled(false);
        }

        if(userCity.isEmpty()){
            city.setError("City is required");
            city.requestFocus();
            return;
        } else {
            city.setErrorEnabled(false);
        }

        if(userPostCode.isEmpty()){
            postalCode.setError("Postal code is required");
            postalCode.requestFocus();
            return;
        } else {
            postalCode.setErrorEnabled(false);
        }

        if(userCountry.isEmpty()){
            country.setError("Country is required");
            country.requestFocus();
            return;
        } else {
            country.setErrorEnabled(false);
        }

        progressBar = findViewById(R.id.loading_spinner2);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.requestFocus();

        fAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            userId = fAuth.getCurrentUser().getUid();
                            DocumentReference docRef = fireStore.collection("users").document(userId);
                            Map<String, Object> user = new HashMap<>();
                            user.put("uName", userFullName);
                            user.put("uEmail", userEmail);
                            user.put("uPhoneNum", userPhoneNum);
                            user.put("uStreet", userStreet);
                            user.put("uCity", userCity);
                            user.put("uPostalCode", userPostCode);
                            user.put("uCountry", userCountry);
                            String[] splitedFullName = userFullName.split("\\s+");
                            user.put("uPhoto", "https://eu.ui-avatars.com/api/?name=" + splitedFullName[0] + "+" + splitedFullName[1]);
                            docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG", "onSuccess: user account created for " + userId);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        } else {
                            progressBar.setVisibility(View.GONE);
                            fullName.getEditText().getText().clear();
                            email.getEditText().getText().clear();
                            password.getEditText().getText().clear();
                            phoneNum.getEditText().getText().clear();
                            street.getEditText().getText().clear();
                            city.getEditText().getText().clear();
                            postalCode.getEditText().getText().clear();
                            country.getEditText().getText().clear();
                            Snackbar snackbar = Snackbar.make(registerUser,"This already exists", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });
    }

}