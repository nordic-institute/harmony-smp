package eu.domibus.logging.persistent;

import eu.domibus.common.persistent.AbstractDAO;
import eu.domibus.common.persistent.JpaUtil;

import javax.persistence.EntityManager;

/**
 * DAO for LoggerMessage
 */
public class LoggerMessageDAO extends AbstractDAO<LoggerMessage> {

    /**
     * {@inheritDoc}
     */
    @Override
    public LoggerMessage findById(final String id) {
        final EntityManager em = JpaUtil.getEntityManager();
        final LoggerMessage res = em.find(LoggerMessage.class, id);
        em.close();
        return res;
    }
}
