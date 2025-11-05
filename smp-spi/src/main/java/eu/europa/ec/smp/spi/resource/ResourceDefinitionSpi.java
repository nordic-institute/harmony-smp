/*-
 * #START_LICENSE#
 * smp-spi
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
package eu.europa.ec.smp.spi.resource;


import java.util.List;

/**
 *
 * SMP Service provider interface (SPI) for implementing resource handling
 * This SPI interface is intended to allow support for various resource types as for example Oasis SMP 1.0 document
 * CPP documents
 *
 *  @author Joze Rihtarsic
 *  @since 5.0
 */
public interface ResourceDefinitionSpi {

    /**
     * Unique identifier of the resource definition. When upgrading to the newer version the indenter must stay the same else the definition
     * is handled as new resource definition.
     *
     * @return resource definition unique identifier
     */
    String identifier();

    /**
     * Default URL path segment for the resource. The DomiSMP can override the url segment for the domain!
     * @return default url segment
     */
    String defaultUrlSegment();

    /**
     * Optional url segments for the resource. The segments can trigger different processing in the resource handler.
     * For example if defaultUrlSegment is "smp-1" and optionalUrlSegments are ["extension","contact"]
     * then when the url for the resource will be http://<host>:<port>/<context>/smp-1/<identifier> it will be handled as default resource
     * when the url for the resource will be http://<host>:<port>/<context>/extension/<identifier> it will be return just extension information
     * The url segment part is provided to the resource handler as property 'resource.url.segment'/'subresource.url.segment' and the handler can decide how to process the request
     * @return list of optional url segments
     */
    default List<String> optionalUrlSegments(){
        return List.of();
    }

    String name();
    String description();

    /**
     * Mimetype of the resource
     * @return mimetype of the resource
     */
    String mimeType();

    /**
     * All subresouce types for the resource
     * @return list of subresource definitions
     */

    List<SubresourceDefinitionSpi> getSubresourceSpiList();

    // resource handle for validating, reading and storing the resource
    ResourceHandlerSpi getResourceHandler();


}
