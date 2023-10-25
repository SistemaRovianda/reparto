package com.rovianda.reparto.home.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.rovianda.reparto.R;
import com.rovianda.reparto.home.models.PreSaleItemForDetails;
import com.rovianda.reparto.home.view.HomeView;
import com.rovianda.reparto.home.view.HomeViewContract;

import java.util.List;

public class AdapterPreSaleListItem extends BaseAdapter {


    private HomeViewContract viewM;
    private Context context;
    private List<PreSaleItemForDetails> listPresales;
    public AdapterPreSaleListItem(Context context, HomeView viewM,List<PreSaleItemForDetails> listPresales){
        this.viewM = viewM;
        this.context=context;
        this.listPresales= listPresales;
    }

    @Override
    public int getCount() {
        return this.listPresales.size();
    }

    @Override
    public Object getItem(int i) {
        return this.listPresales.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View viewToShow = layoutInflater.inflate(R.layout.presale_list_item_for_details,null);
        TextView clientAmountTextView = viewToShow.findViewById(R.id.clientAmountText);
        Button showOptionsOfPreSale = viewToShow.findViewById(R.id.detailsOfPreSale);
        PreSaleItemForDetails item = this.listPresales.get(i);
        clientAmountTextView.setText(item.getFolioPreSale()+" "+item.getClientName()+" - $"+String.format("%.02f",item.getAmount()));
        showOptionsOfPreSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewM.showOptionsForPresale(item.getFolioPreSale());
            }
        });
        return viewToShow;
    }
}
