package com.globalpaysolutions.yovendorecarga.presenters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.globalpaysolutions.yovendorecarga.interactors.HomeInteractor;
import com.globalpaysolutions.yovendorecarga.interactors.HomeListener;
import com.globalpaysolutions.yovendorecarga.model.SimpleResponse;
import com.globalpaysolutions.yovendorecarga.model.rest.RocketBalanceResponse;
import com.globalpaysolutions.yovendorecarga.presenters.interfaces.IHomePresenter;
import com.globalpaysolutions.yovendorecarga.views.HomeView;

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
            String profit = "$ ".concat(mFormatter.format(mSessionManager.getRocketPtofit()));
            String balance = "$ ".concat(mFormatter.format(mSessionManager.getRocketBalance()));

            mView.displayBalance(profit, "", balance);
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
    public void onRocketBalanceSuccess(RocketBalanceResponse response)
    {
        try
        {
            mSessionManager.saveRocketBalanceData(response.getBalanceID(),
                    response.getBalanceAmount(), response.getFromDate(), response.getToDate(),
                    response.getStatus(), response.getConciliationDate(), response.getProfit() );

            String profit = "$ ".concat(mFormatter.format(mSessionManager.getRocketPtofit()));
            String balance = "$ ".concat(mFormatter.format(mSessionManager.getRocketBalance()));

            mView.displayBalance(profit, "", balance);

        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    @Override
    public void onRocketBalanceError(int codeStatus, SimpleResponse errorResponse, Throwable throwable)
    {
        Log.e(TAG, "Error retrieving balances"); //TODO: Manejo de excepcion
    }
}
