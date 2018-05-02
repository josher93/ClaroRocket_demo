package com.globalpaysolutions.yovendorecarga.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Josué Chávez on 12/12/2016.
 */
public class OperatorsBalanceModel
{
    @SerializedName("operatorId")
    private int OperatorID;

    @SerializedName("mobileOperator")
    private String MobileOperator;

    @SerializedName("balance")
    private String Balance;

    public int getOperatorID()
    {
        return OperatorID;
    }

    public String getMobileOperator()
    {
        return MobileOperator;
    }

    public String getBalance()
    {
        return Balance;
    }

    public void setOperatorID(int operatorID)
    {
        OperatorID = operatorID;
    }

    public void setMobileOperator(String mobileOperator)
    {
        MobileOperator = mobileOperator;
    }

    public void setBalance(String balance)
    {
        Balance = balance;
    }

    public OperatorsBalanceModel()
    {

    }

    public OperatorsBalanceModel(int pId, String pMobileOperator, String pBalance)
    {
        this.setOperatorID(pId);
        this.setMobileOperator(pMobileOperator);
        this.setBalance(pBalance);
    }
}
