package com.globalpaysolutions.tigorocket.interactors;

import android.content.Context;
import android.util.Log;

import com.globalpaysolutions.tigorocket.customs.SessionManager;
import com.globalpaysolutions.tigorocket.interactors.interfaces.IEstadoCuentaInteractor;
import com.globalpaysolutions.tigorocket.model.rest.GenericResponse;
import com.globalpaysolutions.tigorocket.model.rest.RocketPaymentHistoryResponse;
import com.globalpaysolutions.tigorocket.model.rest.RocketPaymentReq;
import com.globalpaysolutions.tigorocket.model.rest.RocketPaymentResponse;
import com.globalpaysolutions.tigorocket.rest.ApiClient;
import com.globalpaysolutions.tigorocket.rest.ApiInterface;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstadoCuentaInteractor implements IEstadoCuentaInteractor
{
    private static final String TAG = EstadoCuentaInteractor.class.getSimpleName();

    private Context mContext;
    private SessionManager mSessionManager;

    public EstadoCuentaInteractor(Context context)
    {
        mContext = context;
        mSessionManager = new SessionManager(mContext);
    }
    @Override
    public void retrievePaymentsHistory(final EstadoCuentaListener listener)
    {
        try
        {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<RocketPaymentHistoryResponse> call = apiService.getRocketPaymentHistory(mSessionManager.getSavedToken());

            call.enqueue(new Callback<RocketPaymentHistoryResponse>()
            {
                @Override
                public void onResponse(Call<RocketPaymentHistoryResponse> call, Response<RocketPaymentHistoryResponse> response)
                {
                    if(response.isSuccessful())
                        listener.onGetPaymentsSuccess(response.body());
                    else
                        listener.onGetPaymentsError(response.code(), null);
                }

                @Override
                public void onFailure(Call<RocketPaymentHistoryResponse> call, Throwable t)
                {
                    listener.onGetPaymentsError(0, t);
                }
            });
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    @Override
    public void sendPayment(String pinCode, int paymentID, final EstadoCuentaListener listener)
    {
        try
        {
            RocketPaymentReq request = new RocketPaymentReq();
            request.setBalanceID(String.valueOf(paymentID));
            request.setSecurityPin(pinCode);

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<RocketPaymentResponse> call = apiService.rocketBalancePayment(mSessionManager.getSavedToken(), request);

            call.enqueue(new Callback<RocketPaymentResponse>()
            {
                @Override
                public void onResponse(Call<RocketPaymentResponse> call, Response<RocketPaymentResponse> response)
                {
                    if(response.isSuccessful())
                    {
                        listener.onPaymentSuccess(response);
                    }
                    else
                    {
                        try
                        {
                            listener.onPaymentError(response.code(), null, response.errorBody().string());
                        }
                        catch (Exception ex)
                        {
                            Log.e(TAG, "Error: " + ex.getMessage());
                        }
                    }

                }

                @Override
                public void onFailure(Call<RocketPaymentResponse> call, Throwable t)
                {
                    listener.onPaymentError(0, t, null);
                }
            });
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }
}
