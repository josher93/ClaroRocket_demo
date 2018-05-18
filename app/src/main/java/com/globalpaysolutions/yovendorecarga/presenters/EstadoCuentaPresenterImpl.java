package com.globalpaysolutions.yovendorecarga.presenters;

import android.content.Context;
import android.util.Log;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.customs.Parsers;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.globalpaysolutions.yovendorecarga.interactors.EstadoCuentaInteractor;
import com.globalpaysolutions.yovendorecarga.interactors.EstadoCuentaListener;
import com.globalpaysolutions.yovendorecarga.interactors.HomeInteractor;
import com.globalpaysolutions.yovendorecarga.interactors.HomeListener;
import com.globalpaysolutions.yovendorecarga.model.rest.RocketPaymentHistoryResponse;
import com.globalpaysolutions.yovendorecarga.model.rest.RocketPaymentResponse;
import com.globalpaysolutions.yovendorecarga.model.rest.RocketSaleDetailResponse;
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
            mView.setPaymentButtonEnabled(false);

            float reconcilableAccount = mSessionManager.getReconcileAccount();
            float sales = mSessionManager.getRocketSale();
            float receivableAccount = mSessionManager.getReceivableAccount();

            float profit = sales - reconcilableAccount;

            String profitDisplay = "$ ".concat(mFormatter.format(Parsers.floatToDouble(profit)));
            String conciliation = "$ ".concat(mFormatter.format(reconcilableAccount));
            String sold = "$ ".concat(mFormatter.format(sales));
            String recivableAccount = "$ ".concat(mFormatter.format(receivableAccount));

            mView.presentBalance(profitDisplay, recivableAccount, conciliation, sold, mSessionManager.getBalanceID());

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
    public void onSaleDetailSuccess(RocketSaleDetailResponse response)
    {
        try
        {
            mSessionManager.saveSaleDetail(response.getBalanceID(), response.getPersonMasterID(), response.getReceivable(), response.getReconcileCount(), response.getSale(), response.getDistributor(), response.getName(), response.getMinDate(), response.getMaxDate());

            float reconcilableAccount = mSessionManager.getReconcileAccount(); //CxConciliar
            float sales = mSessionManager.getRocketSale();
            float receivableAccount = mSessionManager.getReceivableAccount(); //CxCobrar

            float profit = sales - reconcilableAccount;

            String profitDisplay = "$ ".concat(mFormatter.format(Parsers.floatToDouble(profit)));
            String conciliation = "$ ".concat(mFormatter.format(reconcilableAccount));
            String sold = "$ ".concat(mFormatter.format(sales));
            String recivableAccount = "$ ".concat(mFormatter.format(receivableAccount));

            if(receivableAccount > 0)
            {
                mView.setPaymentButtonEnabled(true);
                mView.presentBalance(profitDisplay, recivableAccount, conciliation, sold, mSessionManager.getBalanceID());
                mView.displayRecivableGenerated();
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    @Override
    public void onSaleDetailError(int code, Throwable throwable, String response)
    {

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