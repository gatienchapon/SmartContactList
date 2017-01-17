package fr.unice.polytech.smartcontactlistapp.contactList;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import fr.unice.polytech.smartcontactlistapp.R;
import fr.unice.polytech.smartcontactlistapp.historyList.ContactActivity;
import fr.unice.polytech.smartcontactlistapp.localHistoryManager.ManageLocalFile;
import fr.unice.polytech.smartcontactlistapp.synchronisation.SynchronisationActivity;

import static fr.unice.polytech.smartcontactlistapp.DB.DB.contact_list_application;
import static fr.unice.polytech.smartcontactlistapp.DB.DB.contact_list_mobile;
import static fr.unice.polytech.smartcontactlistapp.DB.DB.init_contact_list_application;

/**
 * Created by chapon on 04/11/16.
 */

public class PrintContactListActivity  extends AppCompatActivity {

    private StaggeredGridLayoutManager gaggeredGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initialisation();
    }

    private void initialisation() {
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);

        contact_list_mobile = getListItemData();
        init_contact_list_application(this);
        SolventRecyclerViewAdapter rcAdapter = new SolventRecyclerViewAdapter(this, contact_list_application);
        recyclerView.setAdapter(rcAdapter);


        new ManageLocalFile(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialisation();
    }

    private List<Contact> getListItemData(){
        RetreiveContactList retreiveContactList= new RetreiveContactList();
        retreiveContactList.fillContactHistory(this);
        List<Contact> list = retreiveContactList.getListContact();
        return list;
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
            Intent intente = new Intent(PrintContactListActivity.this, PrintContactListActivity.class);
            startActivity(intente);
            return true;
        }
        if (id == R.id.synchronisation) {
            Intent intente = new Intent(PrintContactListActivity.this, SynchronisationActivity.class);
            startActivity(intente);
            return true;
        }
        if (id == R.id.journal_appel) {
            Intent intente = new Intent(PrintContactListActivity.this, ContactActivity.class);
            startActivity(intente);
            return true;
        }
        //if (id == R.id.actualiser) {

          //  Intent intente = getIntent();
            //finish();
            //startActivity(intente);
            //return true;
        //}

        return super.onOptionsItemSelected(item);
    }


}
