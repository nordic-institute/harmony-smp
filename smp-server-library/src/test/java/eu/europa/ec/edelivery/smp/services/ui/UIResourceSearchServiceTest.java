/*-
 * #%L
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #L%
 */
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
