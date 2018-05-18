package com.globalpaysolutions.yovendorecarga;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.model.rest.RocketBalanceList;
import com.globalpaysolutions.yovendorecarga.presenters.EstadoCuentaPresenterImpl;
import com.globalpaysolutions.yovendorecarga.views.EstadoCuentaView;

import java.util.List;

public class FragmentEstadoCuenta extends Fragment implements EstadoCuentaView
{
    private static final String TAG = FragmentEstadoCuenta.class.getSimpleName();

    TextView tvSaleAmount;
    TextView tvComissionAmount;
    TextView tvTotalPayment;
    TextView tvNoRecivableAccounts;
    TextView lblTotalPayment;
    LinearLayout lnlTotalPayment;
    View vDvision;

    Button btnPayBill;
    AlertDialog mPinDialog;
    ProgressDialog mProgressDialog;

    EstadoCuentaPresenterImpl mPresneter;
    private int mCurrentBalanceID;

    public FragmentEstadoCuenta()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_estado_cuenta, container, false);

        tvSaleAmount = (TextView) view.findViewById(R.id.tvSaleAmount);
        tvComissionAmount = (TextView) view.findViewById(R.id.tvComissionAmount);
        tvTotalPayment = (TextView) view.findViewById(R.id.tvTotalPayment);
        tvNoRecivableAccounts = (TextView) view.findViewById(R.id.tvNoRecivableAccounts);
        lblTotalPayment = (TextView) view.findViewById(R.id.lblTotalPayment);
        lnlTotalPayment = (LinearLayout) view.findViewById(R.id.lnlTotalPayment);
        vDvision = view.findViewById(R.id.vDvision);
        btnPayBill = (Button) view.findViewById(R.id.btnPayBill);
        btnPayBill.setOnClickListener(paymentClick);

        mCurrentBalanceID = 0;

        mPresneter = new EstadoCuentaPresenterImpl(getActivity().getApplication(), this);

        mPresneter.presentSavedBalance();
        mPresneter.retrieveBalance();



        return view;
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
        try
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle(title);
            alertDialog.setMessage(content);
            alertDialog.setCancelable(false);
            alertDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
            alertDialog.setNeutralButton("PAGAR", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    mPresneter.confirmPayment();
                }
            });
            alertDialog.show();
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    @Override
    public void setPaymentButtonEnabled(boolean enabled)
    {
        btnPayBill.setEnabled(enabled);
    }

    @Override
    public void showPinCodeInputDialgo()
    {
        try
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_payment_dialog, null);


            TextView tvContent = (TextView) dialogView.findViewById(R.id.tvContentPin);
            final EditText etEnterPin = (EditText) dialogView.findViewById(R.id.etEnterPin);
            Button btnAccept = (Button) dialogView.findViewById(R.id.btnAccept);

            etEnterPin.addTextChangedListener(new TextWatcher()
            {
                int TextLength = 0;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {
                    String str = etEnterPin.getText().toString();
                    TextLength = str.length();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {

                }

                @Override
                public void afterTextChanged(Editable s)
                {
                    String PinText = etEnterPin.getText().toString();

                    if (PinText.length() == 4 && TextLength < PinText.length())
                    {
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                }
            });

            mPinDialog = builder.setView(dialogView).create();
            mPinDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mPinDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mPinDialog.show();

            btnAccept.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mPresneter.sendPayment(etEnterPin.getText().toString(), mCurrentBalanceID);
                }
            });
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
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
    public void showGenericDialog(String title, String content)
    {
        try
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle(title);
            alertDialog.setMessage(content);
            alertDialog.setCancelable(false);
            alertDialog.setNeutralButton("ACEPTAR", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    mPresneter.confirmPayment();
                }
            });
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
    public void displayRecivableGenerated()
    {
        try
        {
            tvNoRecivableAccounts.setVisibility(View.GONE);

            lblTotalPayment.setVisibility(View.VISIBLE);
            lnlTotalPayment.setVisibility(View.VISIBLE);
            tvTotalPayment.setVisibility(View.VISIBLE);
            btnPayBill.setVisibility(View.VISIBLE);
            vDvision.setVisibility(View.VISIBLE);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
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
