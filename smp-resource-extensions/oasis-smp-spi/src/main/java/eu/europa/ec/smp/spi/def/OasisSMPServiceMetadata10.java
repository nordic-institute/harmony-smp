package eu.europa.ec.smp.spi.def;

import eu.europa.ec.smp.spi.handler.OasisSMPServiceMetadata10Handler;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import eu.europa.ec.smp.spi.resource.SubresourceDefinitionSpi;
import org.springframework.stereotype.Component;


/**
 * The SubresourceDefinitionSpi implementation for the Oasis SMP 1.0 ServiceMetadata document.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component
public class OasisSMPServiceMetadata10 implements SubresourceDefinitionSpi {

    public static final String RESOURCE_IDENTIFIER = "edelivery-oasis-smp-1.0-servicemetadata";

    OasisSMPServiceMetadata10Handler serviceMetadata10Handler;

    public OasisSMPServiceMetadata10(OasisSMPServiceMetadata10Handler serviceMetadata10Handler) {
        this.serviceMetadata10Handler = serviceMetadata10Handler;
    }

    @Override
    public String identifier() {
        return RESOURCE_IDENTIFIER;
    }

    @Override
    public String urlSegment() {
        return "services";
    }

    @Override
    public String name() {
        return "Oasis SMP 1.0 ServiceMetadata";
    }

    @Override
    public String description() {
        return "Oasis SMP 1.0 Service Metadata resource handler";
    }

    @Override
    public String mimeType() {
        return "text/xml";
    }

    @Override
    public ResourceHandlerSpi getResourceHandler() {
        return serviceMetadata10Handler;
    }

    @Override
    public String toString() {
        return "OasisSMPServiceMetadata10{" +
                "identifier=" + identifier() +
                "urlSegment=" + urlSegment() +
                "name=" + name() +
                "mimeType=" + mimeType() +
                '}';
    }
}
