package fr.unice.polytech.smartcontactlistapp.synchronisation;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import fr.unice.polytech.smartcontactlistapp.localHistoryManager.Vector;

import static fr.unice.polytech.smartcontactlistapp.DB.DB.synchronise_contact_list_application;

/**
 * Created by chapon on 09/12/16.
 */

public class SynchronisationTask extends AsyncTask<Void, Void, Boolean> {
    Context context;
    ProgressBar bar;

    SynchronisationTask(Context context, ProgressBar bar) {
        this.context = context;
        this.bar = bar;
    }

    @Override
    protected void onPreExecute() {
        bar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        URL url = null;
        try {
            url = new URL("http://192.168.0.41:5000/predict");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            int statusCode = connection.getResponseCode();
            if(statusCode == 200) {
                DataInputStream content = new DataInputStream(connection.getInputStream());
                String inputStr;
                StringBuilder responseStrBuilder = new StringBuilder();
                while ((inputStr = content.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                JSONArray j = new JSONArray(responseStrBuilder.toString());
                synchronise_contact_list_application(j, context);
                content.close();
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }


    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            Log.d("Bien", "recu");
        } else {
            Log.d("Erreur", "Envoi");
        }
        bar.setVisibility(View.INVISIBLE);
    }

}
