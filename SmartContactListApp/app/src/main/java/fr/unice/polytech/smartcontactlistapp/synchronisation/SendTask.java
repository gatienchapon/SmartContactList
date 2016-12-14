package fr.unice.polytech.smartcontactlistapp.synchronisation;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import fr.unice.polytech.smartcontactlistapp.localHistoryManager.Vector;

import static java.nio.charset.StandardCharsets.UTF_8;


public class SendTask extends AsyncTask<Void, Void, Boolean> {
    Context context;
    ProgressBar bar;

    SendTask(Context context, ProgressBar bar) {
        this.context = context;
        this.bar = bar;
    }

    @Override
    protected void onPreExecute() {
        bar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        //Lecture du fichier
        File path = context.getFilesDir();
        File file = new File(path, "historyCall.txt");
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(bytes);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] s= new String(bytes).split("\n");
        JSONObject json = createJson(s);

        URL url = null;
        try {
            url = new URL("http://192.168.1.145:5000/call/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            DataOutputStream content = new DataOutputStream(connection.getOutputStream());
            Log.d("Envoie", json.toString());
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

    private JSONObject createJson(String[] s) {
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for(int i=0; i<s.length; i++){
            JSONObject j = new JSONObject();
            try {
                String[] col = s[i].split(",");
                String[] classe = Vector.classes;
                for(int iter =0; iter<col.length; iter++){
                    if (iter == col.length-1) {
                        String c = col[iter];
                        c = c.replace('é','e');
                        j.put(classe[iter], c);
                    }
                    else
                        j.put(classe[iter],col[iter]);
                }
                jsonArray.put(j);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            json.put("history", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            Log.d("Bien", "envoyé");
        } else {
            Log.d("Erreur", "Envoi");
        }
        bar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        Log.d("Progress", ""+values);
    }
}
