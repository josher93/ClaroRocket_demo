package com.globalpaysolutions.tigorocket.interactors;

import com.globalpaysolutions.tigorocket.model.rest.RocketPaymentHistoryResponse;
import com.globalpaysolutions.tigorocket.model.rest.RocketPaymentResponse;

import retrofit2.Response;

public interface EstadoCuentaListener
{
    void onGetPaymentsSuccess(RocketPaymentHistoryResponse response);
    void onGetPaymentsError(int responseCode, Throwable throwable);
    void onPaymentSuccess(Response<RocketPaymentResponse> response);
    void onPaymentError(int code, Throwable throwable, String errorResponse);
}
