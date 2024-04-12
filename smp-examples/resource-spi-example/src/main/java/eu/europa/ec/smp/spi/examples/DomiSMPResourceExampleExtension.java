/*-
 * #START_LICENSE#
 * resource-spi-example
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
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
