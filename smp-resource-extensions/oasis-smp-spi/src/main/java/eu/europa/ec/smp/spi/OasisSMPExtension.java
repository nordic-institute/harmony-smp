package eu.europa.ec.smp.spi;

import eu.europa.ec.smp.spi.def.OasisSMPServiceGroup10;
import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 * <p>
 * Extension implementation for halding the Oasis SMP resources.
 */
@Service
public class OasisSMPExtension implements ExtensionInfo {

    OasisSMPServiceGroup10 oasisSMPServiceGroup10;


    public OasisSMPExtension(OasisSMPServiceGroup10 oasisSMPServiceGroup10) {
        this.oasisSMPServiceGroup10 = oasisSMPServiceGroup10;
    }

    @Override
    public String identifier() {
        return "edelivery-oasis-smp-extension";
    }

    @Override
    public String name() {
        return "Oasis SMP 1.0 and 2.0";
    }

    @Override
    public String description() {
        return "The extension implements Oasis SMP 1.0 and Oasis 2.0 document handlers";
    }

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public List<ResourceDefinitionSpi> resourceTypes() {
        return Collections.singletonList(oasisSMPServiceGroup10);
    }

    @Override
    public List<PayloadValidatorSpi> payloadValidators() {
        return Collections.emptyList();
    }


    @Override
    public String toString() {
        return "OasisSMPExtension{" +
                "identifier=" + identifier() +
                "name=" + name() +
                "version=" + version() +
                '}';
    }
}
