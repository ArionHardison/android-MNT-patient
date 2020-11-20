package com.dietmanager.app.models;

import com.dietmanager.app.models.ingredients.Ingredient;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Foodingredient {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("food_id")
    @Expose
    private Integer foodId;
    @SerializedName("ingredient_id")
    @Expose
    private Integer ingredientId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("ingredient")
    @Expose
    private Ingredient ingredient;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

    public Integer getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Integer ingredientId) {
        this.ingredientId = ingredientId;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

}
