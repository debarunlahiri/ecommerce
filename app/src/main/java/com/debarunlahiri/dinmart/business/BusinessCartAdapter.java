package com.debarunlahiri.dinmart.business;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.debarunlahiri.dinmart.Cart;
import com.debarunlahiri.dinmart.next.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BusinessCartAdapter extends RecyclerView.Adapter<BusinessCartAdapter.ViewHolder> {

    private Context mContext;
    private List<Cart> cartList;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private String user_id, order_id;

    public BusinessCartAdapter(Context mContext, List<Cart> cartList) {
        this.mContext = mContext;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_business_cart_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Cart cart = cartList.get(position);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mDatabase.child("products").child(cart.getProduct_key()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String product_name = dataSnapshot.child("product_name").getValue().toString();
                String product_image = dataSnapshot.child("product_image").getValue().toString();
                String product_price = dataSnapshot.child("product_price").getValue().toString();
                holder.tvBusinessCartProductName.setText("Product Name: " + product_name);
                holder.tvBusinessProductBasePrice.setText("Product Base Price: " + "₹" + product_price);
                Picasso.get().load(product_image).into(holder.ivBusinessCartImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (cart.getTotal_product_price().equals("0")) {
            holder.tvBusinessCartProductPrice.setText("Total Product Price: " + "₹" + cart.getProduct_price());
        } else {
            holder.tvBusinessCartProductPrice.setText("Total Product Price: " + "₹" + cart.getTotal_product_price());
        }

        holder.tvBusinessProductCount.setText("Product Count: " + cart.getProduct_item_count());
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvBusinessProductCount, tvBusinessCartProductPrice, tvBusinessCartProductName, tvBusinessProductBasePrice;
        private ImageView ivBusinessCartImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBusinessProductCount = itemView.findViewById(R.id.tvBusinessProductCount);
            tvBusinessCartProductPrice = itemView.findViewById(R.id.tvBusinessCartProductPrice);
            tvBusinessCartProductName = itemView.findViewById(R.id.tvBusinessCartProductName);
            ivBusinessCartImage = itemView.findViewById(R.id.ivBusinessCartImage);
            tvBusinessProductBasePrice = itemView.findViewById(R.id.tvBusinessProductBasePrice);
        }
    }
}
