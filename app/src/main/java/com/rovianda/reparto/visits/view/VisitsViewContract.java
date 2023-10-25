package com.rovianda.reparto.visits.view;

import com.rovianda.reparto.history.models.ModelDebtDeliverResponse;
import com.rovianda.reparto.home.models.UpdatePresaleModelResponse;
import com.rovianda.reparto.utils.ModeOfflineNewVersion;
import com.rovianda.reparto.utils.models.ClientV2Response;
import com.rovianda.reparto.utils.models.ClientV2UpdateResponse;
import com.rovianda.reparto.utils.models.ClientV2VisitResponse;
import com.rovianda.reparto.utils.models.SincronizationPreSaleResponse;

import java.util.List;

public interface VisitsViewContract {

    void goToHome();
    void goToSalesHistory();
    void goToClients();
    void setLoadingStatus(boolean loadingStatus);
    void setModeOffline(ModeOfflineNewVersion records);
    void modalMessageOperation(String msg);
    void modalSincronizationStart(String msg);
    void modalSincronizationEnd();
    void setUploadingStatus(boolean flag);
    void firstStep();
    void setClientsRegisters(List<ClientV2Response> clientsRegistered);
    void secondStep();
    void setClientsUpdated(List<ClientV2UpdateResponse> clientsUpdated);
    void thirdStep();
    void setClientVisitedRegistered(List<ClientV2VisitResponse> clientV2Visit);
    void showNotificationSincronization(String msg);
    void hiddeNotificationSincronizastion();
    void sendPreSalesToSystem();
    void completeSincronzation(List<UpdatePresaleModelResponse> sincronizationResponse);
    void checkIfRecordsWithoutSincronization();
    void sincronizationCompleteDebts(List<ModelDebtDeliverResponse> responseList);
    void tryRegisterDebts();
}
