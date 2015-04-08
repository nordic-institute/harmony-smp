package eu.europa.ec.cipa.dispatcher.servlet;

import eu.europa.ec.cipa.dispatcher.util.PropertiesUtil;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.util.Map;

/**
 * Created by feriaad on 18/03/2015.
 */
public class SendServletTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        PropertiesUtil.getProperties().setProperty(PropertiesUtil.TEMP_FOLDER_PATH, tempFolder.newFolder("tmp").getAbsolutePath());
    }

    /**
     * Test the SBDH with an XML with namespaces
     *
     * @throws Exception
     */
    @Test
    public void testTreatSBDHrequest() throws Exception {
        testTreatSBDH("SBDHRequestNamespaceTest.xml");
    }

    @Test
    public void testTreatSBDHrequest1() throws Exception {
        testTreatSBDH("SBDHRequestNamespaceTest1.xml");
    }

    @Test
    public void testTreatSBDHrequest2() throws Exception {
        testTreatSBDH("SBDHRequestNamespaceTest2.xml");
    }


    private void testTreatSBDH(String xmlFile) throws Exception {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlFile);
        Map<String, String> map = new SendServlet().treatSBDHrequest(input);
        Assert.assertTrue(map.size() > 2);
        File tmpFolder = new File(PropertiesUtil.getProperties().getProperty(PropertiesUtil.TEMP_FOLDER_PATH));
        String[] files = tmpFolder.list();
        Assert.assertTrue(files.length == 2);

        for (String file : files) {
            if (!file.endsWith("payload")) {
                File originalFile = new File(Thread.currentThread().getContextClassLoader().getResource(xmlFile).getFile());
                File generatedFile = new File(tmpFolder + File.separator + file);
                XMLUnit.compareXML(new FileReader(originalFile), new FileReader(generatedFile));
            }
        }
    }
}
