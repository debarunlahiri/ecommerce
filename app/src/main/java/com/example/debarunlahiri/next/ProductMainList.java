package com.example.debarunlahiri.next;

public class ProductMainList {
    private String imageview;
    private String name;
    public ProductMainList() {

    }

    public ProductMainList(String imageview, String name) {
        this.imageview = imageview;
        this.name = name;
    }

    public String getImageview() {
        return imageview;
    }

    public void setImageview(String imageview) {
        this.imageview = imageview;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
