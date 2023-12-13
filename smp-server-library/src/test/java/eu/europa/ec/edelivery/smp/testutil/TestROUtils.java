/*-
 * #%L
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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
 * #L%
 */
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
