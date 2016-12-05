package fr.unice.polytech.smartcontactlistapp.historyList;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chapon on 01/11/16.
 */

public class ContactHistory {
    public List<ContactInformation> contactHistory;

    public void fillContactHistory(Context context) {
        contactHistory = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        //On verifie que l'accès aux call logs soit ok
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                null, null, strOrder);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Log :");
        while (managedCursor.moveToNext()) {
            String callNumber = managedCursor.getString(number);
            String callTypeCode = managedCursor.getString(type);
            Date callDate = new Date(Long.valueOf(managedCursor.getString(date)));
            float callDuration = Float.parseFloat(managedCursor.getString(duration));
            String callName = managedCursor.getString(name);
            String callType = null;
            int callcode = Integer.parseInt(callTypeCode);
            switch (callcode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "Outgoing";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callType = "Incoming";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callType = "Missed";
                    break;
            }
            ContactInformation c = new ContactInformation(callDate, callDuration, callNumber, callName, callType);
            increaseNbCallForthisNumber(c);
            contactHistory.add(c);
        }
    }

    private void increaseNbCallForthisNumber(ContactInformation c) {
        for(ContactInformation current : contactHistory){
            if(c.getCallNumber().equals(current.getCallNumber())){
                current.increaseTotalCall();
                c.increaseTotalCall();
            }
        }
    }

    public List<ContactInformation> getContactHistory() {
        return contactHistory;
    }
}
