package com.rovianda.reparto.utils;

import java.util.List;

public class PreSaleRecords {

    private Float amount;
    private Integer clientId;
    private String clientName;
    private Float credit;
    private String date;
    private String dateToDeliver;
    private String folio;
    private String folioSale;
    private Integer keyClient;
    private Float payed;
    private Integer preSaleId;
    private String sellerId;
    private String statusStr;
    private String typeSale;
    private List<SubSaleOfflineNewVersion> products;
    private Boolean solded;
    private String dateSolded;

    public String getDateSolded() {
        return dateSolded;
    }

    public void setDateSolded(String dateSolded) {
        this.dateSolded = dateSolded;
    }

    public String getFolioSale() {
        return folioSale;
    }

    public void setFolioSale(String folioSale) {
        this.folioSale = folioSale;
    }

    public Boolean getSolded() {
        return solded;
    }

    public void setSolded(Boolean solded) {
        this.solded = solded;
    }

    public String getDateToDeliver() {
        return dateToDeliver;
    }

    public void setDateToDeliver(String dateToDeliver) {
        this.dateToDeliver = dateToDeliver;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Float getCredit() {
        return credit;
    }

    public void setCredit(Float credit) {
        this.credit = credit;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Integer getKeyClient() {
        return keyClient;
    }

    public void setKeyClient(Integer keyClient) {
        this.keyClient = keyClient;
    }

    public Float getPayed() {
        return payed;
    }

    public void setPayed(Float payed) {
        this.payed = payed;
    }

    public Integer getPreSaleId() {
        return preSaleId;
    }

    public void setPreSaleId(Integer preSaleId) {
        this.preSaleId = preSaleId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getTypeSale() {
        return typeSale;
    }

    public void setTypeSale(String typeSale) {
        this.typeSale = typeSale;
    }

    public List<SubSaleOfflineNewVersion> getProducts() {
        return products;
    }

    public void setProducts(List<SubSaleOfflineNewVersion> products) {
        this.products = products;
    }
}
