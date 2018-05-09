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
import com.globalpaysolutions.yovendorecarga.model.BillPayment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FragmentHistorialCobros extends Fragment
{
    private static final String TAG = FragmentHistorialCobros.class.getSimpleName();
    ListView lvBillings;
    PaymentsAdapter mAdapter;

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

        if(checkConnection())
            requestPaymentsHistory();

        return view;
    }

    private void requestPaymentsHistory()
    {
        try
        {
            List<BillPayment> paymentList = new ArrayList<>();

            BillPayment payment3 = new BillPayment();
            payment3.setId(1);
            payment3.setPayment(10.25);
            payment3.setDebt(0);
            payment3.setDate(new Date());
            paymentList.add(payment3);

            BillPayment payment2 = new BillPayment();
            payment2.setId(1);
            payment2.setPayment(60.54);
            payment2.setDebt(0);
            payment2.setDate(new Date());
            paymentList.add(payment2);

            BillPayment payment1 = new BillPayment();
            payment1.setId(1);
            payment1.setPayment(183.34);
            payment1.setDebt(0);
            payment1.setDate(new Date());
            paymentList.add(payment1);

            BillPayment payment4 = new BillPayment();
            payment4.setId(1);
            payment4.setPayment(254.64);
            payment4.setDebt(0);
            payment4.setDate(new Date());
            paymentList.add(payment4);

            for (BillPayment item : paymentList)
            {
                mAdapter.add(item);
            }

        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
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
}
