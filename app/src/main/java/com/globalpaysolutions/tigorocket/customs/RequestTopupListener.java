package com.globalpaysolutions.tigorocket.customs;

import com.globalpaysolutions.tigorocket.model.PendingRequestItem;

/**
 * Created by Josué Chávez on 22/07/2017.
 */

public interface RequestTopupListener
{
    void onRequestResponse(PendingRequestItem requestItem, boolean accepted);
}
