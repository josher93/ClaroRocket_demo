package com.globalpaysolutions.tigorocket.model;

/**
 * Created by Josué Chávez on 17/03/2017.
 */

public class TopupRequest
{
    private int mID;
    private String mTargetPhoneNumer;
    private String mOperatorName;
    private String mAmount;
    private int mStatusCode;
    private String mDate;

    public TopupRequest()
    {

    }

    public TopupRequest(int pID, String pTargetPhone, String pOperatorName, String pAmount, int pStatusCode, String pDate)
    {
        this.mID = pID;
        this.mTargetPhoneNumer = pTargetPhone;
        this.mOperatorName = pOperatorName;
        this.mAmount = pAmount;
        this.mStatusCode = pStatusCode;
        this.mDate = pDate;
    }

    public int getID()
    {
        return mID;
    }

    public String getTargetPhoneNumer()
    {
        return mTargetPhoneNumer;
    }

    public String getOperatorName()
    {
        return mOperatorName;
    }

    public String getAmount()
    {
        return mAmount;
    }

    public int getStatusCode()
    {
        return mStatusCode;
    }

    public String getDate()
    {
        return mDate;
    }

    public void setID(int mID)
    {
        this.mID = mID;
    }

    public void setTargetPhoneNumer(String mTargetPhoneNumer)
    {
        this.mTargetPhoneNumer = mTargetPhoneNumer;
    }

    public void setOperatorName(String mOperatorName)
    {
        this.mOperatorName = mOperatorName;
    }

    public void setAmount(String mAmount)
    {
        this.mAmount = mAmount;
    }

    public void setStatusCode(int mStatusCode)
    {
        this.mStatusCode = mStatusCode;
    }

    public void setDate(String mDate)
    {
        this.mDate = mDate;
    }
}
