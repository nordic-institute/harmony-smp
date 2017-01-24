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
