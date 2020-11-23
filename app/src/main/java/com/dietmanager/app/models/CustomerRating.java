package com.dietmanager.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomerRating {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_request_id")
    @Expose
    private Integer orderRequestId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("dietitian_id")
    @Expose
    private Object dietitianId;
    @SerializedName("chef_id")
    @Expose
    private Integer chefId;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("comment")
    @Expose
    private Object comment;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderRequestId() {
        return orderRequestId;
    }

    public void setOrderRequestId(Integer orderRequestId) {
        this.orderRequestId = orderRequestId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Object getDietitianId() {
        return dietitianId;
    }

    public void setDietitianId(Object dietitianId) {
        this.dietitianId = dietitianId;
    }

    public Integer getChefId() {
        return chefId;
    }

    public void setChefId(Integer chefId) {
        this.chefId = chefId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Object getComment() {
        return comment;
    }

    public void setComment(Object comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
