package com.debarunlahiri.dinmart.activity;

import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Geocoder;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.debarunlahiri.dinmart.model.Address;
import com.debarunlahiri.dinmart.next.R;
import com.debarunlahiri.dinmart.utils.Variables;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddDeliveryDetails extends AppCompatActivity {

    private Toolbar toolbar14;
    private Button bSaveAddress;
    private EditText etFullName, etMobNo, etPincode, etHseNo, etColony, etLandmark;
    private TextView tvCity, tvState, tvCityHeader, tvStateHeader;

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
    private String type;
    private String address_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery_details);

        address_id = getIntent().getStringExtra("address_id");

//        Bundle bundle = getIntent().getExtras();
//        from_cart = bundle.get("from_cart").toString();
//        if (from_cart.equals("no")) {
//            product_name = bundle.get("product_name").toString();
//            product_price = bundle.get("product_price").toString();
//            product_description = bundle.get("product_description").toString();
//            seller_name = bundle.get("seller_name").toString();
//            product_image = bundle.get("product_image").toString();
//        } else if (from_cart.equals("yes")){
//            total_price = bundle.get("total_price").toString();
//            total_product_count = bundle.get("total_product_count").toString();
//        }
//        type = getIntent().getStringExtra("type");

        toolbar14 = findViewById(R.id.toolbar14);
        if (getIntent().getStringExtra("type") != null) {
            toolbar14.setTitle("Edit Address");
        } else {
            toolbar14.setTitle("Add Address");
        }
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

        etFullName = findViewById(R.id.etFullName);
        etMobNo = findViewById(R.id.etMobNo);
        etPincode = findViewById(R.id.etPincode);
        etColony = findViewById(R.id.etColony);
        etLandmark = findViewById(R.id.etLandmark);
        etHseNo = findViewById(R.id.etHseNo);
        bSaveAddress = findViewById(R.id.bSaveAddress);
        tvCity = findViewById(R.id.tvCity);
        tvState = findViewById(R.id.tvState);
        tvCityHeader = findViewById(R.id.tvCityHeader);
        tvStateHeader = findViewById(R.id.tvStateHeader);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (address_id != null) {
            mDatabase.child("users").child(Variables.global_user_id).child("address_list").child(address_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Address address = dataSnapshot.getValue(Address.class);

                    etFullName.setText(address.getName());
                    etMobNo.setText(address.getPhone());
                    etFullName.setText(address.getName());
                    etPincode.setText(address.getPincode());
                    etColony.setText(address.getColony());
                    etLandmark.setText(address.getLandmark());
                    etHseNo.setText(address.getHouse_no());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        bSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etFullName.getText().toString();
                String mob_no = etMobNo.getText().toString();
                String pincode = etPincode.getText().toString();
                String house_no = etHseNo.getText().toString();
                String colony = etColony.getText().toString();
                String landmark = etLandmark.getText().toString();
                String city = tvCity.getText().toString();
                String state = tvState.getText().toString();

                if (name.isEmpty()) {
                    etFullName.setError("Please enter name");
                } else if (mob_no.isEmpty()) {
                    etMobNo.setError("Please enter mobile number");
                } else if (pincode.isEmpty()) {
                    etPincode.setError("Please enter pincode");
                } else if (colony.isEmpty()) {
                    etColony.setError("Please enter this field");
                } else if (house_no.isEmpty()) {
                    etHseNo.setError("Please enter this field");
                } else if (landmark.isEmpty()) {
                    etLandmark.setError("Please enter landmark");
                } else if (pincode.contains("-")) {
                    etPincode.setError("Please enter proper pincode");
                } else if (mob_no.contains("-")) {
                    etMobNo.setError("Please enter prober mob no.");
                } else {
                    String address = house_no + ", " + colony + ", " + city + ", " + state + ", " + pincode + "\n" + landmark;
                    String address_id = mDatabase.child("users").child(Variables.global_user_id).child("address_list").push().getKey();
                    HashMap<String, Object> mAddressDataMap = new HashMap<>();
                    mAddressDataMap.put("name", name);
                    mAddressDataMap.put("phone", mob_no);
                    mAddressDataMap.put("house_no", house_no);
                    mAddressDataMap.put("colony", colony);
                    mAddressDataMap.put("landmark", landmark);
                    mAddressDataMap.put("pincode", pincode);
                    mAddressDataMap.put("city", city);
                    mAddressDataMap.put("state", state);
                    mAddressDataMap.put("full_address", address);
                    mAddressDataMap.put("address_id", address_id);
                    mAddressDataMap.put("isDefault", false);

                    mDatabase.child("users").child(Variables.global_user_id).child("address_list").child(address_id).updateChildren(mAddressDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Address successfully saved", Toast.LENGTH_LONG).show();
                                onBackPressed();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }


        });

        tvCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Currently we are delivery to this city", Toast.LENGTH_LONG).show();
            }
        });

        tvState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Currently we are delivery to this state", Toast.LENGTH_LONG).show();
            }
        });

