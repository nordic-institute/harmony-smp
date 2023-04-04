package eu.europa.ec.edelivery.smp.services.spi.data;

import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *  The resource metadata.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class SpiResponseData implements ResponseData {

    OutputStream outputStream;
    Map<String, String> httpHeaders = new HashMap();
    String contentType;
    Integer responseCode;

    public SpiResponseData(OutputStream outputStream) {
        this.outputStream= outputStream;
    }

    public OutputStream getOutputStream() {
        if (outputStream==null) {
            outputStream = new ByteArrayOutputStream();
        }
        return outputStream;
    }

    public void addHttpHeader(String name, String value) {
        httpHeaders.put(name, value);
    }


    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }
}
