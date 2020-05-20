package com.debarunlahiri.dinmart.business;

public class BusinessOrders {

    private String address;
    private String name;
    private String order_id;
    private String order_status;
    private String user_id;
    private String user_phone_number;
    private long timestamp;

    BusinessOrders() {

    }

    public BusinessOrders(String address, String name, String order_id, String order_status, String user_id, String user_phone_number, long timestamp) {
        this.address = address;
        this.name = name;
        this.order_id = order_id;
        this.order_status = order_status;
        this.user_id = user_id;
        this.user_phone_number = user_phone_number;
        this.timestamp = timestamp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_phone_number() {
        return user_phone_number;
    }

    public void setUser_phone_number(String user_phone_number) {
        this.user_phone_number = user_phone_number;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
