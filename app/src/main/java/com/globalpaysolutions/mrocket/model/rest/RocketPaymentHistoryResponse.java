package com.globalpaysolutions.mrocket.model.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RocketPaymentHistoryResponse
{
    @SerializedName("History")
    @Expose
    private History history;
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("token")
    @Expose
    private String token;

    public History getHistory()
    {
        return history;
    }

    public void setHistory(History history)
    {
        this.history = history;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

}
