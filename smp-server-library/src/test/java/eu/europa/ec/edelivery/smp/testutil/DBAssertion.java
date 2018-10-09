package eu.europa.ec.edelivery.smp.testutil;

import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroupDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *  Purpose of class is to test database data. Class is created as a bean so that
 *  annotated the method with @Transactional go through the proxy and no transaction is opened!
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class DBAssertion {

    @Autowired
    protected ServiceGroupDao serviceGroupDao;

    @Transactional
    public void assertServiceGroupForOnlyDomain(String partId, String partSchema, String domainCode){

        Optional<DBServiceGroup> optRes= serviceGroupDao.findServiceGroup(partId, partSchema);

        // then
        assertTrue(optRes.isPresent());
        DBServiceGroup dbServiceGroup = optRes.get();
        assertFalse(dbServiceGroup.getServiceGroupDomains().isEmpty());
        assertEquals(domainCode,dbServiceGroup.getServiceGroupDomains().get(0).getDomain().getDomainCode());
        assertEquals(partId,dbServiceGroup.getParticipantIdentifier());
        assertEquals(partSchema,dbServiceGroup.getParticipantScheme());
    }

    @Transactional
    public void assertServiceGroupExtensionEqual(String partId, String partSchema, String expectedExt){
        String ext = getExtensionForServiceGroup(partId, partSchema);
        assertEquals(expectedExt,ext);
    }

    @Transactional
    public String getExtensionForServiceGroup(String partId, String partSchema){
        DBServiceGroup sg= serviceGroupDao.findServiceGroup(partId, partSchema).get();
        return sg.getExtension();
    }

    @Transactional
    public Optional<DBServiceGroup> findAndInitServiceGroup(String partId, String partSchema){
        Optional<DBServiceGroup> sg= serviceGroupDao.findServiceGroup(partId, partSchema);
        if (sg.isPresent()){
            sg.get().getExtension();
            sg.get().getUsers().size();
            sg.get().getServiceGroupDomains().size();
            for (DBServiceGroupDomain d:sg.get().getServiceGroupDomains() ){
                d.getDomain().getDomainCode();
                d.getServiceMetadata().size();

            }

        }
        return sg;
    }
}
