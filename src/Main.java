import at.kraschitzer.roco.ComController;
import at.kraschitzer.roco.consumers.ImageFileWriter;
import at.kraschitzer.roco.consumers.awt.gui.VidFrame;
import at.kraschitzer.roco.exceptions.FormatException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.net.SocketException;
import java.net.UnknownHostException;

public class Main{

    public static void main(String[] args) throws IOException, FormatException {
        writeImagesToFile();
    }

    private static void writeImagesToFile() throws SocketException, FormatException {
        ImageFileWriter imageFileWriter = new ImageFileWriter("./out");
        ComController comController = new ComController();
        comController.startLoco(imageFileWriter, "192.168.1.114");
    }

    private static void showImagesInFrame() throws SocketException, FormatException {
        VidFrame f = new VidFrame();
        f.setVisible(true);
        ComController comController = new ComController();
        comController.startLoco(f.getImagePanel(), "192.168.1.114");
    }

    private static void showImagesInExtendedFrame() throws SocketException, FormatException {
        VidFrame f = new VidFrame();
        f.setVisible(true);
        ComController comController = new ComController();
        comController.startLoco(f.getImagePanel(), "192.168.1.114");
    }

    private static void startLocoForImagePanel() {
//        Scanner s = new Scanner(System.in);
//        String input = "";
//
//        InetAddress address = null;
//        int port = 0;
//
//
//        System.out.println("Please provide ip range [192.168.0.0]");
//        //input = s.nextLine();
//        try {
//            if (input != null && input.isEmpty()) {
//                input = "192.168.0.0";
//            }
//            address = InetAddress.getByName(input);
//        } catch (UnknownHostException e) {
//            System.out.println("invalid input '" + input + "'");
//        }
//
//        System.out.println("Please provide a port for setting up the communication [5152]");
//        //input = s.nextLine();
//        try {
//            if (input != null && input.isEmpty()) {
//                input = "5152";
//            }
//            port = Integer.parseInt(input);
//        } catch (NumberFormatException e) {
//            System.out.println("invalid input '" + input + "'");
//        }

        /*
        READ FROM INI/cams.ini
        ArrayList<CamLoco> locos = new ArrayList<>();

        BufferedReader br = FileOperation.loadData("INI/cams.ini");
        String line = br.readLine();

        while (line != null) {
            String[] values = line.split(";");
            locos.add(new CamLoco(values[0], Integer.parseInt(values[1]), Integer.parseInt(values[2]), values[3]));
            line = br.readLine();
        }

        ComController com = new ComController();

        for (CamLoco loco : locos) {
            com.startSpecificLoco(loco);

        }
         */




//        com.startAllLocos();
    }
    
    
    
}
