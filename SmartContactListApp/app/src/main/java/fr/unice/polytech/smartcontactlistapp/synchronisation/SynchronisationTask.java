package fr.unice.polytech.smartcontactlistapp.synchronisation;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.Normalizer;
import java.util.Date;
import java.util.Enumeration;

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
            try {

                String rep ="[{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"15\",\"Minute\":\"38\",\"Seconde\":\"46\",\"SlotTime\":\"1416\",\"Class\":\"Martine Robin\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"14\",\"Minute\":\"31\",\"Seconde\":\"5\",\"SlotTime\":\"1416\",\"Class\":\"Xavier Grondinos\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"14\",\"Minute\":\"30\",\"Seconde\":\"44\",\"SlotTime\":\"1416\",\"Class\":\"Xavier Grondin\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"12\",\"Minute\":\"10\",\"Seconde\":\"15\",\"SlotTime\":\"1214\",\"Class\":\"Olivier Martinez\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"11\",\"Minute\":\"16\",\"Seconde\":\"11\",\"SlotTime\":\"1012\",\"Class\":\"Olivier Martinez\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"11\",\"Minute\":\"13\",\"Seconde\":\"31\",\"SlotTime\":\"1012\",\"Class\":\"Pico Florent\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"11\",\"Minute\":\"11\",\"Seconde\":\"46\",\"SlotTime\":\"1012\",\"Class\":\"Severine Filipini\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"10\",\"Minute\":\"59\",\"Seconde\":\"23\",\"SlotTime\":\"1012\",\"Class\":\"Marie-Therese German\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"10\",\"Minute\":\"46\",\"Seconde\":\"14\",\"SlotTime\":\"1012\",\"Class\":\"Macciotta Yann\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"10\",\"Minute\":\"41\",\"Seconde\":\"4\",\"SlotTime\":\"1012\",\"Class\":\"Greg Piscitello\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"10\",\"Minute\":\"40\",\"Seconde\":\"41\",\"SlotTime\":\"1012\",\"Class\":\"Stephane Devos\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"10\",\"Minute\":\"38\",\"Seconde\":\"46\",\"SlotTime\":\"1012\",\"Class\":\"Repondeur\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"10\",\"Minute\":\"38\",\"Seconde\":\"30\",\"SlotTime\":\"1012\",\"Class\":\"Kais Shido\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"10\",\"Minute\":\"36\",\"Seconde\":\"2\",\"SlotTime\":\"1012\",\"Class\":\"Emmanuelle Abad\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"9\",\"Minute\":\"51\",\"Seconde\":\"43\",\"SlotTime\":\"810\",\"Class\":\"Emmanuelle Abad\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"8\",\"Minute\":\"33\",\"Seconde\":\"29\",\"SlotTime\":\"810\",\"Class\":\"Gilbert Robin\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"8\",\"Minute\":\"31\",\"Seconde\":\"36\",\"SlotTime\":\"810\",\"Class\":\"Martine Robin\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"8\",\"Minute\":\"28\",\"Seconde\":\"46\",\"SlotTime\":\"810\",\"Class\":\"Mon Amour\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"8\",\"Minute\":\"27\",\"Seconde\":\"58\",\"SlotTime\":\"810\",\"Class\":\"Mon Amour\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"7\",\"DayWeek\":\"7\",\"Hour\":\"7\",\"Minute\":\"23\",\"Seconde\":\"43\",\"SlotTime\":\"608\",\"Class\":\"Isp Codis\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"6\",\"DayWeek\":\"6\",\"Hour\":\"22\",\"Minute\":\"26\",\"Seconde\":\"39\",\"SlotTime\":\"2200\",\"Class\":\"Isp Codis\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"6\",\"DayWeek\":\"6\",\"Hour\":\"20\",\"Minute\":\"47\",\"Seconde\":\"24\",\"SlotTime\":\"2022\",\"Class\":\"Mon Amour\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"6\",\"DayWeek\":\"6\",\"Hour\":\"19\",\"Minute\":\"39\",\"Seconde\":\"14\",\"SlotTime\":\"1820\",\"Class\":\"Xavier Grondin\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"6\",\"DayWeek\":\"6\",\"Hour\":\"19\",\"Minute\":\"38\",\"Seconde\":\"57\",\"SlotTime\":\"1820\",\"Class\":\"Xavier Grondinos\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"6\",\"DayWeek\":\"6\",\"Hour\":\"19\",\"Minute\":\"16\",\"Seconde\":\"34\",\"SlotTime\":\"1820\",\"Class\":\"Mon Amour\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"6\",\"DayWeek\":\"6\",\"Hour\":\"17\",\"Minute\":\"26\",\"Seconde\":\"31\",\"SlotTime\":\"1618\",\"Class\":\"Mon Amour\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"6\",\"DayWeek\":\"6\",\"Hour\":\"17\",\"Minute\":\"11\",\"Seconde\":\"31\",\"SlotTime\":\"1618\",\"Class\":\"Mon Amour\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"6\",\"DayWeek\":\"6\",\"Hour\":\"16\",\"Minute\":\"54\",\"Seconde\":\"8\",\"SlotTime\":\"1618\",\"Class\":\"Mathieu P\"},{\"Year\":\"2017\",\"Month\":\"1\",\"DayNumber\":\"6\",\"DayWeek\":\"6\",\"Hour\":\"15\",\"Minute\":\"51\",\"Seconde\":\"8\",\"SlotTime\":\"1416\",\"Class\":\"Mon Amour\"}]";
                jsonHistory = new JSONArray(rep);
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
            // url = new URL("http://"+SynchronisationActivity.ipAdress+":5000/predict/");
            url = new URL("http://gatienchapon.pythonanywhere.com/predict/");
            Log.d("HOST","http://"+SynchronisationActivity.ipAdress+":5000/predict/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
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



    private void sendUDPRequest() {

        /*Discover d = new Discover(context,6000);
        try {
            DatagramPacket receivePacket = d.sendBroadcast("coucou");
            String ip = receivePacket.getAddress().toString();
            String sentence = new String(receivePacket.getData());
            Log.d("AdresseIP", ip + "  "+sentence );

        } catch (Exception e) {
            e.printStackTrace();
        }*/

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
