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

public class EditCompanyInfoActivity extends AppCompatActivity {

    private Toolbar toolbar11;

    private EditText etEditOwnerName, etEditCompanyName, etEditPhoneNumber, etEditEmailId;
    private Button editcompanysavebtn;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private String user_id;

    private String company_name = null;
    private String owner_name = null;
    private String phone_number = null;
    private String email_id = null;
    private String email = null;
    private String key = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_company_info);

        toolbar11 = findViewById(R.id.toolbar11);
        toolbar11.setTitle("Edit Company Info");
        setSupportActionBar(toolbar11);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar11.setNavigationIcon(getResources().getDrawable(R.mipmap.back));
        toolbar11.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etEditOwnerName = findViewById(R.id.etEditOwnerName);
        etEditCompanyName = findViewById(R.id.etEditCompanyName);
        etEditPhoneNumber = findViewById(R.id.etEditPhoneNumber);
        etEditEmailId = findViewById(R.id.etEditEmailId);
        editcompanysavebtn = findViewById(R.id.editcompanysavebtn);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        etEditEmailId.setFocusable(false);
        etEditEmailId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You cannot change your email id", Toast.LENGTH_LONG).show();
            }
        });
        getUserDetails();
        editcompanysavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCompanyDetails();
            }
        });


    }

    private void saveCompanyDetails() {
        String a = etEditOwnerName.getText().toString();
        String b = etEditCompanyName.getText().toString();
        String c = etEditEmailId.getText().toString();
        String d = etEditPhoneNumber.getText().toString();

        if (a.isEmpty()) {
            etEditOwnerName.setError("Enter value");
        } else if (b.isEmpty()) {
            etEditCompanyName.setError("Enter value");
        } else if (c.isEmpty()) {
            etEditEmailId.setError("Enter value");
        } else if (d.isEmpty()) {
            etEditPhoneNumber.setError("Enter value");
        } else {
            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put("company_name", b);
            dataMap.put("company_phone_number", d);
            dataMap.put("email", c);
            dataMap.put("owner_name", a);
            mDatabase.child("business").child(key).updateChildren(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendToMain();
                    } else {
                        String errMsg = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(), "Error: " + errMsg, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    private void sendToMain() {
        Intent mainIntent = new Intent(EditCompanyInfoActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void getUserDetails() {
        mDatabase.child("users").child(currentUser.getUid()).child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email_id = dataSnapshot.getValue().toString();
                getCompanyDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getCompanyDetails() {
        mDatabase.child("business").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    email = ds.child("email").getValue().toString();
                    key = ds.getKey();
                    if (email_id.equals(email)) {
                        fetchCompanyDetails();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchCompanyDetails() {
        mDatabase.child("business").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    company_name = dataSnapshot.child("company_name").getValue().toString();
                    owner_name = dataSnapshot.child("owner_name").getValue().toString();
                    phone_number = dataSnapshot.child("company_phone_number").getValue().toString();
                    displayDetails();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayDetails() {
        etEditEmailId.setText(email_id);
        etEditPhoneNumber.setText(phone_number);
        etEditOwnerName.setText(owner_name);
        etEditCompanyName.setText(company_name);

    }
}
