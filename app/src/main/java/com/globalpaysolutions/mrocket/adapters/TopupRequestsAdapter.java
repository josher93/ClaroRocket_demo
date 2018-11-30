package com.globalpaysolutions.mrocket.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.globalpaysolutions.mrocket.R;
import com.globalpaysolutions.mrocket.customs.RequestTopupListener;
import com.globalpaysolutions.mrocket.customs.SessionManager;
import com.globalpaysolutions.mrocket.model.PendingRequestItem;
import com.globalpaysolutions.mrocket.model.TopupRequest;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Josu� Ch�vez on 17/03/2017.
 */

public class TopupRequestsAdapter extends ArrayAdapter<PendingRequestItem>
{
    Context mContext;
    int mAdapterResource;
    public int mSelectedItem;
    private SessionManager sessionManager;
    private RequestTopupListener mListener;

    public TopupRequestsAdapter(Context pContext, int pResource, RequestTopupListener pListener)
    {
        super(pContext, pResource);

        this.mContext = pContext;
        this.mAdapterResource = pResource;
        this.sessionManager = new SessionManager(mContext);
        this.mListener = pListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;

        final PendingRequestItem currentItem = getItem(position);

        if(row == null)
        {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mAdapterResource, parent, false);
        }

        row.setTag(currentItem);


        try
        {
            final TextView tvTargetPhone = (TextView) row.findViewById(R.id.tvSalePhoneNumber);
            final TextView tvAmount = (TextView) row.findViewById(R.id.tvSaleAmount);
            final TextView tvDate = (TextView) row.findViewById(R.id.tvSaleDate);
            final TextView btnAcceptTopup = (Button) row.findViewById(R.id.btnAcceptTopup);
            final TextView btnDenyTopup = (Button) row.findViewById(R.id.btnDenyTopup);
            final TextView tvUserNickname = (TextView) row.findViewById(R.id.tvUserNickname);

            //Número de Teléfono
            String PhoneNumber = currentItem.getPhoneNumber();
            PhoneNumber = PhoneNumber.substring(3);
            if(!"".equals(PhoneNumber))
            {
                PhoneNumber = PhoneNumber.substring(0,4) + "-" + PhoneNumber.substring(4,PhoneNumber.length());
            }



            String Amount;
            DecimalFormat formatter = new DecimalFormat("###.##");
            formatter.setDecimalSeparatorAlwaysShown(true);
            formatter.setMinimumFractionDigits(2);
            Amount = currentItem.getAmount();
            String currency = sessionManager.getMoneySymbol();

            String Operator = currentItem.getOperatorName().equals("null") ? "" : currentItem.getOperatorName().toUpperCase() + " - ";

            tvUserNickname.setText(currentItem.getNickname());
            tvTargetPhone.setText(PhoneNumber);
            tvAmount.setText(Operator + currency + " " + Amount);
            tvDate.setText(new SimpleDateFormat("d'-'MMM'-'yy';' h:mm a", new Locale("es", "ES")).format(currentItem.getDate()).toUpperCase());

            btnAcceptTopup.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mListener.onRequestResponse(currentItem, true);
                }
            });

            btnDenyTopup.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mListener.onRequestResponse(currentItem, false);
                }
            });
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }


        return row;

    }
}
