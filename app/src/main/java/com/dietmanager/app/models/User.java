package com.dietmanager.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Tamil on 9/25/2017.
 */

public class User {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("currency_code")
    @Expose
    private String currency_code;
    @SerializedName("terms")
    @Expose
    private String terms;
    @SerializedName("privacy")
    @Expose
    private String privacy;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("device_token")
    @Expose
    private String deviceToken;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("invites")
    @Expose
    private int invites;
    @SerializedName("unsubscribe")
    @Expose
    private int unsubscribe;

    public int getInvites() {
        return invites;
    }

    public int getUnsubscribe() {
        return unsubscribe;
    }

    @SerializedName("device_type")
    @Expose
    private String deviceType;
    @SerializedName("login_by")
    @Expose
    private String loginBy;
    @SerializedName("social_unique_id")
    @Expose
    private Object socialUniqueId;
    @SerializedName("stripe_cust_id")
    @Expose
    private Object stripeCustId;
    @SerializedName("wallet_balance")
    @Expose
    private String walletBalance;
    @SerializedName("diet_tax")
    @Expose
    private String dietTax;
    @SerializedName("otp")
    @Expose
    private String otp;
    @SerializedName("customer_support")
    @Expose
    private String customer_support;
    @SerializedName("referral_code")
    @Expose
    private String referralCode;
    @SerializedName("subscription")
    @Expose
    private String subscription;

    @SerializedName("subscription_plan")
    @Expose
    private SubscriptionPlan subscriptionPlan;
    @SerializedName("dietitian")
    @Expose
    private Dietitian_ dietitian;
    @SerializedName("addresses")
    @Expose
    private List<Address> addresses = null;

    private List<CAddress> cAddresses = null;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setInvites(int invites) {
        this.invites = invites;
    }

    public void setUnsubscribe(int unsubscribe) {
        this.unsubscribe = unsubscribe;
    }

    public String getDietTax() {
        return dietTax;
    }

    public void setDietTax(String diet_tax) {
        this.dietTax = diet_tax;
    }

    public List<CAddress> getcAddresses() {
        return cAddresses;
    }

    public void setcAddresses(List<CAddress> cAddresses) {
        this.cAddresses = cAddresses;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    @SerializedName("cart")
    @Expose
    private List<Cart> cart = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getLoginBy() {
        return loginBy;
    }

    public void setLoginBy(String loginBy) {
        this.loginBy = loginBy;
    }

    public Object getSocialUniqueId() {
        return socialUniqueId;
    }

    public void setSocialUniqueId(Object socialUniqueId) {
        this.socialUniqueId = socialUniqueId;
    }

    public Object getStripeCustId() {
        return stripeCustId;
    }

    public void setStripeCustId(Object stripeCustId) {
        this.stripeCustId = stripeCustId;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getOtp() {
        return otp;
    }

    public String getCustomer_support() {
        return customer_support;
    }

    public void setCustomer_support(String customer_support) {
        this.customer_support = customer_support;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public List<CAddress> getCAddresses() {
        return cAddresses;
    }

    public void setCAddresses(List<CAddress> cAddresses) {
        this.cAddresses = cAddresses;
    }

    public List<Cart> getCart() {
        return cart;
    }

    public void setCart(List<Cart> cart) {
        this.cart = cart;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public Dietitian_ getDietitian() {
        return dietitian;
    }

    public void setDietitian(Dietitian_ dietitian) {
        this.dietitian = dietitian;
    }
}