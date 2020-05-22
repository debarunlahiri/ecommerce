package com.debarunlahiri.dinmart.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.debarunlahiri.dinmart.MainActivity;
import com.debarunlahiri.dinmart.next.R;
import com.debarunlahiri.dinmart.utils.Variables;
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

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CardLoginActivity extends AppCompatActivity {

    private static final String TAG = "CardLoginActivity";
    private ImageButton ibCardLoginClose;
    private TextView tvOtpMessage, tvLogin;
    private EditText etPhone, etOtp, etName, etAddress;
    private Button bLogin, bOtpRetry;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public String phone, name, address, user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_login);

        ibCardLoginClose = findViewById(R.id.ibCardLoginClose);
        etPhone = findViewById(R.id.etPhone);
        etOtp = findViewById(R.id.etOtp);
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        bLogin = findViewById(R.id.bLogin);
        tvOtpMessage = findViewById(R.id.tvOtpMessage);
        bOtpRetry = findViewById(R.id.bOtpRetry);
        tvLogin = findViewById(R.id.tvLogin);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        etOtp.setVisibility(View.GONE);
        etAddress.setVisibility(View.GONE);
        etName.setVisibility(View.GONE);
        tvOtpMessage.setVisibility(View.GONE);
        bOtpRetry.setVisibility(View.GONE);

        bLogin.setText("Get OTP");
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bLogin.getText().toString().equalsIgnoreCase("Submit OTP")) {
                    initOtp(phone);
                } else {
                    final String otp = etOtp.getText().toString();
                    phone = etPhone.getText().toString();
                    if (phone.isEmpty()) {
                        etPhone.setError("Enter phone number");
                    } else {
                        etOtp.setVisibility(View.VISIBLE);
//                        bLogin.setText("Submit OTP");
                        initOtp(phone);
                    }
                }
            }
        });

        ibCardLoginClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etAddress.getVisibility() == View.VISIBLE && etAddress.getText().toString().isEmpty() && etName.getVisibility() == View.VISIBLE && etName.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please complete your details", Toast.LENGTH_LONG).show();
                } else  {
                    onBackPressed();
                }
            }
        });
    }

    private void initOtp(final String phone_number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + phone_number, 60, TimeUnit.SECONDS, CardLoginActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
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
                    tvOtpMessage.setVisibility(View.VISIBLE);
                    tvOtpMessage.setText(e.getMessage());
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    tvOtpMessage.setVisibility(View.VISIBLE);
                    tvOtpMessage.setText(e.getMessage());
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                bLogin.setVisibility(View.GONE);
                bOtpRetry.setVisibility(View.VISIBLE);
                tvOtpMessage.setVisibility(View.VISIBLE);
                tvOtpMessage.setText("OTP Timeout");
            }
        });

        bOtpRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initOtp(phone);
                bOtpRetry.setVisibility(View.GONE);
                bLogin.setVisibility(View.VISIBLE);
                countDownTime();
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bLogin.getText().toString().equalsIgnoreCase("Save Details")) {
                    name = etName.getText().toString();
                    address = etAddress.getText().toString();

                    if (name.isEmpty()) {
                        etName.setError("Please enter name");
                    } else if (address.isEmpty()) {
                        etAddress.setError("Please enter address");
                    } else {
                        registerCompany();
                        HashMap<String, Object> dataMap = new HashMap<>();
                        dataMap.put("name", name);
                        dataMap.put("phone", phone_number);
                        dataMap.put("address", address);
                        mDatabase.child("users").child(user_id).updateChildren(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Details saved successfully", Toast.LENGTH_LONG).show();
                                    onBackPressed();
                                } else {
                                    String errMsg = task.getException().getMessage();
                                    Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void registerCompany() {
        final String key = mDatabase.child("business").push().getKey();
        final HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("owner_name", name);
        dataMap.put("company_phone_number", phone);
        dataMap.put("company_key", key);

        mDatabase.child("business").child(key).updateChildren(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mDatabase.child("users").child(user_id).child("business").updateChildren(dataMap);
                } else {
                    String errMsg = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                }
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

                            final FirebaseUser user = task.getResult().getUser();
                            Variables.global_user_id = user.getUid();
                            Toast.makeText(getApplicationContext(), "Successfully Verified!", Toast.LENGTH_LONG).show();
                            mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("name").exists() && dataSnapshot.child("address").exists()) {
                                        onBackPressed();
                                    } else {
                                        tvLogin.setText("Add Details");
                                        etPhone.setVisibility(View.GONE);
                                        etOtp.setVisibility(View.GONE);
                                        etAddress.setVisibility(View.VISIBLE);
                                        etName.setVisibility(View.VISIBLE);
                                        bLogin.setText("Save Details");
                                        user_id = user.getUid();
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

    private void countDownTime() {
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvOtpMessage.setText("Time Remaining: " + millisUntilFinished / 1000 + " secs");
            }

            public void onFinish() {
                tvOtpMessage.setVisibility(View.GONE);
            }
        }.start();
    }
}
