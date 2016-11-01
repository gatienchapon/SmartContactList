package fr.unice.polytech.smartcontactlistapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


public class IncomingCall extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        try {
            // TELEPHONY MANAGER class object to register one listner
            TelephonyManager tmgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            //Create Listner
            MyPhoneStateListener PhoneListener = new MyPhoneStateListener(context);

            // Register listener for LISTEN_CALL_STATE
            tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            Log.d("Recive","tel");

        } catch (Exception e) {
            Log.d("Phone Receive Error", " " + e);
        }

    }

    private class MyPhoneStateListener extends PhoneStateListener {

        private Context context;

        public MyPhoneStateListener(Context context) {
            this.context = context;
        }

        public void onCallStateChanged(int state, String incomingNumber) {

            if (state == 1) {
                ContactActivity.contactHistory.fillContactHistory(context);
            }
        }
    }
}
