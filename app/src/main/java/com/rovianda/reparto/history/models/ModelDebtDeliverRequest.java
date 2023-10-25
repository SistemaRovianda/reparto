package com.rovianda.reparto.history.models;

public class ModelDebtDeliverRequest {

    private String createdAt;
    private String folioSale;
    private String payedType;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFolioSale() {
        return folioSale;
    }

    public void setFolioSale(String folioSale) {
        this.folioSale = folioSale;
    }

    public String getPayedType() {
        return payedType;
    }

    public void setPayedType(String payedType) {
        this.payedType = payedType;
    }
}
