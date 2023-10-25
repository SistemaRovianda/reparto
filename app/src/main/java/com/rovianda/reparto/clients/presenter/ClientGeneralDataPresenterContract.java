package com.rovianda.reparto.clients.presenter;

import com.rovianda.reparto.utils.models.ClientV2Request;
import com.rovianda.reparto.utils.models.ClientV2UpdateRequest;

import java.util.List;

public interface ClientGeneralDataPresenterContract {
    void getAddressByCoordenates(Double latitude,Double longitude);
    void tryRegisterClient(ClientV2Request clientV2Request);
    void updateCustomerV2(List<ClientV2UpdateRequest> clientV2UpdateRequestList);
}
