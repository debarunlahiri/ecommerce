package com.example.debarunlahiri.next;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProductMainSubListActivity extends AppCompatActivity {

    private Toolbar toolbar4;

    private TextView textView42;

    private RecyclerView recyclerview;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private ProductsAdapter productsAdapter;
    private List<Products> productsLists = new ArrayList<>();
    private Context mContext;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_main_sub_list);

        toolbar4 = findViewById(R.id.toolbar4);



        textView42 = findViewById(R.id.textView42);

        mContext = ProductMainSubListActivity.this;

        recyclerview = findViewById(R.id.recyclerview);
        productsAdapter = new ProductsAdapter(productsLists, mContext);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(productsAdapter);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Bundle bundle = getIntent().getExtras();
        String electronics = bundle.getString("electronics");
        String games = bundle.getString("games");
        String clothes = bundle.getString("clothes");

        if (currentUser == null) {
            sendToLogin();
        } else {
            user_id = mAuth.getCurrentUser().getUid();

            if (electronics != null) {
                toolbar4.setTitle("Electronics");
                mDatabase.child("products").child("Electronics").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()) {
                            textView42.setVisibility(View.INVISIBLE);
                            Products products = dataSnapshot.getValue(Products.class);
                            productsLists.add(products);
                            productsAdapter.notifyDataSetChanged();
                        }


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
            } else if (games != null) {
                toolbar4.setTitle("Games");
                mDatabase.child("products").child("Games").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()) {
                            textView42.setVisibility(View.INVISIBLE);
                            Products products = dataSnapshot.getValue(Products.class);
                            productsLists.add(products);
                            productsAdapter.notifyDataSetChanged();
                        }

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
            } else if (clothes != null) {
                toolbar4.setTitle("Clothes");
                mDatabase.child("products").child("Clothes").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()) {
                            textView42.setVisibility(View.INVISIBLE);
                            Products products = dataSnapshot.getValue(Products.class);
                            productsLists.add(products);
                            productsAdapter.notifyDataSetChanged();
                        }

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
            } else {
                sendToMain();
            }
        }





    }


    private void sendToLogin() {
        Intent loginIntent = new Intent(ProductMainSubListActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(ProductMainSubListActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
