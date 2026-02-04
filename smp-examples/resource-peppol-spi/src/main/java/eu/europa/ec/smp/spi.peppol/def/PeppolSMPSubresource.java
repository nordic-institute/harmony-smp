/*-
 * #START_LICENSE#
 * oasis-smp-spi
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
package eu.europa.ec.smp.spi.peppol.def;

import eu.europa.ec.smp.spi.peppol.handler.PeppolSMPSubresourceHandler;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import eu.europa.ec.smp.spi.resource.SubresourceDefinitionSpi;
import org.springframework.stereotype.Component;


/**
 * The SubresourceDefinitionSpi implementation for the PEPPOL SMP ServiceMetadata document.
 *
 * @author Joze Rihtarsic
 * @since 5.2
 */
@Component
public class PeppolSMPSubresource implements SubresourceDefinitionSpi {

    public static final String RESOURCE_IDENTIFIER = "peppol-smp-servicemetadata";

    PeppolSMPSubresourceHandler subresourceHandler;

    public PeppolSMPSubresource(PeppolSMPSubresourceHandler subresourceHandler) {
        this.subresourceHandler = subresourceHandler;
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
        return "Peppol SMP ServiceMetadata";
    }

    @Override
    public String description() {
        return "Peppol SMP Service Metadata resource handler";
    }

    @Override
    public String mimeType() {
        return "text/xml";
    }

    @Override
    public ResourceHandlerSpi getResourceHandler() {
        return subresourceHandler;
    }

    @Override
    public String toString() {
        return "PeppolSMPSubresource{" +
                "identifier=" + identifier() +
                ",urlSegment=" + urlSegment() +
                ",name=" + name() +
                ",mimeType=" + mimeType() +
                '}';
    }
}
