/*-
 * #START_LICENSE#
 * smp-webapp
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

package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.identifiers.IdentifierFormatter;
import org.springframework.stereotype.Component;


/**
 * Class provides tools to parse, format and normalize Document and Participant identifiers.
 *
 * @author gutowpa
 * @since 3.0.0
 */
@Component
public class IdentifierService {

    private final IdentifierFormatterService identifierFormatterService;

    public IdentifierService(IdentifierFormatterService identifierFormatterService) {
        this.identifierFormatterService = identifierFormatterService;
    }

    public Identifier normalizeParticipant(final String domainCode, final String scheme, final String identifier) {
        return getResourceIdentifierFormatter(domainCode).normalize(scheme, identifier);
    }

    public Identifier normalizeParticipant(final String domainCode, final Identifier participantIdentifier) {
        return getResourceIdentifierFormatter(domainCode).normalize(participantIdentifier);
    }

    public Identifier normalizeParticipantIdentifier(final String domainCode, final String participantId) {
        return getResourceIdentifierFormatter(domainCode).normalizeIdentifier(participantId);
    }

    public String formatParticipant(final String domainCode, final Identifier participantIdentifier) {
        return getResourceIdentifierFormatter(domainCode).format(participantIdentifier);
    }

    public String urlEncodedFormatParticipant(final String domainCode, final Identifier participantIdentifier) {
        return getResourceIdentifierFormatter(domainCode).urlEncodedFormat(participantIdentifier);
    }

    public String formatParticipant(final String domainCode, final String scheme, final String identifier) {
        return getResourceIdentifierFormatter(domainCode).format(scheme, identifier);
    }

    public Identifier normalizeDocument(final String domainCode, final Identifier documentIdentifier) {
        return getSubresourceIdentifierFormatter(domainCode)
                .normalize(documentIdentifier);
    }

    public Identifier normalizeDocument(final String domainCode, final String scheme, final String identifier) {
        return getSubresourceIdentifierFormatter(domainCode)
                .normalize(scheme, identifier);
    }

    public Identifier normalizeDocumentIdentifier(final String domainCode, String value) {
        return getSubresourceIdentifierFormatter(domainCode).normalizeIdentifier(value);
    }

    public String formatDocument(final String domainCode, final Identifier documentIdentifier) {
        return getSubresourceIdentifierFormatter(domainCode).format(documentIdentifier);
    }

    public String urlEncodedFormatDocument(final String domainCode, final Identifier documentIdentifier) {
        return getSubresourceIdentifierFormatter(domainCode).urlEncodedFormat(documentIdentifier);
    }

    public String formatDocument(final String domainCode, final String scheme, final String identifier) {
        return getSubresourceIdentifierFormatter(domainCode).format(scheme, identifier);
    }

    private IdentifierFormatter getSubresourceIdentifierFormatter(final String domainCode) {
        return identifierFormatterService.getSubresourceIdentifierFormatter(domainCode);
    }

    private IdentifierFormatter getResourceIdentifierFormatter(final String domainCode) {
        return identifierFormatterService.getResourceIdentifierFormatter(domainCode);
    }

    /**
     * Check if the identifier is case sensitive or not.
     *
     * @param normalizedIdentifier the normalized identifier to check. The identifier must already have defined the scheme.
     * @param domainCode the domain code
     * @return true if the identifier is case sensitive, false otherwise
     */
    public boolean isResourceIdentifierCaseSensitive(final Identifier normalizedIdentifier, final String domainCode) {

        IdentifierFormatter identifierFormatter =  identifierFormatterService.getResourceIdentifierFormatter(domainCode);
        return !identifierFormatter.isCaseInsensitiveSchema(normalizedIdentifier.getScheme());
    }

    /**
     * Check if the identifier is case sensitive or not.
     *
     * @param normalizedIdentifier the normalized identifier to check. The identifier must already have defined the scheme.
     * @param domainCode the domain code
     * @return true if the identifier is case sensitive, false otherwise
     */
    public boolean isSubresourceIdentifierCaseSensitive(final Identifier normalizedIdentifier, final String domainCode) {

        IdentifierFormatter identifierFormatter =  identifierFormatterService.getSubresourceIdentifierFormatter(domainCode);
        return !identifierFormatter.isCaseInsensitiveSchema(normalizedIdentifier.getScheme());
    }
}
