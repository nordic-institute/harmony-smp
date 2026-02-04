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

import eu.europa.ec.smp.spi.peppol.handler.PeppolSMPResourceHandler;
import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import eu.europa.ec.smp.spi.resource.SubresourceDefinitionSpi;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


/**
 * The SubresourceDefinitionSpi implementation for the Peppol SMP ServiceGroup document.
 *
 * @author Joze Rihtarsic
 * @since 5.2
 */
@Component
public class PeppolSMPResource implements ResourceDefinitionSpi {
    public static final String RESOURCE_BUSINESS_CARD = "businesscard";

    PeppolSMPResourceHandler resourceHandler;
    PeppolSMPSubresource peppolSMPSubresource;

    public PeppolSMPResource(PeppolSMPResourceHandler resource10Handler, PeppolSMPSubresource peppolSMPSubresource) {
        this.resourceHandler = resource10Handler;
        this.peppolSMPSubresource = peppolSMPSubresource;
    }

    @Override
    public String identifier() {
        return "peppol-smp-servicegroup";
    }

    @Override
    public String defaultUrlSegment() {
        return "peppol";
    }

    @Override
    public List<String> optionalUrlSegments(){
        return List.of(RESOURCE_BUSINESS_CARD);
    }

    @Override
    public String name() {
        return "Peppol SMP ServiceGroup";
    }

    @Override
    public String description() {
        return "Peppol SMP Service group resource handler";
    }

    @Override
    public String mimeType() {
        return "text/xml";
    }

    @Override
    public List<SubresourceDefinitionSpi> getSubresourceSpiList() {
        return Collections.singletonList(peppolSMPSubresource);
    }

    @Override
    public ResourceHandlerSpi getResourceHandler() {
        return resourceHandler;
    }

    @Override
    public String toString() {
        return "PeppolSMPResource{" +
                "identifier=" + identifier() +
                ",defaultUrlSegment=" + defaultUrlSegment() +
                ",name=" + name() +
                ",mimeType=" + mimeType() +
                '}';
    }
}
