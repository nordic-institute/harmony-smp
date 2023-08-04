package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupSearchRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.services.ui.filters.ResourceFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class UIResourceSearchServiceTest extends AbstractJunit5BaseDao {


    @Autowired
    protected UIResourceSearchService testInstance;

    @BeforeEach
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createSubresources();
    }

    @Test
    public void testGetTableList() {
        ResourceFilter filter = new ResourceFilter();
        ServiceResult<ServiceGroupSearchRO> result = testInstance.getTableList(-1, -1, null, null, filter);
        assertNotNull(result);
        assertEquals(2, result.getCount().intValue());
    }

    @Test
    public void testGetTableListWithFilter() {
        ResourceFilter filter = new ResourceFilter();
        filter.setIdentifierValueLike(testUtilsDao.getResourceD1G1RD1().getIdentifierValue());

        ServiceResult<ServiceGroupSearchRO> result = testInstance.getTableList(-1, -1, null, null, filter);
        assertNotNull(result);
        assertEquals(1, result.getCount().intValue());
    }

}
