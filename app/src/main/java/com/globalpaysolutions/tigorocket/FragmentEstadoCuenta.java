package com.globalpaysolutions.tigorocket;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.globalpaysolutions.tigorocket.R;
import com.globalpaysolutions.tigorocket.model.rest.RocketBalanceList;
import com.globalpaysolutions.tigorocket.presenters.EstadoCuentaPresenterImpl;
import com.globalpaysolutions.tigorocket.views.EstadoCuentaView;

import java.util.List;

public class FragmentEstadoCuenta extends Fragment implements EstadoCuentaView
{
    private static final String TAG = FragmentEstadoCuenta.class.getSimpleName();

    AppCompatActivity mActivity;
    TextView tvSaleAmount;
    TextView tvComissionAmount;
    TextView tvTotalPayment;
    TextView lblTotalPayment;
    //TextView tvDatesRange;
    //TextView lblPaymentGenerated;
    LinearLayout lnlTotalPayment;
    View vDvision;

    AlertDialog mPinDialog;
    ProgressDialog mProgressDialog;
    Context mContext;
    EstadoCuentaPresenterImpl mPresneter;
    private int mCurrentBalanceID;

    public FragmentEstadoCuenta()
    {

    }

    public static FragmentEstadoCuenta newInstance()
    {
        return new FragmentEstadoCuenta();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_estado_cuenta, container, false);

        tvSaleAmount = (TextView) view.findViewById(R.id.tvSaleAmount);
        tvComissionAmount = (TextView) view.findViewById(R.id.tvComissionAmount);
        tvTotalPayment = (TextView) view.findViewById(R.id.tvTotalPayment);
        lblTotalPayment = (TextView) view.findViewById(R.id.lblTotalPayment);
        lnlTotalPayment = (LinearLayout) view.findViewById(R.id.lnlTotalPayment);
        vDvision = view.findViewById(R.id.vDvision);

        mCurrentBalanceID = 0;

        mPresneter = new EstadoCuentaPresenterImpl(getActivity().getApplication(), this);

        mPresneter.presentSavedBalance();
        mPresneter.retrieveBalance();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }


    @Override
    public void presentBalance(String profit, String receivableAccount, String conciliation, String sold, int balanceID)
    {
        tvSaleAmount.setText(sold);
        tvComissionAmount.setText(profit);
        tvTotalPayment.setText(receivableAccount);
        mCurrentBalanceID = balanceID;
    }

    @Override
    public void renderPaymentsHistory(List<RocketBalanceList> rocketBalanceList)
    {

    }

    @Override
    public void showConfirmPaymentDialog(String title, String content)
    {

    }


    @Override
    public void showPinCodeInputDialgo(int balanceID)
    {

    }

    @Override
    public void dismissPinCodeInputDialog()
    {
        try
        {
            if(mPinDialog != null)
            {
                if(mPinDialog.isShowing())
                    mPinDialog.dismiss();
            }

        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    @Override
    public void showGenericDialog(String title, String content, String button, DialogInterface.OnClickListener clickListener)
    {
        try
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle(title);
            alertDialog.setMessage(content);
            alertDialog.setCancelable(false);
            if(clickListener == null)
            {
                alertDialog.setNeutralButton(button, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
            }
            else
            {
                alertDialog.setNeutralButton(button, clickListener);
            }

            alertDialog.show();
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    @Override
    public void showLoadingDialg(String label)
    {
        try
        {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(label);
            mProgressDialog.show();
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void hideLoadingDialog()
    {
        try
        {
            try
            {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                {
                    mProgressDialog.dismiss();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    @Override
    public void clearListview()
    {

    }

    @Override
    public void noPaymentsPending()
    {
        lblTotalPayment.setVisibility(View.GONE);
        tvTotalPayment.setVisibility(View.GONE);

        lnlTotalPayment.setVisibility(View.GONE);
        //tvDatesRange.setVisibility(View.GONE);
        //lblPaymentGenerated.setVisibility(View.GONE);

    }

    @Override
    public void setDatesRange(String dates)
    {
        //if(dates != null)
            //tvDatesRange.setText(dates);
    }


    View.OnClickListener paymentClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            mPresneter.payBill();
        }
    };

}
