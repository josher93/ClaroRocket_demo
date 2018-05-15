package com.globalpaysolutions.yovendorecarga.interactors.interfaces;

import com.globalpaysolutions.yovendorecarga.interactors.EstadoCuentaListener;

public interface IEstadoCuentaInteractor
{
    void retrievePaymentsHistory(EstadoCuentaListener listener);
    void sendPayment(String pinCode, int paymentID, EstadoCuentaListener listener);
}
