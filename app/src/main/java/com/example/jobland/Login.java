package com.example.jobland;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private TextInputLayout email;
    private TextInputLayout password;
    private TextView loginBtn;
    private TextView dontHaveAccount;
    private ProgressBar progressBar;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fAuth = FirebaseAuth.getInstance();

        loginBtn = (Button) findViewById(R.id.loginBtn2);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        dontHaveAccount = findViewById(R.id.dontHaveAccountTxt);
        dontHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterUser.class));
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

    private void login(){
        email = (TextInputLayout) findViewById(R.id.emailInput);
        String userEmail = email.getEditText().getText().toString().trim();
        password = findViewById(R.id.passwordInput);
        String userPassword = password.getEditText().getText().toString().trim();

        if(userEmail.isEmpty()){
            email.setError("Email is required");
            email.requestFocus();
            return;
        }else {
            email.setErrorEnabled(false);
        }

        if(userPassword.isEmpty()){
            password.setError("Password is required");
            password.requestFocus();
            return;
        }else {
            password.setErrorEnabled(false);
        }

        email.clearFocus();
        password.clearFocus();

        progressBar = findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.VISIBLE);

        fAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                } else {
                    progressBar.setVisibility(View.GONE);
                    email.getEditText().getText().clear();
                    password.getEditText().getText().clear();
                    password.requestFocus();
                    Snackbar snackbar = Snackbar.make(loginBtn,"Wrong email or password", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }
}