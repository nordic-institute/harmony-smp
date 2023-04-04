package eu.europa.ec.edelivery.smp.services.spi;

import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;

public class SPIUtils {

    public static ResourceIdentifier toUrlIdentifier(DBSubresource subresource) {
        return new ResourceIdentifier(subresource.getIdentifierValue(), subresource.getIdentifierScheme());
    }

    public static ResourceIdentifier toUrlIdentifier(DBResource resource) {
        return new ResourceIdentifier(resource.getIdentifierValue(), resource.getIdentifierScheme());
    }

    public static Identifier toIdentifier(ResourceIdentifier identifier) {
        return new Identifier(identifier.getValue(), identifier.getScheme());
    }
}
