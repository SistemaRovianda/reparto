package com.rovianda.reparto.history.presenter;

import com.rovianda.reparto.history.models.ModelDebtDeliverRequest;

import java.util.List;

public interface HistoryPresenterContract {
    void registerDebtsOfPreSales(List<ModelDebtDeliverRequest> requestList);
}
