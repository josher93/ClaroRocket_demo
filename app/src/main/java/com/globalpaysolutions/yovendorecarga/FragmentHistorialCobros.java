package com.globalpaysolutions.yovendorecarga;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.adapters.PaymentsAdapter;
import com.globalpaysolutions.yovendorecarga.model.rest.RocketBalanceList;
import com.globalpaysolutions.yovendorecarga.presenters.EstadoCuentaPresenterImpl;
import com.globalpaysolutions.yovendorecarga.views.EstadoCuentaView;

import java.util.List;

public class FragmentHistorialCobros extends Fragment implements EstadoCuentaView
{
    private static final String TAG = FragmentHistorialCobros.class.getSimpleName();
    ListView lvBillings;
    PaymentsAdapter mAdapter;
    EstadoCuentaPresenterImpl mPresenter;

    public FragmentHistorialCobros()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_fragment_historial_cobros, container, false);
        lvBillings = (ListView) view.findViewById(R.id.lvBillings);

        mAdapter = new PaymentsAdapter(getContext(), R.layout.custom_billing_history_item);
        lvBillings.setAdapter(mAdapter);

        mPresenter = new EstadoCuentaPresenterImpl(getActivity().getApplicationContext(), this);
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
            for(RocketBalanceList item : rocketBalanceList)
            {
                mAdapter.add(item);
            }
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
    public void setPaymentButtonEnabled(boolean enabled)
    {

    }

    @Override
    public void showPinCodeInputDialgo()
    {

    }

    @Override
    public void dismissPinCodeInputDialog()
    {

    }

    @Override
    public void showGenericDialog(String title, String content)
    {

    }

    @Override
    public void showLoadingDialg(String label)
    {

    }

    @Override
    public void hideLoadingDialog()
    {

    }

    @Override
    public void displayRecivableGenerated()
    {

    }
}
