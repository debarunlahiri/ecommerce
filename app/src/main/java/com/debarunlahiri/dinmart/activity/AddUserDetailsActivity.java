package com.debarunlahiri.dinmart.activity;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.debarunlahiri.dinmart.MainActivity;
import com.debarunlahiri.dinmart.next.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddUserDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar3;
    private EditText etAddress;
    private Button savebtn;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private String user_id;
    private String email, name, phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_details);

        toolbar3 = findViewById(R.id.toolbar3);
        toolbar3.setTitle("Add details");
        toolbar3.setTitleTextColor(Color.WHITE);

        etAddress = findViewById(R.id.etAddress);
        savebtn = findViewById(R.id.savebtn);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.get("name").toString();
            phone_number = bundle.get("phone_number").toString();
        }

        if (currentUser == null) {
            sendToLogin();
        } else {
            user_id = mAuth.getCurrentUser().getUid();

            savebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String address = etAddress.getText().toString();

                    if (address.isEmpty()) {
                        etAddress.setError("Please enter your address");
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
                                    sendToMain();
                                } else {
                                    String errMsg = task.getException().getMessage();
                                    Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                }
            });
        }
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(AddUserDetailsActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void sendToMain() {
        Intent registerIntent = new Intent(AddUserDetailsActivity.this, MainActivity.class);
        startActivity(registerIntent);
        finish();
    }

    private void registerCompany() {
        final String key = mDatabase.child("business").push().getKey();
        final HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("owner_name", name);
        dataMap.put("company_phone_number", phone_number);
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
}
