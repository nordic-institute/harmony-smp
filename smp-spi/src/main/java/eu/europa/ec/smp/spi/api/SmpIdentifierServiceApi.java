package eu.europa.ec.smp.spi.api;

import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;

/**
 * Implementation of the class provides the identifier services for the SPI implementation. The identifier formatting
 * is DomiSMP configuration specific!
 *
 *  @author Joze Rihtarsic
 *  @since 5.0
 */
public interface SmpIdentifierServiceApi {

    ResourceIdentifier normalizeResourceIdentifier(String value, String  schema);

    ResourceIdentifier normalizeSubresourceIdentifier(String value, String  schema);

    String formatResourceIdentifier(ResourceIdentifier identifier);

    String formatSubresourceIdentifier(ResourceIdentifier identifier);

    String getURLEncodedResourceIdentifier(ResourceIdentifier identifier);

    String getURLEncodedSubresourceIdentifier(ResourceIdentifier identifier);

    boolean concatenateResourceIdentifier(ResourceIdentifier identifier);
}


