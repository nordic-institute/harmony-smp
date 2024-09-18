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
package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.enums.DocumentReferenceType;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSearchReferenceDocumentMapping;
import eu.europa.ec.edelivery.smp.data.ui.SearchReferenceDocumentRO;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.IdentifierService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;


/**
 * Converter for DBReviewDocumentVersion to ReviewDocumentVersionRO
 *
 * @author Joze RIHTARSIC
 * @since 5.1
 */
@Component
public class DBSearchReferenceDocumentVersionToSearchReferenceDocumentROConverter implements Converter<DBSearchReferenceDocumentMapping, SearchReferenceDocumentRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DBSearchReferenceDocumentVersionToSearchReferenceDocumentROConverter.class);

    IdentifierService identifierService;
    private static final String URL_SEPARATOR = "/";

    public DBSearchReferenceDocumentVersionToSearchReferenceDocumentROConverter(IdentifierService identifierService) {
        this.identifierService = identifierService;
    }

    @Override
    public SearchReferenceDocumentRO convert(DBSearchReferenceDocumentMapping source) {

        SearchReferenceDocumentRO target = new SearchReferenceDocumentRO();
        try {
            BeanUtils.copyProperties(target, source);
            target.setDocumentId(SessionSecurityUtils.encryptedEntityId(source.getDocumentId()));
            target.setResourceId(SessionSecurityUtils.encryptedEntityId(source.getResourceId()));
            target.setSubresourceId(SessionSecurityUtils.encryptedEntityId(source.getSubresourceId()));
            target.setReferenceUrl(getReferenceUrl(source));

        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error("Error occurred while converting DBResource", e);
            return null;
        }
        return target;
    }

    private String getReferenceUrl(DBSearchReferenceDocumentMapping source) {

        String ctx = URL_SEPARATOR + getUrlPart(source.getDomainCode()) + getUrlPart(source.getResourceDefUrlSegment());
        ctx += identifierService.urlEncodedFormatParticipant(source.getDomainCode(), new Identifier(source.getResourceScheme(), source.getResourceValue()));

        if (source.getReferenceType() == DocumentReferenceType.SUBRESOURCE) {
            ctx += URL_SEPARATOR + getUrlPart(source.getSubresourceDefUrlSegment())
                    + identifierService.urlEncodedFormatDocument(source.getDomainCode(), new Identifier(source.getSubresourceScheme(), source.getSubresourceValue()));
        }
        return ctx;
    }

    private String getUrlPart(String value) {
        return StringUtils.isBlank(value) ? "" : value + URL_SEPARATOR;
    }
}
