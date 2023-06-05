/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResourceFilter;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.filters.ResourceFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;


/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Repository
public class ResourceDao extends BaseDao<DBResource> {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ResourceDao.class);


    /**
     * The method returns DBResource for the participant identifier, domain, and resource type. If the resource does not exist, it returns an empty Option.
     * If more than one result exist, it returns IllegalStateException caused by database data inconsistency. Only one combination of
     * participant identifier must be registered in database for the domain and the resource type.
     *
     * @param identifierValue  resource identifier value
     * @param identifierSchema resource identifier schema
     * @return DBResource from the database
     */
    public Optional<DBResource> getResource(String identifierValue, String identifierSchema, DBResourceDef resourceDef, DBDomain domain) {
        LOG.debug("Get resource (identifier [{}], scheme [{}])", identifierValue, identifierSchema);
        try {
            TypedQuery<DBResource> query = memEManager.createNamedQuery(QUERY_RESOURCE_BY_IDENTIFIER_RESOURCE_DEF_DOMAIN, DBResource.class);
            query.setParameter(PARAM_DOMAIN_ID, domain.getId());
            query.setParameter(PARAM_RESOURCE_DEF_ID, resourceDef.getId());
            query.setParameter(IDENTIFIER_VALUE, identifierValue);
            query.setParameter(IDENTIFIER_SCHEME, identifierSchema);
            DBResource res = query.getSingleResult();
            return Optional.of(res);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(ErrorCode.ILLEGAL_STATE_SG_MULTIPLE_ENTRY.getMessage(identifierValue, identifierSchema));
        }
    }


    public Long getResourcesForFilterCount(DBResourceFilter resourceFilter) {
        LOG.debug("Get resources count for filter [{}]", resourceFilter);

        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_RESOURCE_FILTER_COUNT, Long.class);
        query.setParameter(PARAM_GROUP_ID, resourceFilter.getGroupId());
        query.setParameter(PARAM_DOMAIN_ID, resourceFilter.getDomainId());
        query.setParameter(PARAM_RESOURCE_DEF_ID, resourceFilter.getResourceDefId());
        query.setParameter(PARAM_USER_ID, resourceFilter.getUserId());
        query.setParameter(PARAM_MEMBERSHIP_ROLES, resourceFilter.getMembershipRoleTypes());
        query.setParameter(PARAM_RESOURCE_FILTER, resourceFilter.getIdentifierFilter());
        return query.getSingleResult();
    }

    public List<DBResource> getResourcesForFilter(int iPage, int iPageSize, DBResourceFilter resourceFilter) {
        LOG.debug("Get resources page [{}] and page size [{}] for filter [{}]", iPage, iPageSize, resourceFilter);
        TypedQuery<DBResource> query = memEManager.createNamedQuery(QUERY_RESOURCE_FILTER, DBResource.class);

        if (iPageSize > -1 && iPage > -1) {
            query.setFirstResult(iPage * iPageSize);
        }
        if (iPageSize > 0) {
            query.setMaxResults(iPageSize);
        }

        query.setParameter(PARAM_GROUP_ID, resourceFilter.getGroupId());
        query.setParameter(PARAM_DOMAIN_ID, resourceFilter.getDomainId());
        query.setParameter(PARAM_RESOURCE_DEF_ID, resourceFilter.getResourceDefId());
        query.setParameter(PARAM_USER_ID, resourceFilter.getUserId());
        query.setParameter(PARAM_MEMBERSHIP_ROLES, resourceFilter.getMembershipRoleTypes());
        query.setParameter(PARAM_RESOURCE_FILTER, resourceFilter.getIdentifierFilter());
        return query.getResultList();
    }

    public List<DBResource> getPublicResourcesSearch(int iPage, int iPageSize, DBUser user, String schema, String identifier) {
        LOG.debug("Get resources list for user [{}], search scheme [{}] and search value [{}]", user, schema, identifier);

        TypedQuery<DBResource> query = memEManager.createNamedQuery(QUERY_RESOURCE_ALL_FOR_USER, DBResource.class);
        if (iPageSize > -1 && iPage > -1) {
            query.setFirstResult(iPage * iPageSize);
        }
        if (iPageSize > 0) {
            query.setMaxResults(iPageSize);
        }
        query.setParameter(PARAM_USER_ID, user != null ? user.getId() : null);
        query.setParameter(PARAM_RESOURCE_SCHEME, StringUtils.isBlank(schema) ? null : StringUtils.wrapIfMissing(schema, "%"));
        query.setParameter(PARAM_RESOURCE_IDENTIFIER, StringUtils.isBlank(identifier) ? null : StringUtils.wrapIfMissing(identifier, "%"));

        return query.getResultList();
    }

    public Long getPublicResourcesSearchCount(DBUser user, String schema, String identifier) {
        LOG.debug("Get resources count for user [{}], search scheme [{}] and search value [{}]", user, schema, identifier);
        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_RESOURCE_ALL_FOR_USER_COUNT, Long.class);

        query.setParameter(PARAM_USER_ID, user != null ? user.getId() : null);
        query.setParameter(PARAM_RESOURCE_SCHEME, StringUtils.isBlank(schema) ? null : StringUtils.wrapIfMissing(schema, "%"));
        query.setParameter(PARAM_RESOURCE_IDENTIFIER, StringUtils.isBlank(identifier) ? null : StringUtils.wrapIfMissing(identifier, "%"));

        return query.getSingleResult();
    }


    /**
     * Method returns ServiceGroup by participant identifier. If there is no service group it returns empty Option.
     * If more than one result returns IllegalStateException caused by database data inconsistency. Only one combination of
     * participant identifier must be in the database.
     *
     * @param participantId participant identifier
     * @param schema        participant identifier schema
     * @return DBResource
     */
    public Optional<DBResource> findServiceGroup(String participantId, String schema) {


        try {
            TypedQuery<DBResource> query = memEManager.createNamedQuery("DBResource.getServiceGroupByIdentifier", DBResource.class);
            query.setParameter("participantIdentifier", participantId);
            query.setParameter("participantScheme", schema);
            DBResource res = query.getSingleResult();
            return Optional.of(res);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(ErrorCode.ILLEGAL_STATE_SG_MULTIPLE_ENTRY.getMessage(participantId, schema));
        }
    }


    /**
     * Method returns ServiceGroupDomain for participant identifie and domain code. If there is no service group
     * or service group registred to domain it returns empty Option.
     * If more than one result returns IllegalStateException caused by database data inconsistency. Only one combination of
     * participant identifier must be in the database.
     *
     * @param participantId participant identifier
     * @param schema        participant identifier schema
     * @param domainCode    domainCode
     * @return DBResource
     */
    public Optional<DBDomainResourceDef> findServiceGroupDomain(String participantId, String schema, String domainCode) {

        try {
            TypedQuery<DBDomainResourceDef> query = memEManager.createNamedQuery("DBServiceGroupDomain.getServiceGroupDomain", DBDomainResourceDef.class);
            query.setParameter("participantIdentifier", participantId);
            query.setParameter("participantScheme", schema);
            query.setParameter("domainCode", domainCode);
            DBDomainResourceDef res = query.getSingleResult();
            return Optional.of(res);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(ErrorCode.ILLEGAL_STATE_SG_MULTIPLE_ENTRY.getMessage(participantId, schema));
        }
    }

    public Optional<DBDomainResourceDef> findServiceGroupDomainForUserIdAndMetadataId(Long userId, Long serviceMetadataId) {

        try {
            TypedQuery<DBDomainResourceDef> query = memEManager.createNamedQuery("DBServiceGroupDomain.getOwnedServiceGroupDomainForUserIdAndServiceMetadataId", DBDomainResourceDef.class);
            query.setParameter("userId", userId);
            query.setParameter("serviceMetadataId", serviceMetadataId);
            DBDomainResourceDef res = query.getSingleResult();
            return Optional.of(res);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(ErrorCode.ILLEGAL_STATE_SMD_ON_MULTIPLE_SGD.getMessage(serviceMetadataId, userId));
        }
    }

    public Long getResourceCountForDomainIdAndResourceDefId(Long domainId, Long resourceDefId) {
        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_RESOURCES_BY_DOMAIN_ID_RESOURCE_DEF_ID_COUNT, Long.class);
        query.setParameter(PARAM_DOMAIN_ID, domainId);
        query.setParameter(PARAM_RESOURCE_DEF_ID, resourceDefId);
        return query.getSingleResult();
    }

    /**
     * Method removes the resource from DB. Related entities (cascade): sub-resources, Document, Document version,
     * group memberships,
     *
     * @param resource
     */
    @Transactional
    public void remove(DBResource resource) {
        removeById(resource.getId());
    }


    public long getServiceGroupCount(ResourceFilter filters) {

        CriteriaQuery<Long> cqCount = createSearchCriteria(filters, true,
                null,
                null);
        return memEManager.createQuery(cqCount).getSingleResult();
    }

    public List<DBResource> getServiceGroupList(int startingAt, int maxResultCnt,
                                                String sortField,
                                                String sortOrder, ResourceFilter filters) {

        List<DBResource> lstResult;
        try {
            CriteriaQuery<DBResource> cq = createSearchCriteria(filters,
                    false, sortField,
                    sortOrder);
            TypedQuery<DBResource> q = memEManager.createQuery(cq);
            if (maxResultCnt > 0) {
                q.setMaxResults(maxResultCnt);
            }
            if (startingAt > 0) {
                q.setFirstResult(startingAt);
            }
            lstResult = q.getResultList();
        } catch (NoResultException ex) {
            lstResult = new ArrayList<>();
        }
        return lstResult;
    }

    protected CriteriaQuery createSearchCriteria(ResourceFilter searchParams,
                                                 boolean forCount, String sortField, String sortOrder) {
        CriteriaBuilder cb = memEManager.getCriteriaBuilder();
        CriteriaQuery cq = forCount ? cb.createQuery(Long.class) : cb.createQuery(DBResource.class);
        Root<DBResource> serviceGroup = cq.from(DBResource.class);
        if (forCount) {
            cq.select(cb.count(serviceGroup));
        } else if (sortField != null) {
            if (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) {
                cq.orderBy(cb.asc(serviceGroup.get(sortField)));
            } else {
                cq.orderBy(cb.desc(serviceGroup.get(sortField)));
            }
        } else {
            if (!StringUtils.isBlank(defaultSortMethod)) {
                cq.orderBy(cb.desc(serviceGroup.get(defaultSortMethod)));
            }
        }

        Join<DBResource, DBDomainResourceDef> serviceGroupJoinServiceGroupDomain = null;
        Predicate ownerPredicate = null;
        if (searchParams != null) {

            if (searchParams.getDomain() != null) {
                serviceGroupJoinServiceGroupDomain = serviceGroup.join("serviceGroupDomains", JoinType.INNER);
                serviceGroupJoinServiceGroupDomain = serviceGroupJoinServiceGroupDomain.on(cb.equal(serviceGroupJoinServiceGroupDomain.get("domain"), searchParams.getDomain()));
            }
            // limit for owner
            if (searchParams.getOwner() != null) {
                ownerPredicate = cb.equal(serviceGroup.join("users"), searchParams.getOwner());
            }
        }

        // set order by
        if (searchParams != null) {
            List<Predicate> lstPredicate = createPredicates(searchParams, serviceGroup, cb);

            if (serviceGroupJoinServiceGroupDomain != null) {
                lstPredicate.add(serviceGroupJoinServiceGroupDomain.getOn());
            }
            if (ownerPredicate != null) {
                lstPredicate.add(ownerPredicate);
            }

            if (!lstPredicate.isEmpty()) {
                Predicate[] tblPredicate = lstPredicate.stream().toArray(Predicate[]::new);
                cq.where(cb.and(tblPredicate));
            }
        }
        return cq;
    }

    public void updateServiceGroupDomain(DBDomainResourceDef serviceGroupDomain) {
        memEManager.merge(serviceGroupDomain);
    }
}
