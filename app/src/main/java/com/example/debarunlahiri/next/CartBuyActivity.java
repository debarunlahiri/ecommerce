package com.example.debarunlahiri.next;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class CartBuyActivity extends AppCompatActivity {

    private Toolbar toolbar13;
    private TextView tvCBName, tvCBAddress, tvCBTItems, tvCBTPrice;
    private Button button11;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private String name;
    private String address;
    private String total_price;
    private String total_product_count;
    private String user_id;

    @SuppressWarnings("unchecked")
    public void myMethod()
    {
        //...
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_buy);

        Bundle bundle = getIntent().getExtras();
        name = bundle.get("name").toString();
        address = bundle.get("address").toString();
        total_price = bundle.get("total_price").toString();
        total_product_count = bundle.get("total_product_count").toString();

        toolbar13 = findViewById(R.id.toolbar13);
        toolbar13.setTitle("Place order");
        setSupportActionBar(toolbar13);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar13.setNavigationIcon(getResources().getDrawable(R.mipmap.back));
        toolbar13.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvCBName = findViewById(R.id.tvCBName);
        tvCBAddress = findViewById(R.id.tvCBAddress);
        tvCBTItems = findViewById(R.id.tvCBTItems);
        tvCBTPrice = findViewById(R.id.tvCBTPrice);
        button11 = findViewById(R.id.button11);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            user_id = currentUser.getUid();
            button11.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getProductShipToOrders();
                }
            });

        } else {
            sendToLogin();
        }



        tvCBName.setText(name);
        tvCBAddress.setText(address);
        tvCBTItems.setText(total_product_count);
        tvCBTPrice.setText("₹ " + total_price);
    }

    private void getProductShipToOrders() {
        mDatabase.child("cart").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object producut_name = map.get("product_name");
                    Object product_image = map.get("product_image");
                    Object seller_name = map.get("seller_name");
                    Object cart_key = map.get("cart_key");
                    Object product_description = map.get("product_description");
                    Object product_price = map.get("product_price");

                    mDatabase.child("orders").child(user_id).push().setValue(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDatabase.child("cart").child(user_id).setValue(null);
        Toast.makeText(getApplicationContext(), "Product purchased successfully", Toast.LENGTH_LONG).show();
        Intent orderIntent = new Intent(CartBuyActivity.this, OrderActivity.class);
        startActivity(orderIntent);
        finish();

    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(CartBuyActivity.this, StartActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
