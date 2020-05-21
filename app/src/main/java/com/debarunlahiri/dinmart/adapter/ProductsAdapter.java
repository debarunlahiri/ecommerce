package com.debarunlahiri.dinmart.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.debarunlahiri.dinmart.activity.AddDeliveryDetails;
import com.debarunlahiri.dinmart.activity.ProductActivity;
import com.debarunlahiri.dinmart.model.Products;
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

import java.util.HashMap;
import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private List<Products> productsList;
    private Context mContext;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private String user_id;
    private int itemCount = 1;

    public ProductsAdapter(List<Products> productsList, Context mContext) {
        this.productsList = productsList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.products_list_item2, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Products products = productsList.get(i);
        //viewHolder.progressBar3.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        viewHolder.cvProductCounter.setVisibility(View.GONE);
        viewHolder.bProductAddToCart.setVisibility(View.VISIBLE);

        if (currentUser != null) {
            user_id = currentUser.getUid();

            mDatabase.child("cart").child(user_id).child(products.getProduct_key()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        boolean isVisible = (boolean) dataSnapshot.child("visibility").getValue();
                        itemCount = Integer.parseInt(dataSnapshot.child("product_item_count").getValue().toString());
                        if (isVisible) {
                            viewHolder.bProductAddToCart.setVisibility(View.GONE);
                            viewHolder.cvProductCounter.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.bProductAddToCart.setVisibility(View.VISIBLE);
                            viewHolder.cvProductCounter.setVisibility(View.GONE);
                        }
                    } else {
                        viewHolder.bProductAddToCart.setVisibility(View.VISIBLE);
                        viewHolder.cvProductCounter.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        Picasso.get().load(products.getProduct_image()).into(viewHolder.productIV, new Callback() {
            @Override
            public void onSuccess() {
                //viewHolder.progressBar3.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Exception e) {

            }
        });

        viewHolder.tvProductName.setText(products.getProduct_name());
        viewHolder.tvProductPrice.setText("₹" + products.getProduct_price() + " | " + products.getProduct_quantity() + "/" + products.getProduct_weight_unit().toLowerCase());
        viewHolder.tvProductDesc.setText(products.getProduct_description());
        viewHolder.tvSellerName.setText("by " + products.getCompany_name());

        viewHolder.productcv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(mContext, ProductActivity.class);
                productIntent.putExtra("product_key", products.getProduct_key());
                productIntent.putExtra("product_category", products.getProduct_category());
                productIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(productIntent);
            }
        });

        viewHolder.button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(mContext, AddDeliveryDetails.class);
                //productIntent.putExtra("product_key", );
                productIntent.putExtra("product_name", products.getProduct_name());
                productIntent.putExtra("product_price", products.getProduct_price());
                productIntent.putExtra("product_image", products.getProduct_image());
                productIntent.putExtra("product_description", products.getProduct_description());
                productIntent.putExtra("seller_name", products.getCompany_name());
                productIntent.putExtra("from_cart", "no");
                mContext.startActivity(productIntent);
            }
        });

        viewHolder.button15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> dataMap = new HashMap<>();
                dataMap.put("product_name", products.getProduct_name());
                dataMap.put("product_price", products.getProduct_price());
                dataMap.put("product_image", products.getProduct_image());
                dataMap.put("seller_name", products.getCompany_name());
                dataMap.put("product_description", products.getProduct_description());
                mDatabase.child("cart").child(user_id).child(products.getProduct_key()).updateChildren(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(50);
                        Toast.makeText(mContext, "Product added successfully to cart", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        viewHolder.ibProductMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCount >= 1) {
                    itemCount = Integer.parseInt(viewHolder.tvProductCount.getText().toString());
                    viewHolder.tvProductCount.setText(String.valueOf(--itemCount));
                    int total_price = Integer.parseInt(products.getProduct_price())*itemCount;
                    mDatabase.child("cart").child(currentUser.getUid()).child(products.getProduct_key()).child("total_product_price").setValue(String.valueOf(total_price));
                    mDatabase.child("cart").child(currentUser.getUid()).child(products.getProduct_key()).child("product_item_count").setValue(String.valueOf(itemCount));
                }

            }
        });

        viewHolder.ibProductPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemCount = Integer.parseInt(viewHolder.tvProductCount.getText().toString());
                viewHolder.tvProductCount.setText(String.valueOf(++itemCount));
                int total_price = Integer.parseInt(products.getProduct_price())*itemCount;
                mDatabase.child("cart").child(currentUser.getUid()).child(products.getProduct_key()).child("total_product_price").setValue(String.valueOf(total_price));
                mDatabase.child("cart").child(currentUser.getUid()).child(products.getProduct_key()).child("product_item_count").setValue(String.valueOf(itemCount));
            }
        });

        viewHolder.bProductAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {

                } else {
                    saveProductToCart(viewHolder, products);
                }

            }
        });
    }

    private void saveProductToCart(final ViewHolder viewHolder, Products products) {
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("product_key", products.getProduct_key());
        dataMap.put("product_item_count", String.valueOf(itemCount));
        dataMap.put("product_price", products.getProduct_price());
        dataMap.put("product_quantity", products.getProduct_quantity());
        dataMap.put("product_weight_unit", products.getProduct_weight_unit());
        dataMap.put("user_id", user_id);
        dataMap.put("visibility", true);
        dataMap.put("total_product_price", "0");
        mDatabase.child("cart").child(user_id).child(products.getProduct_key()).updateChildren(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(mContext, "Product added successfully to cart", Toast.LENGTH_LONG).show();
                viewHolder.bProductAddToCart.setVisibility(View.GONE);
                viewHolder.cvProductCounter.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productIV;
        private TextView tvProductName, tvProductPrice, tvProductDesc, tvSellerName, tvProductCount;
        private CardView productcv, cvProductCounter;
        private ProgressBar progressBar3;
        private Button button14, button15, bProductAddToCart;
        private ImageButton ibProductMinus, ibProductPlus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productIV = itemView.findViewById(R.id.productIV);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductDesc = itemView.findViewById(R.id.tvProductDesc);
            tvSellerName = itemView.findViewById(R.id.tvSellerName);
            productcv = itemView.findViewById(R.id.productcv);
            button14 = itemView.findViewById(R.id.button14);
            button15 = itemView.findViewById(R.id.button15);
            bProductAddToCart = itemView.findViewById(R.id.bProductAddToCart);
            cvProductCounter = itemView.findViewById(R.id.cvProductCounter);
            ibProductMinus = itemView.findViewById(R.id.ibProductMinus);
            ibProductPlus = itemView.findViewById(R.id.ibProductPlus);
            tvProductCount = itemView.findViewById(R.id.tvProductCount);
        }
    }
}
