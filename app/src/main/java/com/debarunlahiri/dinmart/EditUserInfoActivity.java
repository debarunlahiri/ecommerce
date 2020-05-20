package com.debarunlahiri.dinmart;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditUserInfoActivity extends AppCompatActivity {

    private Toolbar toolbar7;

    private EditText etEditName, etEditPhone, etEditAddress;
    private Button editsavebtn;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private String user_id;

    private String ds_name;
    private String ds_email;
    private String ds_phone_number;
    private String ds_address;
    private Integer phone_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);

        toolbar7 = findViewById(R.id.toolbar7);
        toolbar7.setTitle("Edit user info");
        setSupportActionBar(toolbar7);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar7.setNavigationIcon(getResources().getDrawable(R.mipmap.back));
        toolbar7.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etEditName = findViewById(R.id.etEditName);
        etEditPhone = findViewById(R.id.etEditPhone);
        etEditAddress = findViewById(R.id.etEditAddress);
        editsavebtn = findViewById(R.id.editsavebtn);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            sendToLogin();
        } else {
            user_id = currentUser.getUid();
            getUserDetails();
            editsavebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveUserDetails();
                }
            });

        }



    }

    private void saveUserDetails() {
        String name = etEditName.getText().toString();
        String phone_number = etEditPhone.getText().toString();
        String address = etEditAddress.getText().toString();

        if (name.isEmpty()) {
            etEditName.setError("Enter your name");
        } else if (phone_number.isEmpty()) {
            etEditPhone.setError("Enter your phone number");
        } else if (address.isEmpty()) {
            etEditAddress.setError("Enter your address");
        } else if (phone_number.length() < 10) {
            etEditPhone.setError("Please enter correct phone number");
        } else {
            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put("name", name);
            dataMap.put("phone", phone_number);
            dataMap.put("address", address);
            mDatabase.child("users").child(user_id).updateChildren(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Details saved Successfully", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(EditUserInfoActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            });
        }


    }

    private void getUserDetails() {
        mDatabase.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists()) {
                    ds_name = dataSnapshot.child("name").getValue().toString();
                }
                if (dataSnapshot.child("phone").exists()) {
                    ds_phone_number = dataSnapshot.child("phone").getValue().toString();
                }
                if (dataSnapshot.child("address").exists()) {
                    ds_address = dataSnapshot.child("address").getValue().toString();
                }

                putUserDetailsToEditText();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void putUserDetailsToEditText() {
        etEditName.setText(ds_name);
        etEditPhone.setText(ds_phone_number);
        etEditAddress.setText(ds_address);
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(EditUserInfoActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
