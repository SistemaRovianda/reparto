package com.rovianda.reparto.clients.presenter;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.rovianda.reparto.clients.view.ClientsView;
import com.rovianda.reparto.clients.view.ClientsViewContract;

public class ClientsPresenter implements ClientsPresenterContract{

    private ClientsViewContract view;
    private Context context;
    private FirebaseAuth firebaseAuth;
    public ClientsPresenter(Context context, ClientsView view){
        this.context=context;
        this.view=view;
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void doLogout() {
        if(this.firebaseAuth!=null) {
            this.firebaseAuth.signOut();
        }
        view.goToLogin();
    }
}
