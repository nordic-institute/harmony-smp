package eu.europa.ec.edelivery.smp.testutil;

import eu.europa.ec.edelivery.smp.data.model.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.SIMPLE_DOCUMENT_XML;
import static eu.europa.ec.edelivery.smp.testutil.TestConstants.SIMPLE_EXTENSION_XML;

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
        return createDBServiceMetadata(partcId,partcSch, UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()  );
    }
    public static DBServiceMetadata createDBServiceMetadata(String partcId, String partcSch, String docId, String docSch ) {
        return createDBServiceMetadata(partcId,partcSch, docId, docSch, UUID.randomUUID().toString()  );
    }

    public static DBServiceMetadata createDBServiceMetadata(String partcId, String partcSch, String docId, String docSch, String desc) {
        DBServiceMetadata grp = new DBServiceMetadata();
        grp.setDocumentIdentifier(docId);
        grp.setDocumentIdentifierScheme(docSch);
        grp.setXmlContent(generateDocumentSample(partcId, partcSch,docId, docSch, desc));
        return grp;
    }

    public static  byte[]  generateDocumentSample(String partcId, String partcSch, String docId, String docSch, String desc){
        return String.format(SIMPLE_DOCUMENT_XML,partcSch, partcId,docSch, docId, desc).getBytes();
    }
    public static byte[] generateExtension(){
        return String.format(SIMPLE_EXTENSION_XML, UUID.randomUUID().toString()).getBytes();
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

    public static DBUser createDBUserByUsername(String userName) {
        DBUser dbuser = new DBUser();
        dbuser.setUsername(userName);
        dbuser.setRole("test");
        dbuser.setEmailAddress("test@test.eu");
        dbuser.setPasswordChanged(LocalDateTime.now());
        dbuser.setPassword(UUID.randomUUID().toString());
        dbuser.setAccessTokenIdentifier(userName);
        dbuser.setAccessToken(UUID.randomUUID().toString());
        return dbuser;
    }

    public static DBCertificate createDBCertificate() {
        return createDBCertificate(TestConstants.USER_CERT_1);
    }
    public static DBCertificate createDBCertificate(String certId) {
        DBCertificate dbcert = new DBCertificate();
        dbcert.setCertificateId(certId);
        dbcert.setValidFrom(LocalDateTime.now());
        dbcert.setValidTo(LocalDateTime.now());
        return dbcert;
    }
    public static DBUser createDBUserByCertificate(String certId) {
        DBUser dbuser = new DBUser();
        dbuser.setRole("test");
        dbuser.setUsername("test-"+certId);

        DBCertificate dbcert = new DBCertificate();
        dbcert.setCertificateId(certId);
        dbcert.setValidFrom(LocalDateTime.now());
        dbcert.setValidTo(LocalDateTime.now());
        dbuser.setCertificate(dbcert);
        return dbuser;
    }

    public static DBUser createDBUser(String userName, String certId) {
        DBUser dbuser =createDBUserByUsername(userName);
        DBCertificate dbcert =createDBCertificate(certId);
        dbuser.setCertificate(dbcert);
        return dbuser;
    }


    /*
    public static DBOwnership createDBOwnership(){
        DBServiceGroup grp = createDBServiceGroup();

        DBUser dbuser = createDBUser();

        DBOwnershipId ownID = new DBOwnershipId();
        ownID.setBusinessIdentifier(grp.getId().getBusinessIdentifier());
        ownID.setBusinessIdentifierScheme(grp.getId().getBusinessIdentifierScheme());
        ownID.setUsername(dbuser.getUsername());

        DBOwnership own = new DBOwnership();
        own.setId(ownID);
        own.setServiceGroup(grp);
        own.setUser(dbuser);
        return own;
    }

    public static DBServiceMetadata createDBServiceMetadata(){
        DBServiceGroup grp = createDBServiceGroup();


        DBServiceMetadataId smdId = new DBServiceMetadataId();
        smdId.setBusinessIdentifier(grp.getId().getBusinessIdentifier());
        smdId.setBusinessIdentifierScheme(grp.getId().getBusinessIdentifierScheme());
        smdId.setDocumentIdentifier(REVISION_DOCUMENT_ID);
        smdId.setDocumentIdentifierScheme(REVISION_DOCUMENT_SCH);

        DBServiceMetadata smd = new DBServiceMetadata();
        smd.setId(smdId);
        smd.setServiceGroup(grp);
        smd.setXmlContent(UUID.randomUUID().toString());


        return smd;
    }
    */
}
