
package com.pakupaku.user.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AddCart {

    @SerializedName("delivery_charges")
    @Expose
    private String deliveryCharges;
    @SerializedName("delivery_free_minimum")
    @Expose
    private Integer deliveryFreeMinimum;
    @SerializedName("tax_percentage")
    @Expose
    private Integer taxPercentage;
    @SerializedName("carts")
    @Expose
    private List<Cart> products = new ArrayList<>();



    @SerializedName("total_price")
    @Expose
    private int totalPrice;

    @SerializedName("orignal_price")
    @Expose
    private int orignal_price;

    public String getShopDiscount() {
        return shopDiscount;
    }

    public void setShopDiscount(String shopDiscount) {
        this.shopDiscount = shopDiscount;
    }

    @SerializedName("shop_discount")
    @Expose
    private String shopDiscount;
    @SerializedName("tax")
    @Expose
    private String tax;
    @SerializedName("promocode_amount")
    @Expose
    private int promocodeAmount;
    @SerializedName("net")
    @Expose
    private int net;
    @SerializedName("wallet_balance")
    @Expose
    private String walletBalance;
    @SerializedName("payable")
    @Expose
    private double payable;


    public int getTotalPrice() {
        return totalPrice;
    }

    public int getOrignalPrice() {
        return orignal_price;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public int getPromocodeAmount() {
        return promocodeAmount;
    }

    public double getPayable() {
        return payable;
    }
    public String getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public Integer getDeliveryFreeMinimum() {
        return deliveryFreeMinimum;
    }

    public void setDeliveryFreeMinimum(Integer deliveryFreeMinimum) {
        this.deliveryFreeMinimum = deliveryFreeMinimum;
    }

    public Integer getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(Integer taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public List<Cart> getProductList() {
        return products;
    }

    public void setProductList(List<Cart> products) {
        this.products = products;
    }

}
