package eu.ecodex.integrationTests;

import com.eviware.soapui.tools.SoapUITestCaseRunner;
import org.junit.Test;
import seatiger.util.Configuration;

public class SoapUIIntegrationTest extends DefaultTestSetup {

    @Test
    public void runSoapUItests() throws Exception {
        final SoapUITestCaseRunner runner = new SoapUITestCaseRunner();
        runner.setProjectFile("src/test/resources/soapui_testsuites/e-CODEXBackendServiceTestSuite.xml");
        runner.setProjectProperties(
                new String[]{System.getProperty(Configuration.SOAPUI_SERVICE_ENDPOINT_11_RECEIVING), System.getProperty(Configuration.SOAPUI_SERVICE_ENDPOINT_11_SENDING),
                             System.getProperty(Configuration.SOAPUI_SERVICE_ENDPOINT_SENDING), System.getProperty(Configuration.SOAPUI_SERVICE_ENDPOINT_RECEIVING),
                             System.getProperty(Configuration.SOAPUI_SERVICE_GATEWAY_SENDING), System.getProperty(Configuration.SOAPUI_SERVICE_GATEWAY_RECEIVING)});
        runner.setSettingsFile("src/test/resources/soapui_testsuites/soapui-settings.xml");
        runner.run();
    }
}