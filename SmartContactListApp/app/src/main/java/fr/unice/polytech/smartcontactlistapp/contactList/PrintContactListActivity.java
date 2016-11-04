package fr.unice.polytech.smartcontactlistapp.contactList;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import fr.unice.polytech.smartcontactlistapp.R;

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
    }

    private List<Contact> getListItemData(){
        RetreiveContactList retreiveContactList= new RetreiveContactList();
        retreiveContactList.fillContactHistory(this);
        List<Contact> list = retreiveContactList.getListContact();
        return list;
    }


}
