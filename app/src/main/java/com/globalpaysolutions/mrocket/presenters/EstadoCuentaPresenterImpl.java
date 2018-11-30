package com.globalpaysolutions.mrocket.presenters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.globalpaysolutions.mrocket.R;
import com.globalpaysolutions.mrocket.customs.Parsers;
import com.globalpaysolutions.mrocket.customs.SessionManager;
import com.globalpaysolutions.mrocket.interactors.EstadoCuentaInteractor;
import com.globalpaysolutions.mrocket.interactors.EstadoCuentaListener;
import com.globalpaysolutions.mrocket.interactors.HomeInteractor;
import com.globalpaysolutions.mrocket.interactors.HomeListener;
import com.globalpaysolutions.mrocket.model.rest.RocketPaymentHistoryResponse;
import com.globalpaysolutions.mrocket.model.rest.RocketPaymentResponse;
import com.globalpaysolutions.mrocket.model.rest.RocketSaleDetailResponse;
import com.globalpaysolutions.mrocket.presenters.interfaces.IEstadoCuentaPresenter;
import com.globalpaysolutions.mrocket.views.EstadoCuentaView;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

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
            float reconcilableAccount = mSessionManager.getReconcileAccount();
            float sales = mSessionManager.getRocketSale();
            float receivableAccount = mSessionManager.getReceivableAccount();

            float profit = sales - reconcilableAccount;

            String profitDisplay = "$ ".concat(mFormatter.format(Parsers.floatToDouble(profit)));
            String conciliation = "$ ".concat(mFormatter.format(reconcilableAccount));
            String sold = "$ ".concat(mFormatter.format(sales));
            String recivableAccount = "$ ".concat(mFormatter.format(receivableAccount));

            mView.presentBalance(profitDisplay, recivableAccount, conciliation, sold, mSessionManager.getBalanceID());

            if(receivableAccount <= 0)
            {
                mView.noPaymentsPending();
            }
            /*else
            {
                String dateFrom = dateFormatter(mSessionManager.getMinDate());
                String dateTo = dateFormatter(mSessionManager.getMaxDate());

                String dates = String.format(mContext.getString(R.string.label_date_from_date_to), dateFrom, dateTo);

                mView.setDatesRange(dates);
            }*/

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
    public void confirmPayment(int balanceID)
    {
        try
        {
            mView.showPinCodeInputDialgo(balanceID);
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

            mView.presentBalance(profitDisplay, recivableAccount, conciliation, sold, mSessionManager.getBalanceID());

            if(receivableAccount <= 0)
            {
                mView.noPaymentsPending();
            }
            else
            {

            }
            /*else
            {
                String dateFrom = dateFormatter(response.getMinDate());
                String dateTo = dateFormatter(response.getMaxDate());

                String dates = String.format(mContext.getString(R.string.label_date_from_date_to), dateFrom, dateTo);

                mView.setDatesRange(dates);
            }*/
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
        {
            mView.clearListview();
            mView.showGenericDialog("Éxito", "El pago se realizó correctamente", "ACEPTAR", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.dismiss();
                    mInteractor.retrievePaymentsHistory(EstadoCuentaPresenterImpl.this);
                }
            });
        }
        else
            mView.showGenericDialog("Algo salió mal", "Por favor inténtelo nuevamente o más tarde.", "ACEPTAR", null);
    }

    @Override
    public void onPaymentError(int code, Throwable throwable, String errorResponse)
    {
        try
        {
            mView.hideLoadingDialog();

            if(errorResponse != null)
                Log.i(TAG, errorResponse);

            mView.showGenericDialog("Algo salió mal", "Por favor inténtelo nuevamente o más tarde.", "ACEPTAR", null);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    private String dateFormatter(String stringDate)
    {
        String date = "";
        try
        {
            DateFormat originalformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat newFormat = new SimpleDateFormat("d'-'MMM'-'yy", new Locale("es", "ES"));

            String reformattedStr = newFormat.format(originalformat.parse(stringDate));
            date = reformattedStr.toUpperCase();
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }

        return date;
    }
}