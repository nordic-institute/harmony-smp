/*-
 * #START_LICENSE#
 * oasis-smp-spi
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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
 * Extension implementation for handling the Oasis SMP resources.
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
