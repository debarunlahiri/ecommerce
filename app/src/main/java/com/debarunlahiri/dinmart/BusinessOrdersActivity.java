package com.debarunlahiri.dinmart;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.debarunlahiri.dinmart.adapter.CartAdapter;
import com.debarunlahiri.dinmart.model.Cart;
import com.debarunlahiri.dinmart.next.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class BusinessOrdersActivity extends AppCompatActivity {

    private Toolbar businesstoolbar;

    private Context mContext;
    private RecyclerView rvBusinessOrders;
    private CartAdapter cartAdapter;
    private List<Cart> cartList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_orders);

        mContext = BusinessOrdersActivity.this;

        businesstoolbar = findViewById(R.id.businesstoolbar);
        businesstoolbar.setTitleTextColor(Color.WHITE);
        businesstoolbar.setTitle("All Orders");
        setSupportActionBar(businesstoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        businesstoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        businesstoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rvBusinessOrders = findViewById(R.id.rvBusinessOrders);
        cartAdapter = new CartAdapter(cartList, mContext);
        linearLayoutManager = new LinearLayoutManager(mContext);
        rvBusinessOrders.setLayoutManager(linearLayoutManager);
        rvBusinessOrders.setAdapter(cartAdapter);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        user_id = currentUser.getUid();

        mDatabase.child("cart").child(user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Cart cart = dataSnapshot.getValue(Cart.class);
                cartList.add(cart);
                cartAdapter.notifyDataSetChanged();
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

    }
}
