package eu.europa.ec.cipa.smp.server.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by migueti on 13/01/2017.
 */
@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException>{
    @Override
    public Response toResponse(BadRequestException e) {
        return ErrorResponseBuilder.status(Status.BAD_REQUEST)
                .businessCode(e.getBusinessCode())
                .errorDescription(e.getMessage())
                .build();
    }
}
