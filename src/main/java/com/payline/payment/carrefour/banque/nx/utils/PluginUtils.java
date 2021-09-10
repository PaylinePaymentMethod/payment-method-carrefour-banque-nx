package com.payline.payment.carrefour.banque.nx.utils;

public class PluginUtils {

    /* Static utility class : no need to instantiate it (to please Sonar) */
    private PluginUtils() {
    }

    /**
     * Truncate the given string with the given length, if necessary.
     *
     * @param value  The string to truncate
     * @param length The maximum allowed length
     * @return The truncated string
     */
    public static String truncate(String value, int length) {
        if (value != null && value.length() > length) {
            value = value.substring(0, length);
        }
        return value;
    }

    /**
     * check if a String is null or empty
     *
     * @param s The string to check
     * @return true if the String is empty
     */
    public static boolean isEmpty(String s) {
        return (s == null || s.length() == 0);
    }
}