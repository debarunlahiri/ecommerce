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

public class CardActivity extends AppCompatActivity {

    private Toolbar toolbar9;

    private EditText etCardNumber, etCardName, etCardMonth, etCardYear, etCardCVV, etAtmPin;
    private Button button6;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private String user_id;
    private String email;
    private String name;
    private String address;

    private String product_key;
    private String product_name;
    private String product_price;
    private String product_description;
    private String seller_name;
    private String product_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        toolbar9 = findViewById(R.id.toolbar9);
        toolbar9.setTitle("Add Card");

        etCardNumber = findViewById(R.id.etCardNumber);
        etCardName = findViewById(R.id.etCardName);
        etCardMonth = findViewById(R.id.etCardMonth);
        etCardYear = findViewById(R.id.etCardYear);
        etCardCVV = findViewById(R.id.etCardCVV);
        etAtmPin = findViewById(R.id.etAtmPin);
        button6 = findViewById(R.id.button6);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Bundle b = getIntent().getExtras();
        //product_key = b.get("product_key").toString();
        product_name = b.get("product_name").toString();
        product_price = b.get("product_price").toString();
        product_description = b.get("product_description").toString();
        seller_name = b.get("seller_name").toString();
        product_image = b.get("product_image").toString();
        name = b.get("name").toString();
        address = b.get("address").toString();
        email = b.get("email").toString();
        user_id = b.get("user_id").toString();

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n1 = etCardNumber.getText().toString();
                String n2 = etCardName.getText().toString();
                String n3 = etCardMonth.getText().toString();
                String n4 = etCardYear.getText().toString();
                String n5 = etCardCVV.getText().toString();
                String n6 = etAtmPin.getText().toString();

                if (n1.isEmpty()) {
                    etCardNumber.setError("Please enter details");
                } else if (n2.isEmpty()) {
                    etCardName.setError("Please enter details");
                } else if (n3.isEmpty()) {
                    etCardMonth.setError("Please enter details");
                } else if (n4.isEmpty()) {
                    etCardYear.setError("Please enter details");
                } else if (n5.isEmpty()) {
                    etCardCVV.setError("Please enter details");
                } else if (n6.isEmpty()) {
                    etAtmPin.setError("Please enter details");
                } else if (Integer.parseInt(n3) > 12) {
                    etCardMonth.setError("Please enter correct card details");
                } else if (n1.length() < 16) {
                    etCardNumber.setError("Please enter correct card details");
                } else {
                    HashMap<String, Object> dataMap = new HashMap<>();
                    dataMap.put("product_image", product_image);
                    dataMap.put("product_name", product_name);
                    dataMap.put("product_price", product_price);
                    dataMap.put("product_description", product_description);
                    dataMap.put("company_name", seller_name);
                    dataMap.put("name", name);
                    dataMap.put("address", address);
                    dataMap.put("email", email);
                    dataMap.put("user_id", user_id);
                    mDatabase.child("orders").child(user_id).push().setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Product purchased successfully", Toast.LENGTH_LONG).show();
                            Intent mainIntent = new Intent(CardActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                        }
                    });
                }


            }
        });


    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(CardActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
