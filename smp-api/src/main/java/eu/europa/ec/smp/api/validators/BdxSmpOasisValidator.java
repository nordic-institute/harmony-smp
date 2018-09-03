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
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.Unmarshaller;
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

    /**
     * Class has only static members.
     */
    private  BdxSmpOasisValidator() {

    }

    /**
     * thread safe validator
     */
    private static final ThreadLocal<Validator> validator = ThreadLocal.withInitial( () -> {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL xsdFilePath = BdxSmpOasisValidator.class.getResource("/bdx-smp-201605.xsd");
        try {
            Schema schema = schemaFactory.newSchema(xsdFilePath);
            Validator vaInstance = schema.newValidator();
            vaInstance.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            vaInstance.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            return vaInstance;
        } catch (SAXException e) {
            throw new IllegalStateException("Unable to initialize BDX SMP OASIS XSD schema validator.", e);
        }
    } );

    private static Validator getValidator() {
        return validator.get();
    }

    public static void validateXSD(String xmlBody) throws XmlInvalidAgainstSchemaException {
        try {
            getValidator().validate(new StreamSource(new StringReader(xmlBody)));
        } catch (SAXException | IOException e) {
            throw new XmlInvalidAgainstSchemaException(e.getMessage(), e);
        }
    }
}