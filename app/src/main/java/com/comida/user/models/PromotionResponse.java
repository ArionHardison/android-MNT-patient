package com.comida.user.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 10/13/2017.
 */

public class PromotionResponse {

    @SerializedName("wallet_balance")
    @Expose
    private String walletMoney;

    public String getWalletMoney() {
        return walletMoney;
    }

    public void setWalletMoney(String walletMoney) {
        this.walletMoney = walletMoney;
    }
}
