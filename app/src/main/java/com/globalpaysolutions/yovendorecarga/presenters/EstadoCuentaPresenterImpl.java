package com.globalpaysolutions.yovendorecarga.presenters;

import android.content.Context;
import android.util.Log;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.globalpaysolutions.yovendorecarga.interactors.EstadoCuentaInteractor;
import com.globalpaysolutions.yovendorecarga.interactors.EstadoCuentaListener;
import com.globalpaysolutions.yovendorecarga.interactors.HomeInteractor;
import com.globalpaysolutions.yovendorecarga.interactors.HomeListener;
import com.globalpaysolutions.yovendorecarga.model.SimpleResponse;
import com.globalpaysolutions.yovendorecarga.model.rest.GenericResponse;
import com.globalpaysolutions.yovendorecarga.model.rest.RocketBalanceResponse;
import com.globalpaysolutions.yovendorecarga.model.rest.RocketPaymentHistoryResponse;
import com.globalpaysolutions.yovendorecarga.model.rest.RocketPaymentResponse;
import com.globalpaysolutions.yovendorecarga.presenters.interfaces.IEstadoCuentaPresenter;
import com.globalpaysolutions.yovendorecarga.views.EstadoCuentaView;
import com.google.gson.Gson;

import java.text.DecimalFormat;

import retrofit2.Response;

public class EstadoCuentaPresenterImpl implements IEstadoCuentaPresenter, HomeListener, EstadoCuentaListener
{
    private static final String TAG = EstadoCuentaPresenterImpl.class.getSimpleName();

    private Context mContext;
    private EstadoCuentaView mView;
    private SessionManager mSessionManager;
    private HomeInteractor mHomeInteractor;
    private EstadoCuentaInteractor mInteractor;
    private DecimalFormat mFormatter;

    public EstadoCuentaPresenterImpl(Context context, EstadoCuentaView view)
    {
        mContext = context;
        mView = view;
        mSessionManager = new SessionManager(mContext);
        mHomeInteractor = new HomeInteractor(mContext);
        mInteractor = new EstadoCuentaInteractor(mContext);

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

            mView.setPaymentButtonEnabled(false);
            mView.presentBalance(profit, balance, mSessionManager.getBalanceID());

        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    @Override
    public void retrieveBalance()
    {
        mHomeInteractor.retrieveBalance(this);
    }

    @Override
    public void retrievePaymentsHistory()
    {
        mInteractor.retrievePaymentsHistory(this);
    }

    @Override
    public void presentSavedHistory()
    {
        try
        {
            String savedHistory = mSessionManager.getPaymentsHistoryData();
            Gson gson = new Gson();

            RocketPaymentHistoryResponse payemntsResponse = gson.fromJson(savedHistory, RocketPaymentHistoryResponse.class);
            mView.renderPaymentsHistory(payemntsResponse.getHistory().getRocketBalanceList());

        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    @Override
    public void payBill()
    {
        String title = "Realizar Pago";
        String content = "¿Desea realizar el pago ahora?";

        mView.showConfirmPaymentDialog(title, content);
    }

    @Override
    public void confirmPayment()
    {
        try
        {
            mView.showPinCodeInputDialgo();
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    @Override
    public void sendPayment(String pinCode, int paymentID)
    {
        mInteractor.sendPayment(pinCode, paymentID, this );
        mView.dismissPinCodeInputDialog();
        mView.showLoadingDialg(mContext.getString(R.string.general_text_wait));
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

            mView.presentBalance(profit, balance, mSessionManager.getBalanceID());
            mView.setPaymentButtonEnabled(true);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    @Override
    public void onRocketBalanceError(int codeStatus, SimpleResponse errorResponse, Throwable throwable)
    {
        Log.e(TAG, "Error retrieving balances"); //TODO: Manejar excepcion
    }

    @Override
    public void onGetPaymentsSuccess(RocketPaymentHistoryResponse response)
    {
        try
        {
            if(response != null)
            {
                mSessionManager.savePaymentsHistoryData(response.toString());
                mView.renderPaymentsHistory(response.getHistory().getRocketBalanceList());
            }

        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    @Override
    public void onGetPaymentsError(int responseCode, Throwable throwable)
    {
        //TODO: manejar error
    }

    @Override
    public void onPaymentSuccess(Response<RocketPaymentResponse> response)
    {
        mView.hideLoadingDialog();
        Log.i(TAG, response.body().toString());

        mHomeInteractor.retrieveBalance(this);

        if(response.body().isStatus())
            mView.showGenericDialog("Éxito", "El pago se realizó correctamente");
        else
            mView.showGenericDialog("Algo salió mal", "Por favor inténtelo nuevamente o más tarde.");
    }

    @Override
    public void onPaymentError(int code, Throwable throwable, String errorResponse)
    {
        try
        {
            mView.hideLoadingDialog();

            //Gson gson = new Gson();
            //GenericResponse response = gson.fromJson(errorResponse, GenericResponse.class);
            if(errorResponse != null)
                Log.i(TAG, errorResponse);

            mView.showGenericDialog("Algo salió mal", "Por favor inténtelo nuevamente o más tarde.");
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }
}