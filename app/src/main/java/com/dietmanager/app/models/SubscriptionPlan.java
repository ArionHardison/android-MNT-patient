package com.dietmanager.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionPlan {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("dietitian_id")
    @Expose
    private Integer dietitianId;
    @SerializedName("plan_id")
    @Expose
    private Integer planId;
    @SerializedName("expiry_date")
    @Expose
    private String expiryDate;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("subscription")
    @Expose
    private Subscription subscription;
    @SerializedName("dietitian")
    @Expose
    private Dietitian dietitian;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getDietitianId() {
        return dietitianId;
    }

    public void setDietitianId(Integer dietitianId) {
        this.dietitianId = dietitianId;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Dietitian getDietitian() {
        return dietitian;
    }

    public void setDietitian(Dietitian dietitian) {
        this.dietitian = dietitian;
    }

}