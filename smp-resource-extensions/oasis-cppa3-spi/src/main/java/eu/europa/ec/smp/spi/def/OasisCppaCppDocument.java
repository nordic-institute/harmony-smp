package eu.europa.ec.smp.spi.def;

import eu.europa.ec.smp.spi.handler.OasisCppa3CppHandler;
import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import eu.europa.ec.smp.spi.resource.SubresourceDefinitionSpi;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


/**
 * The Oasis CPPA cpp document
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component
public class OasisCppaCppDocument implements ResourceDefinitionSpi {


    OasisCppa3CppHandler serviceGroup10Handler;

    public OasisCppaCppDocument(OasisCppa3CppHandler serviceGroup10Handler) {
        this.serviceGroup10Handler = serviceGroup10Handler;
    }

    @Override
    public String identifier() {
        return "edelivery-oasis-cppa-3.0-cpp";
    }

    @Override
    public String defaultUrlSegment() {
        return "cpp";
    }

    @Override
    public String name() {
        return "Oasis CPPA3 CPP document";
    }

    @Override
    public String description() {
        return "Oasis CPPA-CPP document";
    }

    @Override
    public String mimeType() {
        return "text/xml";
    }

    @Override
    public List<SubresourceDefinitionSpi> getSuresourceSpiList() {
        return Collections.emptyList();
    }

    @Override
    public ResourceHandlerSpi getResourceHandler() {
        return serviceGroup10Handler;
    }

    @Override
    public String toString() {
        return "OasisCppaCppDocument {" +
                "identifier=" + identifier() +
                "defaultUrlSegment=" + defaultUrlSegment() +
                "name=" + name() +
                "mimeType=" + mimeType() +
                '}';
    }
}
