package com.globalpaysolutions.mrocket.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Visibility;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.globalpaysolutions.mrocket.R;
import com.globalpaysolutions.mrocket.customs.YVScomSingleton;
import com.globalpaysolutions.mrocket.model.Operator;

/**
 * Created by Geovanni on 20/03/2016.
 */
public class OperatorsAdapter extends ArrayAdapter<Operator>
{
    Context AdapterContext;
    int AdapResource;
    int SelectedItem;

    public OperatorsAdapter(Context pContext, int pResource)
    {
        super(pContext, pResource);

        AdapterContext = pContext;
        AdapResource = pResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;

        final Operator currentItem = getItem(position);

        if (view == null)
        {
            LayoutInflater inflater = ((Activity) AdapterContext).getLayoutInflater();
            view = inflater.inflate(AdapResource, parent, false);
        }

        view.setTag(currentItem);
        if(position == 0)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                view.setBackground(getContext().getResources().getDrawable(R.drawable.custom_rounded_corner_selected));
            }
            else
            {
                view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.custom_rounded_corner_selected));
            }
        }

        //Seteo del logo
        NetworkImageView networkViewOperador = (NetworkImageView) view.findViewById(R.id.networkViewOperador);
        /* ImageView logoOperator = (ImageView) view.findViewById(R.id.ivOperador);

       if(currentItem.getLogoImage() != null)
        {
            networkViewOperador.setVisibility(View.GONE);
            //logoOperator.setVisibility(View.VISIBLE);
            byte[] image = currentItem.getLogoImage();
            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            logoOperator.setImageBitmap(bmp);
        }
        else
        {*/
            //logoOperator.setVisibility(View.GONE);
            networkViewOperador.setVisibility(View.VISIBLE);
            ImageLoader imageLoader = YVScomSingleton.getInstance(getContext()).getImageLoader();
            networkViewOperador.setImageUrl(currentItem.getLogoURL(), imageLoader);

        //}

        return view;
    }


}
