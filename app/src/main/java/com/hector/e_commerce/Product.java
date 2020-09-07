package com.hector.e_commerce;

import android.graphics.Bitmap;

public class Product {
    private String name,brand,price, imgURL, quantity;

    public Product(String name, String brand, String quantity, String price, String imgURL) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.imgURL = imgURL;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public String getImgURL() {
        return imgURL;
    }
}
