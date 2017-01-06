package eu.europa.ec.cipa.smp.server.util;

import com.sun.jersey.api.NotFoundException;
import eu.europa.ec.cipa.smp.server.fault.NotFoundFault;
import eu.europa.ec.cipa.smp.server.hook.PostRegistrationFilter;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by migueti on 05/01/2017.
 */
public class ExceptionHandler {
    private static String getTimeStamp() {
        Date date = new Date();
        StringBuilder timeStamp = new StringBuilder();
        timeStamp.append(new SimpleDateFormat("yyyy-MM-dd").format(date)).append("T").append(new SimpleDateFormat("HH:mm:ss.SSSZ").format(date)).append("UUID");
        return String.valueOf(timeStamp);
    }

    public static void handleException(final Exception ex) throws Exception{
        if(ex instanceof NotFoundException) {
            throw new NotFoundFault("NotFound", getTimeStamp());
        }
    }

    public static Response buildResponse(final Exception ex) {
        return Response.serverError().build();
    }

    public static void buildXmlResponse(PostRegistrationFilter.HttpServletResponseWrapperWithStatus servletResponse) throws IOException {
        String businessCode;
        String errorDescription;
        String timestamp;
        switch(servletResponse.getStatus()) {
            default:
                businessCode = "TECHNICAL";
                errorDescription = "Unexpected technical error occurred.";
                timestamp = getTimeStamp();
                break;
        }
        servletResponse.setContentType("text/xml;charset=UTF-8");
        PrintWriter writer = servletResponse.getWriter();
        writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.append("<ErrorResponse xmlns=\"ec:services:SMP:1.0\">");
        writer.append("<BusinessCode>").append(businessCode).append("</BusinessCode>");
        writer.append("<ErrorDescription>").append(errorDescription).append("</ErrorDescription>");
        writer.append("<ErrorUniqueId>").append(timestamp).append("</ErrorUniqueId>");
        writer.append("</ErrorResponse>");
        writer.flush();
    }
}
