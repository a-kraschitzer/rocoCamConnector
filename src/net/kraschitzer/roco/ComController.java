package net.kraschitzer.roco;

import net.kraschitzer.roco.data.CamConnector;
import net.kraschitzer.roco.data.CamConnectorExtended;
import net.kraschitzer.roco.data.Loco;
import net.kraschitzer.roco.exceptions.CommunicationException;
import net.kraschitzer.roco.exceptions.FormatException;
import net.kraschitzer.roco.util.HexCaster;
import net.kraschitzer.roco.util.IpAddressValidator;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComController {

    private static final int TARGET_ADDRESS_TRY_COUNT = 5;
    private static final int PUBLIC_ADDRESS_TRY_COUNT = 2;
    private static final int CONTROL_PORT = 5152;

    private static final String PUBLIC_BROADCAST_ADDRESS = "255.255.255.255";

    private static final int ESTABLISH_RESPONSE_EXPECTED_LENGTH = 15;
    private static final byte[] ESTABLISH_RESPONSE_EXPECTED = HexCaster.unstringify("bfffffff0b09");
    private static final byte[] ESTABLISH_REQUEST = HexCaster.unstringify("bfffffff0b");

    private static final byte[] CAM_ON_REQUEST = HexCaster.unstringify("8fffffff4f4e00000000000000000000");
    private static final byte[] CAM_ON_RESPONSE_EXPECTED = HexCaster.unstringify("8fffffff4f4e5f4f4b00000000000000");

    private DatagramSocket socket;
    private DataListener dataListener;
    private byte[] receiveBuffer = new byte[256];
    private boolean debug = false;

    public ComController() throws SocketException {
        initializeSocket();
    }

    public ComController(boolean debug) throws SocketException {
        initializeSocket();
        this.debug = debug;
    }

    public void initializeSocket() throws SocketException {
        socket = new DatagramSocket(CONTROL_PORT);
        socket.setSoTimeout(2000);
    }

    public void connectLoco(CamConnector connector, String ip) throws FormatException, CommunicationException {
        if (!IpAddressValidator.isValid(ip)) {
            throw new FormatException("Given string '" + ip + "' is not a valid ip");
        }
        if (connector == null) {
            throw new CommunicationException("The given connector is invalid");
        }
        try {
            Loco loco = new Loco();
            loco.setIp(ip);
            loco.setConnector(connector);

            requestLocoInfo(loco);
            if (connector instanceof CamConnectorExtended) {
                ((CamConnectorExtended) connector).setName(loco.getName());
            }

            requestLocoToStartCam(loco);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectLoco(CamConnector connector) {
        dataListener.removeSource(connector);
    }

    public List<String> findLocos() {
        try {
            List<DatagramPacket> responsePackets = sendRequest(new DatagramPacket(ESTABLISH_REQUEST, ESTABLISH_REQUEST.length, InetAddress.getByName(PUBLIC_BROADCAST_ADDRESS), CONTROL_PORT),
                    PUBLIC_ADDRESS_TRY_COUNT, ESTABLISH_RESPONSE_EXPECTED);
            List<String> list = new ArrayList<>();
            for (DatagramPacket responsePacket : responsePackets) {
                list.add(responsePacket.getAddress().getHostAddress());
            }
            return list;
        } catch (IOException e) {
            log("Failed to send Request: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private void requestLocoInfo(Loco loco) throws IOException, CommunicationException {
        List<DatagramPacket> receivedPackets = sendRequest(new DatagramPacket(ESTABLISH_REQUEST, ESTABLISH_REQUEST.length, InetAddress.getByName(loco.getIp()), CONTROL_PORT),
                TARGET_ADDRESS_TRY_COUNT, ESTABLISH_RESPONSE_EXPECTED);
        if (receivedPackets.size() < 1) {
            throw new CommunicationException("No response from Loco '" + loco.getIp() + "' received.");
        }
        DatagramPacket receivedPacket = receivedPackets.get(0);
        loco.setSocketAddress(receivedPacket.getSocketAddress());
        byte[] receivedData = receivedPacket.getData();
        if (receivedData.length >= ESTABLISH_RESPONSE_EXPECTED_LENGTH) {
            loco.setName(new String(receivedData).substring(ESTABLISH_RESPONSE_EXPECTED.length, ESTABLISH_RESPONSE_EXPECTED_LENGTH));
        }
    }

    private void requestLocoToStartCam(Loco loco) throws IOException {
        List<DatagramPacket> res = sendRequest(new DatagramPacket(CAM_ON_REQUEST, CAM_ON_REQUEST.length, loco.getSocketAddress()), 2, CAM_ON_RESPONSE_EXPECTED);
        if (!res.isEmpty()) {
            if (dataListener == null) {
                dataListener = new DataListener(debug);
                new Thread(dataListener).start();
            }
            dataListener.addSource(loco);
        }
    }

    private List<DatagramPacket> sendRequest(DatagramPacket packet, int maxCount, byte[] expectedResponse) throws IOException {
        List<DatagramPacket> receivedPackets = new ArrayList<>();
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        int tryCount = 0;
        while (receivedPackets.isEmpty() && tryCount < maxCount) {
            log("<-- Sending to " + packet.getAddress() + ":" + packet.getPort() + " hex='" + HexCaster.stringify(packet.getData()) + "' ascii='" + new String(packet.getData()) + "' len=" + packet.getLength());
            socket.send(packet);
            try {
                while (true) {
                    socket.receive(receivePacket);
                    log("--> Received from " + receivePacket.getSocketAddress() + " hex='" + HexCaster.stringify(receivePacket.getData()) + "' ascii='" + new String(receivePacket.getData()) + "' len=" + packet.getLength());
                    if (HexCaster.compareByteArraysFromStart(expectedResponse, receivePacket.getData(), expectedResponse.length)) {
                        receivedPackets.add(receivePacket);
                    }
                }
            } catch (SocketTimeoutException e) {
                log(" -- timeout");
            } finally {
                tryCount++;
            }
        }
        return receivedPackets;
    }

    private void log(String s) {
        if (debug) {
            System.out.println(s);
        }
    }
}
