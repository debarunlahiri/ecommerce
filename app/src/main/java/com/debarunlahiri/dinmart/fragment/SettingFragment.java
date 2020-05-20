package com.debarunlahiri.dinmart.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.debarunlahiri.dinmart.AboutActivity;
import com.debarunlahiri.dinmart.AddProductActivity;
import com.debarunlahiri.dinmart.EditUserInfoActivity;
import com.debarunlahiri.dinmart.next.R;
import com.debarunlahiri.dinmart.StartActivity;
import com.debarunlahiri.dinmart.business.BusinessViewAllOrdersActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    private Toolbar settingstoolbar;

    private TextView tvSettingsLogout, tvSettingsCreateB, tvSettingsBAP, tvSettingsECI, textView15, textView11, textView55, tvSettingsVAP;
    private CardView cvSettingsBusiness;
    private TextView tvViewOrders;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    private String b_email = null;
    private String user_id;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        settingstoolbar = view.findViewById(R.id.settingstoolbar);
        settingstoolbar.setTitle("Settings");
        settingstoolbar.setTitleTextColor(Color.WHITE);

        tvSettingsLogout = view.findViewById(R.id.tvSettingsLogout);
        tvSettingsCreateB = view.findViewById(R.id.tvSettingsCreateB);
        tvSettingsBAP = view.findViewById(R.id.tvSettingsBAP);
        tvSettingsECI = view.findViewById(R.id.tvSettingsECI);
        textView15 = view.findViewById(R.id.textView15);
        textView11 = view.findViewById(R.id.textView11);
        textView55 = view.findViewById(R.id.textView55);
        tvSettingsVAP = view.findViewById(R.id.tvSettingsVAP);
        cvSettingsBusiness = view.findViewById(R.id.cvSettingsBusiness);
        tvViewOrders = view.findViewById(R.id.tvViewOrders);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            sendToLogin();
        } else {
            user_id = currentUser.getUid();
            mDatabase.child("admin").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.child("user_id").equals(user_id)) {
                            cvSettingsBusiness.setVisibility(View.VISIBLE);
                        } else {
                            cvSettingsBusiness.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            tvSettingsBAP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addProductIntent = new Intent(getActivity(), AddProductActivity.class);
                    startActivity(addProductIntent);
                }
            });
            textView15.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent aboutIntent = new Intent(getActivity(), AboutActivity.class);
                    startActivity(aboutIntent);
                }
            });

            textView11.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addProductIntent = new Intent(getActivity(), EditUserInfoActivity.class);
                    startActivity(addProductIntent);
                }
            });

            tvViewOrders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addProductIntent = new Intent(getActivity(), BusinessViewAllOrdersActivity.class);
                    startActivity(addProductIntent);
                }
            });

            tvSettingsLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut();
                    Intent logoutIntent = new Intent(getActivity(), StartActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logoutIntent);
                    getActivity().finish();
                }
            });
        }
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(getActivity(), StartActivity.class);
        startActivity(loginIntent);
        getActivity().finish();
    }
}
