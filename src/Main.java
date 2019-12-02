
import at.kraschitzer.roco.ComController;
import at.kraschitzer.roco.util.IpAddressValidator;
import at.stejskal.data.CamConnector;
import at.stejskal.data.CamLoco;
import at.stejskal.global.FileOperation;
import java.io.BufferedReader;
import java.io.IOException;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main{

    public static void main(String[] args) throws SocketException, UnknownHostException, IOException {
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
        
        
        
        
//        com.startAllLocos();
    }
    
    
    
}
