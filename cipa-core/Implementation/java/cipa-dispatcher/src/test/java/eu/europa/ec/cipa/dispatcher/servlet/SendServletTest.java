package eu.europa.ec.cipa.dispatcher.servlet;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by feriaad on 18/03/2015.
 */
public class SendServletTest {

    /**
     * Test the SBDH with an XML with namespaces
     * @throws Exception
     */
    @Test
    public void testTreatSBDHrequest() throws Exception {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("SBDHRequestNamespaceTest.xml");
        Map<String, String> map = new SendServlet().treatSBDHrequest(input);
        Assert.assertTrue(!map.isEmpty());
    }
}
