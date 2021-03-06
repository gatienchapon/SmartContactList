package fr.unice.polytech.smartcontactlistapp.contactList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fr.unice.polytech.smartcontactlistapp.R;

import static fr.unice.polytech.smartcontactlistapp.DB.DB.contact_list_application;

public class SolventViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView contactName;
    public TextView number;
    public TextView percentage;
    public ImageView contactPhoto;
    private Context context;

    public SolventViewHolders(View itemView, Context context) {
        super(itemView);
        itemView.setOnClickListener(this);
        contactName = (TextView) itemView.findViewById(R.id.contact);
        number = (TextView) itemView.findViewById(R.id.numero);
        contactPhoto =(ImageView) itemView.findViewById(R.id.contactPhoto);
        contactPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call();
            }
        });
        percentage = (TextView) itemView.findViewById(R.id.percentage);
        this.context = context;
    }

    private void call() {
        String numero = contact_list_application.get(getPosition()).numero;
        Intent i = new Intent(Intent.ACTION_DIAL);
        String p = "tel:" + numero;
        i.setData(Uri.parse(p));
        context.startActivity(i);
    }

    @Override
    public void onClick(View view) {


    }

}