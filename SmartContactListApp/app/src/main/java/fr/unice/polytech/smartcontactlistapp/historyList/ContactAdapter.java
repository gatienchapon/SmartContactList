package fr.unice.polytech.smartcontactlistapp.historyList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fr.unice.polytech.smartcontactlistapp.R;

/**
 * Created by chapon on 01/11/16.
 */

public class ContactAdapter extends ArrayAdapter<ContactInformation> {
    public ContactAdapter(Context context, int resource, List<ContactInformation> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_detail,null);
        }
        ContactViewOlder contactViewOlder = (ContactViewOlder) convertView.getTag();
        if(contactViewOlder == null) {
            TextView contactName = (TextView) convertView.findViewById(R.id.contactName);
            TextView duration = (TextView) convertView.findViewById(R.id.duration);
            TextView type = (TextView) convertView.findViewById(R.id.type);
            TextView date = (TextView) convertView.findViewById(R.id.date);
            TextView nbCall = (TextView) convertView.findViewById(R.id.nbCall);
            contactViewOlder = new ContactViewOlder(contactName, duration, type, date,nbCall);
            contactName.setTag(contactViewOlder);
        }
        ContactInformation information = getItem(position);
        if(information != null) {
            contactViewOlder.contactName.setText(information.getCallName() + " : " + information.getCallNumber());
            contactViewOlder.date.setText("Date : " + information.getCallDate().toString());
            contactViewOlder.duration.setText("Duration : " + information.getCallDuration());
            contactViewOlder.type.setText("Type : " + information.getCallType());
            contactViewOlder.nbCall.setText("Nombre total d'appel : "+information.getNbTotalCall());
        }
        return convertView;
    }
}
