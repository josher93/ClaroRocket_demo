package com.globalpaysolutions.mrocket.model;

/**
 * Created by Josué Chávez on 14/03/2017.
 */

public class DataVendorPoint
{
    private String mVendorCode;

    public DataVendorPoint()
    {

    }

    public DataVendorPoint(String VendorCode)
    {
        this.mVendorCode = VendorCode;
    }

    public String getmVendorCode()
    {
        return mVendorCode;
    }

    public void setmVendorCode(String VendorCode)
    {
        this.mVendorCode = VendorCode;
    }
}
