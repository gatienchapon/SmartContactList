package fr.unice.polytech.smartcontactlistapp.synchronisation;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.Semaphore;

import fr.unice.polytech.smartcontactlistapp.contactList.Contact;
import fr.unice.polytech.smartcontactlistapp.contactList.RetreiveContactList;

/**
 * Created by chapon on 31/01/17.
 */

public class SyncContactListTask extends AsyncTask<Void, Void, Boolean> {

    Semaphore semContact;
    Semaphore semServer;
    Map<String, Contact> contact_list_mobile;
    Context context;
    public SyncContactListTask(Semaphore semaphore1, Semaphore semaphore2, Map<String, Contact> contact_list_mobile, Context context) {
        semContact = semaphore1;
        semServer = semaphore2;
        this.contact_list_mobile = contact_list_mobile;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        RetreiveContactList retreiveContactList = new RetreiveContactList(context, contact_list_mobile);
        retreiveContactList.fillContactHistory();
        semContact.release();
        Log.d("ContactList","wait server");
        try {
            semServer.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("ContactList","Server ok");
        return true;
    }
}
