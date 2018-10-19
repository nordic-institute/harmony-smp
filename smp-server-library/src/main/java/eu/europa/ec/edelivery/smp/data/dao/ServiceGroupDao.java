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
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroupDomain;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.services.ui.filters.ServiceGroupFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by gutowpa on 14/11/2017.
 */
@Repository
public class ServiceGroupDao extends BaseDao<DBServiceGroup> {


    /**
     * Method returns ServiceGroup by participant identifier. If there is no service group it returns empty Option.
     * If more than one result returns IllegalStateException caused by database data inconsistency. Only one combination of
     * participant identifier must be in the database.
     *
     * @param participantId participant identifier
     * @param schema        participant identifier schema
     * @return DBServiceGroup
     */
    public Optional<DBServiceGroup> findServiceGroup(String participantId, String schema) {


        try {
            TypedQuery<DBServiceGroup> query = memEManager.createNamedQuery("DBServiceGroup.getServiceGroup", DBServiceGroup.class);
            query.setParameter("participantIdentifier", participantId);
            query.setParameter("participantScheme", schema);
            DBServiceGroup res = query.getSingleResult();
            return Optional.of(res);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(ErrorCode.ILLEGAL_STATE_SG_MULTIPLE_ENTRY.getMessage(participantId, schema));
        }
    }

    /**
     * Method removes service group from DB. Related entities:Extension, ownerships,
     * metadata clobs, metadata are also deleted.
     *
     * @param dbServiceGroup
     */
    @Transactional
    public void removeServiceGroup(DBServiceGroup dbServiceGroup){
     // Because of one to many relationships
         // remove with JPA/JPQL (or native sql) querieas are much more efficient in this case.
         // but it does not capture audit with envers: Hibernate Envers captures the Audit information only when the updates
         // happen through Persistence Context.
 /*       em.createNamedQuery("DBServiceGroup.deleteAllOwnerships")
                .setParameter("serviceGroupId", dbServiceGroup.getId()).executeUpdate();

        em.createNamedQuery("DBServiceGroupExtension.deleteById")
                .setParameter("id", dbServiceGroup.getId()).executeUpdate();

        em.createNamedQuery("DBServiceMetadata.deleteOwnedByServiceGroup")
                .setParameter("serviceGroup", dbServiceGroup).executeUpdate();

        em.createNamedQuery("DBServiceGroup.deleteById")
                .setParameter("id", dbServiceGroup.getId()).executeUpdate()>0;
 */
        memEManager.remove(memEManager.contains(dbServiceGroup) ? dbServiceGroup : memEManager.merge(dbServiceGroup));
    }

    public long getServiceGroupCount(ServiceGroupFilter filters, DBDomain domain) {

        CriteriaQuery<Long> cqCount = createSearchCriteria(filters, true,
                null,
                null, domain);
        return memEManager.createQuery(cqCount).getSingleResult();
    }

    public List<DBServiceGroup> getServiceGroupList(int startingAt, int maxResultCnt,
                               String sortField,
                               String sortOrder, ServiceGroupFilter filters, DBDomain domain) {

        List<DBServiceGroup> lstResult;
        try {
            CriteriaQuery<DBServiceGroup> cq = createSearchCriteria(filters,
                    false, sortField,
                    sortOrder, domain);
            TypedQuery<DBServiceGroup> q = memEManager.createQuery(cq);
            if (maxResultCnt > 0) {
                q.setMaxResults(maxResultCnt);
            }
            if (startingAt > 0) {
                q.setFirstResult(startingAt);
            }
            lstResult = q.getResultList();
        } catch (NoResultException ex) {
            //LOG.warn("No result for '" + filterType.getName() + "' does not have a setter!", ex);
            lstResult = new ArrayList<>();
        }
        return lstResult;
    }

    protected  CriteriaQuery createSearchCriteria(ServiceGroupFilter searchParams,
                                                  boolean forCount, String sortField, String sortOrder, DBDomain domain) {
        CriteriaBuilder cb = memEManager.getCriteriaBuilder();
        CriteriaQuery cq = forCount ? cb.createQuery(Long.class) : cb.createQuery(DBServiceGroup.class);
        Root<DBServiceGroup> serviceGroup = cq.from(DBServiceGroup.class);
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

       // Join<DBServiceGroupDomain, DBDomain> serviceGroupDomainJoinDomain = null;
        Join<DBServiceGroup, DBServiceGroupDomain> serviceGroupJoinServiceGroupDomain = null;
        //Join<DBServiceGroupDomain, DBDomain> serviceGroupDomainJoinDomain = null;
        if (domain!=null){
            serviceGroupJoinServiceGroupDomain =  serviceGroup.join("serviceGroupDomains", JoinType.INNER);
            serviceGroupJoinServiceGroupDomain = serviceGroupJoinServiceGroupDomain.on(cb.equal(serviceGroupJoinServiceGroupDomain.get("domain"), domain));
        }


        // set order by
        if (searchParams != null) {
            List<Predicate> lstPredicate = createPredicates(searchParams, serviceGroup, cb);

            if (serviceGroupJoinServiceGroupDomain!=null) {
                lstPredicate.add(serviceGroupJoinServiceGroupDomain.getOn());
            }

            if (!lstPredicate.isEmpty()) {
                Predicate[] tblPredicate = lstPredicate.stream().toArray(Predicate[]::new);
                cq.where(cb.and(tblPredicate));
            }
        }
        return cq;
    }



}
