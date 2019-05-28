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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class BusinessDetailsAddActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText editText;
    private Button button4;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details_add);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add company details");

        editText = findViewById(R.id.editText);
        button4 = findViewById(R.id.button4);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();

        Intent getIntent = new Intent();
        Bundle b = getIntent().getExtras();
        final String key = b.get("key").toString();

        if (currentUser != null) {
            button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String company_name = editText.getText().toString();
                    button4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (company_name.isEmpty()) {
                                editText.setError("Enter company name");
                            } else {
                                Map<String, Object> dataMap = new HashMap<>();
                                dataMap.put("company_name", company_name);
                                mDatabase.child("business").child(key).updateChildren(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "Company Successfully Registered", Toast.LENGTH_LONG).show();
                                        Intent homeIntent = new Intent(BusinessDetailsAddActivity.this, MainActivity.class);
                                        startActivity(homeIntent);
                                        finish();
                                    }
                                });
                            }

                        }
                    });
                }
            });
        } else {
            sendToLogin();
        }





    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(BusinessDetailsAddActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
