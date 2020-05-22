package com.debarunlahiri.dinmart.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.debarunlahiri.dinmart.MainActivity;
import com.debarunlahiri.dinmart.adapter.ProductsAdapter;
import com.debarunlahiri.dinmart.model.Products;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar toolbar5;

    private Context mContext;

    private TextView tvPName, tvPPrice, tvPDesc, tvSName, tvProductCount;
    private ImageView productIV;
    private Button button2,button;
    private EditText etUnit;
    private ImageButton ibProductMinus, ibProductPlus;
    private CardView cvProductCounter;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private String user_id;
    private String email_id;

    private String product_key;
    private String product_name;
    private String product_price;
    private String product_description;
    private String product_weight_unit;
    private String product_image;
    private String product_category;
    private String product_quantity;

    private Spinner spWeightUnit;
    private int itemCount = 1;
    Products products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        mContext = ProductActivity.this;

        toolbar5 = findViewById(R.id.toolbar5);
        toolbar5.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar5);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar5.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar5.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvPName = findViewById(R.id.tvPName);
        tvPPrice = findViewById(R.id.tvPPrice);
        tvPDesc = findViewById(R.id.tvPDesc);
        tvSName = findViewById(R.id.tvSName);
        productIV = findViewById(R.id.productIV);
        button2 = findViewById(R.id.button2);
        button = findViewById(R.id.button);
        spWeightUnit = findViewById(R.id.spWeightUnit);
        etUnit = findViewById(R.id.etUnit);
        ibProductMinus = findViewById(R.id.ibProductMinus);
        ibProductPlus = findViewById(R.id.ibProductPlus);
        tvProductCount = findViewById(R.id.tvProductCount);
        cvProductCounter = findViewById(R.id.cvProductCounter);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Bundle b = getIntent().getExtras();
        product_key = b.get("product_key").toString();
        product_category = b.get("product_category").toString();

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.weight_unit, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spWeightUnit.setAdapter(arrayAdapter);
        spWeightUnit.setOnItemSelectedListener(this);

        tvProductCount.setText(String.valueOf(itemCount));
        cvProductCounter.setVisibility(View.GONE);

        mDatabase.child("products").child(product_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                products = dataSnapshot.getValue(Products.class);
                product_description = dataSnapshot.child("product_description").getValue().toString();
                product_name = dataSnapshot.child("product_name").getValue().toString();
                product_price = dataSnapshot.child("product_price").getValue().toString();
                product_image = dataSnapshot.child("product_image").getValue().toString();
                product_image = dataSnapshot.child("product_image").getValue().toString();
                product_weight_unit = dataSnapshot.child("product_weight_unit").getValue().toString();
                product_quantity = dataSnapshot.child("product_quantity").getValue().toString();

                Picasso.get().load(product_image).into(productIV);
                toolbar5.setTitle(product_name);
                tvPName.setText(product_name);
                tvSName.setText("by " + "DIN Mart");
                tvPPrice.setText("₹" + product_price + " | " + product_quantity + "/" + product_weight_unit.toLowerCase());
                tvPDesc.setText(product_description);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ibProductMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCount >= 1) {
                    itemCount = Integer.parseInt(tvProductCount.getText().toString());
                    tvProductCount.setText(String.valueOf(--itemCount));
                    int total_price = Integer.parseInt(product_price)*itemCount;
                    mDatabase.child("cart").child(Variables.global_user_id).child(product_key).child("total_product_price").setValue(String.valueOf(total_price));
                    mDatabase.child("cart").child(Variables.global_user_id).child(product_key).child("product_item_count").setValue(String.valueOf(itemCount));
                }

            }
        });

        ibProductPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemCount = Integer.parseInt(tvProductCount.getText().toString());
                tvProductCount.setText(String.valueOf(++itemCount));
                int total_price = Integer.parseInt(product_price)*itemCount;
                mDatabase.child("cart").child(Variables.global_user_id).child(product_key).child("total_product_price").setValue(String.valueOf(total_price));
                mDatabase.child("cart").child(Variables.global_user_id).child(product_key).child("product_item_count").setValue(String.valueOf(itemCount));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null && Variables.global_user_id.equals("")) {
                    Toast.makeText(getApplicationContext(), "User login required", Toast.LENGTH_LONG).show();
                    Intent loginIntent = new Intent(ProductActivity.this, CardLoginActivity.class);
                    loginIntent.putExtra("product_key", product_key);
                    startActivity(loginIntent);

                } else {
                    cvProductCounter.setVisibility(View.VISIBLE);
                    HashMap<String, Object> dataMap = new HashMap<>();
                    dataMap.put("product_key", products.getProduct_key());
                    dataMap.put("product_item_count", String.valueOf(itemCount));
                    dataMap.put("product_price", products.getProduct_price());
                    dataMap.put("product_quantity", products.getProduct_quantity());
                    dataMap.put("product_weight_unit", products.getProduct_weight_unit());
                    dataMap.put("user_id", user_id);
                    dataMap.put("visibility", true);
                    dataMap.put("total_product_price", "0");
                    mDatabase.child("cart").child(user_id).child(product_key).updateChildren(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(mContext, "Product added successfully to cart", Toast.LENGTH_LONG).show();
                            button.setVisibility(View.GONE);
                            cvProductCounter.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    private void saveProductToCart() {

    }

    private void getUserDetails() {
        user_id = currentUser.getUid();
        mDatabase.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                email_id = dataSnapshot.child("email").getValue().toString();
                //showProduct();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showProduct(Products products) {

    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(ProductActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void sendToMain() {
        Intent registerIntent = new Intent(ProductActivity.this, MainActivity.class);
        startActivity(registerIntent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
