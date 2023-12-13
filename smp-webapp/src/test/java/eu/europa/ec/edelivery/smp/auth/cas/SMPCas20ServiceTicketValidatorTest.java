/*-
 * #START_LICENSE#
 * smp-webapp
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
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.auth.cas;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SMPCas20ServiceTicketValidatorTest {

    @Test
    public void testGetUrlSuffix() {
        String casUrl = "https://cas-server.local/cas";
        String casSuffix = "urlSuffix";

        SMPCas20ServiceTicketValidator testInstance = new SMPCas20ServiceTicketValidator(casUrl, casSuffix);

        assertEquals(casSuffix, testInstance.getUrlSuffix());
    }

    @Test
    public void testGetUrlSuffixDefault() {
        String casUrl = "https://cas-server.local/cas";
        String casSuffix = null;

        SMPCas20ServiceTicketValidator testInstance = new SMPCas20ServiceTicketValidator(casUrl, casSuffix);

        assertEquals("serviceValidate", testInstance.getUrlSuffix());
    }
}
