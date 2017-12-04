package eu.europa.ec.cipa.busdox.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 *
 *@author Flavio Santos
 * */
public class CommonUtil {

    private static final String TIMEZONE_REGEX = "^(\\d{4}-\\d{2}-\\d{1,2}T\\d{1,2}:\\d{2}:\\d{2})(.*)$";


    public static String getTimezoneFromDate(String date) {
        String newDate = null;

        Pattern pattern = Pattern.compile(TIMEZONE_REGEX);
        Matcher matcher = pattern.matcher(date);

        if (matcher.find()) {
            newDate = matcher.group(1);
            try {
                newDate = newDate + (matcher.group(2).trim().isEmpty() ? "Z" : matcher.group(2));
            } catch (Exception exc) {
                newDate = newDate + "Z";
            }
        }
        return newDate == null ? date : newDate;
    }
}
