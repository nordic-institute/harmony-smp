package eu.europa.ec.smp.spi.examples;

import eu.europa.ec.smp.spi.ExtensionInfo;
import eu.europa.ec.smp.spi.PayloadValidatorSpi;
import eu.europa.ec.smp.spi.examples.def.DomiSMPJsonResourceExample;
import eu.europa.ec.smp.spi.examples.def.DomiSMPPropertyResourceExample;
import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 * <p>
 * Extension implementation for handling the Oasis CPPA-cpp resources.
 */
@Service
public class DomiSMPResourceExampleExtension implements ExtensionInfo {

    final DomiSMPPropertyResourceExample domiSMPPropertyResourceExample;
    final DomiSMPJsonResourceExample jsonResourceExample;

    public DomiSMPResourceExampleExtension(DomiSMPPropertyResourceExample domiSMPPropertyResourceExample, DomiSMPJsonResourceExample jsonResourceExample) {
        this.domiSMPPropertyResourceExample = domiSMPPropertyResourceExample;
        this.jsonResourceExample = jsonResourceExample;
    }

    @Override
    public String identifier() {
        return "domismp-resource-example-extension";
    }

    @Override
    public String name() {
        return "Resource example extension";
    }

    @Override
    public String description() {
        return "The extension implements json and property examples";
    }

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public List<ResourceDefinitionSpi> resourceTypes() {
        return Arrays.asList(jsonResourceExample, domiSMPPropertyResourceExample);
    }

    @Override
    public List<PayloadValidatorSpi> payloadValidators() {
        return Collections.emptyList();
    }


    @Override
    public String toString() {
        return "DomiSMPResourceExampleExtension{" +
                "identifier=" + identifier() +
                "name=" + name() +
                "version=" + version() +
                '}';
    }
}
