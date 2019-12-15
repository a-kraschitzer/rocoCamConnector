package net.kraschitzer.roco.data;

import net.kraschitzer.roco.ImageParser;

import java.net.SocketAddress;

public class Loco {

    private int packageCount;
    private String ip;
    private String name;
    private CamConnector connector;
    private SocketAddress socketAddress;
    private ImageParser imageParser;

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
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
