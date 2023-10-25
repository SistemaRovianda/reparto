package com.rovianda.reparto.home.models;

public class PreSaleItemForDetails {
    private String folioPreSale;
    private String clientName;
    private Float amount;

    public String getFolioPreSale() {
        return folioPreSale;
    }

    public void setFolioPreSale(String folioPreSale) {
        this.folioPreSale = folioPreSale;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }
}
