package com.globalpaysolutions.clarorocket.presenters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.globalpaysolutions.clarorocket.customs.Parsers;
import com.globalpaysolutions.clarorocket.customs.SessionManager;
import com.globalpaysolutions.clarorocket.interactors.HomeInteractor;
import com.globalpaysolutions.clarorocket.interactors.HomeListener;
import com.globalpaysolutions.clarorocket.model.rest.RocketSaleDetailResponse;
import com.globalpaysolutions.clarorocket.presenters.interfaces.IHomePresenter;
import com.globalpaysolutions.clarorocket.views.HomeView;

import java.text.DecimalFormat;

public class HomePresenterImpl implements IHomePresenter, HomeListener
{
    private static final String TAG = HomePresenterImpl.class.getSimpleName();
    private Context mContext;
    private HomeView mView;
    private HomeInteractor mInteractor;
    private SessionManager mSessionManager;
    private DecimalFormat mFormatter;

    public HomePresenterImpl(Context context, AppCompatActivity activity, HomeView view)
    {
        mContext = context;
        mView = view;
        mInteractor = new HomeInteractor(mContext);
        mSessionManager = new SessionManager(mContext);

        mFormatter = new DecimalFormat("#####.##");
        mFormatter.setDecimalSeparatorAlwaysShown(true);
        mFormatter.setMinimumFractionDigits(2);
    }

    @Override
    public void presentSavedBalance()
    {
        try
        {
            float reconcilableAccount = mSessionManager.getReconcileAccount();
            float sales = mSessionManager.getRocketSale();

            float profit = sales - reconcilableAccount;

            String profitDisplay = "$ ".concat(mFormatter.format(Parsers.floatToDouble(profit)));
            String balance = "$ ".concat(mFormatter.format(reconcilableAccount));
            String sold = "$ ".concat(mFormatter.format(sales));

            mView.displayBalance(profitDisplay, sold, balance);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    @Override
    public void retrieveRocketBalance()
    {
        mInteractor.retrieveBalance(this);
    }



    @Override
    public void onSaleDetailSuccess(RocketSaleDetailResponse response)
    {
        mSessionManager.saveSaleDetail(response.getBalanceID(), response.getPersonMasterID(), response.getReceivable(), response.getReconcileCount(), response.getSale(), response.getDistributor(), response.getName(), response.getMinDate(), response.getMaxDate());

        float reconcilableAccount = mSessionManager.getReconcileAccount();
        float sales = mSessionManager.getRocketSale();

        float profit = sales - reconcilableAccount;

        String profitDisplay = "$ ".concat(mFormatter.format(Parsers.floatToDouble(profit)));
        String balance = "$ ".concat(mFormatter.format(reconcilableAccount));
        String sold = "$ ".concat(mFormatter.format(sales));

        mView.displayBalance(profitDisplay, sold, balance);
    }

    @Override
    public void onSaleDetailError(int code, Throwable throwable, String response)
    {
        if(response != null)
            Log.e(TAG, "Error: code: " + String.valueOf(code) + ", Response: " + response);
        else
            Log.e(TAG, "Error: code " + String.valueOf(code));
    }
}