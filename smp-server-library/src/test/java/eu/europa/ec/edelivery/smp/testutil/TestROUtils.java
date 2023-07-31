package eu.europa.ec.edelivery.smp.testutil;

import eu.europa.ec.edelivery.smp.conversion.X509CertificateToCertificateROConverter;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.GroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ResourceRO;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.UUID;


public class TestROUtils {

    public static final X509CertificateToCertificateROConverter CERT_CONVERTER = new X509CertificateToCertificateROConverter();

    public static ResourceRO createResource(String id, String sch, String resourceType) {
        ResourceRO resourceRO = new ResourceRO();
        resourceRO.setStatus(EntityROStatus.NEW.getStatusNumber());
        resourceRO.setIdentifierValue(id);
        resourceRO.setIdentifierScheme(sch);
        resourceRO.setVisibility(VisibilityType.PUBLIC);
        resourceRO.setResourceTypeIdentifier(resourceType);
        return resourceRO;
    }

    public static CertificateRO createCertificateRO(String certSubject, BigInteger serial) throws Exception {
        X509Certificate cert = X509CertificateTestUtils.createX509CertificateForTest(certSubject, serial, null);
        return CERT_CONVERTER.convert(cert);
    }

    public static GroupRO createGroup(String groupName, VisibilityType visibility) {
        GroupRO group = new GroupRO();
        group.setGroupName(groupName);
        group.setGroupDescription(anyString());
        group.setVisibility(visibility);
        return group;
    }


    public static String anyString() {
        return UUID.randomUUID().toString();
    }
}
