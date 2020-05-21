package com.debarunlahiri.dinmart.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.debarunlahiri.dinmart.next.R;

public class CardLoginActivity extends AppCompatActivity {

    private ImageButton ibCardLoginClose;
    private EditText etPhone, etOtp, etName, etAddress;
    private Button bLogin;

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

        etOtp.setVisibility(View.GONE);
        etAddress.setVisibility(View.GONE);
        etName.setVisibility(View.GONE);

        bLogin.setText("Get OTP");
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String otp = etOtp.getText().toString();
                final String name = etName.getText().toString();
                final String address = etAddress.getText().toString();
                final String phone = etPhone.getText().toString();

                if (phone.isEmpty()) {
                    etPhone.setError("Enter phone number");
                } else {

                }

            }
        });

        ibCardLoginClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
