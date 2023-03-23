package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupSearchRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


@Ignore
@ContextConfiguration(classes = {UIServiceGroupSearchService.class, UIServiceMetadataService.class})
public class UIServiceGroupSearchServiceTest extends AbstractServiceIntegrationTest {

    @Autowired
    protected UIServiceGroupSearchService testInstance;

    @Autowired
    protected UIServiceMetadataService uiServiceMetadataService;


    protected void insertDataObjectsForOwner(int size, DBUser owner) {
        for (int i = 0; i < size; i++) {
            insertServiceGroup(String.format("%4d", i), true, owner);
        }
    }

    protected void insertDataObjects(int size) {
        insertDataObjectsForOwner(size, null);
    }

    protected DBResource insertServiceGroup(String id, boolean withExtension, DBUser owner) {
        DBResource d = TestDBUtils.createDBResource(String.format("0007:%s:utest", id), TestConstants.TEST_SG_SCHEMA_1, withExtension);
        if (owner != null) {
           // d.getUsers().add(owner);
        }
        serviceGroupDao.persistFlushDetach(d);
        return d;
    }

    @Test
    public void testGetTableListEmpty() {

        // given

        //when
        ServiceResult<ServiceGroupSearchRO> res = testInstance.getTableList(-1, -1, null, null, null);
        // then
        assertNotNull(res);
        assertEquals(0, res.getCount().intValue());
        assertEquals(0, res.getPage().intValue());
        assertEquals(-1, res.getPageSize().intValue());
        assertEquals(0, res.getServiceEntities().size());
        assertNull(res.getFilter());
    }

    @Test
    public void testGetTableList15() {

        // given
        insertDataObjects(15);
        //when
        ServiceResult<ServiceGroupSearchRO> res = testInstance.getTableList(-1, -1, null, null, null);


        // then
        assertNotNull(res);
        assertEquals(15, res.getCount().intValue());
        assertEquals(0, res.getPage().intValue());
        assertEquals(-1, res.getPageSize().intValue());
        assertEquals(15, res.getServiceEntities().size());
        assertNull(res.getFilter());

        // all table properties should not be null
        assertNotNull(res);
        assertNotNull(res.getServiceEntities().get(0).getParticipantIdentifier());
        assertNotNull(res.getServiceEntities().get(0).getParticipantScheme());
    }


    @Test
    public void convertToRo() {
        // given
        DBResource sg = TestDBUtils.createDBResource();
        // then when
        ServiceGroupSearchRO sgr = testInstance.convertToRo(sg);
        // then
        assertEquals(sg.getId(), sgr.getId());
        assertEquals(sg.getIdentifierScheme(), sgr.getParticipantScheme());
        assertEquals(sg.getIdentifierValue(), sgr.getParticipantIdentifier());
    }
}
