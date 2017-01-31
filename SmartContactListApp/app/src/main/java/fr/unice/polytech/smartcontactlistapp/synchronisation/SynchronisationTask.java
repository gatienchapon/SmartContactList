package fr.unice.polytech.smartcontactlistapp.synchronisation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import fr.unice.polytech.smartcontactlistapp.R;
import fr.unice.polytech.smartcontactlistapp.contactList.Contact;
import fr.unice.polytech.smartcontactlistapp.localHistoryManager.Vector;

import static fr.unice.polytech.smartcontactlistapp.DB.DB.init_contact_list_application;
import static fr.unice.polytech.smartcontactlistapp.DB.DB.synchronise_contact_list_application;
import static fr.unice.polytech.smartcontactlistapp.contactList.PrintContactListActivity.reload;

/**
 * Created by chapon on 09/12/16.
 */

public class SynchronisationTask extends AsyncTask<Void, Void, Boolean> {
    Context context;
    ProgressBar bar;
    TextView successOrNot;
    TextView lastUpdate;
    TextView loading;
    private Map<String,String> coorspondanceIDContact;
    RecyclerView recyclerView;
    TextView reload1;
    TextView reload2;
    Button makeSync;
    TextView currentSlotTime;
    Semaphore semContact;
    Semaphore semServer;
    Map<String, Contact> contact_list_mobile;
    SynchronisationTask(Context context, ProgressBar bar, TextView successOrNot, TextView lastUpdate, TextView loading, RecyclerView recyclerView, TextView reload1, TextView reload2, Button makeSync, TextView currentSlotTime, Semaphore semaphore1, Map<String, Contact> contact_list_mobile, Semaphore semaphore2) {
        this.context = context;
        this.bar = bar;
        this.successOrNot = successOrNot;
        this.lastUpdate = lastUpdate;
        coorspondanceIDContact = new HashMap<>();
        this.loading = loading;
        this.recyclerView = recyclerView;
        this.reload1 = reload1;
        this.reload2 = reload2;
        this.makeSync = makeSync;
        this.currentSlotTime = currentSlotTime;
        semContact = semaphore1;
        semServer = semaphore2;
        this.contact_list_mobile = contact_list_mobile;
    }

    @Override
    protected void onPreExecute() {
        bar.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
        successOrNot.setVisibility(View.INVISIBLE);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        //Obtention de l'ip du serveur local
       // sendUDPRequest();
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
            String history = "Maman,7,7,15,38,1,46,1416,2017\n" +
                    "Jean Dupuis,7,7,14,31,1,5,1416,2017\n" +
                    "Stéphane Lebon,7,7,14,30,1,44,1416,2017\n" +
                    "Gaëtan Bertot,7,7,12,10,1,15,1214,2017\n" +
                    "Gaëtan Bertot,7,7,11,16,1,11,1012,2017\n" +
                    "Hélène Guillot,7,7,11,13,1,31,1012,2017\n" +
                    "Michelle Jaccob,7,7,11,11,1,46,1012,2017\n" +
                    "Hubert de la Croix,7,7,10,59,1,23,1012,2017\n" +
                    "Pierre Perrin,7,7,10,46,1,14,1012,2017\n" +
                    "Pépé,7,7,10,41,1,4,1012,2017\n" +
                    "Mémé,7,7,10,40,1,41,1012,2017\n" +
                    "Repondeur,7,7,10,38,1,46,1012,2017\n" +
                    "Yann Goubert,7,7,10,38,1,30,1012,2017\n" +
                    "Laura Labruit,7,7,10,36,1,2,1012,2017\n" +
                    "Laura Labruit,7,7,9,51,1,43,810,2017\n" +
                    "Papa,7,7,8,33,1,29,810,2017\n" +
                    "Maman,7,7,8,31,1,36,810,2017\n" +
                    "Mon Amour,7,7,8,28,1,46,810,2017\n" +
                    "Mon Amour,7,7,8,27,1,58,810,2017\n" +
                    "Léa Ravelle,7,7,7,23,1,43,608,2017\n" +
                    "Léa Ravelle,6,6,22,26,1,39,2200,2017\n" +
                    "Mon Amour,6,6,20,47,1,24,2022,2017\n" +
                    "Stéphane Lebon,6,6,19,39,1,14,1820,2017\n" +
                    "Jean Dupuis,6,6,19,38,1,57,1820,2017\n" +
                    "Mon Amour,6,6,19,16,1,34,1820,2017\n" +
                    "Mon Amour,6,6,17,26,1,31,1618,2017\n" +
                    "Mon Amour,6,6,17,11,1,31,1618,2017\n" +
                    "Léo Laccroit,6,6,16,54,1,8,1618,2017\n" +
                    "Mon Amour,6,6,15,51,1,8,1416,2017";
            String table[] = new String(history.getBytes()).split("\n");
            jsonHistory = createJson2(table);
        }else{
            String[] s= new String(bytes).split("\n");
            jsonHistory = createJson(s);
        }

