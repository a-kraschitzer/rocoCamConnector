/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.kraschitzer.roco.data;

import at.kraschitzer.roco.ImageParser;

import java.net.SocketAddress;

public class Loco {

    private int imageCount;
    private String ip;
    private String name;
    private CamConnector connector;
    private SocketAddress socketAddress;
    private ImageParser imageParser;

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CamConnector getConnector() {
        return connector;
    }

    public void setConnector(CamConnector connector) {
        this.connector = connector;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public ImageParser getImageParser() {
        return imageParser;
    }

    public void setImageParser(ImageParser imageParser) {
        this.imageParser = imageParser;
    }
}
