package com.globalpaysolutions.tigorocket.model.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class History
{
    @SerializedName("RocketBalanceList")
    @Expose
    private List<RocketBalanceList> rocketBalanceList = null;

    public List<RocketBalanceList> getRocketBalanceList()
    {
        return rocketBalanceList;
    }

    public void setRocketBalanceList(List<RocketBalanceList> rocketBalanceList)
    {
        this.rocketBalanceList = rocketBalanceList;
    }
}
