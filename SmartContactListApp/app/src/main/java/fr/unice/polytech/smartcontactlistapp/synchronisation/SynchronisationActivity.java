package fr.unice.polytech.smartcontactlistapp.synchronisation;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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

    private ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.synchronisation_activity);
        Button button = (Button)findViewById(R.id.synchronisation_button);
        bar = (ProgressBar)findViewById(R.id.progress);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactServer();
            }
        });
    }

    private void contactServer() {
        SendTask send;
        send = new SendTask(this);
        send.execute((Void) null);
    }

    public class SendTask extends AsyncTask<Void, Void, Boolean> {
        Context context;

        SendTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject json = new JSONObject();
            try {
                json.put("name", "gatien");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            URL url = null;
            try {
                url = new URL("http://192.168.0.41:5000/call/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestMethod("POST");
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Connection", "Keep-Alive");
                DataOutputStream content = new DataOutputStream(connection.getOutputStream());
                content.writeBytes(json.toString());
                Log.d("Code", "" + connection.getResponseCode());
                Log.d("retour",connection.getResponseMessage());

                content.close();
                connection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Log.d("Bien", "envoyé");
            } else {
                Log.d("Erreur", "Envoi");
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.d("Progress", ""+values);
        }
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
