package eu.europa.ec.smp.spi.examples.def;

import eu.europa.ec.smp.spi.examples.handler.DomiSMPPropertyHandlerExample;
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
public class DomiSMPPropertyResourceExample implements ResourceDefinitionSpi {


    DomiSMPPropertyHandlerExample documentHandler;

    public DomiSMPPropertyResourceExample(DomiSMPPropertyHandlerExample documentHandler) {
        this.documentHandler = documentHandler;
    }

    @Override
    public String identifier() {
        return "domismp-resource-example-properties";
    }

    @Override
    public String defaultUrlSegment() {
        return "prop";
    }

    @Override
    public String name() {
        return "DomiSMP property example";
    }

    @Override
    public String description() {
        return "DomiSMP property example";
    }

    @Override
    public String mimeType() {
        return  "text/x-properties";
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
        return "DomiSMPPropertyResourceExample {" +
                "identifier=" + identifier() +
                "defaultUrlSegment=" + defaultUrlSegment() +
                "name=" + name() +
                "mimeType=" + mimeType() +
                '}';
    }
}
