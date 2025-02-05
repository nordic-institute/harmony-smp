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
package eu.europa.ec.smp.spi.examples.def;

import eu.europa.ec.smp.spi.examples.handler.DomiSMPPropertyHandlerExample;
import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import eu.europa.ec.smp.spi.resource.SubresourceDefinitionSpi;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


/**
 * The Oasis CPPA cpp document
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component
public class DomiSMPPropertyResourceExample implements ResourceDefinitionSpi {


    DomiSMPPropertyHandlerExample documentHandler;

    public DomiSMPPropertyResourceExample(DomiSMPPropertyHandlerExample documentHandler) {
        this.documentHandler = documentHandler;
    }

    @Override
    public String identifier() {
        return "domismp-resource-example-properties";
    }

    @Override
    public String defaultUrlSegment() {
        return "prop";
    }

    @Override
    public String name() {
        return "DomiSMP property example";
    }

    @Override
    public String description() {
        return "DomiSMP property example";
    }

    @Override
    public String mimeType() {
        return  "text/x-properties";
    }

    @Override
    public List<SubresourceDefinitionSpi> getSubresourceSpiList() {
        return Collections.emptyList();
    }

    @Override
    public ResourceHandlerSpi getResourceHandler() {
        return documentHandler;
    }

    @Override
    public String toString() {
        return "DomiSMPPropertyResourceExample {" +
                "identifier=" + identifier() +
                ", defaultUrlSegment=" + defaultUrlSegment() +
                ", name=" + name() +
                ", mimeType=" + mimeType() +
                '}';
    }
}
