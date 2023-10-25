package com.rovianda.reparto.utils.bd.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "debts")
public class Debt {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "deb_id")
    public int debId;

    @ColumnInfo(name = "create_at")
    public String createAt;

    @ColumnInfo(name = "payed_type")
    public String payedType;

    @ColumnInfo(name="folio_presale")
    public String folioPreSale;
    @ColumnInfo(name="folio_sale")
    public String folioSale;

    @ColumnInfo(name = "sincronized")
    public Boolean sincronized;

}
