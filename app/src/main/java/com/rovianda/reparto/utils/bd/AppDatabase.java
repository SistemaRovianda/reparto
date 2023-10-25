package com.rovianda.reparto.utils.bd;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.rovianda.reparto.utils.bd.daos.ClientDao;
import com.rovianda.reparto.utils.bd.daos.ClientVisitDao;
import com.rovianda.reparto.utils.bd.daos.DebtDao;
import com.rovianda.reparto.utils.bd.daos.EndingDayDao;
import com.rovianda.reparto.utils.bd.daos.ProductDao;
import com.rovianda.reparto.utils.bd.daos.PreSaleDao;
import com.rovianda.reparto.utils.bd.daos.SubSaleDao;
import com.rovianda.reparto.utils.bd.daos.UserDataInitialDao;
import com.rovianda.reparto.utils.bd.entities.Client;
import com.rovianda.reparto.utils.bd.entities.ClientVisit;
import com.rovianda.reparto.utils.bd.entities.Debt;
import com.rovianda.reparto.utils.bd.entities.EndingDay;
import com.rovianda.reparto.utils.bd.entities.PreSale;
import com.rovianda.reparto.utils.bd.entities.Product;
import com.rovianda.reparto.utils.bd.entities.SubSale;
import com.rovianda.reparto.utils.bd.entities.UserDataInitial;

@Database(entities = {PreSale.class, SubSale.class, Client.class, Product.class, UserDataInitial.class,  EndingDay.class, ClientVisit.class, Debt.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    public abstract PreSaleDao preSaleDao();
    public abstract SubSaleDao subSalesDao();
    public abstract ClientDao clientDao();
    public abstract ProductDao productDao();
    public abstract UserDataInitialDao userDataInitialDao();
    public abstract EndingDayDao endingDayDao();
    public abstract ClientVisitDao clientVisitDao();
    public abstract DebtDao debtDao();
    public static synchronized AppDatabase getInstance(Context context){
        if(instance==null){
            instance= Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"rovisapireparto").fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
