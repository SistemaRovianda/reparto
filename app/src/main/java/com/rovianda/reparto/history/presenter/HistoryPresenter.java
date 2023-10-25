package com.rovianda.reparto.history.presenter;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.rovianda.reparto.history.models.ModelDebtDeliverRequest;
import com.rovianda.reparto.history.models.ModelDebtDeliverResponse;
import com.rovianda.reparto.history.view.HistoryView;
import com.rovianda.reparto.history.view.HistoryViewContract;
import com.rovianda.reparto.home.models.UpdatePresaleModelRequest;
import com.rovianda.reparto.home.models.UpdatePresaleModelResponse;
import com.rovianda.reparto.utils.Constants;
import com.rovianda.reparto.utils.GsonRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryPresenter implements HistoryPresenterContract{

    Context context;
    FirebaseAuth firebaseAuth;
    //JsonRequest
    private Cache cache;
    private Network network;
    private Gson parser;
    private String url = Constants.URL;
    private RequestQueue requestQueue;
    private HistoryViewContract view;
    public HistoryPresenter(Context context, HistoryView view){
        this.context=context;
        this.view=view;
        this.firebaseAuth = FirebaseAuth.getInstance();
        cache = new DiskBasedCache(context.getCacheDir(),1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();
        parser= new Gson();
    }


    @Override
    public void registerDebtsOfPreSales(List<ModelDebtDeliverRequest> requestList) {
        Map<String,String> headers = new HashMap<>();
        String sellerId = firebaseAuth.getUid();
        headers.put("Content-Type", "application/json");
        GsonRequest<ModelDebtDeliverResponse[]> request = new GsonRequest<ModelDebtDeliverResponse[]>
                (url+"/rovianda/delivers/register-debt?sellerId="+sellerId, ModelDebtDeliverResponse[].class,headers,
                        new Response.Listener<ModelDebtDeliverResponse[]>(){
                            @Override
                            public void onResponse(ModelDebtDeliverResponse[] response) {
                                view.sincronizationCompleteDebts(Arrays.asList(response));
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }   , Request.Method.POST,parser.toJson(requestList)
                );
        requestQueue.add(request).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 15000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }
}
