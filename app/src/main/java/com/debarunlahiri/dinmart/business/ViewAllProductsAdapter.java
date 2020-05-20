package com.debarunlahiri.dinmart.business;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.debarunlahiri.dinmart.next.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ViewAllProductsAdapter extends RecyclerView.Adapter<ViewAllProductsAdapter.ViewHolder> {

    private Context mContext;
    private List<BusinessOrders> businessOrdersList;
    private String type;

    public ViewAllProductsAdapter(Context mContext, List<BusinessOrders> businessOrdersList, String type) {
        this.mContext = mContext;
        this.businessOrdersList = businessOrdersList;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_add_business_orders_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BusinessOrders businessOrders = businessOrdersList.get(position);

        setOrderDetails(holder, businessOrders);

        holder.bOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderDetailsIntent = new Intent(mContext, BusinessOrderDetailsActivity.class);
                orderDetailsIntent.putExtra("order_id", businessOrders.getOrder_id());
                orderDetailsIntent.putExtra("type", type);
                mContext.startActivity(orderDetailsIntent);
            }
        });
    }

    private void setOrderDetails(ViewHolder holder, BusinessOrders businessOrders) {
        holder.tvOrderPersonAddress.setText("Person Address: " + businessOrders.getAddress());
        holder.tvOrderPersonName.setText("Person Name: " + businessOrders.getName());
        holder.tvOrderStatus.setText("Order Status: " + businessOrders.getOrder_status());
        holder.tvOrderPersonPhone.setText("Person Phone No.: " + businessOrders.getUser_phone_number());

        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        java.util.Date currenTimeZone=new java.util.Date((long)businessOrders.getTimestamp());

        holder.tvOrderDate.setText("Order Date: " + sdf.format(currenTimeZone));
    }

    @Override
    public int getItemCount() {
        return businessOrdersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvOrderPersonName, tvOrderPersonAddress, tvOrderPersonPhone, tvOrderDate, tvOrderStatus;
        private Button bOrderDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderPersonName = itemView.findViewById(R.id.tvOrderPersonName);
            tvOrderPersonAddress = itemView.findViewById(R.id.tvOrderPersonAddress);
            tvOrderPersonPhone = itemView.findViewById(R.id.tvOrderPersonPhone);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            bOrderDetails = itemView.findViewById(R.id.bOrderDetails);
        }
    }
}
