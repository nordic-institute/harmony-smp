package eu.europa.ec.edelivery.smp.testutil;

import eu.europa.ec.edelivery.smp.data.enums.CredentialTargetType;
import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.model.DBAlert;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocument;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocumentVersion;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.model.ext.DBExtension;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;
import eu.europa.ec.edelivery.smp.data.model.user.DBCertificate;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertLevelEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertStatusEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;

import java.time.OffsetDateTime;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;

public class TestDBUtils {

    public static DBDomain createDBDomain(String domainCode) {
        DBDomain domain = new DBDomain();
        domain.setDomainCode(domainCode);
        domain.setSignatureKeyAlias(anyString());
        domain.setSmlClientCertHeader(anyString());
        domain.setSmlClientKeyAlias(anyString());
        domain.setSmlSubdomain(anyString());
        domain.setSmlSmpId(anyString());
        return domain;
    }

    public static DBGroup createDBGroup(String groupName) {
        DBGroup group = new DBGroup();
        group.setGroupName(groupName);
        group.setGroupDescription(anyString());
        group.setVisibility(VisibilityType.PUBLIC);
        return group;
    }

    public static DBExtension createDBExtension(String implName) {
        DBExtension entity = new DBExtension();
        entity.setImplementationName(implName);
        entity.setName(anyString());
        entity.setDescription(anyString());
        entity.setVersion(anyString());
        entity.setExtensionType(anyString());
        return entity;
    }

    public static DBSubresourceDef createDBSubresourceDef(String identifier, String urlSegment) {
        DBSubresourceDef entity = new DBSubresourceDef();
        entity.setIdentifier(identifier);
        entity.setUrlSegment(urlSegment);
        entity.setName(anyString());
        entity.setDescription(anyString());

        return entity;
    }

    public static DBResourceDef createDBResourceDef(String identifier, String urlSegment) {
        DBResourceDef entity = new DBResourceDef();
        entity.setIdentifier(identifier);
        entity.setUrlSegment(urlSegment);
        entity.setName(anyString());
        entity.setDescription(anyString());
        entity.setMimeType(anyString());
        return entity;
    }

    public static DBResourceDef createDBResourceDef(String identifier) {
       return createDBResourceDef(identifier, anyString());
    }

    public static DBAlert createDBAlert(String username) {
        return createDBAlert(username, "mail-subject", "mail.to@test.eu", AlertLevelEnum.MEDIUM, AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION);
    }

    public static DBAlert createDBAlert(String username, String mailSubject,
                                        String mailTo,
                                        AlertLevelEnum level,
                                        AlertTypeEnum alertType) {
        DBAlert alert = new DBAlert();
        alert.setMailSubject(mailSubject);
        alert.setMailTo(mailTo);
        alert.setUsername(username);
        alert.setReportingTime(OffsetDateTime.now());
        alert.setAlertType(alertType);
        alert.setAlertLevel(level);
        alert.setAlertStatus(AlertStatusEnum.PROCESS);
        alert.addProperty("prop1", "propValue1");
        alert.addProperty("prop2", "propValue2");
        return alert;
    }

    public static DBGroup createDBGroup() {
        return createDBGroup(TestConstants.TEST_GROUP_A);
    }

    public static DBDomain createDBDomain() {
        return createDBDomain(TestConstants.TEST_DOMAIN_CODE_1);
    }

    public static DBResource createDBResource() {
        return createDBResource(TestConstants.TEST_SG_ID_1, TestConstants.TEST_SG_SCHEMA_1);
    }

    public static DBSubresource createDBSubresource(String partcId, String partcSch) {
        return createDBSubresource(partcId, partcSch, anyString(), anyString(), anyString());
    }

    public static DBSubresource createDBSubresource(String partcId, String partcSch, String docId, String docSch) {
        return createDBSubresource(partcId, partcSch, docId, docSch, anyString());
    }

    public static DBSubresource createDBSubresource(String partcId, String partcSch, String docId, String docSch, String desc) {
        DBSubresource grp = new DBSubresource();
        grp.setIdentifierValue(docId);
        grp.setIdentifierScheme(docSch);

        return grp;
    }

    public static DBSubresource createDBSubresourceRedirect(String docId, String docSch, String url) {
        DBSubresource grp = new DBSubresource();
        grp.setIdentifierValue(docId);
        grp.setIdentifierScheme(docSch);

        return grp;
    }

    public static byte[] generateDocumentSample(String partcId, String partcSch, String docId, String docSch, String desc) {
        return String.format(SIMPLE_DOCUMENT_XML, partcSch, partcId, docSch, docId, desc).getBytes();
    }

    public static byte[] generateExtension() {
        return String.format(SIMPLE_EXTENSION_XML, anyString()).getBytes();
    }

    public static byte[] generateRedirectDocumentSample(String url) {
        return String.format(SIMPLE_REDIRECT_DOCUMENT_XML, url).getBytes();

    }

    public static DBResource createDBResource(String id, String sch) {
        return createDBResource(id, sch, true);
    }


