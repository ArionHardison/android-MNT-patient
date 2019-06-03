
package com.pakupaku.user.models;

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
    private Integer paid;
    @SerializedName("gross")
    @Expose
    private int gross;
    @SerializedName("discount")
    @Expose
    private int discount;
    @SerializedName("promocode_amount")
    @Expose
    private int promocode_amount;
    @SerializedName("delivery_charge")
    @Expose
    private Integer deliveryCharge;
    @SerializedName("tax")
    @Expose
    private int tax;
    @SerializedName("net")
    @Expose
    private int net;
    @SerializedName("total_pay")
    @Expose
    private Object totalPay;
    @SerializedName("wallet_amount")
    @Expose
    private int walletAmount;
    @SerializedName("payable")
    @Expose
    private int payable;
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

    public Integer getPaid() {
        return paid;
    }

    public void setPaid(Integer paid) {
        this.paid = paid;
    }

    public int getGross() {
        return gross;
    }

    public void setGross(int gross) {
        this.gross = gross;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public Integer getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(Integer deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public int getNet() {
        return net;
    }

    public void setNet(int net) {
        this.net = net;
    }

    public Object getTotalPay() {
        return totalPay;
    }

    public void setTotalPay(Object totalPay) {
        this.totalPay = totalPay;
    }

    public int getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(int walletAmount) {
        this.walletAmount = walletAmount;
    }

    public int getPayable() {
        return payable;
    }

    public void setPayable(int payable) {
        this.payable = payable;
    }

    public int getPromocode_amount() {
        return promocode_amount;
    }

    public void setPromocode_amount(int payable) {
        this.promocode_amount = promocode_amount;
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
