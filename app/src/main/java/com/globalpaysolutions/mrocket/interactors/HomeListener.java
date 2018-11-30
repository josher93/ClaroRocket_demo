package com.globalpaysolutions.mrocket.interactors;

import com.globalpaysolutions.mrocket.model.SimpleResponse;
import com.globalpaysolutions.mrocket.model.rest.RocketBalanceResponse;
import com.globalpaysolutions.mrocket.model.rest.RocketSaleDetailResponse;

public interface HomeListener
{
/*    void onRocketBalanceSuccess(RocketBalanceResponse response);
    void onRocketBalanceError(int codeStatus, SimpleResponse errorResponse, Throwable throwable);*/
    void onSaleDetailSuccess(RocketSaleDetailResponse response);
    void onSaleDetailError(int code, Throwable throwable, String response);
}