    public static DBResource createDBResource(String id, String sch, boolean withExtension) {
        DBResource resource = new DBResource();
        resource.setIdentifierValue(id);
        resource.setIdentifierScheme(sch);
        if (withExtension) {
            DBDocument document = createDBDocument();
            DBDocumentVersion documentVersion = createDBDocumentVersion();
            createDBDocumentVersion().setContent(generateExtension());
            document.addNewDocumentVersion(documentVersion);
            resource.setDocument(document);
        }
        return resource;
    }

    public static DBDocument createDBDocument() {
        DBDocument doc = new DBDocument();
        doc.setMimeType("application/xml");
        doc.setName(anyString());
        return doc;
    }

    public static DBDocumentVersion createDBDocumentVersion() {
        DBDocumentVersion docuVersion = new DBDocumentVersion();
        docuVersion.setContent(anyString().getBytes());
        return docuVersion;
    }

    public static DBUser createDBUser(String username1) {
        return createDBUserByUsername(username1);
    }


    public static DBCredential createDBCredential() {
        return createDBCredential("name", "value", CredentialType.USERNAME_PASSWORD, CredentialTargetType.UI);
    }

    public static DBCredential createDBCredentialForUser(DBUser user) {
        return createDBCredential(user, user.getUsername(), "value", CredentialType.USERNAME_PASSWORD, CredentialTargetType.UI);
    }

    public static DBCredential createDBCredential(DBUser dbUser, String name, String value, CredentialType credentialType, CredentialTargetType credentialTargetType) {
        DBCredential dbCredential = new DBCredential();
        dbCredential.setValue(value);
        dbCredential.setName(name);
        dbCredential.setCredentialType(credentialType);
        dbCredential.setCredentialTarget(credentialTargetType);
        dbCredential.setActiveFrom(OffsetDateTime.now().minusDays(1l));
        dbCredential.setExpireOn(OffsetDateTime.now().plusDays(2l));
        dbCredential.setChangedOn(OffsetDateTime.now());
        dbCredential.setExpireAlertOn(OffsetDateTime.now());
        dbCredential.setSequentialLoginFailureCount(1);
        dbCredential.setUser(dbUser);
        return dbCredential;
    }

    public static DBCredential createDBCredential(String name, String value, CredentialType credentialType, CredentialTargetType credentialTargetType) {
        DBCredential dbCredential = new DBCredential();
        dbCredential.setValue(value);
        dbCredential.setName(name);
        dbCredential.setCredentialType(credentialType);
        dbCredential.setCredentialTarget(credentialTargetType);
        dbCredential.setActiveFrom(OffsetDateTime.now().minusDays(1l));
        dbCredential.setExpireOn(OffsetDateTime.now().plusDays(2l));
        dbCredential.setChangedOn(OffsetDateTime.now());
        dbCredential.setExpireAlertOn(OffsetDateTime.now());
        dbCredential.setSequentialLoginFailureCount(1);
        return dbCredential;
    }

    public static DBAlert createDBAlert() {
        DBAlert dbalert = new DBAlert();
        dbalert.setAlertLevel(AlertLevelEnum.MEDIUM);
        dbalert.setAlertStatus(AlertStatusEnum.SUCCESS);
        dbalert.setAlertType(AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION);
        dbalert.setProcessedTime(OffsetDateTime.now());
        dbalert.setReportingTime(OffsetDateTime.now());
        return dbalert;
    }

    public static DBUser createDBUserByUsername(String userName) {
        DBUser dbuser = new DBUser();

        dbuser.setUsername(userName);
        dbuser.setEmailAddress(userName + "@test.eu");
        dbuser.setActive(true);
        return dbuser;
    }

    public static DBCertificate createDBCertificate() {
        return createDBCertificate(TestConstants.USER_CERT_1);
    }

    public static DBCertificate createDBCertificate(String certId) {
        return createDBCertificate(certId, OffsetDateTime.now().minusDays(5), OffsetDateTime.now().plusDays(5));
    }

    public static DBCertificate createDBCertificate(String certId, OffsetDateTime validFrom, OffsetDateTime validTo) {
        DBCertificate dbcert = new DBCertificate();
        dbcert.setCertificateId(certId);
        dbcert.setValidFrom(validFrom);
        dbcert.setValidTo(validTo);
        return dbcert;
    }

    public static DBUser createDBUserByCertificate(String certId) {
        return createDBUserByCertificate(certId, OffsetDateTime.now().minusDays(5), OffsetDateTime.now().plusDays(5));
    }

    public static DBUser createDBUserByCertificate(String certId, OffsetDateTime validFrom, OffsetDateTime validTo) {
        return createDBUser("test-" + certId, certId, validFrom, validTo);
    }

    public static DBUser createDBUser(String userName, String certId) {
        return createDBUser(userName, certId, OffsetDateTime.now().minusDays(5), OffsetDateTime.now().plusDays(5));
    }

    public static DBUser createDBUser(String userName, String certId, OffsetDateTime validFrom, OffsetDateTime validTo) {
        DBUser dbuser = createDBUserByUsername(userName);
        // dbuser.setCertificate(createDBCertificate(certId, validFrom, validTo));
        return dbuser;
    }
    
    public static String anyString(){
        return UUID.randomUUID().toString();
    }
}
