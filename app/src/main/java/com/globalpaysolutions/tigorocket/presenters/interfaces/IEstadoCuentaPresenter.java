package com.globalpaysolutions.tigorocket.presenters.interfaces;

public interface IEstadoCuentaPresenter
{
    void presentSavedBalance();
    void retrieveBalance();
    void retrievePaymentsHistory();
    void presentSavedHistory();
    void payBill();
    void confirmPayment(int balanceID);
    void sendPayment(String pinCode, int paymentID);
}
