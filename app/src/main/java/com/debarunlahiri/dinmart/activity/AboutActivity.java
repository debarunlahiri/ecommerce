package com.debarunlahiri.dinmart.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import com.debarunlahiri.dinmart.next.BuildConfig;
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

    private TextView tvUserId, tvPrivacyPolicy, tvAboutAppVersion;

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
        tvAboutAppVersion = findViewById(R.id.tvAboutAppVersion);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (currentUser == null) {
            tvUserId.setVisibility(View.GONE);
        } else {
            tvUserId.setText(currentUser.getUid());

        }

        tvAboutAppVersion.setText("v" + BuildConfig.VERSION_NAME);
        tvPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this, PrivacyPolicyActivity.class));
            }
        });
    }
}
