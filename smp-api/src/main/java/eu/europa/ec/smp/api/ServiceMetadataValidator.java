package eu.europa.ec.smp.api;

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
public class ServiceMetadataValidator {

    public boolean validateXSD(String xmlBody) throws XsdInvalidException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            URL xsdFilePath = this.getClass().getResource("/bdx-smp-201605.xsd");
            Schema schema = schemaFactory.newSchema(xsdFilePath);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlBody)));
            return true;
        } catch (SAXException | IOException e) {
            throw new XsdInvalidException(e.getMessage());
        }
    }
}
