package com.debarunlahiri.dinmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.debarunlahiri.dinmart.next.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Toolbar toolbar16;

    private Button button13;
    private EditText editText5;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        toolbar16 = findViewById(R.id.toolbar16);
        toolbar16.setTitle("Forgot password");
        setSupportActionBar(toolbar16);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar16.setNavigationIcon(getResources().getDrawable(R.mipmap.back));
        toolbar16.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPasswordActivity.super.onBackPressed();
            }
        });

        button13 = findViewById(R.id.button13);
        editText5 = findViewById(R.id.editText5);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editText5.getText().toString();

                if (email.isEmpty()) {
                    editText5.setError("Please enter your email id");
                } else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Password reset email sent", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }


            }
        });
    }
}
