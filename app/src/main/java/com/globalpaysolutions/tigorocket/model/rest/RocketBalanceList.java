package com.globalpaysolutions.tigorocket.model.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RocketBalanceList
{
    @SerializedName("balanceID")
    @Expose
    private int balanceID;
    @SerializedName("balanceAmount")
    @Expose
    private double balanceAmount;
    @SerializedName("fromDate")
    @Expose
    private String fromDate;
    @SerializedName("toDate")
    @Expose
    private String toDate;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("conciliationDate")
    @Expose
    private String conciliationDate;
    @SerializedName("profit")
    @Expose
    private double profit;

    public int getBalanceID() {
        return balanceID;
    }

    public void setBalanceID(int balanceID) {
        this.balanceID = balanceID;
    }

    public double getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(double balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getConciliationDate() {
        return conciliationDate;
    }

    public void setConciliationDate(String conciliationDate) {
        this.conciliationDate = conciliationDate;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

}
