package com.debarunlahiri.dinmart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.debarunlahiri.dinmart.next.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VAPAdapter extends RecyclerView.Adapter<VAPAdapter.ViewHoler> {

    private List<Products> productsList;
    private Context mContext;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    public VAPAdapter(List<Products> productsList, Context mContext) {
        this.productsList = productsList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_list_item, viewGroup, false);
        return new ViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHoler viewHoler, int i) {
        final Products products = productsList.get(i);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        viewHoler.product_name.setText(products.getProduct_name());
        viewHoler.product_price.setText("₹" + products.getProduct_price());
        viewHoler.product_description.setText(products.getProduct_description());
        viewHoler.seller_name.setText("by " + products.getCompany_name());
        Picasso.get().load(products.getProduct_image()).into(viewHoler.imageView10, new Callback() {
            @Override
            public void onSuccess() {
                viewHoler.progressBar3.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Exception e) {

            }
        });

        viewHoler.cartDelbtn.setVisibility(View.VISIBLE);
        viewHoler.cartDelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Delete product");
                    builder.setMessage("Deleting this product will not affect in consumer's orders and cart. Are you sure delete it?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDatabase.child("business").child(products.getCompany_key()).child("products").child(products.getProduct_key()).removeValue();
                            mDatabase.child("products").child(products.getProduct_category()).child(products.getProduct_key()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(mContext, "Product deleted successfully", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(mContext, "Requires android version 5.0 and up", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class ViewHoler extends RecyclerView.ViewHolder {

        private ImageView imageView10;
        private TextView product_name, product_price, product_description, seller_name;
        private CardView productcv;
        private TextView cartDelbtn;
        private ProgressBar progressBar3;

        public ViewHoler(@NonNull View itemView) {
            super(itemView);

            imageView10 = itemView.findViewById(R.id.imageView10);
            product_name = itemView.findViewById(R.id.product_name);
            product_price = itemView.findViewById(R.id.product_price);
            product_description = itemView.findViewById(R.id.product_description);
            seller_name = itemView.findViewById(R.id.seller_name);
            productcv = itemView.findViewById(R.id.productcv);
            progressBar3 = itemView.findViewById(R.id.progressBar3);
            cartDelbtn = itemView.findViewById(R.id.cartDelbtn);
        }
    }
}
