package com.debarunlahiri.dinmart.fragment;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.debarunlahiri.dinmart.next.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OfferFragment extends Fragment {

    private Toolbar offertoolbar;

    public OfferFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        offertoolbar = view.findViewById(R.id.offertoolbar);
        offertoolbar.setTitle("Offers");
        offertoolbar.setTitleTextColor(Color.WHITE);
    }
}
