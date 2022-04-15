package eu.europa.ec.edelivery.smp.services.ui;


import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.ui.DeleteEntityValidation;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.*;


/**
 *  Purpose of class is to test UIDomainService base methods
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@ContextConfiguration(classes= UIDomainService.class)
public class UIDomainServiceIntegrationTest extends AbstractServiceIntegrationTest {
    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    @Autowired
    protected UIDomainService testInstance;

    protected void insertDataObjects(int size){
        for (int i=0; i < size; i++){
            DBDomain d = TestDBUtils.createDBDomain("domain"+i);
            domainDao.persistFlushDetach(d);
        }
    }

    @Test
    public void testGetTableListEmpty(){

        // given

        //when
        ServiceResult<DomainRO> res = testInstance.getTableList(-1,-1,null, null, null);
        // then
        assertNotNull(res);
        assertEquals(0, res.getCount().intValue());
        assertEquals(0, res.getPage().intValue());
        assertEquals(0, res.getPageSize().intValue());
        assertEquals(0, res.getServiceEntities().size());
        assertNull(res.getFilter());
    }

    @Test
    public void testGetTableList15(){

        // given
        insertDataObjects(15);
        //when
        ServiceResult<DomainRO> res = testInstance.getTableList(-1,-1,null, null,null);


        // then
        assertNotNull(res);
        assertEquals(15, res.getCount().intValue());
        assertEquals(0, res.getPage().intValue());
        assertEquals(15, res.getPageSize().intValue());
        assertEquals(15, res.getServiceEntities().size());
        assertNull(res.getFilter());

        // all table properties should not be null
        assertNotNull(res);
        assertNotNull(res.getServiceEntities().get(0).getDomainCode());
        assertNotNull(res.getServiceEntities().get(0).getSignatureKeyAlias());
        assertNotNull(res.getServiceEntities().get(0).getSmlClientKeyAlias());
        assertNotNull(res.getServiceEntities().get(0).getSmlSubdomain());
    }

    @Test
    public void validateDeleteRequest(){

        DeleteEntityValidation dev= new DeleteEntityValidation();
        dev.getListIds().add("10");
        DeleteEntityValidation res = testInstance.validateDeleteRequest(dev);
        assertEquals(true, res.isValidOperation());
    }

    @Test
    public void validateDeleteRequestNotToDelete(){
        // given
        DBDomain d = TestDBUtils.createDBDomain("domain");
        DBDomain d2 = TestDBUtils.createDBDomain("domain1");

        domainDao.persistFlushDetach(d);
        domainDao.persistFlushDetach(d2);

        DBServiceGroup sg = TestDBUtils.createDBServiceGroup();
        sg.addDomain(d);
        serviceGroupDao.persistFlushDetach(sg);

        // when
        DeleteEntityValidation dev= new DeleteEntityValidation();
        dev.getListIds().add(d.getId()+"");
        dev.getListIds().add(d2.getId()+"");

        DeleteEntityValidation res = testInstance.validateDeleteRequest(dev);

        // then
        assertEquals(false, res.isValidOperation());
        assertEquals(1, res.getListDeleteNotPermitedIds().size());
        assertEquals(d.getId()+"", res.getListDeleteNotPermitedIds().get(0));
        assertEquals("Could not delete domains used by Service groups! Domain: domain (domain ) uses by:1 SG.", res.getStringMessage());

    }
}
