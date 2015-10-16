package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractDAO;
import eu.domibus.common.persistent.JpaUtil;

import javax.persistence.EntityManager;

/**
 * DAO for SyncResponse
 */
public class SyncResponseDAO extends AbstractDAO<SyncResponse> {

    /**
     * {@inheritDoc}
     */
    @Override
    public SyncResponse findById(final String id) {
        final EntityManager em = JpaUtil.getEntityManager();
        final SyncResponse res = em.find(SyncResponse.class, id);
        em.close();
        return res;
    }
}
