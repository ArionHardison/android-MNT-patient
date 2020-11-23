package com.dietmanager.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SaveCustomerAddress {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("Address")
    @Expose
    private Address address;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

}
