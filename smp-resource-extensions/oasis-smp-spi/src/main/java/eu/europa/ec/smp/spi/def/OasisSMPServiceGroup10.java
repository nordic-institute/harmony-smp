package eu.europa.ec.smp.spi.def;

import eu.europa.ec.smp.spi.handler.OasisSMPServiceGroup10Handler;
import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import eu.europa.ec.smp.spi.resource.SubresourceDefinitionSpi;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


/**
 * The SubresourceDefinitionSpi implementation for the Oasis SMP 1.0 ServiceGroup document.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component
public class OasisSMPServiceGroup10 implements ResourceDefinitionSpi {


    OasisSMPServiceGroup10Handler serviceGroup10Handler;
    OasisSMPServiceMetadata10 oasisSMPServiceMetadata10;

    public OasisSMPServiceGroup10(OasisSMPServiceGroup10Handler serviceGroup10Handler,  OasisSMPServiceMetadata10 oasisSMPServiceMetadata10) {
        this.serviceGroup10Handler = serviceGroup10Handler;
        this.oasisSMPServiceMetadata10 = oasisSMPServiceMetadata10;
    }

    @Override
    public String identifier() {
        return "edelivery-oasis-smp-1.0-servicegroup";
    }

    @Override
    public String defaultUrlSegment() {
        return "smp-1";
    }

    @Override
    public String name() {
        return "Oasis SMP 1.0 ServiceGroup";
    }

    @Override
    public String description() {
        return "Oasis SMP 1.0 Service group resource handler";
    }

    @Override
    public String mimeType() {
        return "text/xml";
    }

    @Override
    public List<SubresourceDefinitionSpi> getSuresourceSpiList() {
        return Collections.singletonList(oasisSMPServiceMetadata10);
    }

    @Override
    public ResourceHandlerSpi getResourceHandler() {
        return serviceGroup10Handler;
    }

    @Override
    public String toString() {
        return "OasisSMPServiceGroup10{" +
                "identifier=" + identifier() +
                "defaultUrlSegment=" + defaultUrlSegment() +
                "name=" + name() +
                "mimeType=" + mimeType() +
                '}';
    }
}
