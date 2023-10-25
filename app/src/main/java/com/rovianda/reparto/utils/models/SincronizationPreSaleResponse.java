package com.rovianda.reparto.utils.models;

import java.util.List;

public class SincronizationPreSaleResponse {
    private List<PreSaleSincronizedResponse> preSalesSincronized;

    public List<PreSaleSincronizedResponse> getPreSalesSincronized() {
        return preSalesSincronized;
    }

    public void setPreSalesSincronized(List<PreSaleSincronizedResponse> preSalesSincronized) {
        this.preSalesSincronized = preSalesSincronized;
    }
}
