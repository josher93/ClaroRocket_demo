package com.globalpaysolutions.yovendorecarga.interactors;

import android.content.Context;
import android.util.Log;

import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.globalpaysolutions.yovendorecarga.interactors.interfaces.IHomeInteractor;
import com.globalpaysolutions.yovendorecarga.model.rest.RocketBalanceResponse;
import com.globalpaysolutions.yovendorecarga.model.rest.RocketSaleDetailResponse;
import com.globalpaysolutions.yovendorecarga.rest.ApiClient;
import com.globalpaysolutions.yovendorecarga.rest.ApiInterface;

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


            /*ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<RocketBalanceResponse> call = apiService.getRocketBalance(mSessionManager.getSavedToken());

            call.enqueue(new Callback<RocketBalanceResponse>()
            {
                @Override
                public void onResponse(Call<RocketBalanceResponse> call, Response<RocketBalanceResponse> response)
                {
                    if(response.isSuccessful())
                    {
                        listener.onRocketBalanceSuccess(response.body());
                    }
                    else
                    {
                        listener.onRocketBalanceError(response.code(), null, null);
                    }
                }

                @Override
                public void onFailure(Call<RocketBalanceResponse> call, Throwable t)
                {
                    listener.onRocketBalanceError(0, null, t);
                }
            });*/
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }
}
