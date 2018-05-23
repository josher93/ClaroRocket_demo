package com.globalpaysolutions.clarorocket.model;

import java.util.Date;

public class BillPayment
{
    private int id;
    private double payment;
    private double debt;
    private Date date;

    public int getId()
    {
        return id;
    }

    public double getPayment()
    {
        return payment;
    }

    public double getDebt()
    {
        return debt;
    }

    public Date getDate()
    {
        return date;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setPayment(double payment)
    {
        this.payment = payment;
    }

    public void setDebt(double debt)
    {
        this.debt = debt;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }
}
