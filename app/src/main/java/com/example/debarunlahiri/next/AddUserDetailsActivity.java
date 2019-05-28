package com.example.debarunlahiri.next;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class AddUserDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar3;
    private EditText etAddName, etAddPhone, etAddress;
    private Button savebtn;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private String user_id;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_details);

        toolbar3 = findViewById(R.id.toolbar3);
        toolbar3.setTitle("Add details");

        etAddName = findViewById(R.id.etAddName);
        etAddPhone = findViewById(R.id.etAddPhone);
        etAddress = findViewById(R.id.etAddress);
        savebtn = findViewById(R.id.savebtn);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            sendToLogin();
        } else {
            user_id = mAuth.getCurrentUser().getUid();

            savebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = etAddName.getText().toString();
                    String phone = etAddPhone.getText().toString();
                    String address = etAddress.getText().toString();

                    if (name.isEmpty()) {
                        etAddName.setError("Please enter your name");
                    } else if (phone.isEmpty()) {
                        etAddPhone.setError("Please enter your phone number");
                    } else  if (address.isEmpty()) {
                        etAddress.setError("Please enter address");
                    } else if (phone.length() < 10) {
                        etAddPhone.setError("Please enter correct phone number");
                    } else {
                        HashMap<String, Object> dataMap = new HashMap<>();
                        dataMap.put("name", name);
                        dataMap.put("phone", phone);
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
        Intent registerIntent = new Intent(AddUserDetailsActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
        finish();
    }
}
