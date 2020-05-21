package com.debarunlahiri.dinmart.activity;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.debarunlahiri.dinmart.MainActivity;
import com.debarunlahiri.dinmart.next.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private Toolbar otptoolbar;

    private static final String TAG = "OtpActivity";

    private Button bOtpSubmit, bOtpRetry;
    private EditText etOtp;
    private TextView tvotpMessage;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String name, phone_number, revert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        revert = getIntent().getStringExtra("revert");

        otptoolbar = findViewById(R.id.otptoolbar);
        otptoolbar.setTitle("OTP");
        setSupportActionBar(otptoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        otptoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back));
        otptoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.get("name").toString();
            phone_number = bundle.get("phone_number").toString();
        }

        bOtpRetry = findViewById(R.id.bOtpRetry);
        etOtp = findViewById(R.id.etOtp);
        tvotpMessage = findViewById(R.id.tvotpMessage);
        bOtpSubmit = findViewById(R.id.bOtpSubmit);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        tvotpMessage.setVisibility(View.GONE);
        bOtpRetry.setVisibility(View.GONE);

        tvotpMessage.setVisibility(View.VISIBLE);

        initOtp();
        countDownTime();

        bOtpSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = etOtp.getText().toString();

                if (otp.isEmpty()) {
                    etOtp.setError("Please enter otp");
                } else {
                    initOtp();
                    countDownTime();
                }
            }
        });
    }

    private void countDownTime() {
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvotpMessage.setText("Time Remaining: " + millisUntilFinished / 1000 + " secs");
            }

            public void onFinish() {
                tvotpMessage.setVisibility(View.GONE);
            }
        }.start();
    }

    private void initOtp() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + phone_number, 60, TimeUnit.SECONDS, OtpActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                etOtp.setText(phoneAuthCredential.getSmsCode());
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    tvotpMessage.setVisibility(View.VISIBLE);
                    tvotpMessage.setText(e.getMessage());
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    tvotpMessage.setVisibility(View.VISIBLE);
                    tvotpMessage.setText(e.getMessage());
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                bOtpSubmit.setVisibility(View.GONE);
                bOtpRetry.setVisibility(View.VISIBLE);
                tvotpMessage.setVisibility(View.VISIBLE);
                tvotpMessage.setText("OTP Timeout");
            }
        });

        bOtpRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initOtp();
                bOtpRetry.setVisibility(View.GONE);
                bOtpSubmit.setVisibility(View.VISIBLE);
                countDownTime();
            }
        });


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(getApplicationContext(), "Successfully Verified!", Toast.LENGTH_LONG).show();
                            mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("name").exists() && dataSnapshot.child("address").exists()) {
                                        Intent mainIntent = new Intent(OtpActivity.this, MainActivity.class);
                                        startActivity(mainIntent);
                                        finish();
                                    } else {
                                        sendToAddDetails();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void sendToAddDetails() {
        Intent mainIntent = new Intent(OtpActivity.this, AddUserDetailsActivity.class);
        mainIntent.putExtra("name", name);
        mainIntent.putExtra("phone_number", phone_number);
        startActivity(mainIntent);
        finish();
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(OtpActivity.this, MainActivity.class);
        mainIntent.putExtra("name", name);
        mainIntent.putExtra("phone_number", phone_number);
        startActivity(mainIntent);
        finish();
    }
}
