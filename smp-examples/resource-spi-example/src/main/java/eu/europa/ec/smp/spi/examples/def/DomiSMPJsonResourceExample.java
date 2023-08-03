package eu.europa.ec.smp.spi.examples.def;

import eu.europa.ec.smp.spi.examples.handler.DomiSMPJSONHandlerExample;
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
public class DomiSMPJsonResourceExample implements ResourceDefinitionSpi {


    DomiSMPJSONHandlerExample documentHandler;

    public DomiSMPJsonResourceExample(DomiSMPJSONHandlerExample documentHandler) {
        this.documentHandler = documentHandler;
    }

    @Override
    public String identifier() {
        return "domismp-resource-example-json";
    }

    @Override
    public String defaultUrlSegment() {
        return "json";
    }

    @Override
    public String name() {
        return "DomiSMP JSON example";
    }

    @Override
    public String description() {
        return "DomiSMP JSON example";
    }

    @Override
    public String mimeType() {
        return "application/json";
    }

    @Override
    public List<SubresourceDefinitionSpi> getSuresourceSpiList() {
        return Collections.emptyList();
    }

    @Override
    public ResourceHandlerSpi getResourceHandler() {
        return documentHandler;
    }

    @Override
    public String toString() {
        return "DomiSMPJsonResourceExample {" +
                "identifier=" + identifier() +
                "defaultUrlSegment=" + defaultUrlSegment() +
                "name=" + name() +
                "mimeType=" + mimeType() +
                '}';
    }
}
