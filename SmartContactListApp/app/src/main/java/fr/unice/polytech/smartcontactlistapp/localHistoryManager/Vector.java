package fr.unice.polytech.smartcontactlistapp.localHistoryManager;

import java.util.Calendar;
import java.util.Date;

/**
 * Creation d'un vecteur pour une date passé en argument
 */

public class Vector {
    public static String[] classes = {"Year","Month","DayNumber","DayWeek","Hour","Minute","Seconde","SlotTime","Class"};
    public static String[] classes2 = {"Class","DayNumber","DayWeek","Hour","Minute","Month","Seconde","SlotTime","Year"};
    public String year;
    public String month;
    public String numberDay;
    private String weekDay;
    public String hour;
    public String minute;
    private String seconde;

    public Vector(String slotTime) {
        timeSlot =slotTime;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    private String timeSlot;
    private String contactName;

    public Vector(Date date, String contactName){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        year = ""+calendar.get(Calendar.YEAR);
        int mois = calendar.get(Calendar.MONTH)+1;
        month = ""+mois;
        numberDay = ""+calendar.get(Calendar.DATE);
        weekDay = ""+calendar.get(Calendar.DAY_OF_WEEK);
        hour = ""+calendar.get(Calendar.HOUR_OF_DAY);
        minute = ""+calendar.get(Calendar.MINUTE);
        seconde = ""+calendar.get(Calendar.SECOND);
        fillTimeSlot(calendar.get(Calendar.HOUR_OF_DAY));
        this.contactName = contactName;
    }
    public String[] getBeginEndSlotTime(){
        String[] result = new String[2];
        if(timeSlot.length() == 4){
            result[0] = timeSlot.charAt(0)+""+timeSlot.charAt(1);
            result[1] = timeSlot.charAt(2)+""+timeSlot.charAt(3);
        }else if(timeSlot.length() == 3){
            result[0] = "0"+timeSlot.charAt(0)+"";
            result[1] = timeSlot.charAt(1)+""+timeSlot.charAt(2);
        }else{
            result[0] = "00";
            result[1] = timeSlot.charAt(0)+"";
        }
        return result;
    }
    public void fillTimeSlot(int hour){
        String[] slots = {"810","1012","1214","1416","1618","1820","2022","2200","6","608"};
        if(hour >= 8 && hour <10){
            timeSlot = slots[0];
        }else if(hour >= 10 && hour <12){
            timeSlot = slots[1];
        }else if(hour >= 12 && hour <14){
            timeSlot = slots[2];
        }else if(hour >= 14 && hour <16){
            timeSlot = slots[3];
        }else if(hour >= 16 && hour <18){
            timeSlot = slots[4];
        }else if(hour >= 18 && hour <20){
            timeSlot = slots[5];
        }else if(hour >= 20 && hour <22){
            timeSlot = slots[6];
        }else if(hour >= 22){
            timeSlot = slots[7];
        }else if(hour >= 0 && hour <6){
            timeSlot = slots[8];
        }else if(hour >= 6 && hour <8){
            timeSlot = slots[9];
        }

    }

    public String toString(){
        String res ="";
        res+=year+",";
        res+=month+",";
        res+=numberDay+",";
        res+=weekDay+",";
        res+=hour+",";
        res+=minute+",";
        res+=seconde+",";
        res+=timeSlot+",";
        res+=contactName+"\n";
        return res;
    }
}
