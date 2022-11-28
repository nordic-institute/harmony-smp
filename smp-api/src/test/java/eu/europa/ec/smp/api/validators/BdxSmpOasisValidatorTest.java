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
import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;


import static org.junit.Assert.*;

/**
 * @author migueti
 * @since 3.0.0
 */
@RunWith(Parameterized.class)
public class BdxSmpOasisValidatorTest {

    private static final String UTF_8 = "UTF-8";

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection testCases() {
        return Arrays.asList(new Object[][]{
                {"ServiceMetadata_OK.xml",false, null},
                {"ServiceGroup_OK.xml", false, null},
                {"ServiceMetadata_ElementAdded.xml", true, "cvc-complex-type.2.4.a: Invalid content was found starting with element \\'\\{?(\\\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\\\")?:?ElementAdded\\}?\\'.*Redirect.* is expected."},
                {"ServiceMetadata_ElementMissing.xml", true, "cvc-complex-type.2.4.b: The content of element 'Redirect' is not complete. One of \\'\\{?(\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\")?:?CertificateUID\\}?\\' is expected."},
                {"ServiceGroup_MissingAssignment.xml", true, "Attribute name \"missingAssignment\" associated with an element type \"ServiceMetadataReferenceCollection\" must be followed by the ' = ' character."},
                {"ServiceGroup_UnexpectedAttribute.xml", true, "cvc-complex-type.3.2.2: Attribute 'unexpectedAttribute' is not allowed to appear in element 'ServiceMetadataReferenceCollection'."},
                {"ServiceGroup_externalDTD.xml", true, "External DTD: Failed to read external DTD 'any_external_file_address.dtd', because 'file' access is not allowed due to restriction set by the accessExternalDTD property."}
        });
    }

    @Parameterized.Parameter
    public String xmlFilename;
    @Parameterized.Parameter(1)
    public boolean throwsError;
    @Parameterized.Parameter(2)
    public String errorMessage;

    @Test
    public void testValidate() throws IOException, XmlInvalidAgainstSchemaException {
        // given
        byte[] xmlBody = loadXMLFileAsByteArray(xmlFilename);

        XmlInvalidAgainstSchemaException result=null;
        // when
        if (throwsError){
            result = assertThrows(XmlInvalidAgainstSchemaException.class, () -> BdxSmpOasisValidator.validateXSD(xmlBody));
        } else {
            BdxSmpOasisValidator.validateXSD(xmlBody);
        }
        assertEquals(throwsError, result!=null);
    }

    public byte[] loadXMLFileAsByteArray(String path) throws IOException {
        URL fileUrl = BdxSmpOasisValidatorTest.class.getResource("/XMLValidation/" + path);
        return IOUtils.toByteArray(fileUrl.openStream());
    }
}
