package fr.unice.polytech.smartcontactlistapp.contactList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.unice.polytech.smartcontactlistapp.R;


public class SolventRecyclerViewAdapter  extends RecyclerView.Adapter<SolventViewHolders> {

    private List<Contact> itemList;
    private Context context;

    public SolventRecyclerViewAdapter(Context context, List<Contact> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public SolventViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_detail, null);
        SolventViewHolders rcv = new SolventViewHolders(layoutView, context);
        return rcv;
    }

    @Override
    public void onBindViewHolder(SolventViewHolders holder, int position) {
        Contact contact = itemList.get(position);
        if (contact != null){
            holder.contactName.setText(contact.name);
            holder.number.setText(contact.numero);
            holder.percentage.setText(contact.percentage);
        }

    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

}