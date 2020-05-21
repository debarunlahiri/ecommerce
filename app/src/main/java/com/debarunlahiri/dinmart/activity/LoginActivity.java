package com.debarunlahiri.dinmart.activity;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.debarunlahiri.dinmart.MainActivity;
import com.debarunlahiri.dinmart.next.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private Toolbar logintoolbar;
    private ProgressBar progressBar;

    private EditText etLEmail, etLPassword;
    private Button lbtn, lrbtn;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        etLEmail = findViewById(R.id.etLEmail);
        etLPassword = findViewById(R.id.etLPassword);
        lbtn = findViewById(R.id.lbtn);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //GaussianBlur.with(getApplicationContext()).size(300).radius(10).put(R.drawable.mainbg, imageView16);
        if (currentUser != null) {
            sendToMain();
        }

        lbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                final String name = etLEmail.getText().toString();
                final String phone = etLPassword.getText().toString();

                if (name.isEmpty()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    etLEmail.setError("Enter name");
                } else if (phone.isEmpty()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    etLPassword.setError("Enter phone number");
                } else if (Long.parseLong(phone) < 10) {
                    progressBar.setVisibility(View.INVISIBLE);
                    etLPassword.setError("Please enter correct phone number");
                } else {
                    Intent otpIntent = new Intent(LoginActivity.this, OtpActivity.class);
                    otpIntent.putExtra("name", name);
                    otpIntent.putExtra("phone_number", phone);
                    startActivity(otpIntent);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void sendToMain() {
        Intent registerIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(registerIntent);
        finish();
    }
}
