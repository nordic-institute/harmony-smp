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
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.services.ui.filters.ServiceGroupFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.*;

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
        memEManager.remove(memEManager.contains(dbServiceGroup) ? dbServiceGroup : memEManager.merge(dbServiceGroup));
    }

    public long getServiceGroupCount(ServiceGroupFilter filters) {

        CriteriaQuery<Long> cqCount = createSearchCriteria(filters, true,
                null,
                null);
        return memEManager.createQuery(cqCount).getSingleResult();
    }

    public List<DBServiceGroup> getServiceGroupList(int startingAt, int maxResultCnt,
                               String sortField,
                               String sortOrder, ServiceGroupFilter filters) {

        List<DBServiceGroup> lstResult;
        try {
            CriteriaQuery<DBServiceGroup> cq = createSearchCriteria(filters,
                    false, sortField,
                    sortOrder);
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
                                                  boolean forCount, String sortField, String sortOrder) {
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

        Join<DBServiceGroup, DBServiceGroupDomain> serviceGroupJoinServiceGroupDomain = null;
        Predicate ownerPredicate = null;
        if (searchParams!=null) {

            if (searchParams.getDomain()!=null){
                serviceGroupJoinServiceGroupDomain =  serviceGroup.join("serviceGroupDomains", JoinType.INNER);
                serviceGroupJoinServiceGroupDomain = serviceGroupJoinServiceGroupDomain.on(cb.equal(serviceGroupJoinServiceGroupDomain.get("domain"), searchParams.getDomain()));
            }
            // limit for owner
            if (searchParams.getOwner() !=null){
                ownerPredicate = cb.equal(serviceGroup.join("users"),searchParams.getOwner());
            }
        }

        // set order by
        if (searchParams != null) {
            List<Predicate> lstPredicate = createPredicates(searchParams, serviceGroup, cb);

            if (serviceGroupJoinServiceGroupDomain!=null) {
                lstPredicate.add(serviceGroupJoinServiceGroupDomain.getOn());
            }
            if (ownerPredicate!=null) {
                lstPredicate.add(ownerPredicate);
            }

            if (!lstPredicate.isEmpty()) {
                Predicate[] tblPredicate = lstPredicate.stream().toArray(Predicate[]::new);
                cq.where(cb.and(tblPredicate));
            }
        }
        return cq;
    }



}
