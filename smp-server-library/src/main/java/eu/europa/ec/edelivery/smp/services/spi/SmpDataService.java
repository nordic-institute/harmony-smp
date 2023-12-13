/*-
 * #%L
 * smp-server-library
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
 * #L%
 */
package eu.europa.ec.edelivery.smp.services.spi;

import eu.europa.ec.edelivery.smp.data.dao.SubresourceDao;
import eu.europa.ec.edelivery.smp.data.dao.SubresourceDefDao;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.SmpUrlBuilder;
import eu.europa.ec.smp.spi.api.SmpDataServiceApi;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Implementation of the class provides the DomiSMP misc data services for the SPI implementation.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Service
public class SmpDataService implements SmpDataServiceApi {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SmpDataServiceApi.class);
    final SubresourceDao subresourceDao;
    final SubresourceDefDao subresourceDefDao;
    final SmpUrlBuilder smpUrlBuilder;

    public SmpDataService(SubresourceDao subresourceDao, SubresourceDefDao subresourceDefDao, SmpUrlBuilder smpUrlBuilder) {
        this.subresourceDao = subresourceDao;
        this.subresourceDefDao = subresourceDefDao;
        this.smpUrlBuilder = smpUrlBuilder;
    }

    @Override
    public List<ResourceIdentifier> getSubResourceIdentifiers(ResourceIdentifier identifier, String subresourceDefinitionIdentifier) {
        LOG.info("Retrieve list of sub-resources for the resource: [{}] and document type: [{}]", identifier, subresourceDefinitionIdentifier);
        List<DBSubresource> subresources = subresourceDao.getSubResourcesForResource(SPIUtils.toIdentifier(identifier), subresourceDefinitionIdentifier);
        LOG.info("Got list [{}] of sub-resources for the resource: [{}] and document type: [{}]", subresources.size(), identifier, subresourceDefinitionIdentifier);
        return subresources.stream().map(SPIUtils::toUrlIdentifier).collect(Collectors.toList());
    }

    @Override
    public String getResourceUrl() {
        return smpUrlBuilder.buildSMPUrlForApplication();
    }

    @Override
    public String getURIPathSegmentForSubresource(String subresourceIdentifier) {
        LOG.info("Get URI path segment for the sub-resource type: [{}]", subresourceIdentifier);
        Optional<DBSubresourceDef> optSubresourceDef = subresourceDefDao.getSubresourceDefByIdentifier(subresourceIdentifier);
        return optSubresourceDef.isPresent() ? optSubresourceDef.get().getUrlSegment() : null;
    }
}
