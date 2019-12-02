/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.stejskal.data;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class CamLoco {

    public InetAddress inetAddress = null;
    public int startPosX = -1;
    public int startPosY = -1;
    public String name;
    public SocketAddress socketAddress = null;

    public CamLoco(String address) throws UnknownHostException {
        this.inetAddress = InetAddress.getByName(address);
    }

    public CamLoco(String address, int startPosX, int startPosY, String name) throws UnknownHostException {

        this.inetAddress = InetAddress.getByName(address);
        this.startPosX = startPosX;
        this.startPosY = startPosY;
        this.name = name;
    }
}
