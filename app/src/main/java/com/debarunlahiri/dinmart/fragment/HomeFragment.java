package com.debarunlahiri.dinmart.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.debarunlahiri.dinmart.activity.ProductMainSubListActivity;
import com.debarunlahiri.dinmart.next.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private Toolbar hometoolbar;

    private CardView cvHomeFruit, cvHomeVegetables, cvHomeGroceries, cvHomeSweets, cvHomeSnacks, cvOrderOnCall;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hometoolbar = view.findViewById(R.id.hometoolbar);
        hometoolbar.setTitle("Home");
        hometoolbar.setTitleTextColor(Color.WHITE);

        cvHomeFruit = view.findViewById(R.id.cvHomeFruit);
        cvHomeVegetables = view.findViewById(R.id.cvHomeVegetables);
        cvHomeGroceries = view.findViewById(R.id.cvHomeGroceries);
        cvHomeSweets = view.findViewById(R.id.cvHomeSweets);
        cvHomeSnacks = view.findViewById(R.id.cvHomeSnacks);
        cvOrderOnCall = view.findViewById(R.id.cvOrderOnCall);

        cvHomeFruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(getActivity(), ProductMainSubListActivity.class);
                productIntent.putExtra("category", "Fruits");
                startActivity(productIntent);
            }
        });

        cvHomeVegetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(getActivity(), ProductMainSubListActivity.class);
                productIntent.putExtra("category", "Vegetables");
                startActivity(productIntent);
            }
        });

        cvHomeGroceries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(getActivity(), ProductMainSubListActivity.class);
                productIntent.putExtra("category", "Groceries");
                startActivity(productIntent);
            }
        });

        cvHomeSweets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(getActivity(), ProductMainSubListActivity.class);
                productIntent.putExtra("category", "Sweets");
                startActivity(productIntent);
            }
        });

        cvHomeSnacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(getActivity(), ProductMainSubListActivity.class);
                productIntent.putExtra("category", "Snacks");
                startActivity(productIntent);
            }
        });

        cvOrderOnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(getActivity())
                        .withPermission(Manifest.permission.CALL_PHONE)
                        .withListener(new PermissionListener() {
                            @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + getString(R.string.order_on_call_phone_number)));
                                getActivity().startActivity(intent);
                            }
                            @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                                PermissionListener dialogPermissionListener =
                                        DialogOnDeniedPermissionListener.Builder
                                                .withContext(getActivity())
                                                .withTitle("Phone permission")
                                                .withMessage("Phone permission is needed in order to make call")
                                                .withButtonText(android.R.string.ok)
                                                .withIcon(R.drawable.din_logo)
                                                .build();

                            }
                            @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();

            }
        });
    }
}
