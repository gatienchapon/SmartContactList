package fr.unice.polytech.smartcontactlistapp.contactList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.unice.polytech.smartcontactlistapp.synchronisation.SendTask.removeAccent;

/**
 * Created by chapon on 04/11/16.
 */

public class RetreiveContactList {
    public Map<String, Contact> listContact;
    private Context context;

    public RetreiveContactList(Context context, Map<String, Contact> listContact) {
        this.context = context;
        this.listContact = listContact;
    }

    public Map<String, Contact> getListContact() {
        return listContact;
    }

    public void fillContactHistory() {
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        while (cur.moveToNext()) {
            String id = cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cur.getString(cur.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME));

            if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                Cursor pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                        new String[]{id}, null);
                while (pCur.moveToNext()) {
                    String phoneNo = pCur.getString(pCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                    listContact.put(name, new Contact(name,phoneNo, "100%"));

                }
                pCur.close();
            }
        }
    }


}
