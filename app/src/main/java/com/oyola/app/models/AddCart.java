
package com.oyola.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AddCart {

    @SerializedName("delivery_charges")
    @Expose
    private Double deliveryCharges;
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
    private Double totalPrice;

    @SerializedName("orignal_price")
    @Expose
    private Double orignal_price;
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
    private Double net;
    @SerializedName("wallet_balance")
    @Expose
    private String walletBalance;
    @SerializedName("payable")
    @Expose
    private Double payable;
    @SerializedName("first_commission_admin")
    @Expose
    private Double firstCommision;
    @SerializedName("second_commission_admin")
    @Expose
    private Double secondCommision;


    public Double getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(Double deliveryCharges) {
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

    public List<Cart> getProducts() {
        return products;
    }

    public void setProducts(List<Cart> products) {
        this.products = products;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getOrignal_price() {
        return orignal_price;
    }

    public void setOrignal_price(Double orignal_price) {
        this.orignal_price = orignal_price;
    }

    public String getShopDiscount() {
        return shopDiscount;
    }

    public void setShopDiscount(String shopDiscount) {
        this.shopDiscount = shopDiscount;
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

    public void setPromocodeAmount(int promocodeAmount) {
        this.promocodeAmount = promocodeAmount;
    }

    public Double getNet() {
        return net;
    }

    public void setNet(Double net) {
        this.net = net;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }

    public Double getPayable() {
        return payable;
    }

    public void setPayable(Double payable) {
        this.payable = payable;
    }

    public List<Cart> getProductList() {
        return products;
    }

    public void setProductList(List<Cart> products) {
        this.products = products;
    }

    public Double getFirstCommision() {
        return firstCommision;
    }

    public void setFirstCommision(Double firstCommision) {
        this.firstCommision = firstCommision;
    }

    public Double getSecondCommision() {
        return secondCommision;
    }

    public void setSecondCommision(Double secondCommision) {
        this.secondCommision = secondCommision;
    }
}
