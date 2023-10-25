package com.rovianda.reparto.home.view;


import android.Manifest;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.rovianda.reparto.R;
import com.rovianda.reparto.home.adapters.AdapterPreSaleListItem;
import com.rovianda.reparto.home.adapters.AdapterPreSaleProductListItem;
import com.rovianda.reparto.home.adapters.ProductsPreSalesAdapter;
import com.rovianda.reparto.home.models.PreSaleItemForDetails;
import com.rovianda.reparto.home.models.UpdatePreSaleProductRequest;
import com.rovianda.reparto.home.models.UpdatePresaleModelRequest;
import com.rovianda.reparto.home.models.UpdatePresaleModelResponse;
import com.rovianda.reparto.home.presenter.HomePresenter;
import com.rovianda.reparto.home.presenter.HomePresenterContract;
import com.rovianda.reparto.utils.DatePickerFragment;
import com.rovianda.reparto.utils.PrinterUtil;
import com.rovianda.reparto.utils.ViewModelStore;
import com.rovianda.reparto.utils.bd.AppDatabase;
import com.rovianda.reparto.utils.bd.entities.Client;
import com.rovianda.reparto.utils.bd.entities.ClientVisit;
import com.rovianda.reparto.utils.bd.entities.EndingDay;
import com.rovianda.reparto.utils.bd.entities.PreSale;
import com.rovianda.reparto.utils.bd.entities.Product;
import com.rovianda.reparto.utils.bd.entities.SubSale;
import com.rovianda.reparto.utils.bd.entities.UserDataInitial;
import com.rovianda.reparto.utils.models.ModeOfflineSM;
import com.rovianda.reparto.utils.models.ModeOfflineSMP;
import com.rovianda.reparto.utils.models.PreSaleSincronizedResponse;
import com.rovianda.reparto.utils.models.ProductRoviandaToSale;
import com.rovianda.reparto.utils.models.SincronizationPreSaleResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class HomeView extends Fragment implements HomeViewContract, View.OnClickListener {

    private BluetoothAdapter bluetoothAdapter;
    private NavController navController;
    private TextView userNameTextView;
    private ViewModelStore viewModelStore;
    private HomePresenterContract presenter;
    private ImageView printerButton;
    private Button logoutButton, endDayButton;
    private Float amount;
    private CircularProgressIndicator circularProgressIndicator;
    private BottomNavigationView bottomMenu;
    private List<ProductRoviandaToSale> carList;
    private PrinterUtil printerUtil;
    private ListView listOfPresales;
    private boolean logoutPicked = false;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    private boolean sincronizateSession = false;
    private boolean isCreatingSale = false;
    private BluetoothDevice printer;
    private boolean printerConnected = false;
    private boolean isPickingPrinter = false;
    private int printerIndexSeleced=1;
    private String dateForPresale=null;
    private TextView currentDateTextView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, null);
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.navController = NavHostFragment.findNavController(this);
        this.userNameTextView = view.findViewById(R.id.userName);
        this.presenter = new HomePresenter(getContext(), this);
        this.printerButton = view.findViewById(R.id.printerButton);
        this.printerButton.setOnClickListener(this);
        this.logoutButton = view.findViewById(R.id.Logout_button);
        this.logoutButton.setOnClickListener(this);
        this.circularProgressIndicator = view.findViewById(R.id.loginLoadingSpinner);
        this.bottomMenu = view.findViewById(R.id.bottom_navigation_home);
        this.bottomMenu.setSelectedItemId(R.id.home_section);
        this.endDayButton = view.findViewById(R.id.end_day_button);
        this.endDayButton.setOnClickListener(this);
        this.printerUtil = new PrinterUtil(getContext());
        this.listOfPresales=  view.findViewById(R.id.listOfPresales);

        this.currentDateTextView = view.findViewById(R.id.currentDateTextView);

        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_section:
                        // PANTALLA ACTUAL
                        break;
                    case R.id.visitas_section:
                        goToVisits();
                        break;
                    case R.id.cliente_section:
                        goToClient();
                        break;
                    case R.id.history_section:
                        goToHistory();
                        break;
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        this.userNameTextView.setText("Usuario: " + this.viewModelStore.getStore().getUsername());
        checkIfSincronizate();
        checkIfPrinterConfigured();
        getPreSalesForToday();
        String currentDate = getCurrentDateStr();
        this.currentDateTextView.setText("Dia actual: " +currentDate);
    }
    private String getCurrentDateStr(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        return formater.format(calendar.getTime());
    }
    private void getPreSalesForToday(){
        String currentDate = getCurrentDateStr();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance(getContext());
                List<PreSale> allPresales = appDatabase.preSaleDao().getAllPreSalesUnSoldedByDeliverDate(currentDate);
                System.out.println("All PreSales: "+allPresales.size());
                List<PreSaleItemForDetails> preSaleItemForDetails = new ArrayList<>();
                for(PreSale preSale : allPresales){
                    PreSaleItemForDetails item = new PreSaleItemForDetails();
                    item.setAmount(preSale.amount);
                    item.setClientName(preSale.clientName);
                    item.setFolioPreSale(preSale.folio);
                    preSaleItemForDetails.add(item);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        fillListOfPreSalesOfToday(preSaleItemForDetails);
                    }
                });
            }
        });
    }

    void fillListOfPreSalesOfToday(List<PreSaleItemForDetails> preSaleItemForDetails){
        AdapterPreSaleListItem adapterPreSaleListItem = new AdapterPreSaleListItem(getContext(),this,preSaleItemForDetails);
        listOfPresales.setAdapter(adapterPreSaleListItem);
    }

    void checkIfPrinterConfigured() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                UserDataInitial userDataInitial = conexion.userDataInitialDao().getDetailsInitialByUid(viewModelStore.getStore().getSellerId());
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        if (userDataInitial != null && userDataInitial.printerMacAddress != null) {
                            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            if (bluetoothAdapter.isEnabled()) {
                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                                    for (BluetoothDevice bluetoothDevice : pairedDevices) {
                                        if (bluetoothDevice.getAddress().equals(userDataInitial.printerMacAddress)) {
                                            printer = bluetoothDevice;
                                            printerConnected = true;
                                            printerConnected();
                                        }
                                    }
                                    if (!printerConnected) {
                                        printerDisconnected();
                                    }
                                    return;
                                }
                            } else {
                                genericMessage("Bluetooth", "Activa el bluetooth");
                            }
                        }
                    }
                });
            }
        });
    }

    private void printerConnected() {
        ImageViewCompat.setImageTintList(printerButton, ColorStateList.valueOf(Color.parseColor("#BDB5B5")));
    }

    private void printerDisconnected() {
        ImageViewCompat.setImageTintList(printerButton, ColorStateList.valueOf(Color.parseColor("#39ED20")));
    }

    void checkIfSincronizate() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                UserDataInitial userDataInitials = conexion.userDataInitialDao().getDetailsInitialByUid(viewModelStore.getStore().getSellerId());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (userDataInitials == null) {
                            genericMessage("Alerta Sistema", "Requiere de su primera sincronización");
                            sincronizateSession = false;
                        } else {
                            viewModelStore.setUserDataInitial(userDataInitials);
                            sincronizateSession = true;
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Logout_button:
                if (!logoutPicked) {
                    logoutPicked = true;
                    logout();
                }
                break;

            case R.id.printerButton:
                if (this.printerConnected) {
                    this.printerConnected = false;
                    if (this.printerUtil != null) {
                        this.printerUtil.desconect();
                        this.printer = null;
                    }
                    this.printerDisconnected();
                } else {
                    if(!isPickingPrinter) {
                        isPickingPrinter = true;
                        activatePrinter();
                    }
                }
                break;
            case R.id.end_day_button:
                showDatePicker();
                break;
        }
    }

    void showDatePicker() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String monthStr = String.valueOf(month + 1);
                String day = String.valueOf(dayOfMonth);
                if ((month + 1) < 10) monthStr = "0" + monthStr;
                if (dayOfMonth < 10) day = "0" + day;
                String dateSelected = year + "-" + monthStr + "-" + day;
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String currentDateFormat = dateFormat.format(calendar.getTime());
                if (currentDateFormat.equals(dateSelected)) {
                    checkAllVisitsForCurrentDay(currentDateFormat);
                } else {
                    getEndDayTicketOffline(dateSelected);
                }
            }
        });
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    void checkAllVisitsForCurrentDay(String currentDate){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                Calendar calendar = Calendar.getInstance();
                int day =calendar.get(Calendar.DAY_OF_WEEK);
                List<Client> clients = new ArrayList<>();
                List<Client> withouPendingVisitRecord = new ArrayList<>();
                switch (day){
                    case Calendar.MONDAY:
                        clients = conexion.clientDao().getClientsMonday(viewModelStore.getStore().getSellerId());
                        break;
                    case Calendar.TUESDAY:
                        clients = conexion.clientDao().getClientsTuesday(viewModelStore.getStore().getSellerId());
                        break;
                    case Calendar.WEDNESDAY:
                        clients=conexion.clientDao().getClientsWednesday(viewModelStore.getStore().getSellerId());
                        break;
                    case Calendar.THURSDAY:
                        clients=conexion.clientDao().getClientsThursday(viewModelStore.getStore().getSellerId());
                        break;
                    case Calendar.FRIDAY:
                        clients = conexion.clientDao().getClientsFriday(viewModelStore.getStore().getSellerId());
                        break;
                    case Calendar.SATURDAY:
                        clients = conexion.clientDao().getClientsSaturday(viewModelStore.getStore().getSellerId());
                        break;
                }
                for(Client client : clients){
                    ClientVisit clientVisit=null;
                    if(client.clientRovId!=null && client.clientRovId!=0) {
                        clientVisit = conexion.clientVisitDao().getClientVisitByIdAndDate(client.clientRovId,currentDate);
                    }else{
                        clientVisit = conexion.clientVisitDao().getClientVisitByIdAndDate(client.clientMobileId,currentDate);
                    }
                    if(clientVisit==null){
                        withouPendingVisitRecord.add(client);
                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if(withouPendingVisitRecord.size()>0){
                            genericMessage("Visitas pendientes","Falta registrar visitas a clientes");
                        }else{
                            findEndDay(currentDate, "CLOSE");
                        }
                    }
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void activatePrinter() {
        if (!this.bluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
            isPickingPrinter=false;
        } else {
            printerUtil = new PrinterUtil(getContext());
            final Set<BluetoothDevice> deviceList = printerUtil.findDevices();
            if (deviceList.size() > 0) {
                findPrinter(deviceList);
            }else{
                isPickingPrinter=false;
                genericMessage("Sin dispositivos","Dispositivos bluetooth no encontrados");
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void findPrinter(Set<BluetoothDevice> devices) {

        List<BluetoothDevice> bluetoothDevicesMapped = new ArrayList<>();
        for (BluetoothDevice device : devices) {
            bluetoothDevicesMapped.add(device);
        }
        String[] bluetoothDevices = new String[devices.size()];
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            for (int i = 0; i < devices.size(); i++) {
                bluetoothDevices[i] = bluetoothDevicesMapped.get(i).getName();
            }
        }
        int checkedItem = 1;

        new MaterialAlertDialogBuilder(getContext()).setTitle("Selecciona la impresora bluetooth")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showErrorConnectingPrinter();
                        isPickingPrinter=false;
                    }
                }).setPositiveButton("Enlazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Index: "+which);
                        if(printerIndexSeleced!=-1) {
                            which=printerIndexSeleced;
                            System.out.println("Estableciendo conexión");
                            String printerName = bluetoothDevices[which];
                            printer = bluetoothDevicesMapped.get(which);
                            printerConnected = printerUtil.connectWithPrinter(printer);
                            if(printerConnected==true) {
                                connectionPrinterSuccess(printerName);
                                printerConnected();
                            }else {
                                showErrorConnectingPrinter();
                                printerDisconnected();
                            }
                            isPickingPrinter=false;
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    AppDatabase conexion = AppDatabase.getInstance(getContext());
                                    conexion.userDataInitialDao().updatePrinterAddress(viewModelStore.getStore().getSellerId(),printer.getAddress());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            System.out.println("Printer saved");
                                        }
                                    });
                                }
                            });

                        }
                    }
                }).setSingleChoiceItems(bluetoothDevices, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Se selecciono uno: "+which);
                        printerIndexSeleced=which;
                    }
                }).setCancelable(false).show();;
    }




    void findEndDay(String endDay, String type) { // METODO PARA VERIFICAR SI ES POSIBLE REALIZAR UNA VENTA SIEMPRE Y CUANDO NO SE HAYA CERRADO EL DIA
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                EndingDay endingDay = conexion.endingDayDao().getEndingDayByDate(endDay);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (type.equals("CLOSE")) {
                            if (endingDay != null) {
                                getEndDayTicketOffline(endDay);
                            } else {
                                showAlertEndDay(endDay);
                            }
                        }
                    }
                });
            }
        });
    }

    void showAlertEndDay(String currentDate) {// MODAL DE CIERRE DE DIA
        new MaterialAlertDialogBuilder(getContext()).setTitle("Cierre de día")
                .setMessage("¿Está seguro que desea cerrar las preventas del dia " + currentDate + ", (No podrá realizar otra preventa en esta fecha) ?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveEndDayRecord(currentDate);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isCreatingSale = false;
            }
        }).show();

    }

    void saveEndDayRecord(String endDay) { // METODO DE GUARDADO DE FIN DE DIA
        executor.execute(new Runnable() {
            @Override
            public void run() {

                EndingDay endingDay = new EndingDay();
                endingDay.date = endDay;
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                conexion.endingDayDao().saveEndingDay(endingDay);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        presenter.sendEndDayRecord(endDay, viewModelStore.getStore().getSellerId());
                        getEndDayTicketOffline(endDay);
                    }
                });
            }
        });
    }



    void tryToUpdatePreSales() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String currentDate=  getCurrentDateStr();
                String date1 = currentDate+"T00:00:00.000Z";
                String date2 = currentDate+"T23:59:59.000Z";
                AppDatabase conexion = AppDatabase.getInstance(getContext());
                List<PreSale> preSales = conexion.preSaleDao().getAllPreSalesSoldedByDateSolded(date1,date2);

                List<UpdatePresaleModelRequest> updatePresaleModelRequests = new ArrayList<>();

                for (PreSale preSale : preSales) {
                    List<SubSale> subSales = conexion.subSalesDao().getSubSalesBySale(preSale.folio);
                    UpdatePresaleModelRequest updateRequest = new UpdatePresaleModelRequest();
                    updateRequest.setDateSolded(preSale.dateSolded);
                    updateRequest.setFolioForSale(preSale.folioForSale);
                    updateRequest.setFolioPresale(preSale.folio);
                    updateRequest.setTypePayment(preSale.typePreSale);
                    updateRequest.setModificated(false);
                    if(preSale.modified!=null && preSale.modified){
                        updateRequest.setModificated(true);
                    }
                    List<UpdatePreSaleProductRequest> products = new ArrayList<>();
                    for(SubSale subSale : subSales){
                        UpdatePreSaleProductRequest product = new UpdatePreSaleProductRequest();
                        product.setAmount(subSale.price);
                        product.setPresentationId(subSale.presentationId);
                        product.setQuantity(subSale.quantity);
                        products.add(product);
                    }
                    updateRequest.setModifications(products);
                    updatePresaleModelRequests.add(updateRequest);
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (updatePresaleModelRequests.size() > 0) {
                            showNotificationSincronization("HOME Sincronizando...");
                            presenter.sincronizePreSales(updatePresaleModelRequests,viewModelStore.getStore().getSellerId());
                        } else {
                            showNotificationSincronization("Nada por sincronizar...");
                        }
                    }

                });
            }
        });
    }

    private void fillList() {
        amount = 0f;

        for (int i = 0; i < carList.size(); i++) {
            ProductRoviandaToSale product = carList.get(i);
            amount = product.getPrice() * product.getWeight();

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View view = layoutInflater.inflate(R.layout.item_list_product_sale, null);
            TextView code = (TextView) view.findViewById(R.id.productCode);
            TextView name = (TextView) view.findViewById(R.id.productName);
            TextView weight = (TextView) view.findViewById(R.id.productWeight);
            code.setText(product.getKeySae());
            name.setText(product.getNameProduct() + "\n" + product.getPresentationType());
            weight.setText(String.valueOf(product.getWeight()));
            final int index = i;
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    carList.remove(index);
                    fillList();
                    return true;
                }
            });

        }

    }



    void logout() {
        new MaterialAlertDialogBuilder(getContext()).setTitle("Cerrar sesión")
                .setMessage("¿Está seguro que desea cerrar sesión?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase conexion = AppDatabase.getInstance(getContext());
                        conexion.userDataInitialDao().updateAllLogedInFalse();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                presenter.doLogout();
                            }
                        });
                    }
                });

            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logoutPicked = false;
                dialog.dismiss();
            }
        }).setCancelable(false).show();
    }




    @Override
    public void goToLogin() {
        this.navController.navigate(HomeViewDirections.actionHomeViewToLoginView());
    }

    public void goToVisits() {
        this.navController.navigate(HomeViewDirections.actionHomeViewToVisitsView());
    }

    private void goToClient(){
        this.navController.navigate(HomeViewDirections.actionHomeViewToClientsView());
    }
    private void goToHistory(){
        this.navController.navigate(HomeViewDirections.actionHomeViewToHistoryView());
    }

    @Override
    public void showErrorConnectingPrinter() {
        new MaterialAlertDialogBuilder(getContext()).setTitle("Error de enlace")
                .setMessage("No se pudo enlazar a la impresora ").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        printerDisconnected();
                    }
                }).show();
    }

    @Override
    public void connectionPrinterSuccess(String printerName) {
        new MaterialAlertDialogBuilder(getContext()).setTitle("Enlace de impresora")
                .setMessage("Enlace exitoso con impresora : " + printerName).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                printerConnected();
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                printerConnected();
            }
        }).show();
    }

    int intentsToClose = 0;

    @Override
    public void saleSuccess(String ticket) {
        intentsToClose = 0;
        this.carList = new ArrayList<>();
        this.fillList();
        printTiket(ticket);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.setTitle("Imprimir ticket")
                .setNeutralButton("Terminar", null)
                .setPositiveButton("Reimprimir", null)
                .setCancelable(false).show();
        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setTextColor(Color.parseColor("#000000"));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //connectPrinter();
                printTiket(ticket);
            }
        });

        Button neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        neutral.setTextColor(Color.parseColor("#000000"));
        neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                tryToUpdatePreSales();
            }
        });
    }

    void printTiket(String ticket){
        Toast.makeText(getContext(),"Imprimiendo",Toast.LENGTH_LONG).show();
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    printerUtil.connectWithPrinter(printer);
                    sleep(3000);
                    printerUtil.IntentPrint(ticket);
                }catch (InterruptedException e){
                    System.out.println("Exception: "+e.getMessage());
                }
            }
        }.start();
    }


    @Override
    public void genericMessage(String title, String msg) {
        new MaterialAlertDialogBuilder(getContext()).setTitle(title)
                .setMessage(msg).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                }).show();
    }


    @Override
    public void showNotificationSincronization(String msg) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "rovisapireparto")
                .setSmallIcon(R.drawable.ic_logorov)
                .setContentTitle("Sistema Rovianda")
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void hiddeNotificationSincronizastion() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.cancel(1);
    }

    @Override
    public void completeSincronzation(List<UpdatePresaleModelResponse> response) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance(getContext());
                for(UpdatePresaleModelResponse itemResponse : response){
                    PreSale preSale = appDatabase.preSaleDao().getByFolio(itemResponse.getFolioPreSale());
                    preSale.sincronized=true;
                    appDatabase.preSaleDao().updateSale(preSale);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });

    }

    void getEndDayTicketOffline(String date) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Double weightG = Double.parseDouble("0");
                int totalTickets=0;
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm a");
                String dateParsed = dateFormat.format(calendar.getTime());
                String ticket = "\nReporte de cierre\nVendedor: "+viewModelStore.getStore().getUsername()+"\nFecha: "+dateParsed+"\n------------------------\n";
                ticket+="ART.   DESC    CANT    PRECIO  IMPORTE\n";
                Map<String,String> skus = new HashMap<>();
                Map<String,Float> pricesBySku = new HashMap<>();
                Map<String,Float> weightTotal = new HashMap<>();
                Map<String,Float> piecesTotal = new HashMap<>();
                Map<String,Float> amountTotal = new HashMap<>();
                Float efectivo=Float.parseFloat("0");
                Float credito=Float.parseFloat("0");
                Float transferencia=Float.parseFloat("0");
                Float cheque=Float.parseFloat("0");
                Float creditCob = Float.parseFloat("0");
                Float iva = Float.parseFloat("0");
                String clientsStr="";

                AppDatabase conexion = AppDatabase.getInstance(getContext());
                List<PreSale> salesSoldedOfday = conexion.preSaleDao().getAllPreSalesSoldedByDateSolded(date+"T00:00:00.000Z",date+"T23:59:59.000Z");
                for(PreSale preSale : salesSoldedOfday){
                    totalTickets++;

                    List<SubSale> subSales = conexion.subSalesDao().getSubSalesBySale(preSale.folio);
                    Float amountOfSale=0f;
                    amountOfSale=preSale.amount;

                        for (SubSale subSale : subSales) {
                            String productName = skus.get(subSale.productKey);
                            if (productName == null) {
                                skus.put(subSale.productKey,subSale.productName + " " + subSale.productPresentationType);
                            }
                            Float weight = weightTotal.get(subSale.productKey);
                            if (weight == null) {
                                Float weightByProduct = (subSale.uniMed.toLowerCase().equals("pz") ? subSale.quantity * subSale.weightStandar : subSale.quantity);
                                weightTotal.put(subSale.productKey, weightByProduct);
                                weightG += weightByProduct;
                            } else {
                                weight += (subSale.uniMed.toLowerCase().equals("pz") ? subSale.quantity * subSale.weightStandar : subSale.quantity);
                                weightTotal.put(subSale.productKey, weight);
                                weightG += (subSale.uniMed.toLowerCase().equals("pz") ? subSale.quantity * subSale.weightStandar : subSale.quantity);
                            }

                            Float amountByProduct = pricesBySku.get(subSale.productKey);
                            if (amountByProduct == null) {
                                amount = subSale.price / subSale.quantity;
                                pricesBySku.put(subSale.productKey, amount);
                            }

                            Float amountSubSale = amountTotal.get(subSale.productKey);
                            if (amountSubSale == null) {
                                Float amount = subSale.price;
                                amountTotal.put(subSale.productKey, amount);
                            }else{
                                amountSubSale+=subSale.price;
                                amountTotal.put(subSale.productKey, amountSubSale);
                            }
                            if(subSale.uniMed.toLowerCase().equals("pz")){
                                Float piecesOfProduct = piecesTotal.get(subSale.productKey);
                                if(piecesOfProduct==null){
                                    piecesTotal.put(subSale.productKey,subSale.quantity);
                                }else{
                                    piecesTotal.put(subSale.productKey,(piecesOfProduct+subSale.quantity));
                                }
                            }
                        }

                        clientsStr += "\n" + preSale.folioForSale + " " + preSale.clientName +" "+preSale.keyClient+ "\n $" + amountOfSale + " " + ((preSale.typePreSale.equals("CREDITO")) ? "C" : (preSale.typePreSale.equals("TRANSFERENCIA")?"TRANSFER":"")) + " ";

                        if (preSale.typePreSale.equals("CREDITO")) {
                            credito += amountOfSale;
                        } else if (preSale.typePreSale.equals("TRANSFERENCIA")) {
                            transferencia += amountOfSale;
                        } else if (preSale.typePreSale.equals("EFECTIVO")) {
                            efectivo += amountOfSale;
                        } else if (preSale.typePreSale.equals("CHEQUE")) {
                            cheque += amountOfSale;
                        }

                }

                List<PreSale> debtsPayed = conexion.preSaleDao().getAllPreSalesPayedByDateSolded(date+"T00:00:00.000Z",date+"T23:59:59.000Z");
                for(PreSale preSale : debtsPayed){
                    creditCob += preSale.amount;
                }
                List<String> allSkus = new ArrayList<>();
                for(String sku : skus.keySet()){
                    allSkus.add(sku);
                }
                Collections.sort(allSkus, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });
                for(String sku : allSkus){
                    String productName = skus.get(sku);
                    Float weight = weightTotal.get(sku);
                    Float price = pricesBySku.get(sku);
                    Float amount = amountTotal.get(sku);
                    Float totalPieces = piecesTotal.get(sku);
                    if(sku.length()>5){
                        sku=sku.substring(sku.length()-5,sku.length());
                    }
                    if(totalPieces==null) {
                        ticket += "\n" + sku + " " + productName + "\n" + String.format("%.02f", weight) + " $" + String.format("%.02f",price) + " $" + String.format("%.02f", amount);
                    }else{
                        ticket += "\n" + sku + " " + productName + "\n"+Math.round(totalPieces)+ " pz " + String.format("%.02f", weight) + " $" + String.format("%.02f",price) + " $" + String.format("%.02f", amount);
                    }
                }
                ticket+="\n-----------------------------------------\nDOC NOMBRE  CLIENTE IMPORTE TIPOVENTA\n-----------------------------------------\n";
                ticket+=clientsStr+"\n\n";
                ticket+="Total de notas: "+totalTickets+"\n";
                ticket+="VENTAS POR CONCEPTO\nEFECTIVO: $"+String.format("%.02f",efectivo)+"\nCREDITO: $"+String.format("%.02f",credito)+"\nTRANSFERENCIA: $"+String.format("%.02f",transferencia)+"\nCHEQUE: $"+String.format("%.02f",cheque)+"\n";
                ticket+="TOTAL KILOS: "+String.format("%.02f",weightG)+"\nVENTA TOTAL:$ "+String.format("%.02f",efectivo+transferencia+cheque+credito)+"\n";
                ticket+="Recup. Cobranza\n$ "+String.format("%.02f",creditCob)+"\n";
                ticket+="\n\n\n\n";
                String ticketCreated = ticket;
                ///adeudos pendientes
                System.out.println("Ticket: "+ticket);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        circularProgressIndicator.setVisibility(View.INVISIBLE);
                        printTiket(ticketCreated);
                    }
                });
            }
        });
    }


    @Override
    public void getPreSaleDetails(String folioPreSale,String option) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance(getContext());
                PreSale preSale = appDatabase.preSaleDao().getByFolio(folioPreSale);
                List<SubSale> subSales = appDatabase.subSalesDao().getSubSalesBySale(folioPreSale);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(preSale!=null && subSales.size()>0){
                            if(option.equals("DETAILS")) {
                                showModalDetailsOfPreSale(preSale, subSales);
                            }else{
                                showModalModification(preSale,subSales);
                            }
                        }
                    }
                });
            }
        });
    }

    public List<SubSale> subSalesForModification;
    public TextView textViewAmountMofiication;
    public LinearLayout listProductsToEdit;
    Float amountForModifications = 0f;
    public void showModalModification(PreSale preSale,List<SubSale> subSales){
        this.subSalesForModification = subSales;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.modal_presale_modification,null);
        TextView presaleModFolio = view.findViewById(R.id.presaleModFolio);
        textViewAmountMofiication = view.findViewById(R.id.presaleModAmount);
        listProductsToEdit = view.findViewById(R.id.listProductsToEdit);
        addViewsToForPresaleEdit(subSales);
        presaleModFolio.setText(preSale.folio);
        textViewAmountMofiication.setText(String.format("%.02f",preSale.amount));
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button modificateButton = view.findViewById(R.id.modificateButton);

        builder.setView(view);

        AlertDialog alertDialog = builder.show();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        modificateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(subSalesForModification.isEmpty()){
                    genericMessage("Error","No se puede crear una preventa sin productos");
                }else if(amountForModifications==0f){
                    genericMessage("Error","No se puede crear la preventa con monto: $0");
                }else{
                    alertDialog.dismiss();
                    preSale.amount=Float.parseFloat(String.format("%.02f",amountForModifications));
                    preSale.modified=true;
                    showModalDetailsOfPreSale(preSale,subSalesForModification);
                }
            }
        });

    }

    public void addViewsToForPresaleEdit(List<SubSale> subSales){
        this.listProductsToEdit.removeAllViews();
        for(int position =0;position<subSales.size();position++){
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View listItemV = layoutInflater.inflate(R.layout.presale_products_modification,null);
            TextView productName = listItemV.findViewById(R.id.productName);
            TextInputLayout inputQuantity= listItemV.findViewById(R.id.inputQuantity);
            ImageButton delProduct = listItemV.findViewById(R.id.delProduct);
            SubSale product = subSales.get(position);
            productName.setText(product.productName+" "+product.productPresentationType);
            inputQuantity.getEditText().setText(String.valueOf(product.quantity));
            Float priceUnique=product.price/product.quantity;
            delProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSubSalesForModification(product,"DELETE");
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
                    setSubSalesForModification(product,"MODIFICATE");
                }
            });

            this.listProductsToEdit.addView(listItemV);
        }
    }


    @Override
    public void setSubSalesForModification(SubSale subSale, String typeOperation) {
        List<SubSale> subSalesToFiltered =new ArrayList<>();
        amountForModifications = 0f;
        if(typeOperation.equals("MODIFICATE")){

            for(SubSale subSale1 : subSalesForModification){
                if(subSale1.presentationId!=subSale.presentationId) {
                    subSalesToFiltered.add(subSale1);
                    amountForModifications+=subSale1.price;
                }else{
                    subSalesToFiltered.add(subSale);
                    amountForModifications+=subSale.price;
                }
            }
        }else{
           for(SubSale subSale1 : subSalesForModification) {
               if (subSale1.presentationId != subSale.presentationId) {
                   subSalesToFiltered.add(subSale1);
                   amountForModifications+=subSale1.price;
               }
           }
            addViewsToForPresaleEdit(subSalesToFiltered);
           /*AdapterPreSaleProductListItem adapter = new AdapterPreSaleProductListItem(this,getContext(),subSalesToFiltered);
           listProductsToEdit.setAdapter(adapter);
            */
        }
        subSalesForModification=subSalesToFiltered;
        textViewAmountMofiication.setText("Monto: "+String.format("%.02f",amountForModifications));
    }

    @Override
    public void showOptionsForPresale(String folioPreSale) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.modal_options_for_presale,null);
        Button optionDetailsButton = view.findViewById(R.id.optionDetailsButton);
        Button optionModificateButton = view.findViewById(R.id.optionModificateButton);
        builder.setView(view);
        android.app.AlertDialog dialog = builder.show();
        dialog.show();
        optionDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                getPreSaleDetails(folioPreSale,"DETAILS");
            }
        });
        optionModificateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                getPreSaleDetails(folioPreSale,"MODIFICATION");
            }
        });
    }

    private void showModalDetailsOfPreSale(PreSale preSale, List<SubSale> subSales){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.presale_details_modal,null);
        TextView clientNameTextView=  view.findViewById(R.id.clientNameTextView);
        TextView preSaleFolioTextView = view.findViewById(R.id.preSaleFolioTextView);
        ListView listOfProductsOfPreSale = view.findViewById(R.id.listProductsOfPreSale);
        TextView totalAmountForPreSale=  view.findViewById(R.id.totalAmountForPreSale);
        Button cancelSaleOfPresaleButton = view.findViewById(R.id.cancelSaleOfPresaleButton);
        Button genSaleOfPresaleButton=  view.findViewById(R.id.genSaleOfPresaleButton);

        ProductsPreSalesAdapter productsPreSalesAdapter = new ProductsPreSalesAdapter(getContext(),subSales,this);
        listOfProductsOfPreSale.setAdapter(productsPreSalesAdapter);

        clientNameTextView.setText("Cliente: "+preSale.clientName);
        preSaleFolioTextView.setText("Folio Preventa: "+preSale.folio);
        totalAmountForPreSale.setText("Monto total: $"+String.format("%.02f",preSale.amount));
        amountForModifications = preSale.amount;
        subSalesForModification=subSales;
        builder.setView(view);
        builder.setCancelable(false);
        android.app.AlertDialog modal = builder.show();
        modal.show();
        cancelSaleOfPresaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modal.dismiss();
            }
        });
        genSaleOfPresaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modal.dismiss();
                getInfoClientForPayment(preSale,subSales);
            }
        });
    }
    int selectionPay = 0;
    String[] contadoOptions = {"EFECTIVO", "TRANSFERENCIA", "CHEQUE"};
    String[] creditoOptions = {"CREDITO", "EFECTIVO"};

    void getInfoClientForPayment(PreSale preSale,List<SubSale> subSales){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance(getContext());
                Client client = appDatabase.clientDao().getClientBydId(preSale.clientId);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(preSale!=null && client!=null) {
                            showOptionsPayed(preSale,subSales, client.type);
                        }
                    }
                });
            }
        });
    }
    String[] selectMode = null;
    void showOptionsPayed(PreSale preSale,List<SubSale> subSales,String type) {
        this.selectionPay=0;
        this.selectMode=null;
            if (type.equals("CONTADO")) {
                selectMode = contadoOptions;
            } else {
                selectMode = creditoOptions;
            }

            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getContext()).setTitle("Seleccione el tipo de pago")
                    .setSingleChoiceItems(selectMode, selectionPay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("Selected: " + which);
                            selectionPay = which;

                        }
                    }).setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectionPay=0;
                        }
                    }).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            doSaleModalConfirmation(preSale,subSales,selectMode[selectionPay]);
                        }
                    }).setCancelable(false).create();
            dialog.show();
    }

    void doSaleModalConfirmation(PreSale preSale,List<SubSale> subSales,String typePayment){
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Cobrar");
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view= layoutInflater.inflate(R.layout.pay_modal, null);
        TextView amountView = view.findViewById(R.id.totalApagarInput);
        amountView.setText("Total a cobrar: "+preSale.amount);
        TextInputLayout textInputLayout = view.findViewById(R.id.pagoCon);
        textInputLayout.getEditText().setEnabled(false);
        textInputLayout.getEditText().setText(String.valueOf(preSale.amount));
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!textInputLayout.getEditText().getText().toString().isEmpty()) {
                    System.out.println("acepto el cobró");
                    updateToPayedPreSale(preSale,subSales,typePayment);
                }
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                genericMessage("Nota de Venta no generada","Se canceló el cobro");
            }
        });
        builder.show();
    }

    void updateToPayedPreSale(PreSale preSale,List<SubSale> subSales,String typePayment){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance(getContext());
                UserDataInitial userDataInitial = appDatabase.userDataInitialDao().getDetailsInitialByUid(viewModelStore.getStore().getSellerId());
                Map<String,Product> productsMap = new HashMap<>();
                Integer folioCount = userDataInitial.count;
                preSale.typePreSale=typePayment;
                if(typePayment.equals("CREDITO")){
                    preSale.payed=false;
                }else{
                    preSale.payed=true;
                }
                preSale.amount=amountForModifications;
                preSale.folioForSale=userDataInitial.nomenclature+(folioCount+1);
                preSale.solded=true;
                TimeZone tz = TimeZone.getTimeZone("UTC");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
                df.setTimeZone(tz);
                Calendar calendar = Calendar.getInstance();
                String nowAsISO = df.format(calendar.getTime());
                preSale.dateSolded=nowAsISO;
                List<SubSale> subSalesToFilter = appDatabase.subSalesDao().getSubSalesBySale(preSale.folio);
                List<Integer> subSalesValidsIds = new ArrayList<>();
                userDataInitial.count=folioCount+1;
                appDatabase.userDataInitialDao().updateUserData(userDataInitial);
                for(SubSale subSale : subSales){
                    Product product = appDatabase.productDao().getProductByKey(subSale.productKey);
                    productsMap.put(product.productKey,product);
                    subSalesValidsIds.add(subSale.subSaleId);
                    appDatabase.subSalesDao().updateSubSale(subSale);
                }
                for(SubSale item : subSalesToFilter){
                    if(!subSalesValidsIds.contains(item.subSaleId)){
                        appDatabase.subSalesDao().deleteSubSale(item.subSaleId);
                    }
                }
                appDatabase.preSaleDao().updateSale(preSale);
                String currentDateFormat =getCurrentDateStr();
                ClientVisit clientVisit=appDatabase.clientVisitDao().getClientVisitByIdAndDate(preSale.clientId,currentDateFormat);
                if(clientVisit==null){
                    clientVisit = new ClientVisit();
                    clientVisit.isClientIdTemp=false;
                    clientVisit.clientId=preSale.clientId;
                    clientVisit.visited=true;
                    clientVisit.observations="";
                    clientVisit.sincronized=false;
                    clientVisit.amount=preSale.amount;
                    clientVisit.date=currentDateFormat;
                    appDatabase.clientVisitDao().insertClientVisit(clientVisit);
                }else{
                    clientVisit.amount+=preSale.amount;
                    clientVisit.sincronized=false;
                    clientVisit.visited=true;
                    clientVisit.observations="";
                    appDatabase.clientVisitDao().updateClientVisit(clientVisit);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(subSales.size()>0) {
                            getPreSalesForToday();
                            doTicketSaleForSoldedOffline(preSale, subSales, productsMap);
                        }
                    }
                });
            }
        });
    }

    void doTicketSaleForSoldedOffline(PreSale preSale,List<SubSale> subSales,Map<String,Product> productsMap){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm a");
        String dateParsed = dateFormat.format(cal.getTime());
        String ticket = "ROVIANDA SAPI DE CV\nAV.1 #5 Esquina Calle 1\nCongregación Donato Guerra\nParque Industrial Valle de Orizaba\nC.P 94780\nRFC 8607056P8\nTEL 272 72 46077, 72 4 5690\n";
        ticket+="Pago en una Sola Exhibición\nLugar de Expedición: Ruta\nNota No. "+preSale.folioForSale+"\nFecha: "+dateParsed+"\n\n";
        ticket+="Vendedor:"+viewModelStore.getStore().getUsername()+"\n\nCliente: "+preSale.clientName+"\n"+"Clave:"+preSale.keyClient+"\n";
        ticket+="Tipo de venta: "+ preSale.typePreSale +"\n--------------------------------\nDESCR   PRECIO   CANT  IMPU.   IMPORTE \n--------------------------------\n";
        Float total = Float.parseFloat("0");
        Float totalImp = Float.parseFloat("0");
        for(SubSale product : subSales){
            Product product1 = productsMap.get(product.productKey);
            Float singleIva = Float.parseFloat("0");
            Float singleIeps = Float.parseFloat("0");
            Float amount = (product.price/product.quantity);
            switch (product1.esqKey){
                case 1:
                    singleIva=this.extractIva(amount);
                    break;
                case 4:
                    singleIva=this.extractIva(amount);
                    singleIeps=this.extractIeps((amount-this.extractIva(amount)),Float.parseFloat("8"));
                    break;
                case 5:
                    singleIva=this.extractIva(amount);
                    singleIeps=this.extractIeps((amount-this.extractIva(amount)),Float.parseFloat("25"));
                    break;
                case 6:
                    singleIva=this.extractIva(amount);
                    singleIeps=this.extractIeps((amount-this.extractIva(amount)),Float.parseFloat("50"));
                    break;
            }
            Float singlePrice=amount-(singleIva+singleIeps);
            totalImp+=((singleIva+singleIeps)*product.quantity);
            if(product.uniMed.equals("PZ")) {
                ticket += product.productName + " " + product.productPresentationType + "\n" + String.format("%.02f",singlePrice) +" "+ Math.round(product.quantity) + "pz " +String.format("%.02f",(singleIeps+singleIva)*product.quantity)  +" "+String.format("%.02f",product.price) +"\n";
            }else{
                ticket += product.productName + " " + product.productPresentationType + "\n"+ Math.round(singlePrice) +" " + product.quantity + "kg "+ String.format("%.02f",(singleIeps+singleIva)*product.quantity) +" "+String.format("%.02f",product.price)+ "\n";
            }
            total+=product.price;
        }
        ticket+="--------------------------------\n";
        ticket+="SUB TOTAL: $"+String.format("%.02f",total-totalImp)+"\n";
        ticket+="IMPUESTO:  $"+String.format("%.02f",totalImp)+"\n";
        ticket+="TOTAL: $ "+String.format("%.02f",total)+"\n\n\n";
        //ticket+="ticket+=`Piezas:  \n\n*** GRACIAS POR SU COMPRA ***\n";
        if(preSale.typePreSale.equals("CREDITO")){
            ticket+="Esta venta se incluye en la\nventa global del dia, por el\npresente reconozco deber\ny me obligo a pagar en esta\nciudad y cualquier otra que\nse me de pago a la orden de\nROVIANDA S.A.P.I. de C.V. la\ncantidad que se estipula como\ntotal en el presente documento.\n-------------------\n      Firma\n\n";
            ticket+=(preSale.payed)?"\nSE ADEUDA\n\n\n\n\n":"\nPAGADO\n\n\n\n\n";
        }
        System.out.println(ticket);
        ticket+=ticket;

        saleSuccess(ticket);
    }
    Float extractIva(Float amount) {
        return (amount / 116) * 16;
    }
    Float extractIeps(Float amount, Float percent) {
        return (amount / (100 + percent)) * percent;
    }
}
