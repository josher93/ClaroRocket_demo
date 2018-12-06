package com.globalpaysolutions.tigorocket.interactors;

import android.content.Context;
import android.util.Log;

import com.globalpaysolutions.tigorocket.customs.SessionManager;
import com.globalpaysolutions.tigorocket.interactors.interfaces.IHomeInteractor;
import com.globalpaysolutions.tigorocket.model.rest.RocketBalanceResponse;
import com.globalpaysolutions.tigorocket.model.rest.RocketSaleDetailResponse;
import com.globalpaysolutions.tigorocket.rest.ApiClient;
import com.globalpaysolutions.tigorocket.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeInteractor implements IHomeInteractor
{
    private static final String TAG = HomeInteractor.class.getSimpleName();

    private Context mContext;
    private SessionManager mSessionManager;

    public HomeInteractor(Context context)
    {
        this.mContext = context;
        this.mSessionManager = new SessionManager(mContext);
    }

    @Override
    public void retrieveBalance(final HomeListener listener)
    {
        try
        {

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<RocketSaleDetailResponse> call = apiService.getRocketSaleDetail(mSessionManager.getSavedToken());

            call.enqueue(new Callback<RocketSaleDetailResponse>()
            {
                @Override
                public void onResponse(Call<RocketSaleDetailResponse> call, Response<RocketSaleDetailResponse> response)
                {
                    if(response.isSuccessful())
                    {
                        listener.onSaleDetailSuccess(response.body());
                    }
                    else
                    {
                        try
                        {
                            listener.onSaleDetailError(response.code(), null, response.errorBody().string());
                        }
                        catch (Exception ex){   Log.e(TAG, "Error: " + ex.getMessage()); }
                    }
                }

                @Override
                public void onFailure(Call<RocketSaleDetailResponse> call, Throwable t)
                {
                    listener.onSaleDetailError(0, t, "");
                }
            });



        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }
}
