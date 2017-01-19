package eu.europa.ec.cipa.smp.server.errors;

import com.helger.commons.mime.CMimeType;
import ec.services.smp._1.ErrorResponse;

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
    private ErrorBusinessCode errorBusinessCode = ErrorBusinessCode.TECHNICAL;
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

    private ErrorResponse buildBody() {
        ErrorResponse err = new ErrorResponse();
        err.setBusinessCode(errorBusinessCode.name());
        err.setErrorDescription(strErrorDescription);
        err.setErrorUniqueId(getErrorUniqueId());

        return err;
    }

    public ErrorResponseBuilder businessCode(ErrorBusinessCode newErrorBusinessCode) {
        this.errorBusinessCode = newErrorBusinessCode;
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
