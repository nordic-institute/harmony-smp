/*-
 * #START_LICENSE#
 * smp-server-library
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
package eu.europa.ec.edelivery.smp.services.spi;

import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.IdentifierService;
import eu.europa.ec.smp.spi.api.SmpIdentifierServiceApi;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import org.springframework.stereotype.Service;

/**
 * Implementation of the class provides the identifier services for the SPI implementation. The identifier formatting
 * is DomiSMP configuration specific!
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Service
public class SmpIdentifierService implements SmpIdentifierServiceApi {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SmpIdentifierService.class);

    final IdentifierService identifierService;
    final ConfigurationService configurationService;

    public SmpIdentifierService(IdentifierService identifierService, ConfigurationService configurationService) {
        this.identifierService = identifierService;
        this.configurationService = configurationService;
    }

    @Override
    public ResourceIdentifier normalizeResourceIdentifier(final String domainCode, final String value, final String scheme) {
        return toUrlIdentifier(identifierService.normalizeParticipant(domainCode, scheme, value));
    }

    @Override
    public ResourceIdentifier normalizeSubresourceIdentifier(final String domainCode, final String value, final String scheme) {
        return toUrlIdentifier(identifierService.normalizeDocument(domainCode, scheme, value));
    }

    @Override
    public String formatResourceIdentifier(final String domainCode, ResourceIdentifier identifier) {
        if (identifier == null) {
            LOG.debug("formatResourceIdentifier: identifier is null for domain [{}]", domainCode);
            return null;
        }
        Identifier id = toIdentifier(identifier);
        return identifierService.formatParticipant(domainCode, id);
    }

    @Override
    public String formatSubresourceIdentifier(final String domainCode, ResourceIdentifier identifier) {
        if (identifier == null) {
            LOG.debug("formatSubresourceIdentifier: identifier is null for domain [{}]", domainCode);
            return null;
        }
        Identifier id = toIdentifier(identifier);
        return identifierService.formatDocument(domainCode, id);
    }

    @Override
    public String getURLEncodedResourceIdentifier(final String domainCode, ResourceIdentifier identifier) {
        if (identifier == null) {
            LOG.debug("getURLEncodedResourceIdentifier: identifier is null for domain [{}]", domainCode);
            return null;
        }
        Identifier id = toIdentifier(identifier);
        return identifierService.urlEncodedFormatParticipant(domainCode, id);
    }

    @Override
    public String getURLEncodedSubresourceIdentifier(final String domainCode, ResourceIdentifier identifier) {
        if (identifier == null) {
            LOG.debug("getURLEncodedSubresourceIdentifier: identifier is null for domain [{}]", domainCode);
            return null;
        }
        Identifier id = toIdentifier(identifier);
        return identifierService.urlEncodedFormatDocument(domainCode, id);
    }


    private Identifier toIdentifier(ResourceIdentifier identifier) {
        return identifier == null ? null :
                new Identifier(identifier.getValue(), identifier.getScheme());
    }

    private ResourceIdentifier toUrlIdentifier(Identifier identifier) {
        return identifier == null ? null :
                new ResourceIdentifier(identifier.getValue(), identifier.getScheme());
    }

}
