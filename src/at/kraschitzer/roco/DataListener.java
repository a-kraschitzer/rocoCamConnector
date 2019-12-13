package at.kraschitzer.roco;

import at.kraschitzer.roco.data.CamConnector;
import at.kraschitzer.roco.data.Loco;
import at.kraschitzer.roco.util.HexCaster;

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
    private static final int COUNT_LENGTH = 4;

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

                int imageCount = new BigInteger(HexCaster.stringify(Arrays.copyOfRange(packet.getData(), packet.getData().length - COUNT_LENGTH, packet.getData().length)), 16).intValue();
                for (Map.Entry<String, Loco> e : videoSources.entrySet()) {
                    if (e.getKey().equals(packet.getAddress().toString())) {
                        Loco loco = e.getValue();
                        if (imageCount > loco.getImageCount()) {
                            byte[] img = loco.getImageParser().addData(Arrays.copyOf(packet.getData(), packet.getData().length - COUNT_LENGTH));
                            if (img != null) {
                                loco.getConnector().setImage(img);
                            }
                            loco.setImageCount(imageCount);
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
