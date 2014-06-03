package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractDAO;
import eu.domibus.common.persistent.JpaUtil;

import javax.persistence.EntityManager;

/**
 * DAO for ErrorMessage entities.
 */

public class ErrorMessageDAO extends AbstractDAO<ErrorMessage> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorMessage findById(String id) {
        final EntityManager em = JpaUtil.getEntityManager();
        return em.find(ErrorMessage.class, id);
    }
}
