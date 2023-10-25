package com.rovianda.reparto.utils.bd.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.rovianda.reparto.utils.bd.entities.SubSale;

import java.util.List;


@Dao
public interface SubSaleDao {

    @Query("select * from sub_sales where folio= :folio")
    List<SubSale> getSubSalesBySale(String folio);

    @Query("delete from sub_sales")
    void deleteAllSubSales();

    @Query("delete from sub_sales where sub_sale_id=:subSaleId")
    void deleteSubSale(Integer subSaleId);

    @Insert
    void insertAllSubSales(SubSale... subSales);

    @Query("select * from sub_sales where sub_sale_id=:subSaleid")
    SubSale getSubSaleBySubSaleId(Integer subSaleid);

    @Update
    void updateSubSale(SubSale... subSales);

}
