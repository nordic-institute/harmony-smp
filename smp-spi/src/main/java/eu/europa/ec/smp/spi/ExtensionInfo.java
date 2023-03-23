package eu.europa.ec.smp.spi;


import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;

import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 *
 * DomiSMP extension information. When updating the extension it must have the same Name  for DomiSMP to handle the
 * upgrade correctly.
 */
public interface ExtensionInfo {

    String identifier();
    String name();
    String description();
    String version();

    List<ResourceDefinitionSpi> resourceTypes();

    List<PayloadValidatorSpi> payloadValidators();

}
