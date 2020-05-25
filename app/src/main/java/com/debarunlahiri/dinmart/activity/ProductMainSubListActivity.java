package com.debarunlahiri.dinmart.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.debarunlahiri.dinmart.MainActivity;
import com.debarunlahiri.dinmart.adapter.ProductsAdapter;
import com.debarunlahiri.dinmart.fragment.CartFragment;
import com.debarunlahiri.dinmart.fragment.HomeFragment;
import com.debarunlahiri.dinmart.fragment.OfferFragment;
import com.debarunlahiri.dinmart.fragment.OrdersFragment;
import com.debarunlahiri.dinmart.fragment.SettingFragment;
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

public class ProductMainSubListActivity extends AppCompatActivity {

    private Toolbar producttoolbar;

    private TextView textView42;
    private FrameLayout flProductSearch;
    private ImageButton ibCloseSearch;
    private EditText etProductSearch;

    private RecyclerView recyclerview;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private ProductsAdapter productsAdapter;
    private List<Products> productsLists = new ArrayList<>();
    private Context mContext;

    private String user_id, category;

    private Menu myMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_main_sub_list);

        mContext = ProductMainSubListActivity.this;

        Bundle bundle = getIntent().getExtras();
        category = bundle.get("category").toString();

//        setUpBottomNavMenu();

        producttoolbar = findViewById(R.id.producttoolbar);
        producttoolbar.setTitleTextColor(Color.WHITE);
        producttoolbar.setTitle(category);
        setSupportActionBar(producttoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        producttoolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        producttoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        textView42 = findViewById(R.id.textView42);
        flProductSearch = findViewById(R.id.flProductSearch);
        ibCloseSearch = findViewById(R.id.ibCloseSearch);
        etProductSearch = findViewById(R.id.etProductSearch);

        flProductSearch.setVisibility(View.GONE);

        recyclerview = findViewById(R.id.recyclerview);
        productsAdapter = new ProductsAdapter(productsLists, mContext);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(productsAdapter);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();



        if (currentUser == null) {

        } else {
            user_id = mAuth.getCurrentUser().getUid();
            Variables.global_user_id = user_id;
        }

        etProductSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchProducts(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDatabase.child("products").addChildEventListener(new ChildEventListener() {
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

        ibCloseSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = etProductSearch.getText().toString();

                if (search.isEmpty()) {
                    if (myMenu != null) {
                        myMenu.findItem(R.id.product_search_menu_item).setVisible(true);
                        flProductSearch.setVisibility(View.GONE);
                    }
                } else {
                    etProductSearch.setText("");
                }
            }
        });

    }

    private void searchProducts(CharSequence searchItem) {
        productsLists.clear();
        mDatabase.child("products").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Products products = dataSnapshot.getValue(Products.class);
                if (products.getProduct_category().equals(category)) {
                    if (products.getProduct_name().toLowerCase().contains(searchItem.toString().toLowerCase())) {
                        productsLists.add(products);
                        productsAdapter.notifyDataSetChanged();
                    }
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

    private void setUpBottomNavMenu() {
        BottomNavigationView bottomNavigationView = bottomNavigationView = findViewById(R.id.bottomNavigationView);

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

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home_menu_list_item :
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                        return true;

                    case R.id.home_offer_menu_list_item :
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OfferFragment()).commit();
                        return true;

                    case R.id.home_cart_menu_list_item :
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CartFragment()).commit();
                        return true;

                    case R.id.home_settings_menu_list_item :
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingFragment()).commit();
                        return true;

                    case R.id.home_orders_menu_list_item :
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrdersFragment()).commit();
                        return true;

                }
                return false;
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_menu, menu);
        myMenu = menu;
        if (flProductSearch.getVisibility() == View.VISIBLE) {
            menu.findItem(R.id.product_search_menu_item).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.product_search_menu_item:
                if (flProductSearch.getVisibility() == View.GONE) {
                    flProductSearch.setVisibility(View.VISIBLE);
                    myMenu.findItem(R.id.product_search_menu_item).setVisible(false);
                }
                break;


        }
        return super.onOptionsItemSelected(item);
    }
}
