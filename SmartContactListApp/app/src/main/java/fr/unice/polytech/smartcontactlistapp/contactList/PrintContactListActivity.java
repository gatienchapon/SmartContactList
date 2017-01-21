package fr.unice.polytech.smartcontactlistapp.contactList;

import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

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
import fr.unice.polytech.smartcontactlistapp.localHistoryManager.Vector;
import fr.unice.polytech.smartcontactlistapp.synchronisation.SynchronisationActivity;

import static fr.unice.polytech.smartcontactlistapp.DB.DB.contact_list_application;
import static fr.unice.polytech.smartcontactlistapp.DB.DB.contact_list_mobile;
import static fr.unice.polytech.smartcontactlistapp.DB.DB.init_contact_list_application;
import static fr.unice.polytech.smartcontactlistapp.DB.DB.loadFile;

/**
 * Created by chapon on 04/11/16.
 */

public class PrintContactListActivity  extends AppCompatActivity {

    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private TextView currentSlotTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initialisation();
    }

    private void initialisation() {
        currentSlotTime = (TextView) findViewById(R.id.currentSlotTime);
        Date date = new Date();
        Vector v = new Vector(date,"");
        fillSlotTime(v.getTimeSlot());
        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        Button makeSync =(Button)findViewById(R.id.make_Sync);

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);

        contact_list_mobile = getListItemData();
        if(init_contact_list_application(this)){
            reload(recyclerView);
            makeSync.setVisibility(View.INVISIBLE);
        }else
        {
            currentSlotTime.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            makeSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intente = new Intent(PrintContactListActivity.this, SynchronisationActivity.class);
                    startActivity(intente);
                }
            });

        }
        currentSlotTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPupUp(recyclerView);

            }
        });
        new ManageLocalFile(this);
    }

    private void fillSlotTime(String slotTime) {
        Vector v = new Vector(slotTime);
        String begin =v.getBeginEndSlotTime()[0];
        String end =v.getBeginEndSlotTime()[1];
        currentSlotTime.setText("Current period : "+begin+":00 to "+end+":00");
    }

    private void openPupUp(final RecyclerView recyclerView) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Change Slot Time");
        dialog.setContentView(R.layout.pop_up);
        final List<String > allSlotTimeAvalaibleToPrint = new ArrayList<>();
        final List<String > allSlotTimeAvalaible = new ArrayList<>();
        Date today = new Date();
        Vector v = new Vector(today,"");
        int hour = 6;
        for(int i=0; i<=16; i+=2){
            v.fillTimeSlot(hour+i);
            String begin =v.getBeginEndSlotTime()[0];
            String end =v.getBeginEndSlotTime()[1];
            allSlotTimeAvalaible.add(v.getTimeSlot());
            allSlotTimeAvalaibleToPrint.add(begin+":00 to "+end+":00");
        }
        v.fillTimeSlot(1);
        String begin =v.getBeginEndSlotTime()[0];
        String end =v.getBeginEndSlotTime()[1];
        allSlotTimeAvalaible.add(v.getTimeSlot());
        allSlotTimeAvalaibleToPrint.add(begin+":00 to "+end+":00");
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allSlotTimeAvalaibleToPrint);

        GridView gridView = (GridView) dialog.findViewById(R.id.popup);
        gridView.setAdapter(itemsAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadslotime(recyclerView, position,allSlotTimeAvalaible);
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    private void loadslotime(RecyclerView recyclerView, int position, List<String > allSlotTimeAvalaible) {
        for(int i=0; i<allSlotTimeAvalaible.size(); i++){
            Log.d("SlotTime", allSlotTimeAvalaible.get(i)+" position : "+position);
        }
        if(loadFile(allSlotTimeAvalaible.get(position), this)) {
            fillSlotTime(allSlotTimeAvalaible.get(position));
            reload(recyclerView);
        }
    }

    private void reload(RecyclerView recyclerView) {
        SolventRecyclerViewAdapter rcAdapter = new SolventRecyclerViewAdapter(this, contact_list_application);
        recyclerView.swapAdapter(rcAdapter,false);
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
        /*if (id == R.id.journal_appel) {
            Intent intente = new Intent(PrintContactListActivity.this, ContactActivity.class);
            startActivity(intente);
            return true;
        }*/
        //if (id == R.id.actualiser) {

          //  Intent intente = getIntent();
            //finish();
            //startActivity(intente);
            //return true;
        //}

        return super.onOptionsItemSelected(item);
    }


}
