/*-
 * #START_LICENSE#
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
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

public class SubresourceDaoTest extends AbstractBaseDao {
    @Autowired
    SubresourceDao testInstance;

    @BeforeEach
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createSubresources();
    }

    @Test
    public void getSubResource() {
        Identifier suberesId = new Identifier(TEST_DOC_ID_1,TEST_DOC_SCHEMA_1 );
        Optional<DBSubresource> subresource = testInstance.getSubResource(suberesId,
                testUtilsDao.getResourceD1G1RD1(), TEST_SUBRESOURCE_DEF_SMP10_URL);

        assertTrue(subresource.isPresent());
    }

    @Test
    public void getSubResourceWrongResource() {
        Identifier suberesId = new Identifier(TEST_DOC_ID_1,TEST_DOC_SCHEMA_1 );
        Optional<DBSubresource> subresource = testInstance.getSubResource(suberesId,
                testUtilsDao.getResourceD2G1RD1(), TEST_SUBRESOURCE_DEF_SMP10_URL);

        assertFalse(subresource.isPresent());
    }

    @Test
    public void getSubResourcesForResource() {
        Identifier identifier = new Identifier(TEST_SG_ID_1,TEST_SG_SCHEMA_1 );

        List<DBSubresource> subresourceList =  testInstance.getSubResourcesForResource(identifier, TEST_SUBRESOURCE_DEF_SMP10_ID);

        assertEquals(1, subresourceList.size());
    }
}
