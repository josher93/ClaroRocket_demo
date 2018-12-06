package com.globalpaysolutions.tigorocket.interactors.interfaces;

import com.globalpaysolutions.tigorocket.interactors.EstadoCuentaListener;

public interface IEstadoCuentaInteractor
{
    void retrievePaymentsHistory(EstadoCuentaListener listener);
    void sendPayment(String pinCode, int paymentID, EstadoCuentaListener listener);
}
