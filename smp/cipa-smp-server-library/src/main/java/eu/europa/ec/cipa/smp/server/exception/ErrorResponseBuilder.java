package eu.europa.ec.cipa.smp.server.exception;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by migueti on 05/01/2017.
 */
public class ErrorResponseBuilder {
    private static String getTimeStamp() {
        Date date = new Date();
        StringBuilder timeStamp = new StringBuilder();
        timeStamp.append(new SimpleDateFormat("yyyy-MM-dd").format(date))
                .append("T")
                .append(new SimpleDateFormat("HH:mm:ss.SSSZ").format(date))
                .append(UUID.randomUUID());
        return String.valueOf(timeStamp);
    }

    public static String build() {
        StringBuilder result = new StringBuilder();
        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        result.append("<ErrorResponse xmlns=\"ec:services:SMP:1.0\">");
        result.append("<BusinessCode>TECHNICAL</BusinessCode>");
        result.append("<ErrorDescription>Unexpected technical error occurred.</ErrorDescription>");
        result.append("<ErrorUniqueId>").append(getTimeStamp()).append("</ErrorUniqueId>");
        result.append("</ErrorResponse>");
        return String.valueOf(result);
    }
}
