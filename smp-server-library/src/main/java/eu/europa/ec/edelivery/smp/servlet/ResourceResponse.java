package eu.europa.ec.edelivery.smp.servlet;

import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INVALID_REQUEST;

public class ResourceResponse {

    HttpServletResponse response;

    public ResourceResponse(HttpServletResponse response) {
        this.response = response;
    }

    public int getHttpStatus() {
        return response.getStatus();
    }

    public void setHttpStatus(int httpStatus) {
        response.setStatus(httpStatus);
    }

    public String getMimeType() {
        return response.getContentType();
    }

    public void setContentType(String mimeType) {
        response.setContentType(mimeType);
    }

    public String getHttpHeader(String name) {
        return response.getHeader(name);
    }

    public void setHttpHeader(String name, String value) {
        response.setHeader(name, value);
    }

    public OutputStream getOutputStream() {
        try {
            return response.getOutputStream();
        } catch (IOException e) {
            throw new SMPRuntimeException(INVALID_REQUEST, "Can not open output stream for response!", e);
        }
    }
}
