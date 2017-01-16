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
    private Status status = Status.INTERNAL_SERVER_ERROR;
    private ErrorResponse.BusinessCode businessCode = ErrorResponse.BusinessCode.TECHNICAL;
    private String strErrorDescription = "Unexpected technical error occurred.";

    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");

    private static String getErrorUniqueId() {
        StringBuilder errId = new StringBuilder();
        errId.append(TIMESTAMP_FORMAT.format(new Date()))
                .append(":")
                .append(UUID.randomUUID());
        return String.valueOf(errId);
    }

    private ErrorResponseBuilder(Status status) {
        this.status = status;
    }

    public static ErrorResponseBuilder status(Status status) {
        return new ErrorResponseBuilder(status);
    }

    private String buildBody() {
        StringBuilder response = new StringBuilder();
        response.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        response.append("<ErrorResponse xmlns=\"ec:services:SMP:1.0\">");
        response.append("<BusinessCode>").append(businessCode.toString()).append("</BusinessCode>");
        response.append("<ErrorDescription>").append(strErrorDescription).append("</ErrorDescription>");
        response.append("<ErrorUniqueId>").append(getErrorUniqueId()).append("</ErrorUniqueId>");
        response.append("</ErrorResponse>");
        return String.valueOf(response);
    }

    public ErrorResponseBuilder businessCode(ErrorResponse.BusinessCode newBusinessCode) {
        this.businessCode = newBusinessCode;
        return this;
    }

    public ErrorResponseBuilder errorDescription(String newErrorDescription) {
        this.strErrorDescription = newErrorDescription;
        return this;
    }

    public Response build() {
        return Response.status(this.status)
                .entity(this.buildBody())
                .type(CMimeType.TEXT_XML.getAsString ())
                .build();
    }
}
