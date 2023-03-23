package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.Assert.*;

public class SubresourceDaoTest extends AbstractBaseDao {
    @Autowired
    SubresourceDao testInstance;

    @Before
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createSubresources();
    }

    @Test
    public void getSubResource() {
        Identifier suberesId = new Identifier(TEST_DOC_ID_1,TEST_DOC_SCHEMA_1 );
        Optional<DBSubresource> subresource = testInstance.getSubResource(suberesId,
                testUtilsDao.getResourceD1G1RD1(),TEST_SUBRESOURCE_DEF_SMP10 );

        assertTrue(subresource.isPresent());
    }

    @Test
    public void getSubResourceWrongResource() {
        Identifier suberesId = new Identifier(TEST_DOC_ID_1,TEST_DOC_SCHEMA_1 );
        Optional<DBSubresource> subresource = testInstance.getSubResource(suberesId,
                testUtilsDao.getResourceD2G1RD1(),TEST_SUBRESOURCE_DEF_SMP10 );

        assertFalse(subresource.isPresent());
    }

    @Test
    public void getSubResourcesForResource() {
        Identifier identifier = new Identifier(TEST_SG_ID_1,TEST_SG_SCHEMA_1 );

        List<DBSubresource> subresourceList =  testInstance.getSubResourcesForResource(identifier, TEST_SUBRESOURCE_DEF_SMP10);

        assertEquals(1, subresourceList.size());
    }
}
