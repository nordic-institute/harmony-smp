package eu.europa.ec.smp.spi;

import eu.europa.ec.smp.spi.def.OasisCppaCppDocument;
import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 * <p>
 * Extension implementation for handling the Oasis CPPA-cpp resources.
 */
@Service
public class OasisCPPA3Extension implements ExtensionInfo {

    final OasisCppaCppDocument oasisCppaCppDocument;

    public OasisCPPA3Extension(OasisCppaCppDocument oasisCppaCppDocument) {
        this.oasisCppaCppDocument = oasisCppaCppDocument;
    }

    @Override
    public String identifier() {
        return "edelivery-oasis-cppa3-extension";
    }

    @Override
    public String name() {
        return "Oasis CPPA 3.0";
    }

    @Override
    public String description() {
        return "The extension implements Oasis CPPA-CPP document handlers";
    }

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public List<ResourceDefinitionSpi> resourceTypes() {
        return Collections.singletonList(oasisCppaCppDocument);
    }

    @Override
    public List<PayloadValidatorSpi> payloadValidators() {
        return Collections.emptyList();
    }


    @Override
    public String toString() {
        return "OasisCPPA3Extension{" +
                "identifier=" + identifier() +
                "name=" + name() +
                "version=" + version() +
                '}';
    }
}
