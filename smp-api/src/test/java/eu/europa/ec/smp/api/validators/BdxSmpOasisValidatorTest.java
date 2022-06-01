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

package eu.europa.ec.smp.api.validators;

import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * Created by migueti on 20/01/2017.
 */
@RunWith(JUnitParamsRunner.class)
public class BdxSmpOasisValidatorTest {

    private static final String UTF_8 = "UTF-8";

    @Test
    @Parameters({"ServiceMetadata_OK.xml", "ServiceGroup_OK.xml"})
    public void testValidatePositive(String xmlFilename) throws IOException, XmlInvalidAgainstSchemaException {
        // given
        byte[] xmlBody = loadXMLFileAsByteArray(xmlFilename);

        // when
        BdxSmpOasisValidator.validateXSD(xmlBody);

        // then
        // no exception occur
    }

    private static Object[] negativeCases() {
        return new Object[][]{
                {"ServiceMetadata_ElementAdded.xml", "cvc-complex-type.2.4.a: Invalid content was found starting with element \\'\\{?(\\\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\\\")?:?ElementAdded\\}?\\'.*Redirect.* is expected."},
                {"ServiceMetadata_ElementMissing.xml", "cvc-complex-type.2.4.b: The content of element 'Redirect' is not complete. One of \\'\\{?(\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\")?:?CertificateUID\\}?\\' is expected."},
                {"ServiceGroup_MissingAssignment.xml", "Attribute name \"missingAssignment\" associated with an element type \"ServiceMetadataReferenceCollection\" must be followed by the ' = ' character."},
                {"ServiceGroup_UnexpectedAttribute.xml", "cvc-complex-type.3.2.2: Attribute 'unexpectedAttribute' is not allowed to appear in element 'ServiceMetadataReferenceCollection'."},
                {"ServiceGroup_externalDTD.xml", "External DTD: Failed to read external DTD 'any_external_file_address.dtd', because 'file' access is not allowed due to restriction set by the accessExternalDTD property."}
        };
    }

    @Test
    @Parameters(method = "negativeCases")
    public void testValidateNegative(String xmlFilename, String output) throws IOException {
        // given
        byte[] xmlBody = loadXMLFileAsByteArray(xmlFilename);

        // when
        try {
            BdxSmpOasisValidator.validateXSD(xmlBody);
        } catch (XmlInvalidAgainstSchemaException e) {
            // then
            assertThat(e.getMessage(), org.hamcrest.Matchers.matchesPattern(output));
            return;
        }
        fail();
    }

    public String loadXMLFile(String path) throws IOException {
        URL fileUrl = BdxSmpOasisValidatorTest.class.getResource("/XMLValidation/" + path);
        return IOUtils.toString(fileUrl.openStream(), UTF_8);
    }

    public byte[] loadXMLFileAsByteArray(String path) throws IOException {
        URL fileUrl = BdxSmpOasisValidatorTest.class.getResource("/XMLValidation/" + path);
        return IOUtils.toByteArray(fileUrl.openStream());
    }
}
