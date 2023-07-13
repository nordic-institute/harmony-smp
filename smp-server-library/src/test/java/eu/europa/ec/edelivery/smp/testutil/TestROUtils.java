package eu.europa.ec.edelivery.smp.testutil;

import eu.europa.ec.edelivery.smp.conversion.X509CertificateToCertificateROConverter;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.SIMPLE_DOCUMENT_XML;
import static eu.europa.ec.edelivery.smp.testutil.TestConstants.SIMPLE_EXTENSION_XML;

public class TestROUtils {

    public static final X509CertificateToCertificateROConverter CERT_CONVERTER = new X509CertificateToCertificateROConverter();
    private static final String RES_PATH = "";


    public static ServiceMetadataRO createServiceMetadataDomain(DBDomain domain, ServiceGroupRO sgo, String docid, String docSch) {
        ServiceMetadataRO sgdmd = new ServiceMetadataRO();
        sgdmd.setDomainCode(domain.getDomainCode());
        sgdmd.setSmlSubdomain(domain.getSmlSubdomain());
        sgdmd.setDocumentIdentifier(docid);
        sgdmd.setDocumentIdentifierScheme(docSch);
        sgdmd.setXmlContent(generateServiceMetadata(sgo.getParticipantIdentifier(), sgo.getParticipantScheme(), docid, docSch));
        return sgdmd;
    }

    public static ServiceGroupDomainRO createServiceGroupDomain(DBDomain domain) {

        ServiceGroupDomainRO sgd = new ServiceGroupDomainRO();
        sgd.setDomainId(domain.getId());
        sgd.setDomainCode(domain.getDomainCode());
        sgd.setSmlSubdomain(domain.getSmlSubdomain());
        return sgd;
    }

    public static ResourceRO createResource() {
        return createResource(TestConstants.TEST_SG_ID_1, TestConstants.TEST_SG_SCHEMA_1);
    }



    public static ResourceRO createResource(String id, String sch) {
        return createResource(id, sch, null);
    }

    public static ResourceRO createResource(String id, String sch, String resourceType) {
        ResourceRO resourceRO = new ResourceRO();
        resourceRO.setStatus(EntityROStatus.NEW.getStatusNumber());
        resourceRO.setIdentifierValue(id);
        resourceRO.setIdentifierScheme(sch);
        resourceRO.setVisibility(VisibilityType.PUBLIC);
        resourceRO.setResourceTypeIdentifier(resourceType);
        return resourceRO;
    }

    public static String generateExtension() {
        return String.format(SIMPLE_EXTENSION_XML, UUID.randomUUID().toString());
    }

    public static String generateServiceMetadata(String partId, String partSch, String docId, String docSch) {
        return String.format(SIMPLE_DOCUMENT_XML, partSch, partId, docSch, docId, UUID.randomUUID().toString());
    }

    public static ServiceGroupValidationRO getExtensionRO(String extension) {
        ServiceGroupValidationRO sg = new ServiceGroupValidationRO();
        sg.setServiceGroupId((long) 1);
        sg.setExtension(extension);
        return sg;
    }


    public static CertificateRO createCertificateRO(String certSubject, BigInteger serial) throws Exception {
        X509Certificate cert = X509CertificateTestUtils.createX509CertificateForTest(certSubject, serial, null);
        return CERT_CONVERTER.convert(cert);
    }
}
