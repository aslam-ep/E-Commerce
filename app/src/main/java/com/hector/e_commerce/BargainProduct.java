package com.hector.e_commerce;

public class BargainProduct {
    String bargainId, productId, requestedPrice, requestedQuantity, buyerId, productName, currentPrice;

    public BargainProduct(String bargainId, String productId, String requestedPrice, String requestedQuantity, String buyerId, String productName, String currentPrice) {
        this.bargainId = bargainId;
        this.productId = productId;
        this.requestedPrice = requestedPrice;
        this.requestedQuantity = requestedQuantity;
        this.buyerId = buyerId;
        this.productName = productName;
        this.currentPrice = currentPrice;
    }

    public String getBargainId() {
        return bargainId;
    }

    public String getProductId() {
        return productId;
    }

    public String getRequestedPrice() {
        return requestedPrice;
    }

    public String getRequestedQuantity() {
        return requestedQuantity;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getProductName() {
        return productName;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }
}
