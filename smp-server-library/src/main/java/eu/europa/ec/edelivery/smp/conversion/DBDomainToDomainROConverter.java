/*-
 * #START_LICENSE#
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
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.dao.DomainMemberDao;
import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Converter for domain DAO entity {@link DBDomain} to enriched webservice object {@link DomainRO}.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component
public class DBDomainToDomainROConverter implements Converter<DBDomain, DomainRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DBDomainToDomainROConverter.class);

    private final DomainMemberDao domainMemberDao;

    public DBDomainToDomainROConverter(DomainMemberDao domainMemberDao) {
        this.domainMemberDao = domainMemberDao;
    }

    @Override
    public DomainRO convert(DBDomain source) {

        DomainRO target = new DomainRO();
        try {
            BeanUtils.copyProperties(target, source);
            Long memberCount = domainMemberDao.getDomainMemberCount(source.getId(), null, MembershipRoleType.ADMIN);
            target.setAdminMemberCount(memberCount);

            List<String> domainDocuments = source.getDomainResourceDefs().stream().map(dbDomainResourceDef -> dbDomainResourceDef.getResourceDef().getIdentifier()).collect(Collectors.toList());
            target.getResourceDefinitions().addAll(domainDocuments);
            target.setDomainId(SessionSecurityUtils.encryptedEntityId(source.getId()));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error("Error occurred while converting DBDomain", e);
            return null;
        }
        return target;
    }
}
