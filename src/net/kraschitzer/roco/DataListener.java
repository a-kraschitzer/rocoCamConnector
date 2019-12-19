package net.kraschitzer.roco;

import net.kraschitzer.roco.data.CamConnector;
import net.kraschitzer.roco.data.Loco;
import net.kraschitzer.roco.util.HexCaster;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DataListener implements Runnable {

    private static final int DATA_PORT = 5153;
    private static final int RECEIVE_BUFFER_LENGTH = 1028;
    private static final int META_DATA_LENGTH = 4;
    private static final int PACKAGE_COUNT_OFFSET_FROM_BACK = 4;
    private static final int PACKAGE_COUNT_LENGTH = 2;
    private static final int IMAGE_COUNT_OFFSET_FROM_BACK = 2;
    private static final int IMAGE_COUNT_LENGTH = 1;

    private DatagramSocket socket;
    private boolean running = true;
    private Map<String, Loco> videoSources = new HashMap<>();
    private byte[] receiveBuffer = new byte[RECEIVE_BUFFER_LENGTH];
    private boolean debug = false;

    public DataListener(boolean debug) throws SocketException {
        this.debug = debug;
        socket = new DatagramSocket(DATA_PORT);
    }

    public void run() {
        try {
            while (running || !videoSources.isEmpty()) {
                DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(packet);

                int newImageCount = parseImageCount(packet);
                int newPackageCount = parsePackageCount(packet);

                for (Map.Entry<String, Loco> e : videoSources.entrySet()) {
                    if (e.getKey().equals(packet.getAddress().getHostAddress())) {
                        Loco loco = e.getValue();
                        log("[" + loco.getImageParser() + "] " + ", PackageCount: (old=" + loco.getPackageCount() + ", new=" + newPackageCount + ")");
                        if (didPackageCountIncrease(loco.getPackageCount(), newPackageCount)) {
                            if (newPackageCount != (loco.getPackageCount() + 1)) {
                                loco.getImageParser().resetImageData();
                            }
                            byte[] img = loco.getImageParser().addData(Arrays.copyOf(packet.getData(), packet.getData().length - META_DATA_LENGTH), newImageCount);
                            if (img != null) {
                                loco.getConnector().setImage(img);
                            }
                            loco.setPackageCount(newPackageCount);
                        }
                    }
                }
            }
            System.out.println("Shutting down DataListener.");
        } catch (IOException e) {
            System.out.println("Caught exception");
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    private int parseImageCount(DatagramPacket packet) {
        return new BigInteger(
                HexCaster.stringifyInvertByteOrder(Arrays.copyOfRange(packet.getData(),
                        packet.getData().length - IMAGE_COUNT_OFFSET_FROM_BACK,
                        packet.getData().length - IMAGE_COUNT_OFFSET_FROM_BACK + IMAGE_COUNT_LENGTH))
                , 16).intValue();
    }

    private int parsePackageCount(DatagramPacket packet) {
        return new BigInteger(
                HexCaster.stringifyInvertByteOrder(Arrays.copyOfRange(packet.getData(),
                        packet.getData().length - PACKAGE_COUNT_OFFSET_FROM_BACK,
                        packet.getData().length - PACKAGE_COUNT_OFFSET_FROM_BACK + PACKAGE_COUNT_LENGTH))
                , 16).intValue();
    }

    private boolean didPackageCountIncrease(int oldPackageCount, int newPackageCount) {
        if (newPackageCount > oldPackageCount) {
            return true;
        }
        if ((oldPackageCount - newPackageCount) > 10000) {
            return true;
        }
        return false;
    }

    public void shutDown() {
        running = false;
        videoSources.clear();
    }

    public void addSource(Loco loco) {
        loco.setImageParser(new ImageParser(debug, loco.getIp()));
        videoSources.put(loco.getIp(), loco);
        System.out.println("Added Video Source at '" + loco.getIp() + "'");
    }

    public void removeSource(CamConnector connector) {
        for (Map.Entry<String, Loco> e : videoSources.entrySet()) {
            if (e.getValue().getConnector() == connector) {
                videoSources.remove(e.getKey());
                System.out.println("Removed frame for source '" + e.getKey() + "'");
                running = false;
            }
        }
    }

    private void log(String s) {
        if (debug) {
            System.out.println(s);
        }
    }
}
