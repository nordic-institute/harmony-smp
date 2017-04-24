package eu.europa.ec.smp.api.validators;

import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

/**
 * Created by migueti on 19/01/2017.
 */
public class BdxSmpOasisValidator {

    private static final Validator validator;

    static {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL xsdFilePath = BdxSmpOasisValidator.class.getResource("/bdx-smp-201605.xsd");
        try {
            Schema schema = schemaFactory.newSchema(xsdFilePath);
            validator = schema.newValidator();
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        } catch (SAXException e) {
            throw new RuntimeException("Unable to initialize BDX SMP OASIS XSD schema validator.", e);
        }
    }

    public static void validateXSD(String xmlBody) throws XmlInvalidAgainstSchemaException {
        try {
            validator.validate(new StreamSource(new StringReader(xmlBody)));
        } catch (SAXException | IOException e) {
            throw new XmlInvalidAgainstSchemaException(e.getMessage(), e);
        }
    }
}