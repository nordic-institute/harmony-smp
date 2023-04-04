/*
 * Copyright 2018 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.user.DBDomainMember;
import eu.europa.ec.edelivery.smp.data.model.user.DBGroupMember;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Repository
public class DomainMemberDao extends BaseDao<DBDomainMember> {


    public boolean isUserDomainMember(DBUser user, DBDomain domain){
        return isUserDomainsMember(user.getId(), Collections.singletonList(domain.getId()));
    }
    public boolean  isUserDomainsMember(DBUser user, List<DBDomain> domainList){
        List<Long> domainIds = domainList.stream().map(dbDomain -> dbDomain.getId()).collect(Collectors.toList());
        return isUserDomainsMember(user.getId(), domainIds);
    }

    public boolean  isUserDomainsMember(Long userId, List<Long> domainIdList){
        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_DOMAIN_MEMBER_BY_USER_DOMAINS_COUNT,
                Long.class);
        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_DOMAIN_IDS, domainIdList);
        return query.getSingleResult() > 0;
    }

    public boolean isUserDomainMemberWithRole(Long userId, List<Long> domainsIds, MembershipRoleType roleType) {
        TypedQuery<DBDomainMember> query = memEManager.createNamedQuery(QUERY_DOMAIN_MEMBER_BY_USER_DOMAINS, DBDomainMember.class);

        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_DOMAIN_IDS, domainsIds);
        return query.getResultList().stream().anyMatch(member ->member.getRole() == roleType );
    }

}
