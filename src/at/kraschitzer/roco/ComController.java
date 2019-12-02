package at.kraschitzer.roco;

import at.kraschitzer.roco.util.HexCaster;
import at.stejskal.data.CamLoco;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ComController {
    
    private static final int TARGET_ADDRESS_TRY_COUNT = 5;
    private static final int LOCAL_ADDRESS_TRY_COUNT = 5;
    private static final int PUBLIC_ADDRESS_TRY_COUNT = 5;
    
    private static final int CONTROL_PORT = 5152;
    
    private static final String LOCAL_BROADCAST_ADDRESS = "255";
    private static final String PUBLIC_BROADCAST_ADDRESS = "255.255.255.255";
    
    private static final byte[] ESTABLISH_RESPONSE_EXPECTED = HexCaster.unstringify("bfffffff0b09");
    private static final byte[] ESTABLISH_REQUEST = HexCaster.unstringify("bfffffff0b");
    
    private static final byte[] CAM_ON_REQUEST = HexCaster.unstringify("8fffffff4f4e00000000000000000000");
    private static final byte[] CAM_ON_RESPONSE_EXPECTED = HexCaster.unstringify("8fffffff4f4e5f4f4b00000000000000");
    
    private DatagramSocket socket;
    private DataListener dataListener;
    private byte[] receiveBuffer = new byte[256];
    
    private boolean broadcast;
    
    public ComController() {
        
    }
    
    public void initializeSocket() throws SocketException {
        socket = new DatagramSocket(CONTROL_PORT);
        socket.setSoTimeout(700);
    }
    
    public void startSpecificLoco(CamLoco loco) {
        try {
            
            initializeSocket();
            
            requestLocoInfo(loco);
            
            requestLocoToStartCam(loco);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
    
      
    public void startAllLocos() {
        try {
            
            initializeSocket();
            
            ArrayList<CamLoco> allLocos = requestLocoInfos();
            
            for(CamLoco loco : allLocos){
                requestLocoToStartCam(loco);
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
    
    private void requestLocoInfo(CamLoco loco) throws IOException {
        
        List<DatagramPacket> recivedPackets = sendRequest(new DatagramPacket(ESTABLISH_REQUEST, ESTABLISH_REQUEST.length, loco.inetAddress, CONTROL_PORT), TARGET_ADDRESS_TRY_COUNT, ESTABLISH_RESPONSE_EXPECTED);
        loco.socketAddress = recivedPackets.get(0).getSocketAddress();
    }
    
    private void requestLocoToStartCam(CamLoco loco) throws IOException {
        List<DatagramPacket> res = sendRequest(new DatagramPacket(CAM_ON_REQUEST, CAM_ON_REQUEST.length, loco.socketAddress), 2, CAM_ON_RESPONSE_EXPECTED);
        if (!res.isEmpty()) {
            if (dataListener == null) {
                dataListener = new DataListener();
                new Thread(dataListener).start();
            }
            dataListener.addSource(loco);
        }
    }
   

    private ArrayList<CamLoco> requestLocoInfos() throws IOException {
        List<DatagramPacket> responsePackets = sendRequest(new DatagramPacket(ESTABLISH_REQUEST, ESTABLISH_REQUEST.length, InetAddress.getByName(PUBLIC_BROADCAST_ADDRESS), CONTROL_PORT),
                PUBLIC_ADDRESS_TRY_COUNT, ESTABLISH_RESPONSE_EXPECTED);

        ArrayList<CamLoco> locos = new ArrayList<>();
        for (DatagramPacket paket : responsePackets) {
            String ip = paket.getAddress().getHostAddress();
            CamLoco c = new CamLoco(ip);
            c.socketAddress = paket.getSocketAddress();
                    
            locos.add(c);
        }

        return locos;
    }
    
    
    private List<DatagramPacket> sendRequest(DatagramPacket packet, int maxCount, byte[] expectedResponse) throws IOException {
        List<DatagramPacket> receivedPackets = new ArrayList<>();
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        int tryCount = 0;
        while (receivedPackets.isEmpty() && tryCount < maxCount) {
            System.out.println("<-- Sending to " + packet.getAddress() + ":" + packet.getPort() + " hex='" + HexCaster.stringify(packet.getData()) + "' ascii='" + new String(packet.getData()) + "' len=" + packet.getLength());
            socket.send(packet);
            try {
                while (true) {
                    socket.receive(receivePacket);
                    System.out.println("--> Received from " + receivePacket.getSocketAddress() + " hex='" + HexCaster.stringify(receivePacket.getData()) + "' ascii='" + new String(receivePacket.getData()) + "' len=" + packet.getLength());
                    if (HexCaster.compareByteArraysFromStart(expectedResponse, receivePacket.getData(), expectedResponse.length)) {
                        receivedPackets.add(receivePacket);
                    }
                }
            } catch (SocketTimeoutException e) {
                System.out.println(" -- timeout");
            } finally {
                tryCount++;
            }
        }
        return receivedPackets;
    }
    
}