        URL url = null;
        try {
            //Log.d("DEBUG", "ipadress dans task :"+SynchronisationActivity.ipAdress+" ");
            // url = new URL("http://"+SynchronisationActivity.ipAdress+":5000/predict/");
            url = new URL("http://gatienchapon.pythonanywhere.com/predict/");
            //Log.d("HOST","http://"+SynchronisationActivity.ipAdress+":5000/predict/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();
            DataOutputStream contentSend = new DataOutputStream(connection.getOutputStream());
            Vector v = new Vector(new Date(), "coucou");
            JSONObject json  = fillJsonArray(v).put("history", jsonHistory);
            Log.d("To send ", json.toString());

            contentSend.writeBytes(json.toString());
            Log.d("ServerRespon","On attend contactList");
            semContact.acquire();
            Log.d("ServerRespon","On acquire");
            semServer.release();
            int statusCode = connection.getResponseCode();
            if(statusCode == 200) {
                BufferedReader content = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputStr;
                StringBuilder responseStrBuilder = new StringBuilder();
                while ((inputStr = content.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                JSONObject j = new JSONObject(responseStrBuilder.toString());
                Log.d("JSON", j.toString());

                synchronise_contact_list_application(j, context, coorspondanceIDContact, contact_list_mobile);
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
        } catch (InterruptedException e) {
            e.printStackTrace();
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
    private JSONArray createJson2(String[] s) {
        Map<String, String> nameToId = new HashMap<>();
        JSONArray jsonArray = new JSONArray();
        for(int i=0; i<s.length; i++){
            JSONObject j = new JSONObject();
            try {
                String[] col = s[i].split(",");
                String[] classe = Vector.classes2;
                for(int iter =0; iter<col.length; iter++){
                    if (iter == 0) {
                        String c = col[iter];
                        String id = "C"+i;
                        if(!nameToId.containsKey(c))
                        {
                            coorspondanceIDContact.put(id, c);
                            nameToId.put(c,id);
                        }
                        j.put(classe[iter], nameToId.get(c));
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
    private JSONArray createJson(String[] s) {
        Map<String, String> nameToId = new HashMap<>();
        JSONArray jsonArray = new JSONArray();
        for(int i=0; i<s.length; i++){
            JSONObject j = new JSONObject();
            try {
                String[] col = s[i].split(",");
                String[] classe = Vector.classes;
                for(int iter =0; iter<col.length; iter++){
                    if (iter == col.length-1) {
                        String c = col[iter];
                        String id = "C"+i;
                        if(!nameToId.containsKey(c))
                        {
                            coorspondanceIDContact.put(id, c);
                            nameToId.put(c,id);
                        }
                        j.put(classe[iter], nameToId.get(c));
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
            successOrNot.setText(context.getResources().getString(R.string.sucess));

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
            mEdit.putString("last_update", v.numberDay+"/"+v.month+"/"+v.year+" "+context.getResources().getString(R.string.at)+" "+v.hour+":"+v.minute);
            mEdit.commit();

            lastUpdate.setText(context.getResources().getString(R.string.last_sych)+v.numberDay+"/"+v.month+"/"+v.year+" "+context.getResources().getString(R.string.at)+" "+v.hour+":"+v.minute);
        } else {
            successOrNot.setText(context.getResources().getString(R.string.fail));
        }
        successOrNot.setVisibility(View.VISIBLE);
        bar.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.INVISIBLE);
        if(reload1 != null){
            init_contact_list_application(context);
            reload1.setVisibility(View.VISIBLE);
            reload2.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            makeSync.setVisibility(View.INVISIBLE);
            currentSlotTime.setVisibility(View.VISIBLE);
        }
        reload(recyclerView,context);
    }

}
