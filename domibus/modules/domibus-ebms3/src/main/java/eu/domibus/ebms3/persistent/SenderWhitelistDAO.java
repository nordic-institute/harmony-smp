package eu.domibus.ebms3.persistent;


import eu.domibus.common.persistent.AbstractDAO;
import eu.domibus.common.persistent.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * DAO for senderWhitelist check. Contains Method to search for containing parties in the Table
 * Created by nowos01 on 09.05.14.
 */
public class SenderWhitelistDAO extends AbstractDAO<SenderWhitelist> {

    public SenderWhitelist findById(final String id) {
        final EntityManager em = JpaUtil.getEntityManager();
        final SenderWhitelist res = em.find(SenderWhitelist.class, id);
        em.close();
        return res;
    }

    /**
     * Method for sender party search for service/action combination. Also checks against wildcard elements.
     * @param PartyId String - PartyId of the sending Party
     * @param PartyIdType String - PartyIdType of the sending Party
     * @param Service String - Service name of the usermessage
     * @param Action String - Action name of the usermessage
     * @return count of found parties in table as long value
     */
    public long findWhitelistEntry(final String PartyId, final String PartyIdType, final String Service, final String Action) {
        final EntityManager em = JpaUtil.getEntityManager();
        final Query q = em.createNamedQuery("SenderWhitelist.findWhitelistEntry");
        q.setParameter("PARTY_ID", PartyId);
        q.setParameter("PARTY_ID_TYPE", PartyIdType);
        q.setParameter("SERVICE",Service);
        q.setParameter("ACTION",Action);

        final long res = (Long) q.getSingleResult();
        em.close();
        return res;
    }

}
