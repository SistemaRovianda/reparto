package com.rovianda.reparto.history.view;

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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.rovianda.reparto.R;
import com.rovianda.reparto.history.adapter.PreSaleListAdapter;
import com.rovianda.reparto.history.adapter.SaleDebtListAdapter;
import com.rovianda.reparto.history.models.ModelDebtDeliverRequest;
import com.rovianda.reparto.history.models.ModelDebtDeliverResponse;
import com.rovianda.reparto.history.presenter.HistoryPresenter;
import com.rovianda.reparto.history.presenter.HistoryPresenterContract;
import com.rovianda.reparto.home.view.HomeViewContract;
import com.rovianda.reparto.utils.DatePickerFragment;
import com.rovianda.reparto.utils.PrinterUtil;
import com.rovianda.reparto.utils.ViewModelStore;
import com.rovianda.reparto.utils.bd.AppDatabase;
import com.rovianda.reparto.utils.bd.entities.Client;
import com.rovianda.reparto.utils.bd.entities.Debt;
import com.rovianda.reparto.utils.bd.entities.PreSale;
import com.rovianda.reparto.utils.bd.entities.Product;
import com.rovianda.reparto.utils.bd.entities.SubSale;
import com.rovianda.reparto.utils.bd.entities.UserDataInitial;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryView extends Fragment implements HistoryViewContract,View.OnClickListener {

    private Button endDayButon,logoutButton;
    private TextView totalCash,totalWeight;
    private ViewModelStore viewModelStore;
    private ImageView printerButton;
    private BluetoothDevice printer;
    private boolean isPrinterConnected=false;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private PrinterUtil printerUtil;
    private boolean isPickingPrinter=false;
    private BluetoothAdapter bluetoothAdapter;
    private int printerIndexSeleced=1;
    private TextView userNameTextView,fromDate;
    private ImageView changeDateButton;
    private ListView listSales;
    private HistoryPresenterContract presenter;
    private Button changeHistoryDebtsButton;
    private String currentSection;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history,null);
        endDayButon=view.findViewById(R.id.end_day_button);
        endDayButon.setVisibility(View.GONE);
        logoutButton=view.findViewById(R.id.Logout_button);
        logoutButton.setVisibility(View.GONE);
        this.bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        this.printerUtil = new PrinterUtil(getContext());

        this.userNameTextView = view.findViewById(R.id.userName);
        this.fromDate = view.findViewById(R.id.fromDate);
        this.changeDateButton=  view.findViewById(R.id.changeDateButton);
        this.changeDateButton.setOnClickListener(this);
        totalCash = view.findViewById(R.id.totalCash);
        totalCash.setVisibility(View.VISIBLE);
        totalWeight=view.findViewById(R.id.totalWeight);
        totalWeight.setVisibility(View.VISIBLE);
        printerButton = view.findViewById(R.id.printerButton);
        navController= NavHostFragment.findNavController(this);

        listSales = view.findViewById(R.id.listSales);

        bottomNavigationView=view.findViewById(R.id.bottom_navigation_history);
        bottomNavigationView.setSelectedItemId(R.id.history_section);
        this.presenter = new HistoryPresenter(getContext(),this);
        this.changeHistoryDebtsButton = view.findViewById(R.id.changeHistoryDebtsButton);
        this.changeHistoryDebtsButton.setOnClickListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_section:
                        goToHome();
                        break;
                    case R.id.visitas_section:
                        goToVisits();
                        break;
                    case R.id.cliente_section:
                        goToClients();
                        break;
                    case R.id.history_section:
                        // PANTALLA ACTUAL
                        break;
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void goToHome() {
        this.navController.navigate(HistoryViewDirections.actionHistoryViewToHomeView());
    }

    @Override
    public void goToVisits() {
        this.navController.navigate(HistoryViewDirections.actionHistoryViewToVisitsView());
    }

    @Override
    public void goToClients() {
        this.navController.navigate(HistoryViewDirections.actionHistoryViewToClientsView());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.viewModelStore = new ViewModelProvider(requireActivity()).get(ViewModelStore.class);
        if(this.viewModelStore!=null && this.viewModelStore.getStore()!=null) {
            this.userNameTextView.setText(this.viewModelStore.getStore().getUsername());
        }
        checkIfPrinterConfigured();
        Calendar calendar = Calendar.getInstance();
        String currentDate = getDateStr(calendar.getTime());
        this.fromDate.setText("Ventas: "+currentDate);
        this.currentSection="HISTORY";
        fillPreSalesOffline(currentDate);
    }



    String getDateStr(Date date){
        SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public void setDebtsSalesOfDay(List<PreSale> preSales) {
        listSales.removeAllViewsInLayout();
        SaleDebtListAdapter saleDebtListAdapter = new SaleDebtListAdapter(getContext(),preSales,this);
        listSales.setAdapter(saleDebtListAdapter);
    }
    public void setSalesOfDay(List<PreSale> preSales) {
        listSales.removeAllViewsInLayout();
        PreSaleListAdapter preSaleListAdapter = new PreSaleListAdapter(getContext(),preSales,this);
        listSales.setAdapter(preSaleListAdapter);
    }

    void fillPreSalesDebtsOffline(){

        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance(getContext());

                List<PreSale> preSales = appDatabase.preSaleDao().getAllPreSalesCreditWithoutPayment();
                List<PreSale> preSalesAlreadyPayed = appDatabase.preSaleDao().getAllPreSalesCreditWithPayment();
                List<PreSale> allPreSalesDebts = new ArrayList<>();
                for(PreSale preSale : preSales){
                    allPreSalesDebts.add(preSale);
                }
                for(PreSale preSale : preSalesAlreadyPayed){
                    allPreSalesDebts.add(preSale);
                }


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Total presales: "+allPreSalesDebts.size());
                        setDebtsSalesOfDay(allPreSalesDebts);
                        checkAccumulatedOffline(new ArrayList<>());
                    }
                });
            }
        });
    }

    void fillPreSalesOffline(String date){
        String date1 = date+"T00:00:00.000Z";
        String date2 = date+"T23:59:59.000Z";
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance(getContext());
                List<SubSale> subSales = new ArrayList<>();
                List<PreSale> preSales = appDatabase.preSaleDao().getAllPreSalesSoldedByDateSolded(date1,date2);
                for(PreSale preSale : preSales){
                    List<SubSale> subSales1 = appDatabase.subSalesDao().getSubSalesBySale(preSale.folio);
                    for(SubSale subSale : subSales1){
                        subSales.add(subSale);
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Total presales: "+preSales.size());
                        setSalesOfDay(preSales);
                        checkAccumulatedOffline(subSales);
                    }
                });
            }
        });
    }

    void checkAccumulatedOffline(List<SubSale> subSales){
        Float weightG=0f;
        Float AmountG=0f;
        for (SubSale subSale : subSales) {
            if (subSale.uniMed.toLowerCase().equals("pz")) {
                weightG += subSale.weightStandar * subSale.quantity;
            } else {
                weightG += subSale.quantity;
            }
            AmountG += subSale.price;
        }
        this.totalCash.setText("TOTAL VENDIDO\n$ "+String.format("%.02f",AmountG));
        this.totalWeight.setText("PESO TOTAL\n"+String.format("%.02f",weightG)+" KG");
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
                                            isPrinterConnected = true;
                                            printerConnected();
                                        }
                                    }
                                    if (!isPrinterConnected) {
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.printerButton:
                if (this.isPrinterConnected) {
                    this.isPrinterConnected = false;
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
            case R.id.changeDateButton:
                    showDatePicker();
                break;
            case R.id.changeHistoryDebtsButton:
                changeSectionView();
                break;
        }
    }

    void changeSectionView(){
        if(this.currentSection.equals("HISTORY")){
            this.currentSection="DEBTS";
            this.changeHistoryDebtsButton.setText("Ir a Historial");
        }else{
            this.currentSection="HISTORY";
            this.changeHistoryDebtsButton.setText("Ir a Cobranza");
        }
        fillBySection(this.currentSection);
    }
    void fillBySection(String section){
        Calendar calendar = Calendar.getInstance();
        String date = getDateStr(calendar.getTime());
        if(section.equals("DEBTS")){
            changeDateButton.setEnabled(false);
            changeDateButton.setVisibility(View.GONE);
            fillPreSalesDebtsOffline();
        }else{
            changeDateButton.setEnabled(true);
            changeDateButton.setVisibility(View.VISIBLE);
            fillPreSalesOffline(date);
        }
    }

    void showDatePicker(){
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String monthStr = String.valueOf(month+1);
                String day = String.valueOf(dayOfMonth);
                if((month+1)<10) monthStr="0"+monthStr;
                if(dayOfMonth<10) day="0"+day;
                String currentDate = year+"-"+monthStr+"-"+day;
                fromDate.setText("Ventas: "+currentDate);
                fillPreSalesOffline(currentDate);
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(),"datePicker");
    }
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
                genericMessage("Sin dispositivos","Dispositivos bluetooth no encontrados");
            }
        }
    }

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
                    isPrinterConnected = printerUtil.connectWithPrinter(printer);
                    if(isPrinterConnected==true) {
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


    @Override
    public void showOptionsSale(PreSale preSale){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view =layoutInflater.inflate(R.layout.option_presales_modal,null);
        builder.setView(view);
        AlertDialog dialog=builder.show();

        Button reprintButton = view.findViewById(R.id.optionReprintButton);

        reprintButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                checkPrinterConnectionOffline(preSale.folio);
            }
        });

    }
    public void checkPrinterConnectionOffline(String folio){
        if(this.printerUtil==null){
            this.printerUtil = new PrinterUtil(getContext());
        }
        this.printTicketSale(folio);
    }

    @Override
    public void printTicketSale(String folio){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase conexion=AppDatabase.getInstance(getContext());
                PreSale preSale = conexion.preSaleDao().getByFolio(folio);
                List<SubSale> subSales = conexion.subSalesDao().getSubSalesBySale(folio);
                Map<String, Product> productsMap = new HashMap<>();
                for(SubSale subSale : subSales){
                    Product product = conexion.productDao().getProductByKey(subSale.productKey);
                    productsMap.put(subSale.productKey,product);
                }

                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        if(preSale!=null){
                            doTicketSaleForSoldedOffline(preSale,subSales,productsMap);
                        }
                    }
                });

            }
        });
    }

    void doTicketSaleForSoldedOffline(PreSale preSale, List<SubSale> subSales, Map<String,Product> productsMap){
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
            ticket+=(!preSale.payed)?"\nSE ADEUDA\n\n\n\n\n":"\nPAGADO\n\n\n\n\n";
        }
        System.out.println(ticket);
        ticket+=ticket;

        reprintTicket(ticket);
    }

    Float extractIva(Float amount){
        return (amount/116)*16;
    }

    Float extractIeps(Float amount,Float percent){
        return (amount/(100+percent))*percent;
    }

    public void reprintTicket(String ticket) {

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
                System.out.println("Ticket: "+ticket);
                printTiket(ticket);
            }
        });

        Button neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        neutral.setTextColor(Color.parseColor("#000000"));
        neutral.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void printTiket(String ticket){
        if(printerUtil==null){
            printerUtil = new PrinterUtil(getContext());
        }
        Toast.makeText(getContext(), "Imprimiendo", Toast.LENGTH_LONG).show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    printerUtil.connectWithPrinter(printer);
                    sleep(3000);
                    printerUtil.IntentPrint(ticket);
                } catch (InterruptedException e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            }
        }.start();

    }

    @Override
    public void checkPaydeb(PreSale preSale) {
        showOptionsPayed(preSale);
    }

    int selectionPay=0;
    Boolean paying=false;
    Float amount;
    String[] contadoOptions = {"EFECTIVO","TRANSFERENCIA","CHEQUE"};
    void showOptionsPayed(PreSale preSale){
        if(!paying) {
            String[] selectMode = contadoOptions;
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext()).setTitle("Seleccione el tipo de pago")
                    .setSingleChoiceItems(selectMode, selectionPay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("Selected: " + which);
                            selectionPay = which;
                        }
                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {

                            paying = false;
                        }
                    }).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            doSaleModalConfirmation(preSale);
                        }
                    }).setCancelable(false);

            android.app.AlertDialog dialog = builder.create();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            paying = false;
                        }
                    });
            dialog.show();
        }
    }

    public void doSaleModalConfirmation(PreSale preSale){
        amount = preSale.amount;

        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Cobrar");

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view= layoutInflater.inflate(R.layout.pay_modal, null);
        TextView amountView = view.findViewById(R.id.totalApagarInput);
        amountView.setText("Total a cobrar: "+String.format("%.02f",amount));
        TextInputLayout textInputLayout = view.findViewById(R.id.pagoCon);
        textInputLayout.getEditText().setEnabled(false);
        textInputLayout.getEditText().setText(String.valueOf(amount));

        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!textInputLayout.getEditText().getText().toString().isEmpty()) {
                    System.out.println("acepto el cobró");
                    paying=false;
                    setSalePayed(preSale,contadoOptions[selectionPay]);
                }
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                paying = false;
                genericMessage("Venta no realizada","No se asignó un monto de pago ó se canceló el cobro");
            }
        });
        builder.show();

    }

    void setSalePayed(PreSale preSale, String typePay){

        executor.execute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                AppDatabase conexion=AppDatabase.getInstance(getContext());

                List<SubSale> subSales = conexion.subSalesDao().getSubSalesBySale(preSale.folio);
                Map<String,Product> productsMap = new HashMap<>();
                for(SubSale subSale : subSales){
                    Product product = conexion.productDao().getProductByKey(subSale.productKey);
                    productsMap.put(subSale.productKey,product);
                }
                TimeZone tz = TimeZone.getTimeZone("UTC");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
                df.setTimeZone(tz);
                Calendar calendar = Calendar.getInstance();
                String nowAsISO = df.format(calendar.getTime());
                preSale.payed=true;
                preSale.datePayed=nowAsISO;
                conexion.preSaleDao().updateSale(preSale);
                Debt debt = new Debt();
                debt.createAt=nowAsISO;
                debt.folioPreSale=preSale.folio;
                debt.folioSale=preSale.folioForSale;
                debt.payedType=typePay;
                debt.sincronized=false;
                conexion.debtDao().insertDebts(debt);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        fillBySection(currentSection);
                        doTicketSaleForSoldedOffline(preSale,subSales,productsMap);
                        tryRegisterDebts();
                    }
                });
            }
        });
    }

    private void tryRegisterDebts(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase= AppDatabase.getInstance(getContext());
                List<Debt> debts = appDatabase.debtDao().getAllDebsWithoutSincronization();
                List<ModelDebtDeliverRequest> requestList = new ArrayList<>();
                for(Debt debt : debts){
                    ModelDebtDeliverRequest request = new ModelDebtDeliverRequest();
                    request.setCreatedAt(debt.createAt);
                    request.setFolioSale(debt.folioSale);
                    request.setPayedType(debt.payedType);
                    requestList.add(request);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(requestList.size()>0) {
                            presenter.registerDebtsOfPreSales(requestList);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void sincronizationCompleteDebts(List<ModelDebtDeliverResponse> responseList){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase = AppDatabase.getInstance(getContext());

                for(ModelDebtDeliverResponse response : responseList){
                    Debt debt = appDatabase.debtDao().getDebtByFolioSale(response.getFolioPreSale());
                    debt.sincronized=true;
                    appDatabase.debtDao().updateDebtSincronization(debt);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }

}
