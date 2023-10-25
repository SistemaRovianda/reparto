package com.rovianda.reparto.home.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rovianda.reparto.R;
import com.rovianda.reparto.home.view.HomeView;
import com.rovianda.reparto.home.view.HomeViewContract;
import com.rovianda.reparto.utils.bd.entities.SubSale;

import java.util.List;

public class ProductsPreSalesAdapter extends BaseAdapter {

    private HomeViewContract viewM;
    private List<SubSale> subSaleList;
    private Context context;
    public ProductsPreSalesAdapter(Context context, List<SubSale> subSales, HomeView viewM){
        this.context = context;
        this.subSaleList = subSales;
        this.viewM = viewM;
    }

    @Override
    public int getCount() {
        return this.subSaleList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.subSaleList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View viewToShow = layoutInflater.inflate(R.layout.presale_product_item,null);
        TextView codeColumn = viewToShow.findViewById(R.id.codeColumn);
        TextView presentationColumn = viewToShow.findViewById(R.id.presentationColumn);
        TextView quantityColumn = viewToShow.findViewById(R.id.quantityColumn);
        SubSale subSale = this.subSaleList.get(i);
        codeColumn.setText(subSale.productKey);
        presentationColumn.setText(subSale.productName+" "+subSale.productPresentationType);
        quantityColumn.setText(String.valueOf(subSale.quantity));
        return viewToShow;
    }
}
