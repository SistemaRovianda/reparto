package com.rovianda.reparto.visits.presenter;

import com.rovianda.reparto.history.models.ModelDebtDeliverRequest;
import com.rovianda.reparto.home.models.UpdatePresaleModelRequest;
import com.rovianda.reparto.utils.models.ClientDTO;
import com.rovianda.reparto.utils.models.ClientV2Request;
import com.rovianda.reparto.utils.models.ClientV2UpdateRequest;
import com.rovianda.reparto.utils.models.ClientV2VisitRequest;
import com.rovianda.reparto.utils.models.ModeOfflineSM;
import com.rovianda.reparto.utils.models.ModeOfflineSincronize;

import java.util.List;

public interface VisitsPresenterContract {

    void getDataInitial(String sellerUid,String date);
    void tryRegisterClients(List<ClientV2Request> clientV2Request);
    void updateCustomerV2(List<ClientV2UpdateRequest> clientV2UpdateRequestList);
    void registerVisitsV2(List<ClientV2VisitRequest> clientV2VisitRequests);
    void sincronizePreSales(List<UpdatePresaleModelRequest> updatePresaleModelRequests,String sellerId);
    void registerDebtsOfPreSales(List<ModelDebtDeliverRequest> requestList);
}
