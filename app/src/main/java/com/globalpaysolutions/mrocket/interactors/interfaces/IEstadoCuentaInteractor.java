package com.globalpaysolutions.mrocket.interactors.interfaces;

import com.globalpaysolutions.mrocket.interactors.EstadoCuentaListener;

public interface IEstadoCuentaInteractor
{
    void retrievePaymentsHistory(EstadoCuentaListener listener);
    void sendPayment(String pinCode, int paymentID, EstadoCuentaListener listener);
}
