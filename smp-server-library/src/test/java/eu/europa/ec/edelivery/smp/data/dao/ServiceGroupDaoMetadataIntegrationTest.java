package eu.europa.ec.edelivery.smp.data.dao;

import org.junit.Ignore;


/**
 *  Purpose of class is to test all resource methods with database.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@Ignore
public class ServiceGroupDaoMetadataIntegrationTest extends AbstractResourceDaoTest {

/*
    @Test
    @Transactional

    public void persistNewServiceGroupWithMetadata() {
        //  given
        DBDomain d= domainDao.getDomainByCode(TEST_DOMAIN_CODE_1).get();

        DBResource sg = new DBResource();
        sg.setIdentifierValue(TEST_SG_ID_1);
        sg.setIdentifierScheme(TEST_SG_SCHEMA_1);
        sg.addDomain(d);

        DBSubresource md = TestDBUtils.createDBSubresource(TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        sg.getResourceDomains().get(0).addServiceMetadata(md);

        // when
        testInstance.persistFlushDetach(sg);


        // then
        DBResource res = testInstance.findServiceGroup(TEST_SG_ID_1, TEST_SG_SCHEMA_1 ).get();
        assertTrue(sg!=res); // test different object instance
        assertNotNull(res.getId());
        assertEquals(TEST_SG_ID_1, res.getIdentifierValue()); // test equal method - same entity
        assertEquals(TEST_SG_SCHEMA_1, res.getIdentifierScheme()); // test equal method - same entity
        assertEquals(1, res.getResourceDomains().size()); // domain must be loaded
        assertEquals(d.getDomainCode(), res.getResourceDomains().get(0).getDomain().getDomainCode()); // test loaded Domain

    }

    @Test
    @Transactional
    public void addMetadataToServiceGroup() {
        // given
        DBResource sg = createAndSaveNewServiceGroup();
        DBResource res = testInstance.findServiceGroup(sg.getIdentifierValue(), sg.getIdentifierScheme()).get();
        DBSubresource md = TestDBUtils.createDBSubresource(TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        //when
        res.getResourceDomains().get(0).addServiceMetadata(md);
        assertNotNull(md.getXmlContent());
        update(res);
        testInstance.clearPersistenceContext();
        // then
        DBResource res2 = testInstance.findServiceGroup(sg.getIdentifierValue(), sg.getIdentifierScheme()).get();
        assertNotNull(res2);
        assertEquals(1, res2.getResourceDomains().get(0).getSubresourcesList().size());
        assertTrue(Arrays.equals(md.getXmlContent(), res2.getResourceDomains().get(0).getSubresourcesList().get(0).getXmlContent()));
    }

    @Test
    @Transactional
    public void updateServiceMetadataXML() {
        // given
        DBResource sg = createAndSaveNewServiceGroupWithMetadata();
        DBResource res = testInstance.findServiceGroup(sg.getIdentifierValue(), sg.getIdentifierScheme()).get();
        DBSubresource md = res.getResourceDomains().get(0).getSubresource(0);

        byte[]  str = TestDBUtils.generateDocumentSample(sg.getIdentifierValue(),sg.getIdentifierScheme(),
                md.getDocumentIdentifier(),md.getDocumentIdentifierScheme(),UUID.randomUUID().toString());
        assertNotEquals (str, md.getXmlContent());
        //when
        res.getResourceDomains().get(0).getSubresource(0).setXmlContent(str);
        update(res);

        testInstance.clearPersistenceContext();
        // then
        DBResource res2 = testInstance.findServiceGroup(sg.getIdentifierValue(), sg.getIdentifierScheme()).get();
        assertNotNull(res2);
        assertEquals(1, res2.getResourceDomains().get(0).getSubresourcesList().size());
        assertTrue(Arrays.equals(str, res2.getResourceDomains().get(0).getSubresourcesList().get(0).getXmlContent()));

    }

    @Test
    @Transactional
    public void removeServiceMetadata() {
        // given
        DBResource sg = createAndSaveNewServiceGroupWithMetadata();
        DBResource res = testInstance.findServiceGroup(sg.getIdentifierValue(), sg.getIdentifierScheme()).get();

        assertEquals(1, res.getResourceDomains().get(0).getSubresourcesList().size());
        DBSubresource dsmd = res.getResourceDomains().get(0).getSubresourcesList().get(0);

        // when
        res.getResourceDomains().get(0).removeServiceMetadata(dsmd);
        testInstance.update(res);
        testInstance.clearPersistenceContext();
        DBResource res2 = testInstance.findServiceGroup(sg.getIdentifierValue(), sg.getIdentifierScheme()).get();

        // then
        assertEquals(0, res.getResourceDomains().get(0).getSubresourcesList().size());
    }

    @Test
    public void removeDBServiceGroupWithServiceMetadata() {
      // given
        DBResource sg = createAndSaveNewServiceGroupWithMetadata();
        Optional<DBResource> resOpt1 = testInstance.findServiceGroup(sg.getIdentifierValue(), sg.getIdentifierScheme());
        assertTrue(resOpt1.isPresent());

        // when
        testInstance.removeServiceGroup(resOpt1.get());
        testInstance.clearPersistenceContext();

        // then
        Optional<DBResource> resOptDS = testInstance.findServiceGroup(sg.getIdentifierValue(), sg.getIdentifierScheme());
        assertFalse(resOptDS.isPresent());
    }

 */
}
