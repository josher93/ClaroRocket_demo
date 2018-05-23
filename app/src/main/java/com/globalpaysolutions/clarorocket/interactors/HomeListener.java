package com.globalpaysolutions.clarorocket.interactors;

import com.globalpaysolutions.clarorocket.model.SimpleResponse;
import com.globalpaysolutions.clarorocket.model.rest.RocketBalanceResponse;
import com.globalpaysolutions.clarorocket.model.rest.RocketSaleDetailResponse;

public interface HomeListener
{
/*    void onRocketBalanceSuccess(RocketBalanceResponse response);
    void onRocketBalanceError(int codeStatus, SimpleResponse errorResponse, Throwable throwable);*/
    void onSaleDetailSuccess(RocketSaleDetailResponse response);
    void onSaleDetailError(int code, Throwable throwable, String response);
}
