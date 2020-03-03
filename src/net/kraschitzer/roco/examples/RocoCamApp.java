package net.kraschitzer.roco.examples;

import net.kraschitzer.roco.ComController;
import net.kraschitzer.roco.data.CamConnector;
import net.kraschitzer.roco.examples.consumers.ImageFileWriter;
import net.kraschitzer.roco.examples.consumers.awt.gui.VidFrame;
import net.kraschitzer.roco.exceptions.CommunicationException;
import net.kraschitzer.roco.exceptions.FormatException;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class RocoCamApp {

    private enum Mode {
        FRAME,
        FILES,
    }

    public static void main(String[] args) throws IOException, FormatException, CommunicationException {
        Scanner s = new Scanner(System.in);
        String input;

        Mode mode;
        String ip;
        String outDir = null;

        System.out.println("Please select the mode you want to start in:");
        System.out.println("*1 Frame");
        System.out.println("2 Files");
        input = s.nextLine();
        if ("2".equals(input)) {
            mode = Mode.FILES;
        } else {
            mode = Mode.FRAME;
        }

        if (Mode.FILES.equals(mode)) {
            System.out.println("Please supply the output directory for the captured files:");
            outDir = s.nextLine();
        }

        ComController comController = new ComController();
        System.out.println("Please supply the IPv4 address your roco locomotive is connected to:\n" +
                "[giving no ip, will select the first loco found on the network]");
        ip = s.nextLine();
        if (ip.isEmpty()) {
            List<String> ips = comController.findLocos();
            if (ips.isEmpty()) {
                System.out.println("No locos found on the network.");
                System.exit(1);
            }
            ip = ips.get(0);
        }

        CamConnector connector = null;
        switch (mode) {
            case FRAME:
                connector = showImagesInExtendedFrame(ip);
                break;
            case FILES:
                connector = writeImagesToFile(ip, outDir);
                break;
        }

        comController.connectLoco(connector, ip);
    }

    private static CamConnector writeImagesToFile(String ip, String outDir) {
        return new ImageFileWriter(outDir);
    }

    private static CamConnector showImagesInFrame(String ip) {
        VidFrame f = new VidFrame();
        f.setVisible(true);
        return f.getImagePanel();
    }

    private static CamConnector showImagesInExtendedFrame(String ip) {
        VidFrame f = new VidFrame();
        f.setVisible(true);
        return f.getImagePanel();
    }
}
