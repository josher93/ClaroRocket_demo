package com.globalpaysolutions.tigorocket.interactors;

import com.globalpaysolutions.tigorocket.model.SimpleResponse;
import com.globalpaysolutions.tigorocket.model.rest.RocketBalanceResponse;
import com.globalpaysolutions.tigorocket.model.rest.RocketSaleDetailResponse;

public interface HomeListener
{
/*    void onRocketBalanceSuccess(RocketBalanceResponse response);
    void onRocketBalanceError(int codeStatus, SimpleResponse errorResponse, Throwable throwable);*/
    void onSaleDetailSuccess(RocketSaleDetailResponse response);
    void onSaleDetailError(int code, Throwable throwable, String response);
}
