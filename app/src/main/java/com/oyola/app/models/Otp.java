
package com.oyola.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Otp {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("otp")
    @Expose
    private Integer otp;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("user")
    @Expose
    private String user;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
