package com.globalpaysolutions.yovendorecarga.presenters.interfaces;

public interface IEstadoCuentaPresenter
{
    void presentSavedBalance();
    void retrieveBalance();
    void retrievePaymentsHistory();
    void presentSavedHistory();
    void payBill();
    void confirmPayment();
    void sendPayment(String pinCode, int paymentID);
}
