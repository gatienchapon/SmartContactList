package fr.unice.polytech.smartcontactlistapp.localHistoryManager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Creation du fichier local contenant les différents vecteurs d'appel
 */

public class ManageLocalFile {
    private File path;
    private File file;
    private Context context;

    public ManageLocalFile(Context context) {
        this.context = context;
        path = context.getFilesDir();
        file = new File(path, "historyCall.txt");
        Log.d("Path", file.getPath());
        //file.delete();
        if(file.length() == 0){
            fillWithCallLog(); 
        }
    }

    private void fillWithCallLog() {
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        //On verifie que l'accès aux call logs soit ok
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                null, null, strOrder);
        int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        while (managedCursor.moveToNext()) {
            Date callDate = new Date(Long.valueOf(managedCursor.getString(date)));
            String callName = managedCursor.getString(name);
            if (callName != null) {
                Vector v = new Vector(callDate, callName);
                writeToFile(v.toString());
            }
        }

    }

    /*
    * On recupère le dernière appel entrant et on met a jour le fichier
     */
    public void updateCallLog(){
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        //On verifie que l'accès aux call logs soit ok
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                null, null, strOrder);
        int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        managedCursor.moveToFirst();
        Date callDate = new Date(Long.valueOf(managedCursor.getString(date)));
        String callName = managedCursor.getString(name);
        if (callName != null) {
            Vector v = new Vector(callDate, callName);
            writeToFile(v.toString());
        }

    }

    /*
    Lecture de tout le fichier
     */
    public byte[] readFile() {
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

    /*
    * Ecriture à la suite
     */
    private void writeToFile(String texte) {
        int length = (int) file.length();
        byte[] toWrite= texte.getBytes();
        int sizeToWrite = toWrite.length;
        byte[] bytes = new byte[length+sizeToWrite];
        byte[] alreadyFilled = readFile();
        for(int i=0; i<alreadyFilled.length; i++){
            bytes[i] = alreadyFilled[i];
        }
        //On concatene le reste

        for(int i=0; i<sizeToWrite; i++){
            bytes[length +i] = toWrite[i];
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
        String contenu = new String(readFile());
        Log.d("Content", contenu);
    }
}
