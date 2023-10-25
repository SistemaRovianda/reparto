package com.rovianda.reparto.history.view;

import com.rovianda.reparto.history.models.ModelDebtDeliverResponse;
import com.rovianda.reparto.utils.bd.entities.PreSale;

import java.util.List;

public interface HistoryViewContract {
    void genericMessage(String title, String msg);
    void goToHome();
    void goToVisits();
    void goToClients();
    void showErrorConnectingPrinter();
    void connectionPrinterSuccess(String printerName);
    void showOptionsSale(PreSale preSale);
    void checkPaydeb(PreSale preSale);
    void printTicketSale(String folio);
    void sincronizationCompleteDebts(List<ModelDebtDeliverResponse> responseList);
}
