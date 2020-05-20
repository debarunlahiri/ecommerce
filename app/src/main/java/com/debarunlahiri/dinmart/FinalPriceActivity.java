package com.debarunlahiri.dinmart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.debarunlahiri.dinmart.next.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FinalPriceActivity extends AppCompatActivity {

    private Toolbar finalpricetoolbar;

    private TextView tvFinalPrice;
    private Button bFinalPrice;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private List<Cart> cartList = new ArrayList<>();

    private String user_id;
    private int total_price = 0;
    private String p_price;
    public int total_product_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_price);

        finalpricetoolbar = findViewById(R.id.finalpricetoolbar);
        finalpricetoolbar.setTitle("Final Price");
        finalpricetoolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(finalpricetoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        finalpricetoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        finalpricetoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvFinalPrice = findViewById(R.id.tvFinalPrice);
        bFinalPrice = findViewById(R.id.bFinalPrice);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        user_id = currentUser.getUid();

        mDatabase.child("cart").child(user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Cart cart = dataSnapshot.getValue(Cart.class);
                cartList.add(cart);
                if (cart.getTotal_product_price().equals("0")) {
                    total_price = total_price + Integer.parseInt(cart.getProduct_price());
                } else {
                    total_price = total_price + Integer.parseInt(cart.getTotal_product_price());
                }
                if (cart.isVisibility()) {
                    total_product_count++;
                }
                tvFinalPrice.setText("₹" + String.valueOf(total_price));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        bFinalPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent buyIntent = new Intent(FinalPriceActivity.this, AddDeliveryDetails.class);
                buyIntent.putExtra("total_price", String.valueOf(total_price));
                buyIntent.putExtra("total_product_count", String.valueOf(total_product_count));
                buyIntent.putExtra("from_cart", "yes");
                buyIntent.putParcelableArrayListExtra("cart_list", (ArrayList<? extends Parcelable>) cartList);
                startActivity(buyIntent);
            }
        });
    }
}
