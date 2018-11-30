package com.globalpaysolutions.mrocket.model;

/**
 * Created by Geovanni on 20/03/2016.
 */
public class Amount
{
    private int mID;
    private double mAmount;
    private String mDisplay;
    private String mDecimal;
    private String mAditionalText;
    private String mMNO;
    private String mCode;
    private int mPackageCode;
    private int mRelevance;

    public int getID()
    {
        return mID;
    }

    public String getDecimal()
    {
        return mDecimal;
    }

    public String getAditionalText()
    {
        return mAditionalText;
    }

    public String getDisplay()
    {
        return mDisplay;
    }

    public String getMNO()
    {
        return mMNO;
    }

    public String getCode()
    {
        return mCode;
    }

    public double getAmount()
    {
        return mAmount;
    }

    public int getPackageCode()
    {
        return this.mPackageCode;
    }

    public int getRelevance()
    {
        return mRelevance;
    }

    public void setAmount(double pAmount)
    {
        this.mAmount = pAmount;
    }

    public void setID(int mID)
    {
        this.mID = mID;
    }

    public void setDecimal(String pDecimal)
    {
        this.mDecimal = pDecimal;
    }

    public void setAditionalText(String pText)
    {
        this.mAditionalText = pText;
    }

    public void setDisplay(String pDisplay)
    {
        this.mDisplay = pDisplay;
    }

    public void setMNO(String pMNO)
    {
        this.mMNO = pMNO;
    }

    public void setCode(String mCode)
    {
        this.mCode = mCode;
    }

    public void setPackageCode(int pPackageCode)
    {
        this.mPackageCode = pPackageCode;
    }

    public void setRelevance(int mRelevance)
    {
        this.mRelevance = mRelevance;
    }

    public Amount(int pID, double pAmount, String pDecimal, String pText, String pDisplay, String pMNO, String pCode, int pPackageCode, int pRelevance)
    {
        this.setID(pID);
        this.setAmount(pAmount);
        this.setDecimal(pDecimal);
        this.setAditionalText(pText);
        this.setDisplay(pDisplay);
        this.setMNO(pMNO);
        this.setCode(pCode);
        this.setPackageCode(pPackageCode);
        this.setRelevance(pRelevance);
    }


    public Amount()
        {}
}
