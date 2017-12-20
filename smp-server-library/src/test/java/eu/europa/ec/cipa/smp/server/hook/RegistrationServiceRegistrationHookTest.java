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
package eu.europa.ec.cipa.smp.server.hook;

import eu.europa.ec.edelivery.smp.config.SmpServicesTestConfig;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for class {@link RegistrationServiceRegistrationHook}.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SmpServicesTestConfig.class})
@TestPropertySource(properties = {
        "regServiceRegistrationHook.regLocatorUrl=http://localhost:7002/edelivery-sml/manageparticipantidentifier",
        "regServiceRegistrationHook.keystore.classpath = C://Development//Tools//oracle//middleware//domain//bdsml//_appconfdir//bdmsl//keystore.jks",
        "regServiceRegistrationHook.keystore.password  = test",
        "regServiceRegistrationHook.clientCert=serial=000000000000000000009A195D2DD88C&subject=CN=SMP_1000000000,O=DG-DIGIT,C=BE&validFrom=Oct 21 02:00:00 2014 CEST&validTo=Oct 21 01:59:59 2016 CEST&issuer=CN=Issuer Common Name,OU=Issuer Organization Unit,O=Issuer Organization,C=BE"
})
public final class RegistrationServiceRegistrationHookTest {

    @Autowired
    private RegistrationServiceRegistrationHook aHook;

    @Test
    @Ignore("Potentially modifies the DNS!")
    public void testCreateAndDelete() {
        final ParticipantIdentifierType aPI = new ParticipantIdentifierType("0088:12345test", "iso6523-actorid-upis");
        aHook.create(aPI);
        aHook.delete(aPI);
        // Throws ExceptionInInitializerError:
        // Happens when no keystore is present!
    }
}
