package com.debarunlahiri.dinmart;

import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.debarunlahiri.dinmart.activity.AddUserDetailsActivity;
import com.debarunlahiri.dinmart.activity.EditUserInfoActivity;
import com.debarunlahiri.dinmart.activity.HomeActivity;
import com.debarunlahiri.dinmart.activity.LoginActivity;
import com.debarunlahiri.dinmart.next.R;
import com.debarunlahiri.dinmart.utils.Variables;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Toolbar hometoolbar;

    private ImageView settingsbtn, orderIV, imageView7;
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
        hometoolbar.setTitle("DIN Mart");
        hometoolbar.setTitleTextColor(Color.BLACK);

        //BlurKit.getInstance().blur(R.drawable.mainbg, 2);


        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        settingsbtn = findViewById(R.id.settingsbtn);
        tvName = findViewById(R.id.tvName);
        orderIV = findViewById(R.id.orderIV);
        imageView7 = findViewById(R.id.imageView7);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sendToHome();

        if (currentUser != null) {
            user_id = currentUser.getUid();
            Variables.global_user_id = user_id;
        }

        //GaussianBlur.with(getApplicationContext()).size(100).radius(5).put(R.drawable.mainbg, imageView16);

//        if (currentUser == null) {
//            sendToStart();
//        } else {
//            sendToHome();
//        }
    }

    private void sendToHome() {
        Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
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
                    tvName.setText(name);
                }
                if (dataSnapshot.child("email").exists()) {
                    email = dataSnapshot.child("email").getValue().toString();
                }
                if (dataSnapshot.child("phone").exists()) {
                    phone = dataSnapshot.child("phone").getValue().toString();
                }







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

    private void sendToStart() {
        Intent registerIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(registerIntent);
        finish();
    }
}
