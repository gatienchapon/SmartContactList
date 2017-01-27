package fr.unice.polytech.smartcontactlistapp.synchronisation;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import fr.unice.polytech.smartcontactlistapp.R;
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
                synchronisation();
            }
        });
    }

    private void synchronisation() {
        SynchronisationTask s;
        s = new SynchronisationTask(getContext(), barSend, successOrNot,lastUpdate, loading, recyclerView, reload1, reload2, makeSync, currentSlotTime);
        s.execute((Void)null);
    }

    private void contactServer() {
        SendTask send;
        send = new SendTask(getContext(),barSend);
        send.execute((Void) null);
    }

}
