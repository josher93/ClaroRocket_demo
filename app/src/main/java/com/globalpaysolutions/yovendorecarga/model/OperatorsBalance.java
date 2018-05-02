package com.globalpaysolutions.yovendorecarga.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Josu� Ch�vez on 09/12/2016.
 */
public class OperatorsBalance extends RealmObject
{
    @PrimaryKey
    private int operatorId;
    private String mobileOperator;
    private String balance;

    public String getMobileOperator()
    {
        return mobileOperator;
    }

    public int getOperatorId()
    {
        return operatorId;
    }

    public String getBalance()
    {
        return balance;
    }

    public void setMobileOperator(String mobileOperator)
    {
        this.mobileOperator = mobileOperator;
    }

    public void setOperatorId(int operatorId)
    {
        this.operatorId = operatorId;
    }

    public void setBalance(String balance)
    {
        this.balance = balance;
    }

    public OperatorsBalance()
    {

    }
}
