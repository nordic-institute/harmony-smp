package eu.europa.ec.smp.spi;

import eu.europa.ec.smp.spi.def.OasisSMPServiceGroup10;
import eu.europa.ec.smp.spi.def.OasisSMPServiceGroup20;
import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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

    final OasisSMPServiceGroup10 oasisSMPServiceGroup10;

    final OasisSMPServiceGroup20 oasisSMPServiceGroup20;

    public OasisSMPExtension(OasisSMPServiceGroup10 oasisSMPServiceGroup10, OasisSMPServiceGroup20 oasisSMPServiceGroup20) {
        this.oasisSMPServiceGroup10 = oasisSMPServiceGroup10;
        this.oasisSMPServiceGroup20 = oasisSMPServiceGroup20;
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
        return Arrays.asList(oasisSMPServiceGroup10, oasisSMPServiceGroup20);
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
