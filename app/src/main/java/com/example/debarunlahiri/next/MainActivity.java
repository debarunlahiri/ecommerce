package com.example.debarunlahiri.next;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vansuita.gaussianblur.GaussianBlur;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Toolbar hometoolbar;

    private ImageView settingsbtn, orderIV, imageView7, imageView16;
    private TextView textView, textView2, textView3, tvName;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private String user_id;
    private String ds_company_name;

    private String name;
    private String email;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        hometoolbar = findViewById(R.id.hometoolbar);
        hometoolbar.setTitle("Home");
        hometoolbar.setTitleTextColor(Color.WHITE);

        //BlurKit.getInstance().blur(R.drawable.mainbg, 2);


        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        settingsbtn = findViewById(R.id.settingsbtn);
        tvName = findViewById(R.id.tvName);
        orderIV = findViewById(R.id.orderIV);
        imageView7 = findViewById(R.id.imageView7);
        imageView16 = findViewById(R.id.imageView16);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //GaussianBlur.with(getApplicationContext()).size(100).radius(5).put(R.drawable.mainbg, imageView16);

        if (currentUser == null) {
            sendToStart();
        } else {
            if (!mAuth.getCurrentUser().isEmailVerified()) {
                Toast.makeText(getApplicationContext(), "Please verify your email address", Toast.LENGTH_LONG).show();
                Intent startActivity = new Intent(MainActivity.this, EmailVerifyActivity.class);
                startActivity(startActivity);
                finish();
            } else {
                user_id = currentUser.getUid();
                checkUserDetails();
            }
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productMainListIntent = new Intent(MainActivity.this, ProductMainSubListActivity.class);
                productMainListIntent.putExtra("electronics", "electronics");
                startActivity(productMainListIntent);
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productMainListIntent = new Intent(MainActivity.this, ProductMainSubListActivity.class);
                productMainListIntent.putExtra("games", "games");
                startActivity(productMainListIntent);
            }
        });
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productMainListIntent = new Intent(MainActivity.this, ProductMainSubListActivity.class);
                productMainListIntent.putExtra("clothes", "clothes");
                startActivity(productMainListIntent);
            }
        });
        settingsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
        orderIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderIntent = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(orderIntent);
            }
        });
        imageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderIntent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(orderIntent);
            }
        });





    }

    private void checkVerification() {
        mDatabase.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean email = (boolean) dataSnapshot.child("email").getValue();
                boolean phone = (boolean) dataSnapshot.child("phone").getValue();

                if (email == false) {
                    Intent emailIntent = new Intent(MainActivity.this, EmailVerifyActivity.class);
                    startActivity(emailIntent);
                    finish();
                } else if (phone == false) {
                    Intent phoneIntent = new Intent(MainActivity.this, PhoneVerifyActivity.class);
                    startActivity(phoneIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserDetails() {
        mDatabase.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "Add your details first", Toast.LENGTH_LONG).show();
                    sendToAddDetails();
                } else {
                    if (!dataSnapshot.child("email").exists()) {
                        Toast.makeText(getApplicationContext(), "Please login again", Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        Intent detailsActivity = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(detailsActivity);
                        finish();
                    } else if (!dataSnapshot.child("name").exists()) {
                        Toast.makeText(getApplicationContext(), "Add your name first", Toast.LENGTH_LONG).show();
                        sendToEdit();
                    } else if (!dataSnapshot.child("phone").exists()) {
                        Toast.makeText(getApplicationContext(), "Add your phone first", Toast.LENGTH_LONG).show();
                        sendToEdit();
                    } else if (!dataSnapshot.child("address").exists()) {
                        Toast.makeText(getApplicationContext(), "Add your address first", Toast.LENGTH_LONG).show();

                    } else if(!dataSnapshot.child("verification").exists()) {
                        Map<String, Object> dataMap = new HashMap<>();
                        dataMap.put("email", false);
                        dataMap.put("phone", false);
                        mDatabase.child("users").child(user_id).child("verification").updateChildren(dataMap);
                    } else {
                        getUserDetails();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getUserDetails() {
        mDatabase.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists()) {
                    name = dataSnapshot.child("name").getValue().toString();
                }
                if (dataSnapshot.child("email").exists()) {
                    email = dataSnapshot.child("email").getValue().toString();
                }
                if (dataSnapshot.child("phone").exists()) {
                    phone = dataSnapshot.child("phone").getValue().toString();
                }





                tvName.setText(name);

                businessActivity();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void businessActivity() {
        mDatabase.child("business").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        final String ds_email = ds.child("email").getValue().toString();
                        if (ds_email.equals(email)) {
                            if (ds.child("company_name").exists()) {
                                ds_company_name = ds.child("company_name").getValue().toString();
                            }

                            final String key = ds.getKey();
                            settingsbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                                    settingsIntent.putExtra("ds_email", ds_email);
                                    settingsIntent.putExtra("ds_company_name", ds_company_name);
                                    settingsIntent.putExtra("key", key);
                                    startActivity(settingsIntent);
                                }
                            });

                        }


                    }
                } else {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendToAddDetails() {
        Intent detailsActivity = new Intent(MainActivity.this, AddUserDetailsActivity.class);
        startActivity(detailsActivity);
        finish();
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void sendToEdit() {
        Intent editIntent = new Intent(MainActivity.this, EditUserInfoActivity.class);
        startActivity(editIntent);
        finish();
    }

    private void sendToRegister() {
        Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
        finish();
    }

    private void sendToStart() {
        Intent registerIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(registerIntent);
        finish();
    }
}
