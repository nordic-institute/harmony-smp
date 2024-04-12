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

import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import eu.europa.ec.smp.spi.exceptions.ResourceException;

import java.util.List;


/**
 * The class implementing the ResourceHandlerSpi must support read transformation, store transformation, and
 * validation methods for the particular resource type, such as Oasis SMP 1.0 document, CPP document, etc.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 *
 */
public interface ResourceHandlerSpi {

    /**
     * Method get data from the resource in the input stream, and it writes transformation of the data as they are returned to
     *
     * @param resourceData the resource data
     * @param responseData the date object for setting the response
     */
    void readResource(RequestData resourceData, ResponseData responseData) throws ResourceException;

    void storeResource(RequestData resourceData, ResponseData responseData) throws ResourceException;

    /**
     * Validate resource schema and data. if resource is invalid the error is thrown
     * @param resourceData the resource data
     */
    void validateResource(RequestData resourceData) throws ResourceException;


    /**
     * Validate resource schema and data. if resource is invalid the error is thrown
     * @param resourceData the resource data
     */
    void generateResource(RequestData resourceData, ResponseData responseData, List<String> fields) throws ResourceException;

}
