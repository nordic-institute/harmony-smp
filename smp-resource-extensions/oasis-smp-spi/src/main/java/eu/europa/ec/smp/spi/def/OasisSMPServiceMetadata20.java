package eu.europa.ec.smp.spi.def;

import eu.europa.ec.smp.spi.handler.OasisSMPServiceMetadata20Handler;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import eu.europa.ec.smp.spi.resource.SubresourceDefinitionSpi;
import org.springframework.stereotype.Component;


/**
 * The SubresourceDefinitionSpi implementation for the Oasis SMP 2.0 ServiceMetadata document.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component
public class OasisSMPServiceMetadata20 implements SubresourceDefinitionSpi {

    public static final String RESOURCE_IDENTIFIER = "edelivery-oasis-smp-2.0-servicemetadata";

    OasisSMPServiceMetadata20Handler serviceMetadata20Handler;

    public OasisSMPServiceMetadata20(OasisSMPServiceMetadata20Handler serviceMetadata20Handler) {
        this.serviceMetadata20Handler = serviceMetadata20Handler;
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
        return "Oasis SMP 2.0 ServiceGroup";
    }

    @Override
    public String description() {
        return "Oasis SMP 2.0 Service Metadata resource handler";
    }

    @Override
    public String mimeType() {
        return "text/xml";
    }

    @Override
    public ResourceHandlerSpi getResourceHandler() {
        return serviceMetadata20Handler;
    }

    @Override
    public String toString() {
        return "OasisSMPServiceMetadata20{" +
                "identifier=" + identifier() +
                "urlSegment=" + urlSegment() +
                "name=" + name() +
                "mimeType=" + mimeType() +
                '}';
    }
}
