import net.kraschitzer.roco.ComController;
import net.kraschitzer.roco.consumers.ImageFileWriter;
import net.kraschitzer.roco.consumers.awt.gui.VidFrame;
import net.kraschitzer.roco.exceptions.FormatException;

import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;

public class Main {

    private enum Mode {
        FRAME,
        FILES,
    }

    public static void main(String[] args) throws IOException, FormatException {
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

        System.out.println("Please supply the IPv4 address your roco locomotive is connected to:");
        ip = s.nextLine();

        switch (mode) {
            case FRAME:
                showImagesInExtendedFrame(ip);
                break;
            case FILES:
                writeImagesToFile(ip, outDir);
                break;
        }
    }

    private static void writeImagesToFile(String ip, String outDir) throws SocketException, FormatException {
        ImageFileWriter imageFileWriter = new ImageFileWriter(outDir);
        ComController comController = new ComController();
        comController.startLoco(imageFileWriter, ip);
    }

    private static void showImagesInFrame(String ip) throws SocketException, FormatException {
        VidFrame f = new VidFrame();
        f.setVisible(true);
        ComController comController = new ComController();
        comController.startLoco(f.getImagePanel(), ip);
    }

    private static void showImagesInExtendedFrame(String ip) throws SocketException, FormatException {
        VidFrame f = new VidFrame();
        f.setVisible(true);
        ComController comController = new ComController();
        comController.startLoco(f.getImagePanel(), ip);
    }
}
