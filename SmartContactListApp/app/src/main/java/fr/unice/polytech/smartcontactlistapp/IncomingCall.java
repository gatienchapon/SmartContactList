package fr.unice.polytech.smartcontactlistapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import fr.unice.polytech.smartcontactlistapp.historyList.ContactActivity;
import fr.unice.polytech.smartcontactlistapp.localHistoryManager.ManageLocalFile;


public class IncomingCall extends BroadcastReceiver {

    private static int i =0;
    public void onReceive(Context context, Intent intent) {
        String s= intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        if(s.equals(TelephonyManager.EXTRA_STATE_IDLE))
        {
            if(i ==1){
                new ManageLocalFile(context).updateCallLog();
                Log.d("Call", "end");
                i=0;
            }else {
                i++;
            }
        }
    }
}
