package at.kraschitzer.roco;

import at.kraschitzer.roco.util.HexCaster;

import java.util.Arrays;

public class ImageParser {

    private static final byte[] IMAGE_START = HexCaster.unstringify("ffd8");
    private static final byte[] IMAGE_END = HexCaster.unstringify("ffd9");

    private static final int IMAGE_BUFFER_LENGTH = 400240;

    private byte[] imageBuffer = new byte[IMAGE_BUFFER_LENGTH];

    private int imageBufferCnt = 0;
    private boolean withinImage = false;
    private boolean imageStartPartly = false;
    private boolean imageEndPartly = false;

    public byte[] addData(byte[] data) {
        byte[] im = null;
        for (int offset = 0; offset < data.length; offset++) {
            if (withinImage) {
                if (checkForImageEnd(data, offset)) {
                    imageBuffer[imageBufferCnt++] = IMAGE_END[0];
                    imageBuffer[imageBufferCnt++] = IMAGE_END[1];
                    im = Arrays.copyOf(imageBuffer, imageBufferCnt);
                    withinImage = false;
                    continue;
                }
                imageBuffer[imageBufferCnt++] = data[offset];
            } else if (checkForImageStart(data, offset)) {
                imageBuffer = new byte[IMAGE_BUFFER_LENGTH];
                imageBuffer[0] = IMAGE_START[0];
                imageBuffer[1] = IMAGE_START[1];
                imageBufferCnt = 2;
                offset++;
                withinImage = true;
            }
        }
        return im;
    }

    private boolean checkForImageStart(byte[] receiveBuffer, int offset) {
        if (receiveBuffer[offset] == IMAGE_START[0] && offset < receiveBuffer.length - 1 && receiveBuffer[offset + 1] == IMAGE_START[1]) {
            return true;
        }
        if (imageStartPartly && receiveBuffer[0] == IMAGE_START[1]) {
            return true;
        }
        if (receiveBuffer[offset] == IMAGE_START[0] && offset == receiveBuffer.length - 1) {
            imageStartPartly = true;
        }
        return false;
    }

    private boolean checkForImageEnd(byte[] receiveBuffer, int offset) {
        if (receiveBuffer[offset] == IMAGE_END[0] && offset < receiveBuffer.length - 1 && receiveBuffer[offset + 1] == IMAGE_END[1]) {
            return true;
        }
        if (imageEndPartly && receiveBuffer[0] == IMAGE_END[1]) {
            return true;
        }
        if (receiveBuffer[offset] == IMAGE_END[0] && offset == receiveBuffer.length - 1) {
            imageEndPartly = true;
        }
        return false;
    }
}
