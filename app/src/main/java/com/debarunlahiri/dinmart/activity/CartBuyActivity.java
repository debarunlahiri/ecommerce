package com.debarunlahiri.dinmart.activity;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.debarunlahiri.dinmart.model.Cart;
import com.debarunlahiri.dinmart.MainActivity;
import com.debarunlahiri.dinmart.StartActivity;
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

import java.util.ArrayList;
import java.util.HashMap;

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

    ArrayList<String> cart_list;

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
        cart_list = getIntent().getStringArrayListExtra("cart_list");

        toolbar13 = findViewById(R.id.toolbar13);
        toolbar13.setTitle("Place order");
        toolbar13.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar13);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar13.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
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
        final String order_id = mDatabase.child("orders").push().getKey();
        final HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("user_id", user_id);
        dataMap.put("order_id", order_id);
        dataMap.put("timestamp", System.currentTimeMillis());
        dataMap.put("name", name);
        dataMap.put("address", address);
        dataMap.put("user_phone_number", mAuth.getCurrentUser().getPhoneNumber());
        dataMap.put("order_status", "pending");
        mDatabase.child("orders").child(order_id).setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDatabase.child("cart").child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot da : dataSnapshot.getChildren())  {
                            Cart cart = da.getValue(Cart.class);
                            HashMap<String, Object> mCartDataMap = new HashMap<>();
                            mCartDataMap.put("product_key", cart.getProduct_key());
                            mCartDataMap.put("product_item_count", cart.getProduct_item_count());
                            mCartDataMap.put("product_price", cart.getProduct_price());
                            mCartDataMap.put("product_quantity", cart.getProduct_quantity());
                            mCartDataMap.put("product_weight_unit", cart.getProduct_weight_unit());
                            mCartDataMap.put("user_id", cart.getUser_id());
                            mCartDataMap.put("total_product_price", cart.getTotal_product_price());
                            mDatabase.child("cart").child(cart.getUser_id()).child(cart.getProduct_key()).child("visibility").setValue(false);
                            mDatabase.child("orders").child(order_id).child("order_list").child(cart.getProduct_key()).setValue(mCartDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent mainIntent = new Intent(CartBuyActivity.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(mainIntent);
                                    finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
//        mDatabase.child("cart").child(user_id).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
//                    Object producut_name = map.get("product_name");
//                    Object product_image = map.get("product_image");
//                    Object seller_name = map.get("seller_name");
//                    Object cart_key = map.get("cart_key");
//                    Object product_description = map.get("product_description");
//                    Object product_price = map.get("product_price");
//
//                    mDatabase.child("orders").child(user_id).push().setValue(map);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        mDatabase.child("cart").child(user_id).setValue(null);
//        Toast.makeText(getApplicationContext(), "Product purchased successfully", Toast.LENGTH_LONG).show();
//        Intent orderIntent = new Intent(CartBuyActivity.this, OrderActivity.class);
//        startActivity(orderIntent);
//        finish();

    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(CartBuyActivity.this, StartActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
