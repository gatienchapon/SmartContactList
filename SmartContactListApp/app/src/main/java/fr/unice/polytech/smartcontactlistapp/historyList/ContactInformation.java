package fr.unice.polytech.smartcontactlistapp.historyList;

import java.util.Date;

/**
 * Created by chapon on 01/11/16.
 */

public class ContactInformation {
    private String callDate;
    private float callDuration;
    private String callNumber;
    private String callName;
    private String callType;
    private int nbTotalCall=1;


    public ContactInformation(String callDate, float callDuration, String callNumber, String callName, String callType) {
        this.callDate = callDate;
        this.callDuration = callDuration;
        this.callNumber = callNumber;
        this.callName = callName;
        this.callType = callType;
    }

    public String getCallDate() {
        return callDate;
    }

    public float getCallDuration() {
        return callDuration;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public String getCallName() {
        return callName;
    }

    public String getCallType() {
        return callType;
    }

    public int getNbTotalCall() {
        return nbTotalCall;
    }

    public void increaseTotalCall(){
        nbTotalCall++;
    }
}
