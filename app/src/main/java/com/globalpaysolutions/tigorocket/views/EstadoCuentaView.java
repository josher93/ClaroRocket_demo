package com.globalpaysolutions.tigorocket.views;

import android.content.DialogInterface;

import com.globalpaysolutions.tigorocket.model.rest.RocketBalanceList;

import java.util.List;

public interface EstadoCuentaView
{
    void presentBalance(String profit, String receivableAccount, String conciliation, String sold, int balanceID);
    void renderPaymentsHistory(List<RocketBalanceList> rocketBalanceList);
    void showConfirmPaymentDialog(String title, String content);
    void showPinCodeInputDialgo(int balanceID);
    void dismissPinCodeInputDialog();
    void showGenericDialog(String title, String content, String button, DialogInterface.OnClickListener clickListener);
    void showLoadingDialg(String label);
    void hideLoadingDialog();
    void clearListview();
    void noPaymentsPending();
    void setDatesRange(String dates);
}
