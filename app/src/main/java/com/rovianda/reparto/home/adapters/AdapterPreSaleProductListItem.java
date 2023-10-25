package com.rovianda.reparto.home.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rovianda.reparto.R;
import com.rovianda.reparto.home.view.HomeView;
import com.rovianda.reparto.home.view.HomeViewContract;
import com.rovianda.reparto.utils.bd.entities.SubSale;

import java.util.List;

public class AdapterPreSaleProductListItem extends BaseAdapter {

    public HomeViewContract viewM;
    public Context context;
    public List<SubSale> products;
    public AdapterPreSaleProductListItem(HomeView viewM, Context context,List<SubSale> products){
        this.viewM=viewM;
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return this.products.size();
    }

    @Override
    public Object getItem(int i) {
        return this.products.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.products.get(i).subSaleId;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View listItemV = layoutInflater.inflate(R.layout.presale_products_modification,null);
        TextView productName = listItemV.findViewById(R.id.productName);
        TextInputLayout inputQuantity= listItemV.findViewById(R.id.inputQuantity);
        ImageButton delProduct = listItemV.findViewById(R.id.delProduct);
        SubSale product = this.products.get(i);
        productName.setText(product.productName+" "+product.productPresentationType);
        inputQuantity.getEditText().setText(String.valueOf(product.quantity));
        Float priceUnique=product.price/product.quantity;
        delProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewM.setSubSalesForModification(product,"DELETE");
            }
        });
        inputQuantity.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String quantity=inputQuantity.getEditText().getText().toString();
                if(quantity.isEmpty()){
                    quantity="0";
                }
                Float quantityNumber = Float.parseFloat(quantity);
                product.price=(priceUnique)*quantityNumber;
                product.quantity=quantityNumber;
                viewM.setSubSalesForModification(product,"MODIFICATE");
            }
        });
        return listItemV;
    }
}
