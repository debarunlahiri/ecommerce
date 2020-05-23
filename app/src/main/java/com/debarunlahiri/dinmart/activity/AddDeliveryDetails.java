package com.debarunlahiri.dinmart.activity;

import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.debarunlahiri.dinmart.next.R;
import com.debarunlahiri.dinmart.utils.Variables;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddDeliveryDetails extends AppCompatActivity {

    private Toolbar toolbar14;
    private Button button10;
    private EditText etADAddress, etADName;
    private TextView tvWordsCounter;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private String total_price = " ";
    private String total_product_count = " ";
    private String user_id = "";

    private String product_key = "";
    private String product_name = "";
    private String product_price = "";
    private String product_description = "";
    private String seller_name = "";
    private String product_image = "";
    private String product_category = "";
    private String from_cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery_details);

        Bundle bundle = getIntent().getExtras();
        from_cart = bundle.get("from_cart").toString();
        if (from_cart.equals("no")) {
            product_name = bundle.get("product_name").toString();
            product_price = bundle.get("product_price").toString();
            product_description = bundle.get("product_description").toString();
            seller_name = bundle.get("seller_name").toString();
            product_image = bundle.get("product_image").toString();
        } else if (from_cart.equals("yes")){
            total_price = bundle.get("total_price").toString();
            total_product_count = bundle.get("total_product_count").toString();
        }

        toolbar14 = findViewById(R.id.toolbar14);
        toolbar14.setTitle("Add Details");
        toolbar14.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar14);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar14.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar14.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etADAddress = findViewById(R.id.etADAddress);
        etADName = findViewById(R.id.etADName);
        button10 = findViewById(R.id.button10);
        tvWordsCounter = findViewById(R.id.tvWordsCounter);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getApplicationContext(), "Please login again", Toast.LENGTH_LONG).show();
            Intent loginIntent = new Intent(AddDeliveryDetails.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            mDatabase.child("users").child(Variables.global_user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        etADAddress.setText(address);
                        etADName.setText(name);

                        button10.setText("Deliver to this address");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            if (from_cart.equals("yes")) {
                button10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = etADName.getText().toString();
                        String address = etADAddress.getText().toString();

                        if (name.isEmpty()) {
                            etADName.setError("Please enter name");
                        } else if (address.isEmpty()) {
                            etADAddress.setError("Please enter address");
                        } else if (address.length() < 80) {
                            Toast.makeText(getApplicationContext(), "Please enter detail address. Minimum 80 characters.", Toast.LENGTH_LONG).show();
                        } else {
                            Intent cartBuyIntent = new Intent(AddDeliveryDetails.this, CartBuyActivity.class);
                            cartBuyIntent.putExtra("total_product_count", String.valueOf(total_product_count));
                            cartBuyIntent.putExtra("total_price", String.valueOf(total_price));
                            cartBuyIntent.putExtra("name", name);
                            cartBuyIntent.putExtra("address", address);
                            startActivity(cartBuyIntent);
                        }



                    }
                });
            } else if (from_cart.equals("no")){
                button10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = etADName.getText().toString();
                        String address = etADAddress.getText().toString();

                        if (name.isEmpty()) {
                            etADName.setError("Please enter name");
                        } else if (address.isEmpty()) {
                            etADAddress.setError("Please enter address");
                        } else {
//                            Intent productIntent = new Intent(AddDeliveryDetails.this, BuyActivity.class);
//                            //productIntent.putExtra("product_key", );
//                            productIntent.putExtra("product_name", product_name);
//                            productIntent.putExtra("product_price", product_price);
//                            productIntent.putExtra("product_image", product_image);
//                            productIntent.putExtra("product_description", product_description);
//                            productIntent.putExtra("seller_name", seller_name);
//                            productIntent.putExtra("name", name);
//                            productIntent.putExtra("address", address);
//                            startActivity(productIntent);
                        }

                    }
                });
            }

            etADAddress.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    tvWordsCounter.setText(String.valueOf(s.length()) + " characters");
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }




    }


}
