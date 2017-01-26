package eu.europa.ec.smp.api.validators;

import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by migueti on 20/01/2017.
 */
@RunWith(JUnitParamsRunner.class)
public class BdxSmpOasisValidatorTest {

    private static Object[] positiveCases() {
        return new Object[][] {
                {"ServiceMetadata_OK.xml"},
                {"ServiceGroup_OK.xml"}
        };
    }

    @Test
    @Parameters(method = "positiveCases")
    public void testValidatePositive(String xmlFilename) throws IOException, XmlInvalidAgainstSchemaException {
        // given
        String xmlBody = loadXMLFile(xmlFilename);

        // when
        BdxSmpOasisValidator.validateXSD(xmlBody);

        // then
        // no exception occur
    }

    private static Object[] negativeCases() {
        return new Object[][] {
                {"ServiceMetadata_ElementAdded.xml",    "cvc-complex-type.2.4.a: Invalid content was found starting with element 'ElementAdded'. One of '{\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\":ServiceInformation, \"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\":Redirect}' is expected."},
                {"ServiceMetadata_ElementMissing.xml",  "cvc-complex-type.2.4.b: The content of element 'Redirect' is not complete. One of '{\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\":CertificateUID}' is expected."},
                {"ServiceGroup_MissingAssignment.xml",  "Attribute name \"missingAssignment\" associated with an element type \"ServiceMetadataReferenceCollection\" must be followed by the ' = ' character."},
                {"ServiceGroup_UnexpectedAttribute.xml","cvc-complex-type.3.2.2: Attribute 'unexpectedAttribute' is not allowed to appear in element 'ServiceMetadataReferenceCollection'."}
        };
    }

    @Test
    @Parameters(method = "negativeCases")
    public void testValidateNegative(String xmlFilename, String output) throws IOException {
        // given
        String xmlBody = loadXMLFile(xmlFilename);

        // when
        try {
            BdxSmpOasisValidator.validateXSD(xmlBody);
        } catch (XmlInvalidAgainstSchemaException e) {
            // then
            assertEquals(output, e.getMessage());
            return;
        }
        fail();
    }

    public static String loadXMLFile(String path) throws IOException {
        URL fileUrl = BdxSmpOasisValidatorTest.class.getResource("/XMLValidation/"+path);
        BufferedReader in = new BufferedReader(new InputStreamReader(fileUrl.openStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while((line = in.readLine()) != null) {
            result.append(line);
        }
        in.close();
        return result.toString();
    }
}
