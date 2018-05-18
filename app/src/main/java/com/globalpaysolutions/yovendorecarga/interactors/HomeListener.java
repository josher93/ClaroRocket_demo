package com.globalpaysolutions.yovendorecarga.interactors;

import com.globalpaysolutions.yovendorecarga.model.SimpleResponse;
import com.globalpaysolutions.yovendorecarga.model.rest.RocketBalanceResponse;
import com.globalpaysolutions.yovendorecarga.model.rest.RocketSaleDetailResponse;

public interface HomeListener
{
/*    void onRocketBalanceSuccess(RocketBalanceResponse response);
    void onRocketBalanceError(int codeStatus, SimpleResponse errorResponse, Throwable throwable);*/
    void onSaleDetailSuccess(RocketSaleDetailResponse response);
    void onSaleDetailError(int code, Throwable throwable, String response);
}
