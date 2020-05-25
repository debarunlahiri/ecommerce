package com.debarunlahiri.dinmart.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.debarunlahiri.dinmart.MainActivity;
import com.debarunlahiri.dinmart.activity.LoginActivity;
import com.debarunlahiri.dinmart.activity.ProductMainSubListActivity;
import com.debarunlahiri.dinmart.adapter.ProductsAdapter;
import com.debarunlahiri.dinmart.model.Products;
import com.debarunlahiri.dinmart.next.R;
import com.debarunlahiri.dinmart.utils.Variables;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
public class ProductListFragment extends Fragment {

    private Toolbar producttoolbar;

    private TextView textView42;

    private RecyclerView recyclerview;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private ProductsAdapter productsAdapter;
    private List<Products> productsLists = new ArrayList<>();
    private Context mContext;

    private String user_id, category;

    public ProductListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getActivity().getIntent().getExtras();
        category = bundle.get("category").toString();

        setUpBottomNavMenu(view);

        producttoolbar = view.findViewById(R.id.producttoolbar);
        producttoolbar.setTitleTextColor(Color.WHITE);
        producttoolbar.setTitle(category);
        ((AppCompatActivity)getActivity()).setSupportActionBar(producttoolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        producttoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        producttoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildFragmentManager().popBackStack();
            }
        });



        textView42 = view.findViewById(R.id.textView42);

        mContext = getActivity();

        recyclerview = view.findViewById(R.id.recyclerview);
        productsAdapter = new ProductsAdapter(productsLists, mContext);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(productsAdapter);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();



        if (currentUser == null) {

        } else {
            user_id = mAuth.getCurrentUser().getUid();
        }

        mDatabase.child(Variables.product_category).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Products products = dataSnapshot.getValue(Products.class);
                if (products.getProduct_category().equals(category)) {
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
    }

    private void setUpBottomNavMenu(View view) {
        BottomNavigationView bottomNavigationView = bottomNavigationView = view.findViewById(R.id.bottomNavigationView);

        HomeFragment homeFragment;
        CartFragment cartFragment;
        OfferFragment offerFragment;
        OrdersFragment ordersFragment;
        SettingFragment settingFragment;

        homeFragment = new HomeFragment();
        cartFragment = new CartFragment();
        offerFragment = new OfferFragment();
        ordersFragment = new OrdersFragment();
        settingFragment = new SettingFragment();

        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home_menu_list_item :
                        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                        return true;

                    case R.id.home_offer_menu_list_item :
                        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, new OfferFragment()).commit();
                        return true;

                    case R.id.home_cart_menu_list_item :
                        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, new CartFragment()).commit();
                        return true;

                    case R.id.home_settings_menu_list_item :
                        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingFragment()).commit();
                        return true;

                    case R.id.home_orders_menu_list_item :
                        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrdersFragment()).commit();
                        return true;

                }
                return false;
            }
        });
    }
}
