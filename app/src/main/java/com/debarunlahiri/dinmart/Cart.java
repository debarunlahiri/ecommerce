package com.debarunlahiri.dinmart;

import android.os.Parcel;
import android.os.Parcelable;

public class Cart implements Parcelable {

    private String cart_key;
    private String product_item_count;
    private String product_key;
    private String product_price;
    private String product_quantity;
    private String product_weight_unit;
    private String user_id;
    private String total_product_price;
    private boolean visibility;

    Cart() {

    }

    public Cart(String cart_key, String product_item_count, String product_key, String product_price, String product_quantity, String product_weight_unit, String user_id, boolean visibilityvisibility, String total_product_price) {
        this.cart_key = cart_key;
        this.product_item_count = product_item_count;
        this.product_key = product_key;
        this.product_price = product_price;
        this.product_quantity = product_quantity;
        this.product_weight_unit = product_weight_unit;
        this.user_id = user_id;
        this.visibility = visibility;
        this.total_product_price = total_product_price;
    }

    protected Cart(Parcel in) {
        cart_key = in.readString();
        product_item_count = in.readString();
        product_key = in.readString();
        product_price = in.readString();
        product_quantity = in.readString();
        product_weight_unit = in.readString();
        user_id = in.readString();
    }

    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };

    public String getCart_key() {
        return cart_key;
    }

    public void setCart_key(String cart_key) {
        this.cart_key = cart_key;
    }

    public String getProduct_item_count() {
        return product_item_count;
    }

    public void setProduct_item_count(String product_item_count) {
        this.product_item_count = product_item_count;
    }

    public String getProduct_key() {
        return product_key;
    }

    public void setProduct_key(String product_key) {
        this.product_key = product_key;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(String product_quantity) {
        this.product_quantity = product_quantity;
    }

    public String getProduct_weight_unit() {
        return product_weight_unit;
    }

    public void setProduct_weight_unit(String product_weight_unit) {
        this.product_weight_unit = product_weight_unit;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public String getTotal_product_price() {
        return total_product_price;
    }

    public void setTotal_product_price(String total_product_price) {
        this.total_product_price = total_product_price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cart_key);
        dest.writeString(product_item_count);
        dest.writeString(product_key);
        dest.writeString(product_price);
        dest.writeString(product_quantity);
        dest.writeString(product_weight_unit);
        dest.writeString(user_id);
    }

    public static final Parcelable.Creator<Cart> CART_CREATOR = new ClassLoaderCreator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel source, ClassLoader loader) {
            return new Cart(source);
        }

        @Override
        public Cart createFromParcel(Parcel source) {
            return new Cart(source);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };
}
