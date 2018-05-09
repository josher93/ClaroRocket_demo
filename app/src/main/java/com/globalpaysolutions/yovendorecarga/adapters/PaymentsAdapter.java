package com.globalpaysolutions.yovendorecarga.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.model.BillPayment;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PaymentsAdapter extends ArrayAdapter<BillPayment>
{
    private Context mContext;
    private int mAdapterResource;

    public PaymentsAdapter(Context context, int resource)
    {
        super(context, resource);
        mContext = context;
        mAdapterResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        View row = convertView;

        final BillPayment currentItem = getItem(position);

        if(row == null)
        {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mAdapterResource, parent, false);
        }

        row.setTag(currentItem);


        try
        {
            final TextView tvId = (TextView) row.findViewById(R.id.tvId);
            final TextView tvPayment = (TextView) row.findViewById(R.id.tvPayment);
            final TextView tvDebt = (TextView) row.findViewById(R.id.tvDebt);
            final TextView tvDate = (TextView) row.findViewById(R.id.tvDate);

            if(currentItem != null)
            {
                tvId.setText(String.valueOf(currentItem.getId()));
                tvPayment.setText("$".concat(String.valueOf(currentItem.getPayment())));
                tvDebt.setText("$".concat(String.valueOf(currentItem.getDebt())));
                tvDate.setText(new SimpleDateFormat("d'-'MMM'-'yy';' h:mm a", new Locale("es", "ES"))
                        .format(currentItem.getDate()).toUpperCase());
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return row;

    }
}
