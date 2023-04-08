package eu.europa.ec.smp.spi.def;

import eu.europa.ec.smp.spi.handler.OasisSMPServiceGroup20Handler;
import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import eu.europa.ec.smp.spi.resource.SubresourceDefinitionSpi;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


/**
 * The SubresourceDefinitionSpi implementation for the Oasis SMP 2.0 ServiceGroup document.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component
public class OasisSMPServiceGroup20 implements ResourceDefinitionSpi {


    OasisSMPServiceGroup20Handler serviceGroup20Handler;
    OasisSMPServiceMetadata20 oasisSMPServiceMetadata20;

    public OasisSMPServiceGroup20(OasisSMPServiceGroup20Handler serviceGroup20Handler, OasisSMPServiceMetadata20 oasisSMPServiceMetadata20) {
        this.serviceGroup20Handler = serviceGroup20Handler;
        this.oasisSMPServiceMetadata20 = oasisSMPServiceMetadata20;
    }

    @Override
    public String identifier() {
        return "edelivery-oasis-smp-2.0-servicegroup";
    }

    @Override
    public String defaultUrlSegment() {
        return "oasis-bdxr-smp-2";
    }

    @Override
    public String name() {
        return "Oasis SMP 2.0 ServiceGroup";
    }

    @Override
    public String description() {
        return "Oasis SMP 2.0 Service group resource handler";
    }

    @Override
    public String mimeType() {
        return "text/xml";
    }

    @Override
    public List<SubresourceDefinitionSpi> getSuresourceSpiList() {
        return Collections.singletonList(oasisSMPServiceMetadata20);
    }

    @Override
    public ResourceHandlerSpi getResourceHandler() {
        return serviceGroup20Handler;
    }

    @Override
    public String toString() {
        return "OasisSMPServiceGroup20{" +
                "identifier=" + identifier() +
                "defaultUrlSegment=" + defaultUrlSegment() +
                "name=" + name() +
                "mimeType=" + mimeType() +
                '}';
    }
}
