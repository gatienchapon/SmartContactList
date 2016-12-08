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

/**
 * Created by chapon on 04/11/16.
 */

public class PrintContactListActivity  extends AppCompatActivity {

    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    public static List<Contact> listContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);

        listContact = getListItemData();

        SolventRecyclerViewAdapter rcAdapter = new SolventRecyclerViewAdapter(this, listContact);
        recyclerView.setAdapter(rcAdapter);


        new ManageLocalFile(this);
    }

    private byte[] readFile(File file) {
        int length = (int) file.length();

        byte[] bytes = new byte[length];

        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(bytes);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private void writeToFile(File file, String texte) {
        int length = (int) file.length();
        byte[] toWrite= texte.getBytes();
        int sizeToWrite = toWrite.length;
        byte[] bytes = new byte[length+sizeToWrite];
        byte[] alreadyFilled = readFile(file);
        for(int i=0; i<alreadyFilled.length; i++){
            bytes[i] = alreadyFilled[i];
        }
        //On concatene le reste

        for(int i=0; i<sizeToWrite; i++){
            bytes[length +i] = toWrite[i];
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(bytes);
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        return super.onOptionsItemSelected(item);
    }


}
