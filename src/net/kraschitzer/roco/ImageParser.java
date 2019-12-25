package net.kraschitzer.roco;

import net.kraschitzer.roco.util.HexCaster;

import java.util.Arrays;

public class ImageParser {

    private static final byte[] IMAGE_START = HexCaster.unstringify("ffd8");
    private static final byte[] IMAGE_END = HexCaster.unstringify("ffd9");

    private static final int IMAGE_BUFFER_LENGTH = 400240;

    private byte[] imageBuffer = new byte[IMAGE_BUFFER_LENGTH];

    private int currentImageCount = 0;
    private int imageBufferCnt = 0;
    private boolean withinImage = false;
    private boolean imageStartPartly = false;
    private boolean imageEndPartly = false;
    private String ipAddress = "";
    private boolean debug = false;

    public ImageParser(boolean debug, String ipAddress) {
        this.debug = debug;
        this.ipAddress = ipAddress;
    }

    public byte[] addData(byte[] data, int newImageCount) {
        byte[] im = null;
        log("ImageCount: (old=" + currentImageCount + ", new=" + newImageCount + ")");
        for (int offset = 0; offset < data.length; offset++) {
            if (withinImage && (newImageCount == currentImageCount || (newImageCount - 1) == currentImageCount)) {
                if (checkForImageEnd(data, offset)) {
                    log("<- ImageEnd: offset=" + offset + ", imgBufferOffset=" + imageBufferCnt);
                    imageBuffer[imageBufferCnt++] = IMAGE_END[0];
                    imageBuffer[imageBufferCnt++] = IMAGE_END[1];
                    im = Arrays.copyOf(imageBuffer, imageBufferCnt);
                    withinImage = false;
                    continue;
                }
                imageBuffer[imageBufferCnt++] = data[offset];
            }
            if (!withinImage || didImageCountIncrease(currentImageCount, newImageCount)) {
                if (checkForPartlyImageStart(data, offset)) {
                    log("-> ImagePartlyStart: offset=" + offset);
                    initializeImage(newImageCount);
                } else if (checkForImageStart(data, offset)) {
                    log("<- ImageStart: offset=" + offset);
                    initializeImage(newImageCount);
                    offset++;
                }
            }
        }
        return im;
    }

    public void resetImageData() {
        withinImage = false;
        imageStartPartly = false;
        imageEndPartly = false;
    }

    private void initializeImage(int imageCount) {
        resetImageData();
        currentImageCount = imageCount;
        imageBuffer = new byte[IMAGE_BUFFER_LENGTH];
        imageBuffer[0] = IMAGE_START[0];
        imageBuffer[1] = IMAGE_START[1];
        imageBufferCnt = 2;
        withinImage = true;
    }

    private boolean checkForPartlyImageStart(byte[] receiveBuffer, int offset) {
        if (offset == 0 || offset == receiveBuffer.length - 1) {
            if (imageStartPartly && receiveBuffer[0] == IMAGE_START[1] && offset == 0) {
                imageStartPartly = false;
                return true;
            }
            if (receiveBuffer[offset] == IMAGE_START[0] && offset == receiveBuffer.length - 1) {
                imageStartPartly = true;
                return false;
            }
            imageStartPartly = false;
        }
        return false;
    }

    private boolean checkForImageStart(byte[] receiveBuffer, int offset) {
        if (receiveBuffer[offset] == IMAGE_START[0] && offset < receiveBuffer.length - 1 && receiveBuffer[offset + 1] == IMAGE_START[1]) {
            return true;
        }
        return false;
    }

    private boolean checkForImageEnd(byte[] receiveBuffer, int offset) {
        if (offset == 0 || offset == receiveBuffer.length - 1) {
            if (receiveBuffer[offset] == IMAGE_END[0] && offset < receiveBuffer.length - 1 && receiveBuffer[offset + 1] == IMAGE_END[1]) {
                return true;
            }
            if (imageEndPartly && receiveBuffer[0] == IMAGE_END[1] && offset == 0) {
                imageEndPartly = false;
                return true;
            }
            if (receiveBuffer[offset] == IMAGE_END[0] && offset == receiveBuffer.length - 1) {
                imageEndPartly = true;
            }
            imageEndPartly = false;
        }
        return false;
    }

    private boolean didImageCountIncrease(int oldImageCount, int newImageCount) {
        if (newImageCount > oldImageCount) {
            return true;
        }
        if ((oldImageCount - newImageCount) > 50) {
            return true;
        }
        return false;
    }

    private void log(String s) {
        if (debug) {
            System.out.println("[" + ipAddress + "] " + s);
        }
    }
}
