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
import com.globalpaysolutions.yovendorecarga.model.rest.RocketBalanceList;
import com.globalpaysolutions.yovendorecarga.model.rest.RocketBalanceResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentsAdapter extends ArrayAdapter<RocketBalanceList>
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

        final RocketBalanceList currentItem = getItem(position);

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
                tvId.setText("#".concat(String.valueOf(currentItem.getBalanceID())));
                tvPayment.setText("$".concat(String.valueOf(currentItem.getBalanceAmount())));
                tvDebt.setText("$".concat(String.valueOf(currentItem.getBalanceAmount())));

                DateFormat originalformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat newFormat = new SimpleDateFormat("d'-'MMM'-'yy';' h:mm a", new Locale("es", "ES"));

                String reformattedStr = newFormat.format(originalformat.parse(currentItem.getConciliationDate()));
                tvDate.setText(reformattedStr.toUpperCase());
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return row;

    }
}
