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

package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.config.enums.SMPDomainPropertyEnum;
import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBDomainConfiguration;
import eu.europa.ec.edelivery.smp.data.ui.DomainPropertyRO;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;
import static eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType.toList;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.DOMAIN_NOT_EXISTS;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.ILLEGAL_STATE_DOMAIN_MULTIPLE_ENTRY;

/**
 * @author gutowpa
 * @since 3.0
 */
@Repository
public class DomainDao extends BaseDao<DBDomain> {


    /**
     * Returns the only single record from smp_domain table.
     * Returns Optional.empty() if there is more than 1 record present.
     *
     * @return the only single record from smp_domain table
     * @throws IllegalStateException if no domain is configured
     */
    public Optional<DBDomain> getTheOnlyDomain() {
        try {
            // expected is only one domain,
            TypedQuery<DBDomain> query = memEManager.createNamedQuery(QUERY_DOMAIN_ALL, DBDomain.class);
            return Optional.of(query.getSingleResult());
        } catch (NonUniqueResultException e) {
            return Optional.empty();
        } catch (NoResultException e) {
            throw new IllegalStateException(ErrorCode.NO_DOMAIN.getMessage());
        }
    }

    /**
     * Returns domain records from smp_domain table.
     *
     * @return the list of domain records from smp_domain table
     * @throws IllegalStateException if no domain is configured
     */
    public List<DBDomain> getAllDomains() {
        TypedQuery<DBDomain> query = memEManager.createNamedQuery(QUERY_DOMAIN_ALL, DBDomain.class);

        return query.getResultList();
    }

    /**
     * Returns domain code records from smp_domain table.
     *
     * @return the list of domain codes from smp_domain table
     */
    public List<String> getAllDomainCodes() {
        TypedQuery<String> query = memEManager.createNamedQuery(QUERY_DOMAIN_ALL_CODES, String.class);
        return query.getResultList();
    }


