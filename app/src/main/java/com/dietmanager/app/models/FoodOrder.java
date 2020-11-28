package com.dietmanager.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FoodOrder {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("food_id")
    @Expose
    private Integer foodId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("chef_id")
    @Expose
    private Object chefId;
    @SerializedName("dietitian_id")
    @Expose
    private Integer dietitianId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("chef_rating")
    @Expose
    private Integer chefRating;
    @SerializedName("dietitian_rating")
    @Expose
    private Integer dietitianRating;
    @SerializedName("is_scheduled")
    @Expose
    private Integer isScheduled;
    @SerializedName("schedule_at")
    @Expose
    private Object scheduleAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("payable")
    @Expose
    private double payable;
    @SerializedName("total")
    @Expose
    private Double total;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("ingredient_image")
    @Expose
    private Object ingredientImage;
    @SerializedName("food_amount")
    @Expose
    private String foodAmount;
    @SerializedName("ingredient_amount")
    @Expose
    private String ingredientAmount;
    @SerializedName("customer_address_id")
    @Expose
    private Integer customerAddressId;
    @SerializedName("promocode_id")
    @Expose
    private Object promocodeId;
    @SerializedName("prepared_image")
    @Expose
    private String preparedImage;
    @SerializedName("promocode_amount")
    @Expose
    private String promocodeAmount;
    @SerializedName("is_wallet")
    @Expose
    private Integer isWallet;
    @SerializedName("wallet_amount")
    @Expose
    private String walletAmount;
    @SerializedName("tax")
    @Expose
    private String tax;
    @SerializedName("paid_amount")
    @Expose
    private String paidAmount;
    @SerializedName("admin_commission")
    @Expose
    private String adminCommission;
    @SerializedName("dietitian_commission")
    @Expose
    private String dietitianCommission;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("is_paid")
    @Expose
    private Integer isPaid;
    @SerializedName("transaction_id")
    @Expose
    private Object transactionId;
    @SerializedName("chef_commission")
    @Expose
    private String chefCommission;
    @SerializedName("food")
    @Expose
    private Food food;
    @SerializedName("orderingredient")
    @Expose
    private List<Orderingredient> orderingredient = null;
    @SerializedName("dietitian")
    @Expose
    private Dietitian dietitian;
    @SerializedName("chef")
    @Expose
    private Chef chef;
    @SerializedName("customer_address")
    @Expose
    private CustomerAddresss customerAddress;
    @SerializedName("rating")
    @Expose
    private List<CustomerRating> rating = null;

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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Object getChefId() {
        return chefId;
    }

    public void setChefId(Object chefId) {
        this.chefId = chefId;
    }

    public Integer getDietitianId() {
        return dietitianId;
    }

    public void setDietitianId(Integer dietitianId) {
        this.dietitianId = dietitianId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getChefRating() {
        return chefRating;
    }

    public void setChefRating(Integer chefRating) {
        this.chefRating = chefRating;
    }

    public Integer getDietitianRating() {
        return dietitianRating;
    }

    public void setDietitianRating(Integer dietitianRating) {
        this.dietitianRating = dietitianRating;
    }

    public Integer getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(Integer isScheduled) {
        this.isScheduled = isScheduled;
    }

    public Object getScheduleAt() {
        return scheduleAt;
    }

    public void setScheduleAt(Object scheduleAt) {
        this.scheduleAt = scheduleAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public double getPayable() {
        return payable;
    }

    public void setPayable(double payable) {
        this.payable = payable;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public Object getIngredientImage() {
        return ingredientImage;
    }

    public void setIngredientImage(Object ingredientImage) {
        this.ingredientImage = ingredientImage;
    }

    public String getFoodAmount() {
        return foodAmount;
    }

    public void setFoodAmount(String foodAmount) {
        this.foodAmount = foodAmount;
    }

    public String getIngredientAmount() {
        return ingredientAmount;
    }

    public void setIngredientAmount(String ingredientAmount) {
        this.ingredientAmount = ingredientAmount;
    }

    public Integer getCustomerAddressId() {
        return customerAddressId;
    }

    public void setCustomerAddressId(Integer customerAddressId) {
        this.customerAddressId = customerAddressId;
    }

    public Object getPromocodeId() {
        return promocodeId;
    }

    public void setPromocodeId(Object promocodeId) {
        this.promocodeId = promocodeId;
    }

    public String getPromocodeAmount() {
        return promocodeAmount;
    }

    public void setPreparedImage(String preparedImage) {
        this.preparedImage = preparedImage;
    }

    public String getPreparedImage() {
        return preparedImage;
    }

    public void setPromocodeAmount(String promocodeAmount) {
        this.promocodeAmount = promocodeAmount;
    }

    public Integer getIsWallet() {
        return isWallet;
    }

    public void setIsWallet(Integer isWallet) {
        this.isWallet = isWallet;
    }

    public String getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(String walletAmount) {
        this.walletAmount = walletAmount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getAdminCommission() {
        return adminCommission;
    }

    public void setAdminCommission(String adminCommission) {
        this.adminCommission = adminCommission;
    }

    public String getDietitianCommission() {
        return dietitianCommission;
    }

    public void setDietitianCommission(String dietitianCommission) {
        this.dietitianCommission = dietitianCommission;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Integer getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Integer isPaid) {
        this.isPaid = isPaid;
    }

    public Object getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Object transactionId) {
        this.transactionId = transactionId;
    }

    public String getChefCommission() {
        return chefCommission;
    }

    public void setChefCommission(String chefCommission) {
        this.chefCommission = chefCommission;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public List<Orderingredient> getOrderingredient() {
        return orderingredient;
    }

    public void setOrderingredient(List<Orderingredient> orderingredient) {
        this.orderingredient = orderingredient;
    }

    public Dietitian getDietitian() {
        return dietitian;
    }

    public void setDietitian(Dietitian dietitian) {
        this.dietitian = dietitian;
    }

    public Chef getChef() {
        return chef;
    }

    public void setChef(Chef chef) {
        this.chef = chef;
    }

    public CustomerAddresss getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(CustomerAddresss customerAddress) {
        this.customerAddress = customerAddress;
    }
    public List<CustomerRating> getRating() {
        return rating;
    }

    public void setRating(List<CustomerRating> rating) {
        this.rating = rating;
    }
}
