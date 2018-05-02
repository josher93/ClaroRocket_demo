package com.globalpaysolutions.yovendorecarga.customs;

import com.globalpaysolutions.yovendorecarga.model.PendingRequestItem;

/**
 * Created by Josué Chávez on 22/07/2017.
 */

public interface RequestTopupListener
{
    void onRequestResponse(PendingRequestItem requestItem, boolean accepted);
}
