
package com.dalejoak.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Prices {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("discount")
    @Expose
    private int discount;
    @SerializedName("discount_type")
    @Expose
    private String discountType;

    @SerializedName("orignal_price")
    @Expose
    private Double orignal_price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Double getOrignalPrice() {
        return orignal_price;
    }

    public void setOriginalPrice(Double orignal_price) {
        this.orignal_price = orignal_price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

}
