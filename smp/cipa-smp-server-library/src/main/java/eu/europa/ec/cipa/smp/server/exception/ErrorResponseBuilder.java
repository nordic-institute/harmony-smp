package eu.europa.ec.cipa.smp.server.exception;

import com.helger.commons.mime.CMimeType;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by migueti on 05/01/2017.
 */
public class ErrorResponseBuilder {
    private StringBuilder response = new StringBuilder();
    private String strBusinessCode = "TECHNICAL";
    private String strErrorDescription = "Unexpected technical error occurred.";

    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");

    private ErrorResponseBuilder() {
        response = new StringBuilder();
    }

    private static String getErrorUniqueId() {
        StringBuilder errId = new StringBuilder();
        errId.append(TIMESTAMP_FORMAT.format(new Date()))
                .append(":")
                .append(UUID.randomUUID());
        return String.valueOf(errId);
    }

    public static ErrorResponseBuilder newInstance() {
        return new ErrorResponseBuilder();
    }


    public String build() {
        response.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        response.append("<ErrorResponse xmlns=\"ec:services:SMP:1.0\">");
        response.append("<BusinessCode>").append(strBusinessCode).append("</BusinessCode>");
        response.append("<ErrorDescription>").append(strErrorDescription).append("</ErrorDescription>");
        response.append("<ErrorUniqueId>").append(getErrorUniqueId()).append("</ErrorUniqueId>");
        response.append("</ErrorResponse>");
        return String.valueOf(response);
    }

    public ErrorResponseBuilder setBusinessCode(String newBusinessCode) {
        strBusinessCode = newBusinessCode;
        return this;
    }

    public ErrorResponseBuilder setErrorDescription(String newErrorDescription) {
        strErrorDescription = newErrorDescription;
        return this;
    }

    public Response build(Status status) {
        return Response.status(status)
                .entity(build())
                .type(CMimeType.TEXT_XML.getAsString ())
                .build();
    }
}
