package net.kraschitzer.roco.consumers;

import net.kraschitzer.roco.data.CamConnector;

import java.io.File;
import java.io.FileOutputStream;

public class ImageFileWriter implements CamConnector {

    private final File parentDir;

    public ImageFileWriter(String dir) {
        this.parentDir = new File(dir);
    }

    @Override
    public void setImage(byte[] image) {
        try (FileOutputStream fos = new FileOutputStream(parentDir + "/" + System.currentTimeMillis() + "jpg")) {
            fos.write(image);
            fos.flush();
        } catch (Exception e) {
            System.out.println("Failed to write image to file");
            e.printStackTrace();
        }
    }
}
