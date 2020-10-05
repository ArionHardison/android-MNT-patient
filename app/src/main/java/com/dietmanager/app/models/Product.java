
package com.dietmanager.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("shop_id")
    @Expose
    private Integer shopId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("out_of_stock")
    @Expose
    private String out_of_stock;

    @SerializedName("position")
    @Expose
    private Integer position;
    @SerializedName("food_type")
    @Expose
    private String foodType;
    @SerializedName("avalability")
    @Expose
    private Integer avalability;
    @SerializedName("max_quantity")
    @Expose
    private Integer maxQuantity;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("prices")
    @Expose
    private Prices prices;
    @SerializedName("featured_images")
    @Expose
    private List<FeaturedImage> featuredImages = null;
    @SerializedName("images")
    @Expose
    private List<Image> images = null;
    @SerializedName("addons")
    @Expose
    private List<Addon> addons = null;
    @SerializedName("cart")
    @Expose
    private List<Cart> cart;
    @SerializedName("shop")
    @Expose
    private Shop shop;
    @SerializedName("ingredients")
    @Expose
    private String ingredients;
    @SerializedName("calories")
    @Expose
    private Double calories;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public Integer getAvalability() {
        return avalability;
    }

    public void setAvalability(Integer avalability) {
        this.avalability = avalability;
    }

    public Integer getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Integer maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Prices getPrices() {
        return prices;
    }

    public void setPrices(Prices prices) {
        this.prices = prices;
    }

    public List<FeaturedImage> getFeaturedImages() {
        return featuredImages;
    }

    public void setFeaturedImages(List<FeaturedImage> featuredImages) {
        this.featuredImages = featuredImages;
    }

    public List<Cart> getCart() {
        return cart;
    }

    public void setCart(List<Cart> cart) {
        this.cart = cart;
    }

    public List<Addon> getAddons() {
        return addons;
    }

    public void setAddons(List<Addon> addons) {
        this.addons = addons;
    }
    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public String getOut_of_stock() {
        return out_of_stock;
    }

    public void setOut_of_stock(String out_of_stock) {
        this.out_of_stock = out_of_stock;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}