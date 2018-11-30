package com.globalpaysolutions.mrocket.interactors;

import com.globalpaysolutions.mrocket.model.rest.RocketPaymentHistoryResponse;
import com.globalpaysolutions.mrocket.model.rest.RocketPaymentResponse;

import retrofit2.Response;

public interface EstadoCuentaListener
{
    void onGetPaymentsSuccess(RocketPaymentHistoryResponse response);
    void onGetPaymentsError(int responseCode, Throwable throwable);
    void onPaymentSuccess(Response<RocketPaymentResponse> response);
    void onPaymentError(int code, Throwable throwable, String errorResponse);
}
