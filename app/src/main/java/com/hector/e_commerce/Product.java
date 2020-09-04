package com.hector.e_commerce;

import android.graphics.Bitmap;

public class Product {
    private String name,spec,price, imgURL;

    public Product(String name, String spec, String price, String imgURL) {
        this.name = name;
        this.spec = spec;
        this.price = price;
        this.imgURL = imgURL;
    }

    public String getName() {
        return name;
    }

    public String getSpec() {
        return spec;
    }

    public String getPrice() {
        return price;
    }

    public String getImgURL() {
        return imgURL;
    }
}
