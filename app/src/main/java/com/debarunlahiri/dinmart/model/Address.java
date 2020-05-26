package com.debarunlahiri.dinmart.model;

public class Address {

    private String address_id;
    private String city;
    private String colony;
    private String full_address;
    private String house_no;
    private String landmark;
    private String name;
    private String phone;
    private String pincode;
    private String state;
    private boolean isDefault;

    public Address() {

    }

    public Address(String address_id, String city, String colony, String full_address, String house_no, String landmark, String name, String phone, String pincode, String state, boolean isDefault) {
        this.address_id = address_id;
        this.city = city;
        this.colony = colony;
        this.full_address = full_address;
        this.house_no = house_no;
        this.landmark = landmark;
        this.name = name;
        this.phone = phone;
        this.pincode = pincode;
        this.state = state;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getColony() {
        return colony;
    }

    public void setColony(String colony) {
        this.colony = colony;
    }

    public String getFull_address() {
        return full_address;
    }

    public void setFull_address(String full_address) {
        this.full_address = full_address;
    }

    public String getHouse_no() {
        return house_no;
    }

    public void setHouse_no(String house_no) {
        this.house_no = house_no;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
