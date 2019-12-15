package net.kraschitzer.roco.util;

import java.util.regex.Pattern;

public class IpAddressValidator {

    private static final String PART_REGEX = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9]{1,2})";
    private static final String IP_REGEX = PART_REGEX + "\\." + PART_REGEX + "\\." + PART_REGEX + "\\." + PART_REGEX;
    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEX);

    /**
     * Return true if address is a valid ip, false otherwise
     */
    public static boolean isValid(String address) {
        return IP_PATTERN.matcher(address).matches();
    }

}
