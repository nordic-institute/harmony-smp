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

import eu.europa.ec.edelivery.smp.data.model.DBDomainDocumentTemplate;
import eu.europa.ec.edelivery.smp.data.ui.DomainDocumentTemplateRO;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converter from DBDomainDocumentTemplate to DomainDocumentTemplateRO
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component
public class DBDomainDocTmplToDomainDocTmplROConverter implements Converter<DBDomainDocumentTemplate, DomainDocumentTemplateRO> {

    @Override
    public DomainDocumentTemplateRO convert(DBDomainDocumentTemplate source) {
        DomainDocumentTemplateRO target = new DomainDocumentTemplateRO();
        target.setTemplateId(SessionSecurityUtils.encryptedEntityId(source.getId()));
        target.setDomainCode(source.getDomainResourceDef().getDomain().getDomainCode());
        target.setResourceDefIdentifier(source.getDomainResourceDef().getResourceDef().getIdentifier());
        target.setSubresourceDefIdentifier(source.getSubresourceDef() != null ? source.getSubresourceDef().getIdentifier() : null);
        target.setDocumentLevel(source.getDocumentLevelType());
        return target;
    }
}