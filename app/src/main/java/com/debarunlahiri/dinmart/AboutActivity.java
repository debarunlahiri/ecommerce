package com.debarunlahiri.dinmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.debarunlahiri.dinmart.next.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AboutActivity extends AppCompatActivity {

    private Toolbar abouttoolbar;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private TextView tvUserId, tvPrivacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        abouttoolbar = findViewById(R.id.abouttoolbar);
        abouttoolbar.setTitle("About");
        setSupportActionBar(abouttoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        abouttoolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back));
        abouttoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvUserId = findViewById(R.id.tvUserId);
        tvPrivacyPolicy = findViewById(R.id.tvPrivacyPolicy);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        tvUserId.setText(currentUser.getUid());

        tvPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this, PrivacyPolicyActivity.class));
            }
        });

        //GaussianBlur.with(getApplicationContext()).size(100).radius(5).put(R.drawable.aboutbg, imageView12);
    }
}
