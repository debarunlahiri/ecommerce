package com.debarunlahiri.dinmart.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.debarunlahiri.dinmart.model.Cart;
import com.debarunlahiri.dinmart.adapter.CartAdapter;
import com.debarunlahiri.dinmart.activity.FinalPriceActivity;
import com.debarunlahiri.dinmart.next.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    private Toolbar carttoolbar;

    private TextView tvPrice;
    private Button button7;
    private CardView cvCartProceed;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private Context mContext;
    private RecyclerView rvCart;
    private CartAdapter cartAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<Cart> cartList = new ArrayList<>();
    private List<String> mKeys = new ArrayList<>();

    private String user_id;
    private int total_price = 0;
    private String p_price;
    public int total_product_count = 0;
    public int count = 0;


    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();

        tvPrice = view.findViewById(R.id.tvPrice);
        button7 = view.findViewById(R.id.button7);
        cvCartProceed = view.findViewById(R.id.cvCartProceed);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        rvCart = view.findViewById(R.id.rvCart);
        cartAdapter = new CartAdapter(cartList, mContext);
        linearLayoutManager = new LinearLayoutManager(mContext);
        rvCart.setAdapter(cartAdapter);
        rvCart.setLayoutManager(linearLayoutManager);

        cvCartProceed.setVisibility(View.GONE);

        if (currentUser != null) {
            user_id = currentUser.getUid();

            mDatabase.child("cart").child(user_id).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()) {
                        String key = dataSnapshot.getKey();
                        mKeys.add(key);
                        Cart cart = dataSnapshot.getValue(Cart.class);
                        if (cart.isVisibility()) {
                            cartList.add(cart);
                        }
                        cartAdapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                    if (dataSnapshot.exists()) {
//                        Cart cart = dataSnapshot.getValue(Cart.class);
//                        String key = dataSnapshot.getKey();
//                        int index = mKeys.indexOf(key);
//                        if (cart.getProduct_key().equals(key)) {
//                            if (!cart.isVisibility()) {
//                                cartList.remove(index);
//                                cartAdapter.notifyDataSetChanged();
//                            }
//                        }
//
//                        if (cartList.isEmpty()) {
//                            cvCartProceed.setVisibility(View.GONE);
//                        }
//                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getKey();
                    for (Cart cart : cartList) {
                        if (cart.getProduct_key().equals(key)) {
                            cartList.remove(cart);
                            cartAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            getTotalPrice();
            getTotalProductCount();


            button7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent buyIntent = new Intent(getActivity(), FinalPriceActivity.class);
                    buyIntent.putExtra("total_price", String.valueOf(total_price));
                    buyIntent.putExtra("total_product_count", String.valueOf(total_product_count));
                    buyIntent.putExtra("from_cart", "yes");
                    buyIntent.putParcelableArrayListExtra("cart_list", (ArrayList<? extends Parcelable>) cartList);
                    startActivity(buyIntent);
                }
            });

            mDatabase.child("cart").child(user_id).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()) {
                        Cart cart = dataSnapshot.getValue(Cart.class);
                        if (cart.isVisibility()) {
                            cvCartProceed.setVisibility(View.VISIBLE);
                        } else {
                            cvCartProceed.setVisibility(View.GONE);
                        }
                    } else {
                        cvCartProceed.setVisibility(View.GONE);
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
    }

    private void getTotalPrice() {
        mDatabase.child("cart").child(user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int cost = 0;
                Cart cart = dataSnapshot.getValue(Cart.class);
                if (cart.getTotal_product_price().equals("0")) {
                    cost = Integer.parseInt(cart.getProduct_price());
                } else {
                    cost = Integer.parseInt(cart.getTotal_product_price());
                }
                total_price = total_price + cost;
                tvPrice.setText(String.valueOf(total_price));
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

    private void getTotalProductCount() {
        mDatabase.child("cart").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                total_product_count = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
