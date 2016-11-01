package fr.unice.polytech.smartcontactlistapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ContactHistory contactHistory = new ContactHistory();
        contactHistory.fillContactHistory(this);
        ContactAdapter contactAdapter = new ContactAdapter(this, R.layout.activity_contact,contactHistory.getContactHistory());
        GridView gridView = (GridView)findViewById(R.id.list_item);
        gridView.setAdapter(contactAdapter);

    }
}
