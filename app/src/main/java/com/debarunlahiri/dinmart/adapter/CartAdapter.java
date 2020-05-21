package com.debarunlahiri.dinmart.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.debarunlahiri.dinmart.model.Cart;
import com.debarunlahiri.dinmart.next.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<Cart> cartList;
    private Context mContext;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private int itemCount = 1;

    public CartAdapter(List<Cart> cartList, Context mContext) {
        this.cartList = cartList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        viewHolder.cartDelbtn.setVisibility(View.VISIBLE);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();



        final Cart cart = cartList.get(i);

        mDatabase.child("products").child(cart.getProduct_key()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                viewHolder.product_name.setText(dataSnapshot.child("product_name").getValue().toString());
                Picasso.get().load(dataSnapshot.child("product_image").getValue().toString()).into(viewHolder.imageView10, new Callback() {
                    @Override
                    public void onSuccess() {
                        viewHolder.progressBar3.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("cart").child(currentUser.getUid()).child(cart.getProduct_key()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String product_price = dataSnapshot.child("product_price").getValue().toString();
                String product_quantity = dataSnapshot.child("product_quantity").getValue().toString();
                String product_weight_unit = dataSnapshot.child("product_weight_unit").getValue().toString();
                String total_product_price = dataSnapshot.child("total_product_price").getValue().toString();
                itemCount = Integer.parseInt(dataSnapshot.child("product_item_count").getValue().toString());

                viewHolder.tvProductCount.setText(String.valueOf(itemCount));

                if (total_product_price.equals("0")) {
                    viewHolder.product_price.setText("₹" + product_price + " " + product_quantity + "/" + product_weight_unit);
                } else {
                    viewHolder.product_price.setText("₹" + total_product_price + " " + product_quantity + "/" + product_weight_unit);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewHolder.seller_name.setVisibility(View.GONE);

        viewHolder.cartDelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("cart").child(currentUser.getUid()).child(cart.getProduct_key()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Product deleted successfully", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        viewHolder.ibProductMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCount != 0) {
                    itemCount = Integer.parseInt(viewHolder.tvProductCount.getText().toString());
                    viewHolder.tvProductCount.setText(String.valueOf(--itemCount));
                    int total_price = Integer.parseInt(cart.getProduct_price())*itemCount;
                    mDatabase.child("cart").child(currentUser.getUid()).child(cart.getProduct_key()).child("total_product_price").setValue(String.valueOf(total_price));
                    mDatabase.child("cart").child(currentUser.getUid()).child(cart.getProduct_key()).child("product_item_count").setValue(String.valueOf(itemCount));
                }

            }
        });

        viewHolder.ibProductPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemCount = Integer.parseInt(viewHolder.tvProductCount.getText().toString());
                viewHolder.tvProductCount.setText(String.valueOf(++itemCount));
                int total_price = Integer.parseInt(cart.getProduct_price())*itemCount;
                mDatabase.child("cart").child(currentUser.getUid()).child(cart.getProduct_key()).child("total_product_price").setValue(String.valueOf(total_price));
                mDatabase.child("cart").child(currentUser.getUid()).child(cart.getProduct_key()).child("product_item_count").setValue(String.valueOf(itemCount));
            }
        });

        viewHolder.cartDelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("cart").child(currentUser.getUid()).child(cart.getProduct_key()).child("visibility").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Item successfully deleted from the cart", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        viewHolder.cvRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("cart").child(currentUser.getUid()).child(cart.getProduct_key()).child("visibility").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Item successfully deleted from the cart", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView10;
        private TextView product_name, product_price, product_description, seller_name, tvProductCount;
        private CardView productcv, cvRemoveItem;
        private TextView cartDelbtn;
        private ProgressBar progressBar3;
        private ImageButton ibProductMinus, ibProductPlus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView10 = itemView.findViewById(R.id.imageView10);
            product_name = itemView.findViewById(R.id.product_name);
            product_price = itemView.findViewById(R.id.product_price);
            product_description = itemView.findViewById(R.id.product_description);
            seller_name = itemView.findViewById(R.id.seller_name);
            productcv = itemView.findViewById(R.id.productcv);
            cartDelbtn = itemView.findViewById(R.id.cartDelbtn);
            progressBar3 = itemView.findViewById(R.id.progressBar3);
            ibProductMinus = itemView.findViewById(R.id.ibProductMinus);
            ibProductPlus = itemView.findViewById(R.id.ibProductPlus);
            tvProductCount = itemView.findViewById(R.id.tvProductCount);
            cvRemoveItem = itemView.findViewById(R.id.cvRemoveItem);

        }
    }
}
