/*
 * Copyright 2015 e-CODEX Project
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl5
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.domibus.common.dao;

import eu.domibus.common.MSHRole;
import eu.domibus.common.MessageStatus;
import eu.domibus.common.model.MessageType;
import eu.domibus.common.model.logging.MessageLogEntry;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Christian Koch
 * @since 3.0
 */

@Repository
@Transactional
public class MessageLogDao extends BasicDao<MessageLogEntry> {

    public MessageLogDao() {
        super(MessageLogEntry.class);
    }

    public List<String> findUnsentMessages() {

        final Query query = this.em.createNamedQuery("MessageLogEntry.findUnsentMessages");

        //noinspection unchecked
        return query.getResultList();
    }

    public List<String> findUndownloadedUserMessages() {

        final Query query = this.em.createNamedQuery("MessageLogEntry.findUndeletedMessages");
        query.setParameter("MSH_ROLE", MSHRole.RECEIVING);
        query.setParameter("MESSAGE_TYPE", MessageType.USER_MESSAGE);
        //noinspection unchecked
        return query.getResultList();
    }

    public void setMessageAsSent(final String messageId) {
        this.setMessageStatus(messageId, MessageStatus.SENT);
    }

    public void setMessageAsFailed(final String messageId) {
        this.setMessageStatus(messageId, MessageStatus.FAILED);
    }

    public void setMessageAsSentWithWarnings(final String messageId) {
        this.setMessageStatus(messageId, MessageStatus.SENT_WITH_WARNINGS);
    }

    private void setMessageStatus(final String messageId, MessageStatus messageStatus) {

        final Query query = this.em.createNamedQuery("MessageLogEntry.setMessageStatus");
        query.setParameter("MESSAGE_ID", messageId);
        query.setParameter("TIMESTAMP", new Date());
        query.setParameter("MESSAGE_STATUS", messageStatus);
        final int result = query.executeUpdate();
        if (result != 1) {
            this.em.getTransaction().setRollbackOnly();
            BasicDao.LOG.error("Could not set message " + messageId + " as " + messageStatus);
        }
    }

    public MessageStatus getMessageStatus(final String messageId) {

        final TypedQuery<MessageStatus> query = this.em.createNamedQuery("MessageLogEntry.getMessageStatus", MessageStatus.class);
        query.setParameter("MESSAGE_ID", messageId);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return MessageStatus.MESSAGE_NOT_FOUND;
        }
    }

    public MessageLogEntry findByMessageId(String messageId, MSHRole mshRole) {
        final TypedQuery<MessageLogEntry> query = this.em.createNamedQuery("MessageLogEntry.findByMessageId", MessageLogEntry.class);
        query.setParameter("MESSAGE_ID", messageId);
        query.setParameter("MSH_ROLE", mshRole);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<MessageLogEntry> findPaged(int from, int max, String column, boolean asc, HashMap<String, Object> filters) {

        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<MessageLogEntry> cq = cb.createQuery(MessageLogEntry.class);
        Root<MessageLogEntry> mle = cq.from(MessageLogEntry.class);
        cq.select(mle);
        List<Predicate> predicates = new ArrayList<Predicate>();
        for (Map.Entry<String, Object> filter : filters.entrySet()) {
            if (filter.getValue() != null) {
                if (filter.getValue() instanceof String) {
                    if (!filter.getValue().toString().isEmpty()) {
                        switch (filter.getKey().toString()) {
                            case "receivedFrom":
                                predicates.add(cb.greaterThanOrEqualTo(mle.<Date>get("received"), Timestamp.valueOf(filter.getValue().toString())));
                                break;
                            case "receivedTo":
                                predicates.add(cb.lessThanOrEqualTo(mle.<Date>get("received"), Timestamp.valueOf(filter.getValue().toString())));
                                break;
                            default:
                                predicates.add(cb.like(mle.<String>get(filter.getKey()), (String) filter.getValue()));
                                break;
                        }
                    }
                } else {
                    predicates.add(cb.equal(mle.<String>get(filter.getKey()), filter.getValue()));
                }
            }
        }
        cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
        if (column != null) {
            if (asc) {
                cq.orderBy(cb.asc(mle.get(column)));
            } else {
                cq.orderBy(cb.desc(mle.get(column)));
            }

        }
        final TypedQuery<MessageLogEntry> query = this.em.createQuery(cq);
        query.setFirstResult(from);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public List<MessageLogEntry> findAll() {
        final TypedQuery<MessageLogEntry> query = this.em.createNamedQuery("MessageLogEntry.findEntries", MessageLogEntry.class);
        return query.getResultList();
    }

    public long countEntries() {
        final TypedQuery<Long> query = this.em.createNamedQuery("MessageLogEntry.countEntries", Long.class);
        return query.getSingleResult();
    }

    public List<String> getUndownloadedUserMessagesOlderThan(Date date, String mpc) {
        final TypedQuery<String> query = em.createNamedQuery("MessageLogEntry.findUndownloadedUserMessagesOlderThan", String.class);
        query.setParameter("DATE", date);
        query.setParameter("MPC", mpc);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.EMPTY_LIST;
        }
    }

    public List<String> getDownloadedUserMessagesOlderThan(Date date, String mpc) {
        final TypedQuery<String> query = em.createNamedQuery("MessageLogEntry.findDownloadedUserMessagesOlderThan", String.class);
        query.setParameter("DATE", date);
        query.setParameter("MPC", mpc);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.EMPTY_LIST;
        }
    }
}
