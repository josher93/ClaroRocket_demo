package com.globalpaysolutions.tigorocket.model.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RocketSaleDetailResponse
{
    @SerializedName("balanceID")
    @Expose
    private int balanceID;
    @SerializedName("personID")
    @Expose
    private int personID;
    @SerializedName("personMasterID")
    @Expose
    private int personMasterID;
    @SerializedName("receivable")
    @Expose
    private double receivable;
    @SerializedName("reconcileCount")
    @Expose
    private double reconcileCount;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("bStatus")
    @Expose
    private int bStatus;
    @SerializedName("zone")
    @Expose
    private String zone;
    @SerializedName("minDate")
    @Expose
    private String minDate;
    @SerializedName("maxDate")
    @Expose
    private String maxDate;
    @SerializedName("distributor")
    @Expose
    private String distributor;
    @SerializedName("sale")
    @Expose
    private double sale;

    public int getBalanceID()
    {
        return balanceID;
    }

    public void setBalanceID(int balanceID)
    {
        this.balanceID = balanceID;
    }

    public int getPersonID()
    {
        return personID;
    }

    public void setPersonID(int personID)
    {
        this.personID = personID;
    }

    public int getPersonMasterID()
    {
        return personMasterID;
    }

    public void setPersonMasterID(int personMasterID)
    {
        this.personMasterID = personMasterID;
    }

    public double getReceivable()
    {
        return receivable;
    }

    public void setReceivable(double receivable)
    {
        this.receivable = receivable;
    }

    public double getReconcileCount()
    {
        return reconcileCount;
    }

    public void setReconcileCount(double reconcileCount)
    {
        this.reconcileCount = reconcileCount;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getStatus()
    {
        return bStatus;
    }

    public void setBStatus(int bStatus)
    {
        this.bStatus = bStatus;
    }

    public String getZone()
    {
        return zone;
    }

    public void setZone(String zone)
    {
        this.zone = zone;
    }

    public String getMinDate()
    {
        return minDate;
    }

    public void setMinDate(String minDate)
    {
        this.minDate = minDate;
    }

    public String getMaxDate()
    {
        return maxDate;
    }

    public void setMaxDate(String maxDate)
    {
        this.maxDate = maxDate;
    }

    public String getDistributor()
    {
        return distributor;
    }

    public void setDistributor(String distributor)
    {
        this.distributor = distributor;
    }

    public double getSale()
    {
        return sale;
    }

    public void setSale(double sale)
    {
        this.sale = sale;
    }
}
