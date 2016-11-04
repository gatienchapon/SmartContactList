package fr.unice.polytech.smartcontactlistapp.contactList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import fr.unice.polytech.smartcontactlistapp.R;

public class SolventViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView contactName;
    public TextView number;
    public ImageView contactPhoto;
    private Context context;

    public SolventViewHolders(View itemView, Context context) {
        super(itemView);
        itemView.setOnClickListener(this);
        contactName = (TextView) itemView.findViewById(R.id.contact);
        number = (TextView) itemView.findViewById(R.id.numero);
        contactPhoto =(ImageView) itemView.findViewById(R.id.contactPhoto);
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT).show();
        String numero = PrintContactListActivity.listContact.get(getPosition()).numero;
        Intent i = new Intent(Intent.ACTION_DIAL);
        String p = "tel:" + numero;
        i.setData(Uri.parse(p));
        context.startActivity(i);
    }

}