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
package eu.europa.ec.smp.spi.def;

import eu.europa.ec.smp.spi.handler.OasisSMPServiceMetadata10Handler;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import eu.europa.ec.smp.spi.resource.SubresourceDefinitionSpi;
import org.springframework.stereotype.Component;


/**
 * The SubresourceDefinitionSpi implementation for the Oasis SMP 1.0 ServiceMetadata document.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component
public class OasisSMPServiceMetadata10 implements SubresourceDefinitionSpi {

    public static final String RESOURCE_IDENTIFIER = "edelivery-oasis-smp-1.0-servicemetadata";

    OasisSMPServiceMetadata10Handler serviceMetadata10Handler;

    public OasisSMPServiceMetadata10(OasisSMPServiceMetadata10Handler serviceMetadata10Handler) {
        this.serviceMetadata10Handler = serviceMetadata10Handler;
    }

    @Override
    public String identifier() {
        return RESOURCE_IDENTIFIER;
    }

    @Override
    public String urlSegment() {
        return "services";
    }

    @Override
    public String name() {
        return "Oasis SMP 1.0 ServiceMetadata";
    }

    @Override
    public String description() {
        return "Oasis SMP 1.0 Service Metadata resource handler";
    }

    @Override
    public String mimeType() {
        return "text/xml";
    }

    @Override
    public ResourceHandlerSpi getResourceHandler() {
        return serviceMetadata10Handler;
    }

    @Override
    public String toString() {
        return "OasisSMPServiceMetadata10{" +
                "identifier=" + identifier() +
                "urlSegment=" + urlSegment() +
                "name=" + name() +
                "mimeType=" + mimeType() +
                '}';
    }
}
