package fr.unice.polytech.smartcontactlistapp.historyList;

import android.widget.TextView;

/**
 * Created by chapon on 01/11/16.
 */

public class ContactViewOlder {
    public TextView contactName;
    public TextView duration;
    public TextView type;
    public TextView date;
    public TextView nbCall;

    public ContactViewOlder(TextView contactName, TextView duration, TextView type, TextView date, TextView nbCall) {
        this.contactName = contactName;
        this.duration = duration;
        this.type = type;
        this.date = date;
        this.nbCall = nbCall;
    }
}
