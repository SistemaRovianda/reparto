package com.rovianda.reparto.clients.view;

import com.rovianda.reparto.utils.models.AddressCoordenatesResponse;
import com.rovianda.reparto.utils.models.ClientV2Response;
import com.rovianda.reparto.utils.models.ClientV2UpdateResponse;

import java.util.List;

public interface ClientGeneralDataViewContract {

    void setAddressByCoordenates(AddressCoordenatesResponse addressCoordenatesResponse, Double latitude, Double longitude);
    void showModalCoordenatesCustom(String title, String msg,boolean forClose);
    void cannotTakeCoordenatesInfo();
    void goBackToClients();
    void goToMap();
    void failConnectionServiceEdit();
    void closeModal();
    void updateClientInServer(List<ClientV2UpdateResponse> clientV2UpdateResponse);
    void closeModalLoadingCoords();
    void updateClientRegisteredInServer(ClientV2Response clientV2Response);
}
