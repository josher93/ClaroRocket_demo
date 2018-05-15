package com.globalpaysolutions.yovendorecarga.interactors;

import com.globalpaysolutions.yovendorecarga.model.SimpleResponse;
import com.globalpaysolutions.yovendorecarga.model.rest.RocketBalanceResponse;

public interface HomeListener
{
    void onRocketBalanceSuccess(RocketBalanceResponse response);
    void onRocketBalanceError(int codeStatus, SimpleResponse errorResponse, Throwable throwable);
}
