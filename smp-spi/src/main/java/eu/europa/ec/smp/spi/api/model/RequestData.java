package eu.europa.ec.smp.spi.api.model;

import java.io.InputStream;

/**
 *  An object implementing the interface provides a resource data for processing by the SPI resource handler.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public interface RequestData {

    String getDomainCode();

    ResourceIdentifier getResourceIdentifier();

    ResourceIdentifier getSubresourceIdentifier();

    InputStream getResourceInputStream();

}
