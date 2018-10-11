package eu.europa.ec.edelivery.smp.services.ui;


import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.*;


/**
 *  Purpose of class is to test ServiceGroupService base methods
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@ContextConfiguration(classes= UIUserService.class)
public class UIUserServiceIntegrationTest extends AbstractServiceIntegrationTest {
    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    @Autowired
    protected UIUserService testInstance;

    protected void insertDataObjects(int size){
        for (int i=0; i < size; i++){
            DBUser d = TestDBUtils.createDBUserByUsername("user"+i);
            userDao.persistFlushDetach(d);
        }
    }

    @Test
    public void testGetTableListEmpty(){

        // given

        //when
        ServiceResult<UserRO> res = testInstance.getTableList(-1,-1,null, null);
        // then
        assertNotNull(res);
        assertEquals(0, res.getCount().intValue());
        assertEquals(0, res.getPage().intValue());
        assertEquals(-1, res.getPageSize().intValue());
        assertEquals(0, res.getServiceEntities().size());
        assertNull(res.getFilter());
    }

    @Test
    public void testGetTableList15(){

        // given
        insertDataObjects(15);
        //when
        ServiceResult<UserRO> res = testInstance.getTableList(-1,-1,null, null);


        // then
        assertNotNull(res);
        assertEquals(15, res.getCount().intValue());
        assertEquals(0, res.getPage().intValue());
        assertEquals(-1, res.getPageSize().intValue());
        assertEquals(15, res.getServiceEntities().size());
        assertNull(res.getFilter());

        // all table properties should not be null
        assertNotNull(res);
        assertNotNull(res.getServiceEntities().get(0).getUsername());
        assertNotNull(res.getServiceEntities().get(0).getEmail());
        assertNotNull(res.getServiceEntities().get(0).getPassword());
        assertNotNull(res.getServiceEntities().get(0).getPasswordChanged());
        assertNotNull(res.getServiceEntities().get(0).getRole());
    }
}
