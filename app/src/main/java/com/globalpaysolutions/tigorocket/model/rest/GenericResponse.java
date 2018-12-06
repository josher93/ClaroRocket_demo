package com.globalpaysolutions.tigorocket.model.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GenericResponse
{
    @SerializedName("HttpCode")
    @Expose
    private int httpCode;
    @SerializedName("InternalCode")
    @Expose
    private Integer internalCode;
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

    public Integer getInternalCode()
    {
        return internalCode;
    }

    public void setInternalCode(Integer internalCode)
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
