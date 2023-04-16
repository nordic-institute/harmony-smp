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
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import org.apache.commons.lang3.StringUtils;
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

    private final DomainDao domainDao;


    public DomainMemberDao(DomainDao domainDao) {
        this.domainDao = domainDao;
    }

    public boolean isUserDomainMember(DBUser user, DBDomain domain) {
        return isUserDomainsMember(user.getId(), Collections.singletonList(domain.getId()));
    }

    public boolean isUserDomainsMember(DBUser user, List<DBDomain> domainList) {
        List<Long> domainIds = domainList.stream().map(DBDomain::getId).collect(Collectors.toList());
        return isUserDomainsMember(user.getId(), domainIds);
    }

    public boolean isUserDomainsMember(Long userId, List<Long> domainIdList) {
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
        return query.getResultList().stream().anyMatch(member -> member.getRole() == roleType);
    }

    public boolean isUserAnyDomainAdministrator(Long userId){
        return domainDao.getDomainsByUserIdAndDomainRolesCount(userId, MembershipRoleType.ADMIN)>0;
    }

    public boolean isUserResourceAdministrator(Long userId){
        return false;
    }

    public List<DBDomainMember> getDomainMembers(Long domainId, int iPage, int iPageSize, String filter) {
        boolean hasFilter = StringUtils.isNotBlank(filter);
        TypedQuery<DBDomainMember> query = memEManager.createNamedQuery(hasFilter ?
                QUERY_DOMAIN_MEMBERS_FILTER : QUERY_DOMAIN_MEMBERS, DBDomainMember.class);

        if (iPageSize> -1 && iPage >-1 ) {
            query.setFirstResult(iPage * iPageSize);
        }
        if (iPageSize> 0) {
            query.setMaxResults(iPageSize);
        }
        query.setParameter(PARAM_DOMAIN_ID, domainId);
        if (hasFilter) {
            query.setParameter(PARAM_USER_FILTER, StringUtils.wrapIfMissing(StringUtils.trim(filter),"%" ));
        }
        return query.getResultList();
    }

    public Long getDomainMemberCount(Long domainId, String filter) {
        boolean hasFilter = StringUtils.isNotBlank(filter);
        TypedQuery<Long> query = memEManager.createNamedQuery(hasFilter ? QUERY_DOMAIN_MEMBERS_FILTER_COUNT : QUERY_DOMAIN_MEMBERS_COUNT, Long.class);
        query.setParameter(PARAM_DOMAIN_ID, domainId);
        if (hasFilter) {
            query.setParameter(PARAM_USER_FILTER, StringUtils.wrapIfMissing(StringUtils.trim(filter),"%" ));
        }
        return query.getSingleResult();
    }


    public DBDomainMember addMemberToDomain(DBDomain domain, DBUser user , MembershipRoleType role){
        DBDomainMember domainMember = new DBDomainMember();
        domainMember.setRole(role);
        domainMember.setUser(user);
        domainMember.setDomain(domain);
        domainMember = merge(domainMember);
        return domainMember;
    }

}
