
package com.dalejoak.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Invoice {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("paid")
    @Expose
    private double paid;
    @SerializedName("gross")
    @Expose
    private double gross;
    @SerializedName("discount")
    @Expose
    private double discount;
    @SerializedName("promocode_amount")
    @Expose
    private int promocode_amount;
    @SerializedName("delivery_charge")
    @Expose
    private Integer deliveryCharge;
    @SerializedName("tax")
    @Expose
    private double tax;
    @SerializedName("net")
    @Expose
    private double net;
    @SerializedName("total_pay")
    @Expose
    private double totalPay;
    @SerializedName("wallet_amount")
    @Expose
    private int walletAmount;
    @SerializedName("payable")
    @Expose
    private double payable;
    @SerializedName("tender_pay")
    @Expose
    private Object tenderPay;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
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

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public double getGross() {
        return gross;
    }

    public void setGross(double gross) {
        this.gross = gross;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getPromocode_amount() {
        return promocode_amount;
    }

    public void setPromocode_amount(int promocode_amount) {
        this.promocode_amount = promocode_amount;
    }

    public Integer getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(Integer deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getNet() {
        return net;
    }

    public void setNet(double net) {
        this.net = net;
    }

    public double getTotalPay() {
        return totalPay;
    }

    public void setTotalPay(double totalPay) {
        this.totalPay = totalPay;
    }

    public int getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(int walletAmount) {
        this.walletAmount = walletAmount;
    }

    public double getPayable() {
        return payable;
    }

    public void setPayable(double payable) {
        this.payable = payable;
    }

    public Object getTenderPay() {
        return tenderPay;
    }

    public void setTenderPay(Object tenderPay) {
        this.tenderPay = tenderPay;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
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
