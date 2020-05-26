package com.debarunlahiri.dinmart.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.debarunlahiri.dinmart.activity.AddDeliveryDetails;
import com.debarunlahiri.dinmart.model.Address;
import com.debarunlahiri.dinmart.next.R;
import com.debarunlahiri.dinmart.utils.Variables;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sun.mail.imap.IMAPMultipartDataSource;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Context mContext;
    private List<Address> addressList;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    public AddressAdapter(Context mContext, List<Address> addressList) {
        this.mContext = mContext;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_address_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address address = addressList.get(position);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        setAddress(holder, address);

        mDatabase.child("users").child(Variables.global_user_id).child("address_list").child(address.getAddress_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isDefault = (boolean) dataSnapshot.child("isDefault").getValue();

                if (isDefault) {
                    holder.bDeliveryAddress.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
                    holder.bDeliveryAddress.setText("Using this address");
                    holder.bDeliveryAddress.setTextColor(Color.BLACK);
                    holder.bDeliveryAddress.setEnabled(false);
                } else {
                    holder.bDeliveryAddress.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                    holder.bDeliveryAddress.setText("Deliver to this address");
                    holder.bDeliveryAddress.setTextColor(Color.WHITE);
                    holder.bDeliveryAddress.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.bDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("users").child(Variables.global_user_id).child("address_list").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String key = ds.getKey();
                            mDatabase.child("users").child(Variables.global_user_id).child("address_list").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    boolean isDefault = (boolean) dataSnapshot.child("isDefault").getValue();
                                    if (isDefault) {
                                        mDatabase.child("users").child(Variables.global_user_id).child("address_list").child(key).child("isDefault").setValue(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                mDatabase.child("users").child(Variables.global_user_id).child("address").setValue(address.getFull_address()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mDatabase.child("users").child(Variables.global_user_id).child("address_list").child(address.getAddress_id()).child("isDefault").setValue(true);
                            Toast.makeText(mContext, "You are now using this address", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        holder.tvAddressEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editAddressIntent = new Intent(mContext, AddDeliveryDetails.class);
                editAddressIntent.putExtra("type", "Edit");
                editAddressIntent.putExtra("address_id", address.getAddress_id());
                mContext.startActivity(editAddressIntent);
            }
        });
    }

    private void setAddress(ViewHolder holder, Address address) {
        holder.tvDeliveryPersonName.setText(address.getName());
        holder.tvPersonPhone.setText("+91" + address.getPhone());
        holder.tvPersonAddress.setText(address.getFull_address());
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDeliveryPersonName, tvPersonPhone, tvPersonAddress, tvAddressEdit;
        private Button bDeliveryAddress;
        private CardView cvAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDeliveryPersonName = itemView.findViewById(R.id.tvDeliveryPersonName);
            tvPersonPhone = itemView.findViewById(R.id.tvPersonPhone);
            tvPersonAddress = itemView.findViewById(R.id.tvPersonAddress);
            tvAddressEdit = itemView.findViewById(R.id.tvAddressEdit);
            bDeliveryAddress = itemView.findViewById(R.id.bDeliveryAddress);
            cvAddress = itemView.findViewById(R.id.cvAddress);
        }
    }
}
