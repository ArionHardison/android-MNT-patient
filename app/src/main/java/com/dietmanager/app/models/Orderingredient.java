package com.dietmanager.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Orderingredient {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("ingredient_id")
    @Expose
    private Integer ingredientId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("foodingredient")
    @Expose
    private Foodingredient foodingredient;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Integer ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Foodingredient getFoodingredient() {
        return foodingredient;
    }

    public void setFoodingredient(Foodingredient foodingredient) {
        this.foodingredient = foodingredient;
    }


}
