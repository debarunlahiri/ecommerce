package com.debarunlahiri.dinmart.business;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.debarunlahiri.dinmart.model.Cart;
import com.debarunlahiri.dinmart.next.R;
import com.debarunlahiri.dinmart.utils.Variables;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class BusinessOrderDetailsActivity extends AppCompatActivity {

    private Context mContext;

    private Toolbar businessorderdetailstoolbar;

    private TextView tvOrderPersonName, tvOrderPersonAddress, tvOrderPersonPhone, tvOrderDate, tvOrderStatus;
    private Button bOrderDetails, bAcceptOrder;

    private RecyclerView rvCart;
    private BusinessCartAdapter businessCartAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<Cart> cartList = new ArrayList<>();

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private String user_id, order_id;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_order_details2);

        mContext = BusinessOrderDetailsActivity.this;

        order_id = getIntent().getStringExtra("order_id");
        type = getIntent().getStringExtra("type");

        businessorderdetailstoolbar = findViewById(R.id.businessorderdetailstoolbar);
        businessorderdetailstoolbar.setTitle("Order Details");
        businessorderdetailstoolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(businessorderdetailstoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        businessorderdetailstoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        businessorderdetailstoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvOrderPersonName = findViewById(R.id.tvOrderPersonName);
        tvOrderPersonAddress = findViewById(R.id.tvOrderPersonAddress);
        tvOrderPersonPhone = findViewById(R.id.tvOrderPersonPhone);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        bOrderDetails = findViewById(R.id.bOrderDetails);
        bAcceptOrder = findViewById(R.id.bAcceptOrder);

        bOrderDetails.setVisibility(View.GONE);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        rvCart = findViewById(R.id.rvCart);
        businessCartAdapter = new BusinessCartAdapter(mContext, cartList);
        linearLayoutManager = new LinearLayoutManager(mContext);
        rvCart.setAdapter(businessCartAdapter);
        rvCart.setLayoutManager(linearLayoutManager);

        if (type.equals("users")) {
            bAcceptOrder.setVisibility(View.GONE);
        }

        mDatabase.child("orders").child(order_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvOrderPersonAddress.setText("Person Address: " + dataSnapshot.child("address").getValue().toString());
                tvOrderPersonName.setText("Person Name: " + dataSnapshot.child("name").getValue().toString());
                tvOrderStatus.setText("Order Status: " + dataSnapshot.child("order_status").getValue().toString());
                tvOrderPersonPhone.setText("Person Phone No.: " + dataSnapshot.child("user_phone_number").getValue().toString());

                Calendar calendar = Calendar.getInstance();
                TimeZone tz = TimeZone.getDefault();
                calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                java.util.Date currenTimeZone=new java.util.Date((long)dataSnapshot.child("timestamp").getValue());

                tvOrderDate.setText("Order Date: " + sdf.format(currenTimeZone));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("orders").child(order_id).child("order_list").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Cart cart = dataSnapshot.getValue(Cart.class);
                cartList.add(cart);
                businessCartAdapter.notifyDataSetChanged();
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

        mDatabase.child("orders").child(order_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Variables.order_status = dataSnapshot.child("order_status").getValue().toString();

                if (Variables.order_status.equals("pending")) {
                    bAcceptOrder.setText("Accept Order");
                } else if (Variables.order_status.equals("accepted")) {
                    bAcceptOrder.setText("Ready to Deliver");
                } else if (Variables.order_status.equals("delivered")){
                    bAcceptOrder.setText("Delivered");
                    bAcceptOrder.setEnabled(false);
                }

                Toast.makeText(getApplicationContext(), Variables.order_status, Toast.LENGTH_LONG).show();

                bAcceptOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Variables.order_status.equals("pending")) {
                            bAcceptOrder.setText("Accept Order");
                            mDatabase.child("orders").child(order_id).child("order_status").setValue("accepted");
                        } else if (Variables.order_status.equals("accepted")) {
                            bAcceptOrder.setText("Ready to Deliver");
                            mDatabase.child("orders").child(order_id).child("order_status").setValue("ready_to_deliver");
                        } else if (Variables.order_status.equals("ready_to_deliver")) {
                            bAcceptOrder.setText("Delivered");
                            mDatabase.child("orders").child(order_id).child("order_status").setValue("delivered");
                        }

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
