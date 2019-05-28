package com.example.debarunlahiri.next;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar registertoolbar;
    private ProgressBar progressBar2;

    private EditText etREmail, etRPassword;
    private Button rbtn, rlbtn;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String user_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressBar2 = findViewById(R.id.progressBar2);
        progressBar2.setVisibility(View.INVISIBLE);

        etREmail = findViewById(R.id.etREmail);
        etRPassword = findViewById(R.id.etRPassword);
        rbtn = findViewById(R.id.rbtn);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            sendToMain();
        }



        rbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar2.setVisibility(View.VISIBLE);
                final String email = etREmail.getText().toString();
                String password = etRPassword.getText().toString();

                if (email.isEmpty()) {
                    progressBar2.setVisibility(View.INVISIBLE);
                    etREmail.setError("Enter Email");
                } else if (password.isEmpty()) {
                    progressBar2.setVisibility(View.INVISIBLE);
                    etRPassword.setError("Enter Password");
                } else if (password.equals("password") || password.equals("123456") || password.equals("pass1234")) {
                    progressBar2.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Enter strong password", Toast.LENGTH_LONG).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Register Successfully", Toast.LENGTH_LONG).show();
                                            Toast.makeText(getApplicationContext(), "Email verification sent", Toast.LENGTH_SHORT).show();

                                            progressBar2.setVisibility(View.INVISIBLE);

                                        } else {
                                            String errMsg = task.getException().getMessage();
                                            Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                //Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();

                            } else {
                                progressBar2.setVisibility(View.INVISIBLE);
                                String errMsg = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }

            }
        });
    }

    private void sendToRegister() {
        Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(registerIntent);
        finish();
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
