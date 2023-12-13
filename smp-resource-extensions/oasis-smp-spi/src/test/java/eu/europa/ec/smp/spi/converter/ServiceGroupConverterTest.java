/*-
 * #%L
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
 * #L%
 */

package eu.europa.ec.smp.spi.converter;

import eu.europa.ec.dynamicdiscovery.core.extension.impl.oasis10.OasisSMP10ServiceGroupReader;
import eu.europa.ec.dynamicdiscovery.exception.BindException;
import eu.europa.ec.smp.spi.testutils.XmlTestUtils;
import gen.eu.europa.ec.ddc.api.smp10.ServiceGroup;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;


/**
 * Created by gutowpa on 11/04/2017.
 */
class ServiceGroupConverterTest {

    OasisSMP10ServiceGroupReader testInstance = new OasisSMP10ServiceGroupReader();

    private static final String RES_PATH = "/examples/oasis-smp-1.0/";


    @Test
    void testUnmashallingServiceGroup() throws Exception {

        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceGroupOK.xml");

        //when
        ServiceGroup serviceGroup = testInstance.parseNative(new ByteArrayInputStream(inputDoc));

        //then
        Assertions.assertNotNull(serviceGroup);
    }


    @Test
    void testVulnerabilityParsingDTD() throws Exception {
        //given
        byte[] inputDoc = XmlTestUtils.loadDocumentAsByteArray(RES_PATH + "ServiceGroupWithDOCTYPE.xml");
        //when then
        BindException result = Assertions.assertThrows(BindException.class, () -> testInstance.parseNative(new ByteArrayInputStream(inputDoc)));
        MatcherAssert.assertThat(result.getCause().getMessage(), CoreMatchers.containsString("DOCTYPE is disallowed"));
    }
}
