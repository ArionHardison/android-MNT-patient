package com.dietmanager.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceOrderResponse {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("order_id")
    @Expose
    private Integer orderId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
