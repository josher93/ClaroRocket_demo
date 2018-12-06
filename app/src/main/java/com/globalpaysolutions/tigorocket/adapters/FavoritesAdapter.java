
package com.globalpaysolutions.tigorocket.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.globalpaysolutions.tigorocket.R;
import com.globalpaysolutions.tigorocket.customs.Data;
import com.globalpaysolutions.tigorocket.customs.EditFavoriteClickListener;
import com.globalpaysolutions.tigorocket.model.FavoriteNumber;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Josué Chávez on 22/3/2018.
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>
{
    private static final String TAG = FavoritesAdapter.class.getSimpleName();
    private Context mContext;
    private List<FavoriteNumber> mFavoritesList;
    private EditFavoriteClickListener mClickListener;

    public FavoritesAdapter(Context context, List<FavoriteNumber> favorites, EditFavoriteClickListener clickListener)
    {
        this.mContext = context;
        this.mFavoritesList = favorites;
        this.mClickListener = clickListener;
    }

    @Override
    public FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_favorites_listview_item, parent, false);

        return new FavoritesViewHolder(itemView, mClickListener);
    }

    @Override
    public void onBindViewHolder(FavoritesViewHolder holder, int position)
    {
        try
        {
            final FavoriteNumber fav = mFavoritesList.get(position);
            String phone = fav.getMsisdn();

            if (position % 2 == 1)
                holder.lnrParentItem.setBackgroundColor(mContext.getResources().getColor(R.color.PressedWhiteTheme));
            else
                holder.lnrParentItem.setBackgroundColor(mContext.getResources().getColor(R.color.ActivityWhiteBackground));

            if(!"".equals(phone))
            {
                phone = phone.substring(0,4) + "-" + phone.substring(4,phone.length());
            }

            if(Data.isEditMode)
            {
                holder.lnrParentItem.setWeightSum(12);
                holder.rlvIconContainer.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.lnrParentItem.setWeightSum(10);
                holder.rlvIconContainer.setVisibility(View.GONE);
            }


            holder.tvNickname.setText(fav.getName());
            holder.tvPhone.setText(phone);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error binding viewholder: " + ex.getMessage());
        }

    }

    @Override
    public int getItemCount()
    {
        return mFavoritesList.size();
    }

    class FavoritesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView tvNickname;
        TextView tvPhone;
        LinearLayout lnrParentItem;
        ImageView icClear;
        RelativeLayout rlvIconContainer;
        WeakReference<EditFavoriteClickListener> listenerRef;

        FavoritesViewHolder(View row, EditFavoriteClickListener clickListener)
        {
            super(row);
            lnrParentItem = (LinearLayout) row.findViewById(R.id.lnrParentItem);
            tvNickname = (TextView) row.findViewById(R.id.tvNickname);
            tvPhone = (TextView) row.findViewById(R.id.tvPhone);
            icClear = (ImageView) row.findViewById(R.id.icClear);
            listenerRef = new WeakReference<EditFavoriteClickListener>(clickListener);
            rlvIconContainer = (RelativeLayout) row.findViewById(R.id.rlvIconContainer);
            icClear.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            listenerRef.get().onClickListener(getAdapterPosition());
        }
    }



}
