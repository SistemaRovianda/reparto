package com.rovianda.reparto.home.models;

import java.util.List;

public class UpdatePresaleModelRequest {
    private String folioPresale;
    private String folioForSale;
    private String typePayment;
    private String dateSolded;
    private Boolean modificated;
    private List<UpdatePreSaleProductRequest> modifications;

    public Boolean getModificated() {
        return modificated;
    }

    public void setModificated(Boolean modificated) {
        this.modificated = modificated;
    }

    public List<UpdatePreSaleProductRequest> getModifications() {
        return modifications;
    }

    public void setModifications(List<UpdatePreSaleProductRequest> modifications) {
        this.modifications = modifications;
    }

    public String getDateSolded() {
        return dateSolded;
    }

    public void setDateSolded(String dateSolded) {
        this.dateSolded = dateSolded;
    }

    public String getFolioPresale() {
        return folioPresale;
    }

    public void setFolioPresale(String folioPresale) {
        this.folioPresale = folioPresale;
    }

    public String getFolioForSale() {
        return folioForSale;
    }

    public void setFolioForSale(String folioForSale) {
        this.folioForSale = folioForSale;
    }

    public String getTypePayment() {
        return typePayment;
    }

    public void setTypePayment(String typePayment) {
        this.typePayment = typePayment;
    }
}
