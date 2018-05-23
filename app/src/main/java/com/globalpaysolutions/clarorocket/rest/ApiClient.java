package com.globalpaysolutions.clarorocket.rest;

import com.globalpaysolutions.clarorocket.customs.StringsURL;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Josué Chávez on 28/11/2016.
 */
public class ApiClient
{

    private static Retrofit retrofit = null;
    private static Retrofit topupRetrofit;

    public static Retrofit getTopupClient()
    {
        if (topupRetrofit==null)
        {
            topupRetrofit = new Retrofit.Builder()
                    .baseUrl(StringsURL.TOPUP)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return topupRetrofit;
    }

    public static Retrofit getClient()
    {
        if (retrofit==null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(StringsURL.URL_BASE)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(40, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(40, TimeUnit.SECONDS)
            .build();
}
