package com.globalpaysolutions.clarorocket.interactors.interfaces;

import com.globalpaysolutions.clarorocket.interactors.EstadoCuentaListener;

public interface IEstadoCuentaInteractor
{
    void retrievePaymentsHistory(EstadoCuentaListener listener);
    void sendPayment(String pinCode, int paymentID, EstadoCuentaListener listener);
}
