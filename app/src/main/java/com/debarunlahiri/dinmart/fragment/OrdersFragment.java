package com.debarunlahiri.dinmart.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.debarunlahiri.dinmart.business.BusinessOrders;
import com.debarunlahiri.dinmart.business.BusinessViewAllOrdersActivity;
import com.debarunlahiri.dinmart.business.ViewAllProductsAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment {

    private Toolbar orderstoolbar;

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

    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderstoolbar = view.findViewById(R.id.orderstoolbar);
        orderstoolbar.setTitleTextColor(Color.WHITE);
        orderstoolbar.setTitle("Orders");

        mContext = getActivity();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        user_id = currentUser.getUid();

        rvVAP = view.findViewById(R.id.rvVAP);
        viewAllProductsAdapter = new ViewAllProductsAdapter(mContext, businessOrdersList, "users");
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
