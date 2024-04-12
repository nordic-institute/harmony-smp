/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.testutil;

import eu.europa.ec.edelivery.smp.data.enums.ApplicationRoleType;
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

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.SIMPLE_EXTENSION_XML;

public class TestDBUtils {

    public static DBDomain createDBDomain(String domainCode) {
        DBDomain domain = new DBDomain();
        domain.setDomainCode(domainCode);
        domain.setSignatureKeyAlias(anyString());
        domain.setSmlClientKeyAlias(anyString());
        domain.setSmlSubdomain(anyString());
        domain.setSmlSmpId(anyString());
        return domain;
    }

    public static DBGroup createDBGroup(String groupName) {
        return createDBGroup(groupName, VisibilityType.PUBLIC);
    }

    public static DBGroup createDBGroup(String groupName, VisibilityType visibility) {
        DBGroup group = new DBGroup();
        group.setGroupName(groupName);
        group.setGroupDescription(anyString());
        group.setVisibility(visibility);
        return group;
    }

    public static DBExtension createDBExtension(String identifier) {
        DBExtension entity = new DBExtension();
        entity.setIdentifier(identifier);
        entity.setImplementationName(identifier + "Name");
        entity.setName(anyString());
        entity.setDescription(anyString());
        entity.setVersion(anyString());
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

    public static byte[] generateExtension() {
        return String.format(SIMPLE_EXTENSION_XML, anyString()).getBytes();
    }


    public static DBResource createDBResource(String id, String sch) {
        return createDBResource(id, sch, true);
    }


    public static DBResource createDBResource(String id, String sch, boolean withExtension) {
        DBResource resource = new DBResource();
        resource.setIdentifierValue(id);
        resource.setIdentifierScheme(sch);
        resource.setVisibility(VisibilityType.PUBLIC);
        if (withExtension) {
            DBDocument document = createDBDocument();
            DBDocumentVersion documentVersion = createDBDocumentVersion(id, sch);
            createDBDocumentVersion(id, sch).setContent(generateExtension());
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

    public static DBDocumentVersion createDBDocumentVersion(String id, String sch) {
        DBDocumentVersion docuVersion = new DBDocumentVersion();
        docuVersion.setContent(("<ServiceGroup xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\">" +
                "<ParticipantIdentifier scheme=\"" + sch + "\">" + id + "</ParticipantIdentifier>" +
                "<ServiceMetadataReferenceCollection />" +
                "</ServiceGroup>").getBytes());
        return docuVersion;
    }

    public static DBDocumentVersion createDBDocumentVersion(String id, String sch, String docId, String docSch) {
        DBDocumentVersion docuVersion = new DBDocumentVersion();
        docuVersion.setContent(("<ServiceMetadata xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"><ServiceInformation>" +
                "<ParticipantIdentifier scheme=\"" + sch + "\">" + id + "</ParticipantIdentifier>" +
                "<DocumentIdentifier scheme=\"" + docSch + "\">" + docId + "</DocumentIdentifier>" +
                "<ProcessList><Process>" +
                "<ProcessIdentifier scheme=\"[test-schema]\">[test-value]</ProcessIdentifier>" +
                "<ServiceEndpointList>" +
                "<Endpoint transportProfile=\"bdxr-transport-ebms3-as4-v1p0\">" +
                "<EndpointURI>https://mypage.eu</EndpointURI>" +
                "<Certificate>Q2VydGlmaWNhdGUgZGF0YSA=</Certificate>" +
                "<ServiceDescription>Service description for partners </ServiceDescription>" +
                "<TechnicalContactUrl>www.best-page.eu</TechnicalContactUrl>" +
                "</Endpoint>" +
                "</ServiceEndpointList>" +
                "</Process></ProcessList></ServiceInformation></ServiceMetadata>").getBytes());
        return docuVersion;
    }

    public static DBUser createDBUser(String username1) {
        return createDBUserByUsername(username1);
    }


    public static DBCredential createDBCredential(String name) {
        return createDBCredential(name, "value", CredentialType.USERNAME_PASSWORD, CredentialTargetType.UI);
    }

    public static DBCredential createDBCredentialForUser(DBUser user, OffsetDateTime from, OffsetDateTime to, OffsetDateTime lastAlertSent) {
        DBCredential credential = createDBCredential(user, user.getUsername(), "value", CredentialType.USERNAME_PASSWORD, CredentialTargetType.UI);
        credential.setExpireOn(to);
        credential.setActiveFrom(from);
        credential.setExpireAlertOn(lastAlertSent);
        return credential;
    }

    public static DBCredential createDBCredentialForUserAccessToken(DBUser user, OffsetDateTime from, OffsetDateTime to, OffsetDateTime lastAlertSent) {
        DBCredential credential = createDBCredential(user, user.getUsername(), "value", CredentialType.ACCESS_TOKEN, CredentialTargetType.REST_API);
        credential.setExpireOn(to);
        credential.setActiveFrom(from);
        credential.setExpireAlertOn(lastAlertSent);
        return credential;
    }

    public static DBCredential createDBCredentialForUserCertificate(DBUser user, OffsetDateTime from, OffsetDateTime to, OffsetDateTime lastAlertSent) {
        DBCredential credential = createDBCredential(user, user.getUsername(), "value", CredentialType.CERTIFICATE, CredentialTargetType.REST_API);
        credential.setExpireAlertOn(lastAlertSent);
        if (to != null) {
            credential.setExpireOn(to);
        }
        if (from != null) {
            credential.setActiveFrom(from);
        }

        return credential;
    }


    public static DBCredential createDBCredential(DBUser dbUser, String name, String value, CredentialType credentialType, CredentialTargetType credentialTargetType) {
        DBCredential dbCredential = new DBCredential();
        dbCredential.setActive(true);
        dbCredential.setValue(value);
        dbCredential.setName(name);
        dbCredential.setCredentialType(credentialType);
        dbCredential.setCredentialTarget(credentialTargetType);
        dbCredential.setActiveFrom(OffsetDateTime.now().minusDays(1L));
        dbCredential.setExpireOn(OffsetDateTime.now().plusDays(2L));
        dbCredential.setChangedOn(OffsetDateTime.now());
        dbCredential.setExpireAlertOn(OffsetDateTime.now());
        dbCredential.setSequentialLoginFailureCount(1);
        dbCredential.setUser(dbUser);

        if (CredentialType.CERTIFICATE.equals(credentialType)) {

            DBCertificate certificate = new DBCertificate();
            certificate.setCertificateId(name);
            certificate.setValidFrom(dbCredential.getActiveFrom());
            certificate.setValidTo(dbCredential.getExpireOn());

            int iSplit = name.lastIndexOf(':');
            if (iSplit > 0) {
                String subject = name.substring(0, iSplit);
                certificate.setSubject(subject);
                certificate.setIssuer(subject);
                certificate.setSerialNumber(name.substring(iSplit + 1));
            } else {
                certificate.setSubject(name);
                certificate.setIssuer(name);
                certificate.setSerialNumber("1234567890");
            }
            dbCredential.setCertificate(certificate);
        }

        return dbCredential;
    }

    public static DBCredential createDBCredential(String name, String value, CredentialType credentialType, CredentialTargetType credentialTargetType) {
        DBCredential dbCredential = new DBCredential();
        dbCredential.setActive(true);
        dbCredential.setValue(value);
        dbCredential.setName(name);
        dbCredential.setCredentialType(credentialType);
        dbCredential.setCredentialTarget(credentialTargetType);
        dbCredential.setActiveFrom(OffsetDateTime.now().minusDays(1L));
        dbCredential.setExpireOn(OffsetDateTime.now().plusDays(2L));
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
        dbuser.setSmpLocale("en");
        dbuser.setEmailAddress(userName + "@test.eu");
        dbuser.setActive(true);
        dbuser.setApplicationRole(ApplicationRoleType.USER);
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
        DBCredential credential = createDBCredential(dbuser, certId, "", CredentialType.CERTIFICATE, CredentialTargetType.REST_API);
        credential.setActiveFrom(validFrom);
        credential.setExpireOn(validTo);
        credential.setCertificate(createDBCertificate(certId, validFrom, validTo));
        dbuser.getUserCredentials().add(credential);
        return dbuser;
    }

    public static String anyString() {
        return UUID.randomUUID().toString();
    }
}
