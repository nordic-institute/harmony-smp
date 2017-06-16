/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

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