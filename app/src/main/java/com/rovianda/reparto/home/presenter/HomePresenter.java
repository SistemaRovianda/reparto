package com.rovianda.reparto.home.presenter;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
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
import com.rovianda.reparto.home.models.UpdatePresaleModelRequest;
import com.rovianda.reparto.home.models.UpdatePresaleModelResponse;
import com.rovianda.reparto.home.view.HomeView;
import com.rovianda.reparto.home.view.HomeViewContract;
import com.rovianda.reparto.utils.Constants;
import com.rovianda.reparto.utils.GsonRequest;
import com.rovianda.reparto.utils.models.ModeOfflineSM;
import com.rovianda.reparto.utils.models.PreSaleSincronizedRequest;
import com.rovianda.reparto.utils.models.SaleDTO;
import com.rovianda.reparto.utils.models.SincronizationPreSaleResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePresenter implements HomePresenterContract{
    Context context;
    HomeViewContract view;
    FirebaseAuth firebaseAuth;

    //JsonRequest
    private Cache cache;
    private Network network;
    private Gson parser;

    private String url = Constants.URL;
    private RequestQueue requestQueue;
    public HomePresenter(Context context, HomeView view){
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
    public void doLogout() {
        if(this.firebaseAuth!=null) {
            this.firebaseAuth.signOut();
        }
        view.goToLogin();
    }

    @Override
    public void doSale(SaleDTO saleDTO) {

    }

    @Override
    public void getEndDayTicket() {

    }

    @Override
    public void getStockOnline() {

    }

    @Override
    public void checkCommunicationToServer() {

    }

    @Override
    public void sincronizePreSales(List<UpdatePresaleModelRequest> updatePresaleModelRequests,String sellerId) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        GsonRequest<UpdatePresaleModelResponse[]> request = new GsonRequest<UpdatePresaleModelResponse[]>
                (url+"/rovianda/delivers/update-presale?sellerId="+sellerId, UpdatePresaleModelResponse[].class,headers,
                        new Response.Listener<UpdatePresaleModelResponse[]>(){
                            @Override
                            public void onResponse(UpdatePresaleModelResponse[] response) {
                                view.hiddeNotificationSincronizastion();
                                view.completeSincronzation(Arrays.asList(response));
                            }

                        },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.showNotificationSincronization("Error al sincronizar venta ");
                    }
                }   , Request.Method.PUT,parser.toJson(updatePresaleModelRequests)
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

    @Override
    public void sendEndDayRecord(String date, String uid) {

    }
}
