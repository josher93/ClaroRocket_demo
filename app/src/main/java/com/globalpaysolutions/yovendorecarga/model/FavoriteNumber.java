package com.globalpaysolutions.yovendorecarga.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Josué Chávez on 22/3/2018.
 */

public class FavoriteNumber
{
    @SerializedName("FavoriteNumberID")
    @Expose
    private int favoriteNumberID;
    @SerializedName("Msisdn")
    @Expose
    private String msisdn;
    @SerializedName("Name")
    @Expose
    private String name;

    public int getFavoriteNumberID()
    {
        return favoriteNumberID;
    }

    public void setFavoriteNumberID(int favoriteNumberID)
    {
        this.favoriteNumberID = favoriteNumberID;
    }

    public String getMsisdn()
    {
        return msisdn;
    }

    public void setMsisdn(String msisdn)
    {
        this.msisdn = msisdn;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
