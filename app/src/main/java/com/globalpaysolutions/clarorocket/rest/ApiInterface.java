package com.globalpaysolutions.clarorocket.rest;

import com.globalpaysolutions.clarorocket.customs.StringsURL;
import com.globalpaysolutions.clarorocket.model.rest.RocketBalanceResponse;
import com.globalpaysolutions.clarorocket.model.rest.RocketPaymentHistoryResponse;
import com.globalpaysolutions.clarorocket.model.rest.RocketPaymentReq;
import com.globalpaysolutions.clarorocket.model.rest.RocketPaymentResponse;
import com.globalpaysolutions.clarorocket.model.rest.RocketSaleDetailResponse;

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
