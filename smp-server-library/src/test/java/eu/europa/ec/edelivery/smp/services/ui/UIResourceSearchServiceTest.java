package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.config.ConversionTestConfig;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupSearchRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.services.ui.filters.ResourceFilter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@ContextConfiguration(classes = {UIResourceSearchService.class, ConversionTestConfig.class})
public class UIResourceSearchServiceTest extends AbstractServiceIntegrationTest {


    @Autowired
    protected UIResourceSearchService testInstance;

    @Before
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
