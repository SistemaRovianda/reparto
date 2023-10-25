package com.rovianda.reparto.utils.bd.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.rovianda.reparto.utils.bd.entities.PreSale;
import java.util.List;

@Dao
public interface PreSaleDao {

    @Query("delete from pre_sales")
    void deleteAllPreSales();

    @Query("select * from pre_sales where date between :date1 and :date2 order by date asc")
    List<PreSale> getAllPreSalesByDate(String date1, String date2);

    @Query("select * from pre_sales where solded=1 and date_solded between :date1 and :date2 ")
    List<PreSale> getAllPreSalesSoldedByDateSolded(String date1,String date2);

    @Query("select * from pre_sales where solded=1 and payed=1 and date_payed between :date1 and :date2 ")
    List<PreSale> getAllPreSalesPayedByDateSolded(String date1,String date2);

    @Query("select * from pre_sales where solded=0 and date_to_deliver = :date order by date asc")
    List<PreSale> getAllPreSalesUnSoldedByDeliverDate(String date);

    @Query("select * from pre_sales where sincronized=0 and date between :date1 and :date2")
    List<PreSale> getAllPreSalesUnsincronziedByDate(String date1,String date2);

    @Query("select * from pre_sales where  date_solded between :date1 and :date2")
    List<PreSale> getAllPreSalesSoldedByDate(String date1,String date2);

    @Query("select * from pre_sales where client_id=:clientId and date between :date1 and :date2")
    List<PreSale> getAllSalesByDateAndClientId(String date1,String date2,Integer clientId);

    @Query("select * from pre_sales where key_client=:keyClientTemp  and date between :date1 and :date2")
    List<PreSale> getAllSalesByDateAndKeyClientTemp(String date1,String date2,Integer keyClientTemp);

    @Query("select * from pre_sales where sincronized=0 and solded=1")
    List<PreSale> getAllPreSalesSoldedUnsincronized();

    @Query("select * from pre_sales where seller_id=:sellerId and sincronized=0")
    List<PreSale> getAllSalesUnsincronizedBySeller(String sellerId);

    @Query("select * from pre_sales where key_client=:keyClientOld and is_temp_key_client=1")
    List<PreSale> getAllTempClientSales(Integer keyClientOld);

    @Query("select * from pre_sales where sale_server_id=:saleId ")
    PreSale getBySaleId(int saleId);

    @Query("select * from pre_sales where folio=:folio ")
    PreSale getByFolio(String folio);


    @Query("update pre_sales set sale_server_id=:saleId,sincronized=1 where folio=:folio ")
    void updatePreSaleIdByFolio(int saleId,String folio);

    @Insert
    void insertAll(PreSale... sales);

    @Update
    void updateSale(PreSale... sale);


    @Query("select * from pre_sales where type_pre_sale='CREDITO' and payed=0")
    List<PreSale> getAllPreSalesCreditWithoutPayment();

    @Query("select * from pre_sales where type_pre_sale='CREDITO' and payed=1")
    List<PreSale> getAllPreSalesCreditWithPayment();

}
