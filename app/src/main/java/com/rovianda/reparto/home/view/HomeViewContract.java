package com.rovianda.reparto.home.view;


import com.rovianda.reparto.home.models.UpdatePresaleModelResponse;
import com.rovianda.reparto.utils.bd.entities.SubSale;
import com.rovianda.reparto.utils.models.SincronizationPreSaleResponse;

import java.util.List;

public interface HomeViewContract {
    void goToLogin();
    void showErrorConnectingPrinter();
    void connectionPrinterSuccess(String printerName);
    void saleSuccess(String ticket);
    void genericMessage(String title,String msg);
    void showNotificationSincronization(String msg);
    void hiddeNotificationSincronizastion();
    void completeSincronzation(List<UpdatePresaleModelResponse> response);
    void getPreSaleDetails(String folioPreSale,String option);
    void showOptionsForPresale(String folioPreSale);

    void setSubSalesForModification(SubSale subSale, String typeOperation);
}
