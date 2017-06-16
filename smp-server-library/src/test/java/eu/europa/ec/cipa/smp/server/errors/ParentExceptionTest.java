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

package eu.europa.ec.cipa.smp.server.errors;

import ec.services.smp._1.ErrorResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Created by migueti on 16/01/2017.
 */
public abstract class ParentExceptionTest {

    private final Pattern PATTERN = Pattern.compile(".*?(:).*?(:).*?(:)([A-Z0-9]{8}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{12})",Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    protected String checkXmlError(ErrorResponse errorResponse, ErrorBusinessCode errorBusinessCode, String errorDescription) throws ParserConfigurationException, IOException, SAXException {
        assertNotNull(errorResponse);

        assertEquals(errorBusinessCode.toString(), errorResponse.getBusinessCode());
        assertEquals(errorDescription, errorResponse.getErrorDescription());

        String errorUniqueId = null;
        Matcher matcher = PATTERN.matcher(errorResponse.getErrorUniqueId());
        if (matcher.find()) {
            errorUniqueId = matcher.group(4);
        }
        return errorUniqueId;
    }
}
