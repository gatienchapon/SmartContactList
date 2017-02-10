package fr.unice.polytech.smartcontactlistapp.synchronisation;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by chapon on 10/02/17.
 */

public class UDP {

    private Context context;
    private InetAddress broadCast;
    public UDP(Context c){
        context = c;
        broadCast = getBroadcastAddress();
    }
    private InetAddress getBroadcastAddress() {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) (broadcast >> (k * 8));
        try {
            return InetAddress.getByAddress(quads);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getServerIpAdress(){


        byte[] sendData = "smart_contact_list".getBytes();

        try {
            if(broadCast != null) {
                // create an unbound socket
                DatagramSocket c = new DatagramSocket();
                c.setBroadcast(true);
                c.setSoTimeout(5000);
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadCast, 6000);
                c.send(sendPacket);
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                c.receive(packet);
                String received = packet.getAddress().getHostAddress();
                return received;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }
}
