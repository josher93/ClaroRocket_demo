package com.globalpaysolutions.yovendorecarga.model.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RocketPaymentResponse
{

    @SerializedName("Status")
    @Expose
    private boolean status;

    public boolean isStatus()
    {
        return status;
    }

    public void setStatus(boolean status)
    {
        this.status = status;
    }
}
