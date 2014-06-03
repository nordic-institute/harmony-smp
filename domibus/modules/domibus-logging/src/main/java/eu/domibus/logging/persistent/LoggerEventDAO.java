package eu.domibus.logging.persistent;

import eu.domibus.common.persistent.AbstractDAO;
import eu.domibus.common.persistent.JpaUtil;

import javax.persistence.EntityManager;

public class LoggerEventDAO extends AbstractDAO<LoggerEvent> {
    @Override
    public LoggerEvent findById(final String id) {
        final EntityManager em = JpaUtil.getEntityManager();
        final LoggerEvent res = em.find(LoggerEvent.class, id);
        em.close();
        return res;
    }
}