    public Optional<DBDomain> getFirstDomain() {
        TypedQuery<DBDomain> query = memEManager.createNamedQuery(QUERY_DOMAIN_ALL, DBDomain.class);
        query.setMaxResults(1);
        try {
            // expected is only one domain,
            return Optional.of(query.getSingleResult());
        } catch (NonUniqueResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Returns the domain by code.
     * Returns the domain or Optional.empty() if there is no domain.
     *
     * @return the only single record for domain code from smp_domain table or empty value
     * @throws IllegalStateException if more than one domain is not configured for the code!
     */
    public Optional<DBDomain> getDomainByCode(String domainCode) {
        return getDomainByQueryWithParam(domainCode, QUERY_DOMAIN_CODE, PARAM_DOMAIN_CODE);
    }

    public Optional<DBDomain> getDomainBySmlSmpId(String smlSmpId) {
        return getDomainByQueryWithParam(smlSmpId, QUERY_DOMAIN_SMP_SML_ID, PARAM_DOMAIN_SML_SMP_ID);
    }

    /**
     * Returns the Optional DBDomain from database. The domain is searched by domain parameter and queryDomainCode.
     *
     * @param domainParameter - parameter value to search for
     * @param queryName       - The named DBDomain query
     * @param queryParamName  the parameter name in the query
     * @return Optional DBDomain
     */
    private Optional<DBDomain> getDomainByQueryWithParam(String domainParameter, String queryName, String queryParamName) {
        if (StringUtils.isEmpty(domainParameter)) {
            return Optional.empty();
        }
        try {
            TypedQuery<DBDomain> query = memEManager.createNamedQuery(queryName, DBDomain.class);
            query.setParameter(queryParamName, domainParameter);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(ILLEGAL_STATE_DOMAIN_MULTIPLE_ENTRY.getMessage(domainParameter));
        }
    }


    public Long getResourceCountForDomain(Long domainId) {
        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_RESOURCES_BY_DOMAIN_ID_COUNT, Long.class);

        query.setParameter(PARAM_DOMAIN_ID, domainId);
        return query.getSingleResult();
    }

    public Long getDomainsByUserIdAndDomainRolesCount(Long userId, MembershipRoleType... roleTypes) {

        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_DOMAIN_BY_USER_ROLES_COUNT, Long.class);
        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_MEMBERSHIP_ROLES, toList(roleTypes));
        return query.getSingleResult();
    }

    public List<DBDomain> getDomainsByUserIdAndDomainRoles(Long userId, MembershipRoleType... roleTypes) {
        TypedQuery<DBDomain> query = memEManager.createNamedQuery(QUERY_DOMAIN_BY_USER_ROLES, DBDomain.class);
        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_MEMBERSHIP_ROLES, toList(roleTypes));
        return query.getResultList();
    }

    public Long getDomainsByUserIdAndGroupRolesCount(Long userId, MembershipRoleType... roleTypes) {

        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_DOMAIN_BY_USER_GROUP_ROLES_COUNT, Long.class);
        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_MEMBERSHIP_ROLES, toList(roleTypes));
        return query.getSingleResult();
    }

    public List<DBDomain> getDomainsByUserIdAndGroupRoles(Long userId, MembershipRoleType... roleTypes) {

        TypedQuery<DBDomain> query = memEManager.createNamedQuery(QUERY_DOMAIN_BY_USER_GROUP_ROLES, DBDomain.class);
        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_MEMBERSHIP_ROLES, toList(roleTypes));
        return query.getResultList();
    }

    public Long getDomainsByUserIdAndResourceRolesCount(Long userId, MembershipRoleType... roleTypes) {

        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_DOMAIN_BY_USER_RESOURCE_ROLES_COUNT, Long.class);
        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_MEMBERSHIP_ROLES, toList(roleTypes));
        return query.getSingleResult();
    }

    public List<DBDomain> getDomainsByUserIdAndResourceRoles(Long userId, MembershipRoleType... roleTypes) {

        TypedQuery<DBDomain> query = memEManager.createNamedQuery(QUERY_DOMAIN_BY_USER_RESOURCE_ROLES, DBDomain.class);
        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_MEMBERSHIP_ROLES, toList(roleTypes));
        return query.getResultList();
    }

    /**
     * Check if domain for domain code exists. If not SMPRuntimeException with DOMAIN_NOT_EXISTS is thrown.
     * If code is null or blank - then null is returned.
     *
     * @param domainCode
     * @return
     */
    public DBDomain validateDomainCode(String domainCode) {
        DBDomain domain = null;
        if (!StringUtils.isBlank(domainCode)) {
            Optional<DBDomain> od = getDomainByCode(domainCode);
            if (od.isPresent()) {
                domain = od.get();
            } else {
                throw new SMPRuntimeException(DOMAIN_NOT_EXISTS, domainCode);
            }
        }
        return domain;
    }

    /**
     * Removes Entity by given domain code
     *
     * @return true if entity existed before and was removed in this call.
     * False if entity did not exist, so nothing was changed
     */
    @Transactional
    public boolean removeByDomainCode(String domainCode) {
        Optional<DBDomain> optd = getDomainByCode(domainCode);
        if (optd.isPresent()) {
            memEManager.remove(optd.get());
            return true;
        }
        return false;
    }

    /**
     * Returns the domain properties for the given domain.
     *
     * @param domain - domain for which the properties are requested
     * @return - list of domain properties
     */
    public List<DBDomainConfiguration> getDomainConfiguration(DBDomain domain) {
        TypedQuery<DBDomainConfiguration> query = memEManager.createNamedQuery(QUERY_DOMAIN_CONFIGURATION_ALL,
                DBDomainConfiguration.class);
        query.setParameter(PARAM_DOMAIN_ID, domain.getId());
        return query.getResultList();
    }

    /**
     * Update domain property. If property does not exist in the database, it will be created.
     * The method must be called in transactional context, else TransactionRequiredException
     * will be thrown from JPA merge method.
     *
     * @param domain              - domain to update. Value is used in case domain configuration does not exist in the database.
     * @param domainProp          - domain property to update
     * @param domainConfiguration - current domain configuration or null if it does not exist in the database.
     *                            The object must be attached to the persistence context.
     * @param domainPropertyRO    - new domain property value and system default flag
     * @return new/updated  domain configuration
     */
    public DBDomainConfiguration updateDomainProperty(DBDomain domain, SMPDomainPropertyEnum domainProp,
                                                      DBDomainConfiguration domainConfiguration, DomainPropertyRO domainPropertyRO) {
        if (domainConfiguration == null) {
            domainConfiguration = new DBDomainConfiguration();
            domainConfiguration.setDomain(domain);
            domainConfiguration.setProperty(domainProp.getProperty());
            // attach domain configuration to the persistence context
            mergeConfiguration(domainConfiguration);
        }

        if (domainPropertyRO != null) {
            domainConfiguration.setValue(domainPropertyRO.getValue());
            domainConfiguration.setUseSystemDefault(domainPropertyRO.isSystemDefault());
        } else {
            domainConfiguration.setValue(domainProp.getDefValue());
            domainConfiguration.setUseSystemDefault(true);
        }

        return domainConfiguration;
    }


    /**
     * The method Merge the state of the given domain configuration into the current
     * persistence context.  The method must be
     * called in existing transaction, and it is used to manage domain properties.
     *
     * @param entity - domain configuration to be merged
     * @return - jpa merged/managed domain configuration
     * @throws TransactionRequiredException if there is no transaction when
     *                                      invoked on a container-managed entity manager of that is of type
     *                                      <code>PersistenceContextType.TRANSACTION</code>
     */
    public DBDomainConfiguration mergeConfiguration(DBDomainConfiguration entity) {
        return memEManager.merge(entity);
    }

    public void removeConfiguration(DBDomainConfiguration entity) {
        memEManager.remove(entity);
    }

}
