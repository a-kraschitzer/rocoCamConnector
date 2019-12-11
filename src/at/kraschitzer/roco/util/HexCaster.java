package at.kraschitzer.roco.util;

public class HexCaster {
    public static byte[] unstringify(String hexString) {
        byte[] result;
        int strLen = hexString.length();
        if (strLen % 2 != 0) {
            hexString = hexString.substring(0, strLen - 1) + "0"
                    + hexString.substring(strLen - 1, strLen);
            strLen++;
        }

        result = new byte[strLen / 2];
        int ctr = 0;
        int val;
        for (int n = 0; n < strLen; n += 2) {
            val = Integer.valueOf(hexString.substring(n, n + 2), 16);
            if (val > 127) {
                result[ctr] = (byte) (val - 256);
            } else {
                result[ctr] = (byte) val;
            }
            ctr++;
        }
        return result;
    }

    public static String stringify(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(String.format("%02x", aByte));
        }
        return result.toString();
    }

    public static String stringifyAndTrimZeros(byte[] data) {
        String s = stringify(data);
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '0') {
                continue;
            }
            s = s.substring(0, i + 1);
            break;
        }
        return s;
    }

    public static boolean compareByteArraysFromStart(byte[] a, byte[] b, int compareLength) {
        for (int i = 0; i < compareLength; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }
}
