package fr.unice.polytech.smartcontactlistapp.synchronisation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.util.Date;

import fr.unice.polytech.smartcontactlistapp.localHistoryManager.Vector;

import static fr.unice.polytech.smartcontactlistapp.DB.DB.synchronise_contact_list_application;

/**
 * Created by chapon on 09/12/16.
 */

public class SynchronisationTask extends AsyncTask<Void, Void, Boolean> {
    Context context;
    ProgressBar bar;
    TextView successOrNot;
    TextView lastUpdate;
    SynchronisationTask(Context context, ProgressBar bar, TextView successOrNot, TextView lastUpdate) {
        this.context = context;
        this.bar = bar;
        this.successOrNot = successOrNot;
        this.lastUpdate = lastUpdate;

    }

    @Override
    protected void onPreExecute() {
        bar.setVisibility(View.VISIBLE);
        successOrNot.setVisibility(View.INVISIBLE);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        //Lecture du fichier
        File path = context.getFilesDir();
        File file = new File(path, "historyCall.txt");
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        boolean found = true;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(bytes);
            in.close();
        } catch (FileNotFoundException e) {
            found = false;
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonHistory = null;
        if(!found){
            try {
                jsonHistory = new JSONArray();
                JSONObject empty = new JSONObject();
                empty.put("Year","empty");
                jsonHistory.put(empty);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            String[] s= new String(bytes).split("\n");
            jsonHistory = createJson(s);
        }

        URL url = null;
        try {
            Log.d("DEBUG", "ipadress dans task :"+SynchronisationActivity.ipAdress+" ");
            url = new URL("http://"+SynchronisationActivity.ipAdress+":5000/predict/");
            Log.d("HOST","http://"+SynchronisationActivity.ipAdress+":5000/predict/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();
            DataOutputStream contentSend = new DataOutputStream(connection.getOutputStream());
            Vector v = new Vector(new Date(), "coucou");
            JSONObject json  = fillJsonArray(v).put("history", jsonHistory);
            contentSend.writeBytes(json.toString());

            int statusCode = connection.getResponseCode();
            if(statusCode == 200) {
                BufferedReader content = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputStr;
                StringBuilder responseStrBuilder = new StringBuilder();
                while ((inputStr = content.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                JSONObject j = new JSONObject(responseStrBuilder.toString());
                Log.d("JSON", j.toString());
                synchronise_contact_list_application(j, context);
                content.close();
            }else{
                contentSend.close();
                connection.disconnect();
                return false;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
    On remplit un vecteur contenant la date du jour
     */
    private JSONObject fillJsonArray(Vector v) {
        JSONObject toSend = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        String s = v.toString();
        JSONObject j = new JSONObject();
        try {
            String[] col = s.split(",");
            String[] classe = Vector.classes;
            for(int iter =0; iter<col.length-1; iter++){
                j.accumulate(classe[iter],col[iter]);
            }
            jsonArray.put(j);
            toSend.put("request", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return toSend;
    }

    public static String removeAccent(String source) {
        return Normalizer.normalize(source, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
    }
    private JSONArray createJson(String[] s) {
        JSONArray jsonArray = new JSONArray();
        for(int i=0; i<s.length; i++){
            JSONObject j = new JSONObject();
            try {
                String[] col = s[i].split(",");
                String[] classe = Vector.classes;
                for(int iter =0; iter<col.length; iter++){
                    if (iter == col.length-1) {
                        String c = col[iter];
                        c = removeAccent(c);
                        //c = c.replace('ë','e');
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
        return jsonArray;
    }


    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            successOrNot.setText("Sync Succeed !");

            SharedPreferences mShared;
            SharedPreferences.Editor mEdit;
            mShared = PreferenceManager.getDefaultSharedPreferences(context);
            mEdit = mShared.edit();
            Vector v = new Vector(new Date(),"");
            if(v.minute.length() == 1){
                v.minute = "0"+v.minute;
            }
            if(v.month.length() == 1){
                v.month ="0"+v.month;
            }
            mEdit.putString("last_update", v.numberDay+"/"+v.month+"/"+v.year+" at "+v.hour+":"+v.minute);
            mEdit.commit();

            lastUpdate.setText("Last Sync : "+v.numberDay+"/"+v.month+"/"+v.year+" at "+v.hour+":"+v.minute);
        } else {
            successOrNot.setText("Sync Failed !");
        }
        successOrNot.setVisibility(View.VISIBLE);
        bar.setVisibility(View.INVISIBLE);
    }

}
