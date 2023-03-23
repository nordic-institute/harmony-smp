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

import eu.europa.ec.edelivery.smp.data.enums.CredentialTargetType;
import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.model.*;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBServiceGroupExtension;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.model.user.DBCertificate;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertStatusEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.testutil.TestDBUtils.*;
import static org.junit.Assert.assertTrue;

/**
 * Purpose of class is to test all Audit classes and  methods with database.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class AuditIntegrationTest extends AbstractBaseDao{
    private static final Logger LOG = LoggerFactory.getLogger(AuditIntegrationTest.class);

    // because envers creates audit on commit we use PersistenceUnit to control commit...
    // (instead of  PersistenceContext and transaction annotations... )
    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void testClassesForAudit() {
        AuditReader ar = AuditReaderFactory.get(emf.createEntityManager());
        assertTrue(ar.isEntityClassAudited(DBResource.class));
        assertTrue(ar.isEntityClassAudited(DBSubresource.class));
        assertTrue(ar.isEntityClassAudited(DBDomain.class));
        assertTrue(ar.isEntityClassAudited(DBUser.class));
        assertTrue(ar.isEntityClassAudited(DBCertificate.class));
        assertTrue(ar.isEntityClassAudited(DBServiceGroupExtension.class));
        assertTrue(ar.isEntityClassAudited(DBAlert.class));
    }

    @Test
    public void testAuditDBDomain() {

        DBDomain domain = createDBDomain();
        Map<String, Object> alterVal = new HashMap<>();
        alterVal.put("signatureKeyAlias", UUID.randomUUID().toString());
        alterVal.put("smlClientCertHeader", UUID.randomUUID().toString());
        alterVal.put("smlClientKeyAlias", UUID.randomUUID().toString());
        alterVal.put("smlSubdomain", UUID.randomUUID().toString());

        testAuditEntity(domain, alterVal);
    }

    @Test
    public void testAuditDBAlert() {

        DBAlert dbAlert = createDBAlert();
        Map<String, Object> alterVal = new HashMap<>();
        alterVal.put("alertType", AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION);
        alterVal.put("alertStatus", AlertStatusEnum.FAILED);
        testAuditEntity(dbAlert, alterVal);
    }

    @Test
    public void testAuditDBUser() {

        DBUser dbuser = createDBUser(UUID.randomUUID().toString());
        Map<String, Object> alterVal = new HashMap<>();
        alterVal.put("username", UUID.randomUUID().toString());
        alterVal.put("emailAddress", UUID.randomUUID().toString());
        testAuditEntity(dbuser, alterVal);
    }

    @Test
    public void testAuditDBCredentials() {
        DBUser user = createDBUser("Credential-test");
        persist(user);

        DBCredential dbCredential = createDBCredential();
        dbCredential.setUser(user);
        Map<String, Object> alterVal = new HashMap<>();
        alterVal.put("name", UUID.randomUUID().toString());
        alterVal.put("value", UUID.randomUUID().toString());
        alterVal.put("credentialType", CredentialType.CAS);
        alterVal.put("credentialTarget", CredentialTargetType.REST_API);
        alterVal.put("activeFrom", OffsetDateTime.now().plusMinutes(30));
        alterVal.put("changedOn", OffsetDateTime.now().plusMinutes(30));
        alterVal.put("expireAlertOn", OffsetDateTime.now().plusMinutes(30));
        alterVal.put("activeFrom", OffsetDateTime.now().plusMinutes(30));
        alterVal.put("sequentialLoginFailureCount", 10);

        testAuditEntity(dbCredential, alterVal);
    }

    @Test
    public void testAuditDBCredentialsWithCertificate() {
        DBUser dbuser = createDBUser(UUID.randomUUID().toString());
        persist(dbuser);

        DBCertificate cert = createDBCertificate();
        DBCredential dbCredential = createDBCredential(dbuser, cert.getCertificateId(), null, CredentialType.CERTIFICATE, CredentialTargetType.REST_API);
        dbCredential.setCertificate(cert);

        Map<String, Object> alterValCert = new HashMap<>();
        alterValCert.put("certificateId", UUID.randomUUID().toString());
        alterValCert.put("validFrom", OffsetDateTime.now());
        alterValCert.put("validTo", OffsetDateTime.now());
        testAuditSubEntity(dbCredential, cert, alterValCert);
    }



    @Test
    public void testAuditDBResource() {
        DBResource resource = createDBResource();
        resource.setDocument(createDBDocument());
        Map<String, Object> alterVal = new HashMap<>();
        alterVal.put("extension", UUID.randomUUID().toString().getBytes());
        testAuditSubEntity(resource, resource.getServiceGroupExtension(), alterVal);
    }

    /**
     * Method updates value in Map, then checks if revision increased. Last test in removing the entity.
     *
     * @param entity
     * @param alterValues
     */
    private void testAuditEntity(BaseEntity entity, Map<String, Object> alterValues) {
        testAuditSubEntity(entity, entity, alterValues);
    }

    /**
     * Method tests altering of sub-entity parameters. Update and remove is done on master entity
     *
     * @param entity
     * @param subEntity
     * @param alterValues
     */
    private void testAuditSubEntity(BaseEntity entity, BaseEntity subEntity, Map<String, Object> alterValues) {
        EntityManager em = emf.createEntityManager();

        AuditReader ar = AuditReaderFactory.get(em);
        // persist
        persist(em, entity);
        Object dbId = subEntity.getId();
        LOG.info("Store main entity with id [{}]", dbId);
        int iRevSize = ar.getRevisions(subEntity.getClass(), dbId).size();
        // update
        if (alterValues != null && !alterValues.isEmpty()) { // set value to detail
            alterValues.forEach((prop, val) -> {
                ReflectionTestUtils.setField(subEntity, prop, val, val.getClass());
            });
            update(em, entity); // master
            Assert.assertEquals(++iRevSize, ar.getRevisions(subEntity.getClass(), dbId).size());
        }
        // remove master
        remove(em, entity.getClass(), entity.getId());
        Assert.assertEquals(++iRevSize, ar.getRevisions(subEntity.getClass(), dbId).size());
        em.close();
    }

    private void persist(EntityManager em, Object dbEntity) {
        em.getTransaction().begin();
        em.persist(dbEntity);
        em.getTransaction().commit();
    }

    private void persist(Object dbEntity) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(dbEntity);
        em.getTransaction().commit();
        em.close();
    }

    private void update(EntityManager em, Object dbEntity) {
        em.getTransaction().begin();
        em.merge(dbEntity);
        em.getTransaction().commit();
    }

    private void remove(EntityManager em, Class cls, Object dbId) {
        em.getTransaction().begin();
        // get attached reference to delete it
        Object dbEntity = em.getReference(cls, dbId);

        em.remove(dbEntity);
        em.getTransaction().commit();
    }
}
