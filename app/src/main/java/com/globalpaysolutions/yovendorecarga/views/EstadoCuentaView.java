package com.globalpaysolutions.yovendorecarga.views;

import com.globalpaysolutions.yovendorecarga.model.rest.RocketBalanceList;

import java.util.List;

public interface EstadoCuentaView
{
    void presentBalance(String profit, String balance, int balanceID);
    void renderPaymentsHistory(List<RocketBalanceList> rocketBalanceList);
    void showConfirmPaymentDialog(String title, String content);
    void setPaymentButtonEnabled(boolean enabled);
    void showPinCodeInputDialgo();
    void dismissPinCodeInputDialog();
    void showGenericDialog(String title, String content);
    void showLoadingDialg(String label);
    void hideLoadingDialog();
}
