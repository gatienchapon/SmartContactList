package fr.unice.polytech.smartcontactlistapp.contactList;

/**
 * Created by chapon on 04/11/16.
 */
public class Contact {
    public String name;
    public String numero;
    public String percentage;

    public Contact(String name, String numero, String percentage) {
        this.name = name;
        this.numero = numero;
        this.percentage = percentage;

    }

    public Contact(String name, String score) {
        this.name = name;
        this.percentage = score;
        this.numero="00 00 00 00";
    }

    public int compareTo(Contact a) {
        double pers1 = Double.parseDouble(a.percentage.split("%")[0]);
        double pers2 = Double.parseDouble(percentage.split("%")[0]);
        if(pers1>pers2)
            return -1;
        else if(pers1 == pers2)
            return 0;
        else
            return 1;
    }
}
