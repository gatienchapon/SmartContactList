package fr.unice.polytech.smartcontactlistapp.synchronisation;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import fr.unice.polytech.smartcontactlistapp.R;
import fr.unice.polytech.smartcontactlistapp.contactList.Contact;
import fr.unice.polytech.smartcontactlistapp.contactList.PrintContactListActivity;
import fr.unice.polytech.smartcontactlistapp.historyList.ContactActivity;
import fr.unice.polytech.smartcontactlistapp.historyList.ContactAdapter;
import fr.unice.polytech.smartcontactlistapp.historyList.ContactHistory;

public class SynchronisationActivity extends Dialog {

    private ProgressBar barSend;
    //private ProgressBar barSynchronisation;
    private EditText ServerAdress;
    private TextView successOrNot;
    private TextView lastUpdate;
    private TextView loading;
    public static String ipAdress="192.168.1.145";
    private RecyclerView recyclerView;

    TextView currentSlotTime;
    TextView reload1;
    TextView reload2;
    Button makeSync;

    public SynchronisationActivity(Context context, RecyclerView recyclerView) {
        super(context);
        this.recyclerView = recyclerView;

    }

    public SynchronisationActivity(Context context, RecyclerView recyclerView, TextView currentSlotTime, TextView reload1, TextView reload2, Button makeSync) {
        this(context,recyclerView);
        this.reload1 = reload1;
        this.reload2 = reload2;
        this.makeSync = makeSync;
        this.currentSlotTime = currentSlotTime;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.synchronisation_activity);
        final Button synchronisation = (Button)findViewById(R.id.sync);
        successOrNot = (TextView) findViewById(R.id.successOrNot);
        SharedPreferences mShared = PreferenceManager.getDefaultSharedPreferences(getContext());
        lastUpdate = (TextView) findViewById(R.id.lastSync);
        lastUpdate.setText(getContext().getResources().getString(R.string.last_sych)+" "+mShared.getString("last_update",getContext().getResources().getString(R.string.unknown)));
        successOrNot.setVisibility(View.INVISIBLE);
        //Button send = (Button)findViewById(R.id.send_button);
        barSend = (ProgressBar)findViewById(R.id.progressSend);
        //barSynchronisation = (ProgressBar)findViewById(R.id.progressSynchronised);
        barSend.setVisibility(View.INVISIBLE);
        //barSynchronisation.setVisibility(View.INVISIBLE);
        ServerAdress = (EditText) findViewById(R.id.editAdress);
        loading = (TextView) findViewById(R.id.loading);
        loading.setVisibility(View.INVISIBLE);

        /*send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! TextUtils.isEmpty(ServerAdress.getText().toString())){
                    ipAdress = ServerAdress.getText().toString();
                }
                contactServer();
            }
        });*/
        synchronisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( ! TextUtils.isEmpty(ServerAdress.getText().toString())){
                    ipAdress = ServerAdress.getText().toString();
                }
                if(checkInternetConnection()){
                    synchronisation();
                }else{
                    successOrNot.setVisibility(View.VISIBLE);
                    successOrNot.setText(getContext().getResources().getString(R.string.noInternet));
                }

            }
        });
    }

    private boolean checkInternetConnection() {
        ConnectivityManager connManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            return true;
        }else {
            return false;
        }
        /*ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }*/
    }
    private void synchronisation() {
        SynchronisationTask s;
        Map<String, Contact> contact_list_mobile = new HashMap<>();
        Semaphore semaphore1 = new Semaphore(0);
        Semaphore semaphore2 = new Semaphore(0);
        s = new SynchronisationTask(getContext(), barSend, successOrNot,lastUpdate, loading, recyclerView, reload1, reload2, makeSync, currentSlotTime, semaphore1, contact_list_mobile, semaphore2);
        SyncContactListTask syncContactListTask;
        syncContactListTask = new SyncContactListTask(semaphore1, semaphore2, contact_list_mobile, getContext());
        s.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        syncContactListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void contactServer() {
        SendTask send;
        send = new SendTask(getContext(),barSend);
        send.execute((Void) null);
    }

}
