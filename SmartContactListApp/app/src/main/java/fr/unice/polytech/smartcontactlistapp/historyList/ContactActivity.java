package fr.unice.polytech.smartcontactlistapp.historyList;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;

import fr.unice.polytech.smartcontactlistapp.R;
import fr.unice.polytech.smartcontactlistapp.contactList.PrintContactListActivity;
import fr.unice.polytech.smartcontactlistapp.synchronisation.SynchronisationActivity;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.accueil) {
            Intent intente = new Intent(ContactActivity.this, PrintContactListActivity.class);
            startActivity(intente);
            return true;
        }
        if (id == R.id.synchronisation) {
            Intent intente = new Intent(ContactActivity.this, SynchronisationActivity.class);
            startActivity(intente);

            return true;
        }
        /*if (id == R.id.journal_appel) {
            Intent intente = new Intent(ContactActivity.this, ContactActivity.class);
            startActivity(intente);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
