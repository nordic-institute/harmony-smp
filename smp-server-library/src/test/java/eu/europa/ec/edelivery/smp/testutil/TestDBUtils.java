package eu.europa.ec.edelivery.smp.testutil;

import eu.europa.ec.edelivery.smp.data.model.*;
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
        domain.setSignatureKeyAlias(UUID.randomUUID().toString());
        domain.setSmlClientCertHeader(UUID.randomUUID().toString());
        domain.setSmlClientKeyAlias(UUID.randomUUID().toString());
        domain.setSmlSubdomain(UUID.randomUUID().toString());
        domain.setSmlSmpId(UUID.randomUUID().toString());
        domain.setSmlParticipantIdentifierRegExp(UUID.randomUUID().toString());
        return domain;
    }

    public static DBDomain createDBDomain() {
        return createDBDomain(TestConstants.TEST_DOMAIN_CODE_1);
    }

    public static DBServiceGroup createDBServiceGroup() {
        return createDBServiceGroup(TestConstants.TEST_SG_ID_1, TestConstants.TEST_SG_SCHEMA_1);
    }

    public static DBServiceMetadata createDBServiceMetadata(String partcId, String partcSch) {
        return createDBServiceMetadata(partcId, partcSch, UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }

    public static DBServiceMetadata createDBServiceMetadata(String partcId, String partcSch, String docId, String docSch) {
        return createDBServiceMetadata(partcId, partcSch, docId, docSch, UUID.randomUUID().toString());
    }

    public static DBServiceMetadata createDBServiceMetadata(String partcId, String partcSch, String docId, String docSch, String desc) {
        DBServiceMetadata grp = new DBServiceMetadata();
        grp.setDocumentIdentifier(docId);
        grp.setDocumentIdentifierScheme(docSch);
        grp.setXmlContent(generateDocumentSample(partcId, partcSch, docId, docSch, desc));
        return grp;
    }

    public static DBServiceMetadata createDBServiceMetadataRedirect(String docId, String docSch, String url) {
        DBServiceMetadata grp = new DBServiceMetadata();
        grp.setDocumentIdentifier(docId);
        grp.setDocumentIdentifierScheme(docSch);
        grp.setXmlContent(generateRedirectDocumentSample(url));
        return grp;
    }

    public static byte[] generateDocumentSample(String partcId, String partcSch, String docId, String docSch, String desc) {
        return String.format(SIMPLE_DOCUMENT_XML, partcSch, partcId, docSch, docId, desc).getBytes();
    }

    public static byte[] generateExtension() {
        return String.format(SIMPLE_EXTENSION_XML, UUID.randomUUID().toString()).getBytes();
    }

    public static byte[] generateRedirectDocumentSample(String url) {
        return String.format(SIMPLE_REDIRECT_DOCUMENT_XML, url).getBytes();

    }

    public static DBServiceGroup createDBServiceGroup(String id, String sch) {
        return createDBServiceGroup(id, sch, true);
    }

    public static DBServiceGroup createDBServiceGroupRandom() {
        return createDBServiceGroup(UUID.randomUUID().toString(), UUID.randomUUID().toString(), true);
    }

    public static DBServiceGroup createDBServiceGroup(String id, String sch, boolean withExtension) {
        DBServiceGroup grp = new DBServiceGroup();
        grp.setParticipantIdentifier(id);
        grp.setParticipantScheme(sch);
        if (withExtension) {
            grp.setExtension(generateExtension());
        }
        return grp;
    }


    public static DBUser createDBUser(String username1) {
        return createDBUserByUsername(username1);
    }

    public static DBAlert createDBAlert() {
        DBAlert dbalert = new DBAlert();
        dbalert.setAlertLevel(AlertLevelEnum.MEDIUM);
        dbalert.setAlertStatus(AlertStatusEnum.SUCCESS);
        dbalert.setAlertType(AlertTypeEnum.CREDENTIAL_IMMINENT_EXPIRATION);
        dbalert.setProcessed(true);
        dbalert.setProcessedTime(OffsetDateTime.now());
        dbalert.setReportingTime(OffsetDateTime.now());
        return dbalert;
    }

    public static DBUser createDBUserByUsername(String userName) {
        DBUser dbuser = new DBUser();
        dbuser.setUsername(userName);
        dbuser.setRole("test");
        dbuser.setEmailAddress("test@test.eu");
        dbuser.setPasswordChanged(OffsetDateTime.now());
        dbuser.setPassword(UUID.randomUUID().toString());
        dbuser.setAccessTokenIdentifier(userName);
        dbuser.setAccessToken(UUID.randomUUID().toString());
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
        return createDBUser("test-" + certId, certId, validFrom,validTo);
    }

    public static DBUser createDBUser(String userName, String certId) {
        return createDBUser(userName, certId, OffsetDateTime.now().minusDays(5), OffsetDateTime.now().plusDays(5));
    }

    public static DBUser createDBUser(String userName, String certId, OffsetDateTime validFrom, OffsetDateTime validTo) {
        DBUser dbuser = createDBUserByUsername(userName);
        dbuser.setCertificate(createDBCertificate(certId, validFrom, validTo));
        return dbuser;
    }
}
