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
package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.config.H2JPATestConfig;
import eu.europa.ec.edelivery.smp.data.model.*;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.testutil.TestDBUtils.createDBDomain;
import static org.junit.Assert.assertTrue;

/**
 *  Purpose of class is to test all Audit classes and  methods with database.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {H2JPATestConfig.class})
@Sql(scripts = "classpath:cleanup-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig
        (transactionMode = SqlConfig.TransactionMode.ISOLATED,
                transactionManager = "transactionManager",
                dataSource = "h2DataSource"))
public class AuditIntegrationTest {

    // because envers creates audit on commit we user PersistenceUnit to control commit...
    // (instead of  PersistenceContext and transaction annotations... )
    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void testClassesForAudit() {
        AuditReader ar = AuditReaderFactory.get(emf.createEntityManager());
        assertTrue(ar.isEntityClassAudited(DBServiceGroup.class));
        assertTrue(ar.isEntityClassAudited(DBServiceMetadata.class));
        assertTrue(ar.isEntityClassAudited(DBDomain.class));
        assertTrue(ar.isEntityClassAudited(DBUser.class));
        assertTrue(ar.isEntityClassAudited(DBCertificate.class));
        assertTrue(ar.isEntityClassAudited(DBServiceGroupExtension.class));
    }

    @Test
    public void testAuditDBDomain() {

        DBDomain domain = createDBDomain();
        Map<String, Object> alterVal = new HashMap<>();
        alterVal.put("signatureKeyAlias", UUID.randomUUID().toString());
        alterVal.put("smlClientCertHeader", UUID.randomUUID().toString());
        alterVal.put("smlClientKeyAlias", UUID.randomUUID().toString());
        alterVal.put("smlSubdomain", UUID.randomUUID().toString());

        testAuditEntity(domain,alterVal );
    }

  /*  @Test
    public void testAuditDBServiceGroup() {

        DBServiceGroup grp = createDBServiceGroup();

        EntityManager em = emf.createEntityManager();
        persist(em, grp.getDomain());
        Map<String, Object> alterVal = new HashMap<>();
        alterVal.put("extension", UUID.randomUUID().toString());

        testAuditSubEntity(grp, grp.getServiceGroupExtension(),alterVal );
    }

    @Test
    public void testAuditDBUser() {

        DBUser dbuser = createDBUser();
        Map<String, Object> alterVal = new HashMap<>();
        alterVal.put("password", UUID.randomUUID().toString());
        alterVal.put("role", UUID.randomUUID().toString());
        alterVal.put("passwordChanged", LocalDateTime.now());

        testAuditEntity(dbuser,alterVal );

    }
    @Test
    public void testAuditDBUserWithCertificate() {

        DBUser dbuser = createDBUser();
        DBCertificate cert = createDBCertificate();
        dbuser.setCertificate(cert);
        Map<String, Object> alterValCert = new HashMap<>();
        alterValCert.put("certificateId", UUID.randomUUID().toString());
        alterValCert.put("validFrom", LocalDateTime.now());
        alterValCert.put("validTo", LocalDateTime.now());


        testAuditSubEntity(dbuser,dbuser.getCertificate(), alterValCert );
    }

/*
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

*/

    /**
     * Method updates value in Map, then checks if revision increased. Last testi in removing the entity.
     * @param entity
     * @param alterValues
     */
    private void testAuditEntity(BaseEntity entity, Map<String, Object> alterValues ) {
        testAuditSubEntity(entity, entity, alterValues);
        EntityManager em = emf.createEntityManager();
    }

    /**
     * Method tests altering of subentity parameters. Update and remove is done on master entity
     *
     * @param entity
     * @param subEntity
     * @param alterValues
     */
    private void testAuditSubEntity(BaseEntity entity, BaseEntity subEntity, Map<String, Object> alterValues ) {
        EntityManager em = emf.createEntityManager();

        AuditReader ar = AuditReaderFactory.get(em);
        // persist
        persist(em, entity);
        Object dbId = subEntity.getId();

        int iRevSize = ar.getRevisions(subEntity.getClass(), dbId).size();
        // update
        if (alterValues != null && !alterValues.isEmpty()) { // set value to detail
            alterValues.forEach((prop, val) -> {
                ReflectionTestUtils.invokeSetterMethod(subEntity, prop, val, val.getClass());
            });
            update(em, entity); // master
            Assert.assertEquals(++iRevSize, ar.getRevisions(subEntity.getClass(), dbId).size());
        }

        // remove master
        remove(em, entity.getClass(), dbId);
        Assert.assertEquals(++iRevSize,ar.getRevisions(subEntity.getClass(), dbId ).size());
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
