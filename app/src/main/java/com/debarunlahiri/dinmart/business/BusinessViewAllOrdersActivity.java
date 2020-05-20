package com.debarunlahiri.dinmart.business;

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

public class BusinessViewAllOrdersActivity extends AppCompatActivity {

    private Context mContext;

    private Toolbar bvaptoolbar;

    private RecyclerView rvVAP;
    private List<BusinessOrders> businessOrdersList = new ArrayList<>();
    private ViewAllProductsAdapter viewAllProductsAdapter;
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_view_all_products);

        mContext = BusinessViewAllOrdersActivity.this;

        bvaptoolbar = findViewById(R.id.bvaptoolbar);
        bvaptoolbar.setTitleTextColor(Color.WHITE);
        bvaptoolbar.setTitle("Your Orders");
        setSupportActionBar(bvaptoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bvaptoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        bvaptoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        user_id = currentUser.getUid();

        rvVAP = findViewById(R.id.rvVAP);
        viewAllProductsAdapter = new ViewAllProductsAdapter(mContext, businessOrdersList, "business");
        linearLayoutManager = new LinearLayoutManager(mContext);
        rvVAP.setLayoutManager(linearLayoutManager);
        rvVAP.setAdapter(viewAllProductsAdapter);

        mDatabase.child("orders").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                BusinessOrders businessOrders = dataSnapshot.getValue(BusinessOrders.class);
                businessOrdersList.add(businessOrders);
                viewAllProductsAdapter.notifyDataSetChanged();
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
