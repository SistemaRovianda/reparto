package com.rovianda.reparto.history.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.rovianda.reparto.R;
import com.rovianda.reparto.history.view.HistoryView;
import com.rovianda.reparto.history.view.HistoryViewContract;
import com.rovianda.reparto.utils.bd.entities.PreSale;

import java.util.List;

public class PreSaleListAdapter extends BaseAdapter {

    Context context;
    List<PreSale> preSales;
    private HistoryViewContract viewM;
    public PreSaleListAdapter(Context context, List<PreSale> preSales, HistoryView view){
        this.context =context;
        this.preSales =preSales;
        this.viewM=view;
    }

    @Override
    public int getCount() {
        return this.preSales.size();
    }

    @Override
    public Object getItem(int position) {
        return this.preSales.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View view= layoutInflater.inflate(R.layout.presale_item_list, null);
        TextView folio = view.findViewById(R.id.foliosaleTextView);
        TextView clientName = view.findViewById(R.id.clientSaleTextView);
        TextView amount = view.findViewById(R.id.amountSaletextView);
        TextView status = view.findViewById(R.id.statusSaleTextView);
        MaterialButton optionsButton = view.findViewById(R.id.optionsButton);
        PreSale item = this.preSales.get(position);

        folio.setText("Folio: "+item.folioForSale);
        clientName.setText("Cliente: "+item.clientName);
        amount.setText("$"+item.amount);
        String statusStr="ACTIVO";
        if(item.typePreSale.equals("CREDITO") && Boolean.FALSE.equals(Boolean.parseBoolean(item.payed.toString()))){
            statusStr="ADEUDO";
        }
        status.setText("Estatus: "+statusStr);

        status.setTextColor(Color.GREEN);

        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewM.showOptionsSale(preSales.get(position));
            }
        });

        return view;
    }

}
