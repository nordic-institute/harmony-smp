package eu.europa.ec.smp.spi.api.model;

import java.io.OutputStream;
import java.util.Map;

/**
 * The resource metadata.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public interface ResponseData {
    OutputStream getOutputStream();

    void addHttpHeader(String name, String value);

    Map<String, String> getHttpHeaders();

    String getContentType();

    void setContentType(String contentType);

}
