package fr.unice.polytech.smartcontactlistapp.historyList;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;

import fr.unice.polytech.smartcontactlistapp.R;

public class ContactActivity extends AppCompatActivity {

    public static ContactHistory contactHistory = new ContactHistory();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        contactHistory.fillContactHistory(this);
        ContactAdapter contactAdapter = new ContactAdapter(this, R.layout.activity_contact,contactHistory.getContactHistory());
        GridView gridView = (GridView)findViewById(R.id.list_item);
        gridView.setAdapter(contactAdapter);
        //Entete
        TextView entete = (TextView)findViewById(R.id.historyLength);
        entete.setText("Longueur historique : "+ contactHistory.getContactHistory().size());

    }
}
