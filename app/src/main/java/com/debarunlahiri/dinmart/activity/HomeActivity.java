package com.debarunlahiri.dinmart.activity;

import androidx.annotation.NonNull;

import com.debarunlahiri.dinmart.next.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.debarunlahiri.dinmart.fragment.CartFragment;
import com.debarunlahiri.dinmart.fragment.HomeFragment;
import com.debarunlahiri.dinmart.fragment.OfferFragment;
import com.debarunlahiri.dinmart.fragment.OrdersFragment;
import com.debarunlahiri.dinmart.fragment.SettingFragment;

public class HomeActivity extends AppCompatActivity {

    private FrameLayout flHome;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        flHome = findViewById(R.id.fragment_container);

        setUpBottomNavMenu();


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
}
