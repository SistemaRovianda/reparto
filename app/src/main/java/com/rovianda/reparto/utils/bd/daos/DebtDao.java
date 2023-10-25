package com.rovianda.reparto.utils.bd.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.rovianda.reparto.utils.bd.entities.Debt;

import java.util.List;

@Dao
public interface DebtDao {

    @Query("select * from debts where sincronized=0 and create_at between :sale1 and :sale2")
    List<Debt> getAllSalesDebtsBetweenDates(String sale1, String sale2);

    @Query("select * from debts where sincronized=0")
    List<Debt> getAllDebsWithoutSincronization();


    @Query("select * from debts where folio_presale=:folio limit 1")
    Debt getDebtByFolioPreSale(String folio);
    @Query("select * from debts where folio_sale=:folio limit 1")
    Debt getDebtByFolioSale(String folio);

    @Insert
    void insertDebts(Debt... debt);

    @Update
    void updateDebtSincronization(Debt... debts);

}
