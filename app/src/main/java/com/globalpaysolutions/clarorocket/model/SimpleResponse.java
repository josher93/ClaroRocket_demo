package com.globalpaysolutions.clarorocket.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Josué Chávez on 22/3/2018.
 */

public class SimpleResponse
{
    @SerializedName("HttpCode")
    @Expose
    private int httpCode;
    @SerializedName("InternalCode")
    @Expose
    private String internalCode;
    @SerializedName("Message")
    @Expose
    private String message;

    public int getHttpCode()
    {
        return httpCode;
    }

    public void setHttpCode(int httpCode)
    {
        this.httpCode = httpCode;
    }

    public String getInternalCode()
    {
        return internalCode;
    }

    public void setInternalCode(String internalCode)
    {
        this.internalCode = internalCode;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

}
