package com.rovianda.reparto.utils.bd.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pre_sales")
public class PreSale {

    @PrimaryKey
    @NonNull
    public String folio;

    @ColumnInfo(name="sale_server_id")
    public int saleId;

    @ColumnInfo(name = "seller_id")
    public String sellerId;

    @ColumnInfo(name="key_client")
    public int keyClient;

    @ColumnInfo(name="is_temp_key_client")
    public boolean isTempKeyClient;

    @ColumnInfo(name="amount")
    public Float amount;

    @ColumnInfo(name="client_name")
    public String clientName;

    @ColumnInfo(name="date")
    public String date;

    @ColumnInfo(name="date_to_deliver")
    public String dateToDeliver;

    @ColumnInfo(name="status_str")
    public String statusStr;

    @ColumnInfo(name="type_pre_sale")
    public String typePreSale;

    @ColumnInfo(name="date_solded")
    public String dateSolded;

    @ColumnInfo(name="folio_for_sale")
    public String folioForSale;

    @ColumnInfo(name="sincronized")
    public Boolean sincronized;

    @ColumnInfo(name="client_id")
    public int clientId;

    @ColumnInfo(name="solded",defaultValue = "false")
    public Boolean solded;

    @ColumnInfo(name="payed",defaultValue = "true")
    public Boolean payed;

    @ColumnInfo(name="date_payed")
    public String datePayed;

    @ColumnInfo(name="modified",defaultValue = "false")
    public Boolean modified;

}
