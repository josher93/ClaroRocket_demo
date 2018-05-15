package com.globalpaysolutions.yovendorecarga.model.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RocketPaymentReq
{
    @SerializedName("BalanceID")
    @Expose
    private String balanceID;
    @SerializedName("SecurityPin")
    @Expose
    private String securityPin;

    public String getBalanceID() {
        return balanceID;
    }

    public void setBalanceID(String balanceID) {
        this.balanceID = balanceID;
    }

    public String getSecurityPin() {
        return securityPin;
    }

    public void setSecurityPin(String securityPin) {
        this.securityPin = securityPin;
    }
}
