package at.kraschitzer.roco;

import at.kraschitzer.roco.data.CamConnector;
import at.kraschitzer.roco.data.CamConnectorExtended;
import at.kraschitzer.roco.data.Loco;
import at.kraschitzer.roco.exceptions.CommunicationException;
import at.kraschitzer.roco.exceptions.FormatException;
import at.kraschitzer.roco.util.HexCaster;
import at.kraschitzer.roco.util.IpAddressValidator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class ComController {

    private static final int TARGET_ADDRESS_TRY_COUNT = 5;
    private static final int CONTROL_PORT = 5152;

    private static final int ESTABLISH_RESPONSE_EXPECTED_LENGTH = 15;
    private static final byte[] ESTABLISH_RESPONSE_EXPECTED = HexCaster.unstringify("bfffffff0b09");
    private static final byte[] ESTABLISH_REQUEST = HexCaster.unstringify("bfffffff0b");

    private static final byte[] CAM_ON_REQUEST = HexCaster.unstringify("8fffffff4f4e00000000000000000000");
    private static final byte[] CAM_ON_RESPONSE_EXPECTED = HexCaster.unstringify("8fffffff4f4e5f4f4b00000000000000");

    private DatagramSocket socket;
    private DataListener dataListener;
    private byte[] receiveBuffer = new byte[256];

    public ComController() throws SocketException {
        initializeSocket();
    }

    public void initializeSocket() throws SocketException {
        socket = new DatagramSocket(CONTROL_PORT);
        socket.setSoTimeout(700);
    }

    public void startLoco(CamConnector connector, String ip) throws FormatException {
        if (!IpAddressValidator.isValid(ip)) {
            throw new FormatException("Given string '" + ip + "' is not a valid ip");
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
        } finally {
            socket.close();
            dataListener.shutDown();
        }
    }

    public void stopLoco(CamConnector connector) {
        dataListener.removeSource(connector);
    }

    private void requestLocoInfo(Loco loco) throws IOException, CommunicationException {
        List<DatagramPacket> receivedPackets = sendRequest(new DatagramPacket(ESTABLISH_REQUEST, ESTABLISH_REQUEST.length, InetAddress.getByName(loco.getIp()), CONTROL_PORT),
                TARGET_ADDRESS_TRY_COUNT, ESTABLISH_RESPONSE_EXPECTED);
        if (receivedPackets.size() < 1) {
            throw new CommunicationException(loco.getIp());
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
                dataListener = new DataListener();
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
