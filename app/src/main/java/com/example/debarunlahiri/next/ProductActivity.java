package com.example.debarunlahiri.next;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProductActivity extends AppCompatActivity {

    private Toolbar toolbar5;

    private TextView tvPName, tvPPrice, tvPDesc, tvSName;
    private ImageView productIV;
    private Button button2,button;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private String user_id;
    private String email_id;

    private String product_key;
    private String product_name;
    private String product_price;
    private String product_description;
    private String seller_name;
    private String product_image;
    private String product_category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        toolbar5 = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar5);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar5.setNavigationIcon(getResources().getDrawable(R.mipmap.back));
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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Bundle b = getIntent().getExtras();
        product_key = b.get("product_key").toString();
        product_category = b.get("product_category").toString();


        if (currentUser == null) {
            sendToLogin();
        } else {
            if (b != null) {
                getUserDetails();
                mDatabase.child("products").child(product_category).child(product_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        product_description = dataSnapshot.child("product_description").getValue().toString();
                        product_name = dataSnapshot.child("product_name").getValue().toString();
                        product_price = dataSnapshot.child("product_price").getValue().toString();
                        product_image = dataSnapshot.child("product_image").getValue().toString();
                        seller_name = dataSnapshot.child("company_name").getValue().toString();

                        showProduct();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent productIntent = new Intent(ProductActivity.this, AddDeliveryDetails.class);
                        //productIntent.putExtra("product_key", );
                        productIntent.putExtra("product_name", product_name);
                        productIntent.putExtra("product_price", product_price);
                        productIntent.putExtra("product_image", product_image);
                        productIntent.putExtra("product_description", product_description);
                        productIntent.putExtra("seller_name", seller_name);
                        productIntent.putExtra("from_cart", "no");
                        startActivity(productIntent);
                    }
                });
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveProductToCart();
                    }
                });
            } else {
                sendToMain();
            }
        }




    }

    private void saveProductToCart() {
        String cart_key = mDatabase.child("cart").child(user_id).push().getKey();
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("product_name", product_name);
        dataMap.put("product_price", product_price);
        dataMap.put("product_image", product_image);
        dataMap.put("seller_name", seller_name);
        dataMap.put("cart_key", cart_key);
        dataMap.put("product_description", product_description);
        mDatabase.child("cart").child(user_id).child(cart_key).updateChildren(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "Product added successfully to cart", Toast.LENGTH_LONG).show();
                Intent cartIntent = new Intent(ProductActivity.this, CartActivity.class);
                startActivity(cartIntent);
                finish();
            }
        });
    }

    private void getUserDetails() {
        user_id = currentUser.getUid();
        mDatabase.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email_id = dataSnapshot.child("email").getValue().toString();
                //showProduct();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showProduct() {
        Picasso.get().load(product_image).into(productIV);
        toolbar5.setTitle(product_name);
        tvPName.setText(product_name);
        tvSName.setText("by " + seller_name);
        tvPPrice.setText("₹" + product_price);
        tvPDesc.setText(product_description);
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
}
