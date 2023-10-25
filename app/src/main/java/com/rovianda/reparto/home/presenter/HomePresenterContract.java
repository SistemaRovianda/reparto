package com.rovianda.reparto.home.presenter;


import com.rovianda.reparto.home.models.UpdatePresaleModelRequest;
import com.rovianda.reparto.utils.models.ModeOfflineSM;
import com.rovianda.reparto.utils.models.SaleDTO;

import java.util.List;

public interface HomePresenterContract {
    void doLogout();
    void doSale(SaleDTO saleDTO);
    void getEndDayTicket();
    void getStockOnline();
    void checkCommunicationToServer();
    void sincronizePreSales(List<UpdatePresaleModelRequest> updatePresaleModelRequests,String sellerId);
    void sendEndDayRecord(String date,String uid);
}
