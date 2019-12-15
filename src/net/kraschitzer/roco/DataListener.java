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
    private static final int COUNT_LENGTH = 2;
    private static final int PACKAGE_COUNT_OFFSET_FROM_BACK = 4;
    private static final int IMAGE_COUNT_OFFSET_FROM_BACK = 2;

    private DatagramSocket socket;
    private boolean running = true;
    private Map<String, Loco> videoSources = new HashMap<>();
    private byte[] receiveBuffer = new byte[RECEIVE_BUFFER_LENGTH];

    public DataListener() throws SocketException {
        socket = new DatagramSocket(DATA_PORT);
    }

    public void run() {
        try {
            while (running || !videoSources.isEmpty()) {
                DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(packet);

                int imageCount = new BigInteger(
                        HexCaster.stringifyInvertByteOrder(Arrays.copyOfRange(packet.getData(),
                                packet.getData().length - IMAGE_COUNT_OFFSET_FROM_BACK,
                                packet.getData().length - IMAGE_COUNT_OFFSET_FROM_BACK + COUNT_LENGTH))
                        , 16).intValue();
                int packageCount = new BigInteger(
                        HexCaster.stringifyInvertByteOrder(Arrays.copyOfRange(packet.getData(),
                                packet.getData().length - PACKAGE_COUNT_OFFSET_FROM_BACK,
                                packet.getData().length - PACKAGE_COUNT_OFFSET_FROM_BACK + COUNT_LENGTH))
                        , 16).intValue();
                for (Map.Entry<String, Loco> e : videoSources.entrySet()) {
                    if (e.getKey().equals(packet.getAddress().getHostAddress())) {
                        Loco loco = e.getValue();
                        if (packageCount > loco.getPackageCount()) {
                            byte[] img = loco.getImageParser().addData(Arrays.copyOf(packet.getData(), packet.getData().length - COUNT_LENGTH), imageCount);
                            if (img != null) {
                                loco.getConnector().setImage(img);
                            }
                            loco.setPackageCount(imageCount);
                        }
                    }
                }
            }
            System.out.println("Leaving loop di loop");
        } catch (IOException e) {
            System.out.println("Caught exception");
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    public void shutDown() {
        running = false;
        videoSources.clear();
    }

    public void addSource(Loco loco) {
        loco.setImageParser(new ImageParser());
        videoSources.put(loco.getIp(), loco);
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

}
