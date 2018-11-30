package com.globalpaysolutions.mrocket.customs;

import com.globalpaysolutions.mrocket.model.PendingRequestItem;

/**
 * Created by Josué Chávez on 22/07/2017.
 */

public interface RequestTopupListener
{
    void onRequestResponse(PendingRequestItem requestItem, boolean accepted);
}
