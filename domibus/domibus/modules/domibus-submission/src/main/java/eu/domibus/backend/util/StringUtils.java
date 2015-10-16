/*
 * 
 */
package eu.domibus.backend.util;

import java.util.Scanner;

/**
 * The Class StringUtils.
 */
public class StringUtils {

    /**
     * Checks if is empty.
     *
     * @param str the str
     * @return true, if is empty
     */
    public static boolean isEmpty(final String str) {
        return ((str == null) || str.isEmpty());
    }

    /**
     * Checks if is not empty.
     *
     * @param str the str
     * @return true, if is not empty
     */
    public static boolean isNotEmpty(final String str) {
        return ((str != null) && !str.isEmpty());
    }

    /**
     * Equals.
     *
     * @param str1 the str1
     * @param str2 the str2
     * @return true, if successful
     */
    public static boolean equals(final String str1, final String str2) {
        return ((str1 == null) ? (str2 == null) : str1.equals(str2));
    }

    /**
     * Equals ignore case.
     *
     * @param str1 the str1
     * @param str2 the str2
     * @return true, if successful
     */
    public static boolean equalsIgnoreCase(final String str1, final String str2) {
        return ((str1 == null) ? (str2 == null) : str1.equalsIgnoreCase(str2));
    }

    /**
     * Checks if is numeric.
     *
     * @param str the str
     * @return true, if is numeric
     */
    public static boolean isNumeric(final String str) {
        if (str == null) {
            return false;
        }
        final int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    final static int LESSER = -1; //versionA is lesser than versionB
    final static int EQUALS = 0; //versionA equal to versionB
    final static int GREATER = 1; //versionA is greater then versionB

    public static int compareVersions(final String versionA, final String versionB) {
        final Scanner a = (new Scanner(versionA)).useDelimiter("\\.");
        final Scanner b = (new Scanner(versionB)).useDelimiter("\\.");
        int i, j;
        while (a.hasNext() && b.hasNext()) {
            i = Integer.parseInt(a.next());
            j = Integer.parseInt(b.next());
            if (i > j) {
                return StringUtils.GREATER;
            } else if (i < j) {
                return StringUtils.LESSER;
            }
        }
        if (a.hasNext() && !b.hasNext()) {
            return StringUtils.GREATER;
        } else if (!a.hasNext() && b.hasNext()) {
            return StringUtils.LESSER;
        } else {
            return StringUtils.EQUALS;
        }
    }
}