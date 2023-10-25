package com.rovianda.reparto.history.adapter;

import android.content.Context;
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

public class SaleDebtListAdapter extends  BaseAdapter {
        HistoryViewContract viewM;
        Context context;
        List<PreSale> preSales;
        public SaleDebtListAdapter(Context context, List<PreSale> preSales, HistoryView viewM){
            this.context =context;
            this.viewM = viewM;
            this.preSales =preSales;
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
            View view= layoutInflater.inflate(R.layout.sale_item_list_debt, null);
            TextView folio = view.findViewById(R.id.foliosaleTextView);
            TextView clientName = view.findViewById(R.id.clientSaleTextView);
            TextView amount = view.findViewById(R.id.amountSaletextView);
            TextView status = view.findViewById(R.id.statusSaleTextView);
            MaterialButton pay = view.findViewById(R.id.payDebt);
            MaterialButton reprintPay = view.findViewById(R.id.reprintPayDeb);
            PreSale preSale = this.preSales.get(position);
            pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewM.checkPaydeb(preSale);
                }
            });
            reprintPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewM.printTicketSale(preSale.folio);
                }
            });
            folio.setText("Folio: "+preSale.folioForSale);
            clientName.setText("Cliente: "+preSale.clientName);
            amount.setText("$"+ String.format("%.02f",preSale.amount));
            String statusStr = "";
            if(Boolean.FALSE.equals(Boolean.parseBoolean(preSale.payed.toString()))) {
                pay.setVisibility(View.VISIBLE);
                pay.setEnabled(true);
                reprintPay.setVisibility(View.GONE);
                reprintPay.setEnabled(false);
                statusStr = "POR COBRAR";
            }else{
                pay.setVisibility(View.GONE);
                pay.setEnabled(false);
                reprintPay.setVisibility(View.VISIBLE);
                reprintPay.setEnabled(true);
                statusStr = "COBRADO";
            }
            status.setText("Estatus: " + statusStr);

            return view;
        }



}
