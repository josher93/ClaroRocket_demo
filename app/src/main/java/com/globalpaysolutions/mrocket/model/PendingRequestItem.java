package com.globalpaysolutions.mrocket.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Josué Chávez on 22/07/2017.
 */

public class PendingRequestItem
{
    private int topUpRequestID;
    private String nickname;
    private String phoneNumber;
    private String operatorName;
    private String amount;
    private Date date;
    private String dateGMT;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public int getTopUpRequestID() {
        return topUpRequestID;
    }

    public void setTopUpRequestID(int topUpRequestID) {
        this.topUpRequestID = topUpRequestID;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateGMT() {
        return dateGMT;
    }

    public void setDateGMT(String dateGMT) {
        this.dateGMT = dateGMT;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
