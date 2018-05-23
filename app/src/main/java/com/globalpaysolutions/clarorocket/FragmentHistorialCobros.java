package com.globalpaysolutions.clarorocket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalpaysolutions.clarorocket.R;
import com.globalpaysolutions.clarorocket.adapters.PaymentsAdapter;
import com.globalpaysolutions.clarorocket.model.rest.RocketBalanceList;
import com.globalpaysolutions.clarorocket.presenters.EstadoCuentaPresenterImpl;
import com.globalpaysolutions.clarorocket.views.EstadoCuentaView;

import java.util.List;

public class FragmentHistorialCobros extends Fragment implements EstadoCuentaView
{
    private static final String TAG = FragmentHistorialCobros.class.getSimpleName();
    ListView lvBillings;
    PaymentsAdapter mAdapter;
    EstadoCuentaPresenterImpl mPresenter;

    Context mContext;
    AlertDialog mPinDialog;
    ProgressDialog mProgressDialog;

    public FragmentHistorialCobros()
    {

    }

    public static FragmentHistorialCobros newInstance()
    {
        return new FragmentHistorialCobros();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_fragment_historial_cobros, container, false);
        lvBillings = (ListView) view.findViewById(R.id.lvBillings);


        mPresenter = new EstadoCuentaPresenterImpl(getActivity().getApplicationContext(), this );
        mAdapter = new PaymentsAdapter(getContext(), R.layout.custom_billing_history_item, mPresenter);
        lvBillings.setAdapter(mAdapter);


        mPresenter.presentSavedHistory();

        if(checkConnection())
            mPresenter.retrievePaymentsHistory();

        return view;
    }


    private boolean checkConnection()
    {
        boolean connected;

        if(!haveNetworkConnection())
        {
            connected = false;
            String connectionMessage = getString(R.string.no_internet_connection);
            Toast.makeText(getContext(), connectionMessage, Toast.LENGTH_LONG).show();
        }
        else
        {
            connected = true;
        }

        return connected;
    }

    private boolean haveNetworkConnection()
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
            {
                if (ni.isConnected())
                {
                    haveConnectedWifi = true;
                }
            }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
            {
                if (ni.isConnected())
                {
                    haveConnectedMobile = true;
                }
            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public void presentBalance(String profit, String receivableAccount, String conciliation, String sold, int balanceID)
    {

    }

    @Override
    public void renderPaymentsHistory(List<RocketBalanceList> rocketBalanceList)
    {
        try
        {
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();

            for(RocketBalanceList item : rocketBalanceList)
            {
                mAdapter.add(item);
            }

            mAdapter.notifyDataSetChanged();
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    @Override
    public void showConfirmPaymentDialog(String title, String content)
    {

    }

    @Override
    public void showPinCodeInputDialgo(final int balanceID)
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
                    mPresenter.sendPayment(etEnterPin.getText().toString(), balanceID);
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
    public void showGenericDialog(String title, String content, String button, DialogInterface.OnClickListener clickListener)
    {
        try
        {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
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
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void noPaymentsPending()
    {

    }

    @Override
    public void setDatesRange(String dates)
    {

    }

}
