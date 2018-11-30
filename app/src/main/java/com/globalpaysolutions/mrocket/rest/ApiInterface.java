package com.globalpaysolutions.mrocket.rest;

import com.globalpaysolutions.mrocket.customs.StringsURL;
import com.globalpaysolutions.mrocket.model.rest.RocketBalanceResponse;
import com.globalpaysolutions.mrocket.model.rest.RocketPaymentHistoryResponse;
import com.globalpaysolutions.mrocket.model.rest.RocketPaymentReq;
import com.globalpaysolutions.mrocket.model.rest.RocketPaymentResponse;
import com.globalpaysolutions.mrocket.model.rest.RocketSaleDetailResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface
{
    /*@Headers("Content-Type: application/json")
    @GET(StringsURL.ROCKET_BALANCE)
    Call<RocketBalanceResponse> getRocketBalance(@Header("Token-Autorization") String token);*/

    @Headers("Content-Type: application/json")
    @GET(StringsURL.ROCKET_PAYMENT)
    Call<RocketPaymentHistoryResponse> getRocketPaymentHistory(@Header("Token-Autorization") String savedToken);

    @Headers("Content-Type: application/json")
    @POST(StringsURL.ROCKET_BALANCE_PAYMENT)
    Call<RocketPaymentResponse> rocketBalancePayment(@Header("Token-Autorization") String savedToken,
                                                     @Body RocketPaymentReq request);

    @Headers("Content-Type: application/json")
    @GET(StringsURL.ROCKET_SALE_DETAIL)
    Call<RocketSaleDetailResponse> getRocketSaleDetail(@Header("Token-Autorization") String token);
}