//        if (currentUser == null) {
//            Toast.makeText(getApplicationContext(), "Please login again", Toast.LENGTH_LONG).show();
//            Intent loginIntent = new Intent(AddDeliveryDetails.this, LoginActivity.class);
//            startActivity(loginIntent);
//            finish();
//        } else {
//            mDatabase.child("users").child(Variables.global_user_id).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        String name = dataSnapshot.child("name").getValue().toString();
//                        String address = dataSnapshot.child("address").getValue().toString();
//
//                        etADAddress.setText(address);
//                        etADName.setText(name);
//
//                        button10.setText("Deliver to this address");
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//            if (from_cart.equals("yes")) {
//                button10.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String name = etADName.getText().toString();
//                        String address = etADAddress.getText().toString();
//
//                        if (name.isEmpty()) {
//                            etADName.setError("Please enter name");
//                        } else if (address.isEmpty()) {
//                            etADAddress.setError("Please enter address");
//                        } else if (address.length() < 80) {
//                            Toast.makeText(getApplicationContext(), "Please enter detail address. Minimum 80 characters.", Toast.LENGTH_LONG).show();
//                        } else {
//                            Intent cartBuyIntent = new Intent(AddDeliveryDetails.this, CartBuyActivity.class);
//                            cartBuyIntent.putExtra("total_product_count", String.valueOf(total_product_count));
//                            cartBuyIntent.putExtra("total_price", String.valueOf(total_price));
//                            cartBuyIntent.putExtra("name", name);
//                            cartBuyIntent.putExtra("address", address);
//                            startActivity(cartBuyIntent);
//                        }
//
//
//
//                    }
//                });
//            } else if (from_cart.equals("no")){
//                button10.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String name = etADName.getText().toString();
//                        String address = etADAddress.getText().toString();
//
//                        if (name.isEmpty()) {
//                            etADName.setError("Please enter name");
//                        } else if (address.isEmpty()) {
//                            etADAddress.setError("Please enter address");
//                        } else {
////                            Intent productIntent = new Intent(AddDeliveryDetails.this, BuyActivity.class);
////                            //productIntent.putExtra("product_key", );
////                            productIntent.putExtra("product_name", product_name);
////                            productIntent.putExtra("product_price", product_price);
////                            productIntent.putExtra("product_image", product_image);
////                            productIntent.putExtra("product_description", product_description);
////                            productIntent.putExtra("seller_name", seller_name);
////                            productIntent.putExtra("name", name);
////                            productIntent.putExtra("address", address);
////                            startActivity(productIntent);
//                        }
//
//                    }
//                });
//            }
//
//            etADAddress.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    tvWordsCounter.setText(String.valueOf(s.length()) + " characters");
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });
//
//        }




    }


}
