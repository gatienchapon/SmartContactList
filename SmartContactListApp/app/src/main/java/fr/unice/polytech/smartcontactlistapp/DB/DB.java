package fr.unice.polytech.smartcontactlistapp.DB;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fr.unice.polytech.smartcontactlistapp.contactList.Contact;
import fr.unice.polytech.smartcontactlistapp.localHistoryManager.Vector;

/**
 * Created by chapon on 09/12/16.
 */

public class DB {
    public static List<Contact> contact_list_application;


    public static boolean init_contact_list_application(Context context){
        Date date = new Date();
        Vector v = new Vector(date,"");
        return loadFile(v.getTimeSlot(), context);

    }

    public static boolean loadFile(String timeSlot, Context context) {
        File path = context.getFilesDir();
        File file = new File(path, timeSlot+"contact_list_application.txt");
        if(file.length() == 0){
            /*for(int i=0; i<contact_list_mobile.size(); i++){
                Contact c = contact_list_mobile.get(i);
                String ligne = c.name+","+c.numero+","+c.percentage+"\n";
                writeToFile(file, ligne, false);
            }*/
            return false;
        }else{
            //On lit le fichier
            String[] general = new String(readFile(file)).split("\n");
            contact_list_application = new ArrayList<>();
            for(int i=0; i<general.length; i++){
                String[] col = general[i].split(",");
                if(col.length>=3)
                    contact_list_application.add(new Contact(col[0], col[1], col[2]));
            }
            sortFileByPercentage();
            return true;
        }
    }

    public static void synchronise_contact_list_application(JSONObject json, Context context, Map<String, String> coorspondanceIDContact, Map<String, Contact> contact_list_mobile) {
        Date date = new Date();
        Vector v = new Vector(date,"");
        String[] slots = {"810", "1012", "1214", "1416", "1618", "1820", "2022", "2200", "6", "608"};
        for ( String s : slots){
            writeAllFiles(s, json, context, coorspondanceIDContact, contact_list_mobile);
        }
        loadCurrentTimeSlot(v.getTimeSlot(),context);
    }
    private static void loadCurrentTimeSlot(String slotTime, Context context){
        File path = context.getFilesDir();
        File file = new File(path, slotTime+"contact_list_application.txt");
        String[] general = new String(readFile(file)).split("\n");
        contact_list_application = new ArrayList<>();
        for(int i=0; i<general.length; i++){
            String[] col = general[i].split(",");
            if(col.length>=3)
                contact_list_application.add(new Contact(col[0], col[1], col[2]));
        }
        sortFileByPercentage();

    }

    private static void writeAllFiles(String timeSlot, JSONObject json, Context context, Map<String, String> coorspondanceIDContact, Map<String, Contact> contact_list_mobile) {
        File path = context.getFilesDir();
        String fileName = timeSlot+"contact_list_application.txt";
        File file = new File(path, fileName);
        //contact_list_application = new ArrayList<>();
        boolean erase = true;
        try {
            JSONArray myArray = json.getJSONArray(timeSlot);
              for (int i = 0; i < json.getJSONArray(timeSlot).length(); i++){
                JSONObject jsonObject = myArray.getJSONObject(i);
                double score = Double.parseDouble(jsonObject.getString("score"));
                score = score * 100;
                String result = String.format("%.0f", score);
                String id = jsonObject.getString("name");
                  String name = coorspondanceIDContact.get(id);
                String numero = "";
                  if (contact_list_mobile.containsKey(name)){
                      Contact c = contact_list_mobile.get(name);
                      Contact contact = new Contact(name, c.numero, result + "%");
                      String ligne = contact.name + "," + contact.numero + "," + contact.percentage + "\n";
                      writeToFile(file, ligne, erase);
                      erase = false;
                  }

            }
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    private static void sortFileByPercentage() {

        Collections.sort(contact_list_application, new Comparator<Contact>() {
            @Override
            public int compare(Contact c1, Contact c2) {
                double pers1 = Double.parseDouble(c1.percentage.split("%")[0]);
                double pers2 = Double.parseDouble(c2.percentage.split("%")[0]);
                if(pers1>pers2)
                    return -1;
                else if(pers1 == pers2)
                    return 0;
                else
                    return 1;
            }
        });
    }

    private static byte[] readFile(File file) {
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
        return bytes;
    }

    public static void writeToFile(File file, String texte, boolean erase) {
        int length = (int) file.length();
        byte[] toWrite= texte.getBytes();
        int sizeToWrite = toWrite.length;
        byte[] bytes;
        if(!erase) {
            bytes = new byte[length + sizeToWrite];
            byte[] alreadyFilled = readFile(file);
            for(int i=0; i<alreadyFilled.length; i++){
                bytes[i] = alreadyFilled[i];
            }
            //On concatene le reste
            for(int i=0; i<sizeToWrite; i++){
                bytes[length +i] = toWrite[i];
            }
        }
        else {
            bytes = new byte[sizeToWrite];
            for(int i=0; i<sizeToWrite; i++){
                bytes[i] = toWrite[i];
            }
        }

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(bytes);
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
