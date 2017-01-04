package fr.unice.polytech.smartcontactlistapp.synchronisation;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class SynchronisationActivity extends AppCompatActivity {

    private ProgressBar barSend;
    private ProgressBar barSynchronisation;
    private EditText ServerAdress;
    public static String ipAdress="192.168.1.145";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.synchronisation_activity);
        final Button synchronisation = (Button)findViewById(R.id.synchronisation);
        Button send = (Button)findViewById(R.id.send_button);
        barSend = (ProgressBar)findViewById(R.id.progressSend);
        barSynchronisation = (ProgressBar)findViewById(R.id.progressSynchronised);
        barSend.setVisibility(View.INVISIBLE);
        barSynchronisation.setVisibility(View.INVISIBLE);
        ServerAdress = (EditText) findViewById(R.id.editAdress);
        //
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! TextUtils.isEmpty(ServerAdress.getText().toString())){
                    ipAdress = ServerAdress.getText().toString();
                }
                contactServer();
            }
        });
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
        s = new SynchronisationTask(this, barSynchronisation);
        s.execute((Void)null);
    }

    private void contactServer() {
        SendTask send;
        send = new SendTask(this,barSend);
        send.execute((Void) null);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.accueil) {
            Intent intente = new Intent(SynchronisationActivity.this, PrintContactListActivity.class);
            startActivity(intente);
            return true;
        }
        if (id == R.id.synchronisation) {
            Intent intente = new Intent(SynchronisationActivity.this, SynchronisationActivity.class);
            startActivity(intente);
            return true;
        }
        if (id == R.id.journal_appel) {
            Intent intente = new Intent(SynchronisationActivity.this, ContactActivity.class);
            startActivity(intente);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
