package at.kraschitzer.roco.util;

import java.util.regex.Pattern;

public class IpAddressValidator {

    private static final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";

    private static final String IP_REGEXP = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;
    private static final String IP_REGEXP_LOCAL = zeroTo255 + "$";

    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);
    private static final Pattern IP_LOCAL_PATTERN = Pattern.compile(IP_REGEXP_LOCAL);

    // Return true when *address* is IP Address
    public static boolean isValid(String address) {
        return IP_PATTERN.matcher(address).matches();
    }

    public static String replaceLocalAddress(String address, String localAddress) {
        if (!isValid(address)) {
            throw new NumberFormatException();
        }
        return IP_LOCAL_PATTERN.matcher(address).replaceFirst(localAddress);
    }
}
