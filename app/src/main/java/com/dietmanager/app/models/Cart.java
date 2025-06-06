
package com.dietmanager.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Cart {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("promocode_id")
    @Expose
    private Object promocodeId;
    @SerializedName("calculated_price")
    @Expose
    private double calculated_price;
    @SerializedName("order_id")
    @Expose
    private Object orderId;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("savedforlater")
    @Expose
    private Integer savedforlater;
    @SerializedName("product")
    @Expose
    private Product product;
    @SerializedName("cart_addons")
    @Expose
    private List<CartAddon> cartAddons = null;

    public double getCalculated_price() {
        return calculated_price;
    }

    public void setCalculated_price(int calculated_price) {
        this.calculated_price = calculated_price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Object getPromocodeId() {
        return promocodeId;
    }

    public void setPromocodeId(Object promocodeId) {
        this.promocodeId = promocodeId;
    }

    public Object getOrderId() {
        return orderId;
    }

    public void setOrderId(Object orderId) {
        this.orderId = orderId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getSavedforlater() {
        return savedforlater;
    }

    public void setSavedforlater(Integer savedforlater) {
        this.savedforlater = savedforlater;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<CartAddon> getCartAddons() {
        return cartAddons;
    }

    public void setCartAddons(List<CartAddon> cartAddons) {
        this.cartAddons = cartAddons;
    }

}