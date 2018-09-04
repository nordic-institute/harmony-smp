/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.config.H2JPATestConfiguration;
import eu.europa.ec.edelivery.smp.data.model.*;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.persistence.*;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;
import static eu.europa.ec.edelivery.smp.testutil.AuditUtils.*;


/**
 * Created by rihtajo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {H2JPATestConfiguration.class})
public class AuditIntegrationTest {

    // because envers creates audit on commit we user PersistenceUnit to control commit...
    // (instead of  PersistenceContext and transaction annotations... )
    @PersistenceUnit
    EntityManagerFactory emf;

    @Before
    public void before() throws IOException {
        clearDatabase();
    }

    @After
    public void after() throws IOException {
        clearDatabase();
    }


    private void clearDatabase(){

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        clearTable(em,"smp_service_metadata", "businessIdentifier='"+REVISION_BUSSINESS_ID+"'" +
                " AND businessIdentifierScheme='"+REVISION_BUSSINESS_SCH+"'" +
                " AND documentIdentifier='"+REVISION_DOCUMENT_ID+"'" +
                " AND documentIdentifierScheme='"+REVISION_DOCUMENT_SCH+"'"
        );

        clearTable(em,"smp_ownership", "businessIdentifier='"+REVISION_BUSSINESS_ID+"'" +
                " AND businessIdentifierScheme='"+REVISION_BUSSINESS_SCH+"'" +
                " AND  username='"+REVISION_USER+"'"
        );

        clearTable(em,"smp_service_group", "businessIdentifier='"+REVISION_BUSSINESS_ID
                +"' AND businessIdentifierScheme='"+REVISION_BUSSINESS_SCH+"'");

        clearTable(em,"smp_user", "username='"+REVISION_USER+"'");
        clearTable(em,"smp_domain", "domainId='"+REVISION_DOMAIN+"'");

        em.getTransaction().commit();
    }

    public void clearTable(EntityManager em, String tableName, String condition){

        System.out.printf(String.format("DELETE FROM %s WHERE %s", tableName, condition));
        System.out.printf(String.format("DELETE FROM %s_AUD WHERE %s", tableName, condition));
        Query qTable = em.createNativeQuery(String.format("DELETE FROM %s WHERE %s", tableName, condition));
        Query qTableAud = em.createNativeQuery(String.format("DELETE FROM %s_AUD WHERE %s", tableName, condition));
        qTable.executeUpdate();
        qTableAud.executeUpdate();
    }

    @Test
    public void testClassesForAudit() throws IOException, JAXBException {
        AuditReader ar = AuditReaderFactory.get(emf.createEntityManager());
        assertTrue(ar.isEntityClassAudited(DBServiceGroup.class));
        assertTrue(ar.isEntityClassAudited(DBServiceMetadata.class));
        assertTrue(ar.isEntityClassAudited(DBOwnership.class));
        assertTrue(ar.isEntityClassAudited(DBDomain.class));
        assertTrue(ar.isEntityClassAudited(DBUser.class));
    }


    @Test
    public void testAuditDBServiceGroup() {

        DBServiceGroup grp = createDBServiceGroup();

        EntityManager em = emf.createEntityManager();
        persist(em, grp.getDomain());
        Map<String, Object> alterVal = new HashMap<>();
        alterVal.put("Extension", UUID.randomUUID().toString());

        testAuditEntity(DBServiceGroup.class, grp.getId(),grp,alterVal );
    }

    @Test
    public void testAuditDBMetaData() {

        DBServiceMetadata md = createDBServiceMetadata();
        EntityManager em = emf.createEntityManager();
        persist(em, md.getServiceGroup().getDomain());
        persist(em, md.getServiceGroup());
        Map<String, Object> alterVal = new HashMap<>();
        alterVal.put("XmlContent", UUID.randomUUID().toString());

        testAuditEntity(DBServiceMetadata.class, md.getId(),md,alterVal );
    }

    @Test
    public void testAuditDBUser() {

        DBUser dbuser = createDBUser();
        Map<String, Object> alterVal = new HashMap<>();
        alterVal.put("Password", UUID.randomUUID().toString());

        testAuditEntity(DBUser.class, REVISION_USER,dbuser,alterVal );
    }

    @Test
    public void testAuditDBOwnership() {

        DBOwnership owsh = createDBOwnership();
        EntityManager em = emf.createEntityManager();
        persist(em, owsh.getServiceGroup().getDomain());
        persist(em, owsh.getServiceGroup());
        persist(em, owsh.getUser());

        testAuditEntity(DBOwnership.class, owsh.getId(),owsh,null );
    }

    @Test
    public void testAuditDBDomain() {

        DBDomain domain = createDBDomain();
        Map<String, Object> alterVal = new HashMap<>();
        alterVal.put("BdmslSmpId", UUID.randomUUID().toString());
        alterVal.put("BdmslClientCertAlias", UUID.randomUUID().toString());

        testAuditEntity(DBDomain.class, REVISION_DOMAIN,domain,alterVal );
    }




    private void testAuditEntity(Class cls, Object id, Object entity, Map<String, Object> alterValues ) {
        EntityManager em = emf.createEntityManager();

        AuditReader ar = AuditReaderFactory.get(em);
        // persist
        persist(em, entity);
        int iRevSize = ar.getRevisions(cls, id).size();
        // update
        if (alterValues != null && !alterValues.isEmpty()) {
            alterValues.forEach((prop, val) -> {
                reflectionSetFiled(cls, entity, prop, val);
            });
            update(em, entity);
            assertEquals(++iRevSize, ar.getRevisions(cls, id).size());
        }

        // remove
        remove(em, cls, id);
        assertEquals(++iRevSize,ar.getRevisions(cls, id ).size());
    }

   private void persist(EntityManager em, Object dbEnetity){
       em.getTransaction().begin();
       em.persist(dbEnetity);
       em.getTransaction().commit();
   }

    private void update(EntityManager em, Object dbEntity){
        em.getTransaction().begin();
        em.merge(dbEntity);
        em.getTransaction().commit();
    }

    private void remove(EntityManager em, Class cls,  Object dbId){
        em.getTransaction().begin();
        // get attached reference to delete it
        Object dbEntity = em.getReference(cls, dbId);

        em.remove(dbEntity);
        em.getTransaction().commit();
    }
}
