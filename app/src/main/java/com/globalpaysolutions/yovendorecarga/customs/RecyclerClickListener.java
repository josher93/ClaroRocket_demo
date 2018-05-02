package com.globalpaysolutions.yovendorecarga.customs;

import android.view.View;

/**
 * Created by Josué Chávez on 22/3/2018.
 */

public interface RecyclerClickListener
{
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}
