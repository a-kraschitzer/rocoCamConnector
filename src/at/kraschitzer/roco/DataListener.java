package at.kraschitzer.roco;

import at.kraschitzer.roco.util.HexCaster;
import at.stejskal.data.CamLoco;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DataListener implements Runnable {

    private static final int DATA_PORT = 5153;
    private static final int RECEIVE_BUFFER_LENGTH = 1028;

    private DatagramSocket socket;
    private boolean running = true;
    private Map<InetAddress, VidFrame> videoSources = new HashMap<>();
    private byte[] receiveBuffer = new byte[RECEIVE_BUFFER_LENGTH];

    public DataListener() throws SocketException {
        socket = new DatagramSocket(DATA_PORT);
    }

    public void run() {
        try {
            while (running || !videoSources.isEmpty()) {
                DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                //System.out.println("Waiting to receive...");
                socket.receive(packet);

                // ########### shorten array 4 bytes due to counter
                packet.setData(Arrays.copyOf(packet.getData(), packet.getData().length - 4));

                //System.out.println("Received " + packet.getLength() + "bytes from " + packet.getAddress());
                for (Map.Entry<InetAddress, VidFrame> e : videoSources.entrySet()) {
                    if (e.getKey().toString().equals(packet.getAddress().toString())) {
                        //e.getValue().addData(receiveBuffer);
                        e.getValue().addData(packet.getData());
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

    public void addSource(CamLoco loco) {
        VidFrame vidFrame = new VidFrame(this);
        vidFrame.setVisible(true);
        videoSources.put(loco.inetAddress, vidFrame);
        if (loco.startPosX != -1) {
            vidFrame.setLocationCoordinates(loco.startPosX, loco.startPosY);
            vidFrame.setName(loco.name);
        }
    }

    public void removeSource(VidFrame frame) {
        for (Map.Entry<InetAddress, VidFrame> e : videoSources.entrySet()) {
            if (e.getValue() == frame) {
                frame.setVisible(false);
                videoSources.remove(e.getKey());
                System.out.println("Removed frame for source '" + e.getKey() + "'");
                running = false;
            }
        }
    }
}
