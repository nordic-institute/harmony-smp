package eu.europa.ec.cipa.dispatcher.endpoint_interface.domibus;

import eu.europa.ec.cipa.dispatcher.endpoint_interface.as4.AS4GatewayInterface;
import eu.europa.ec.cipa.dispatcher.endpoint_interface.as4.service.AS4PModeService;
import eu.europa.ec.cipa.dispatcher.util.PropertiesUtil;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AS4GatewayInterface.class, DefaultHttpClient.class, AS4PModeService.class})
public class AS4GatewayInterfaceTest {


    @BeforeClass
    public static void beforeClass() throws Exception {
        clean();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        clean();
    }

    private static void clean() {
        String pmodeFilePath = PropertiesUtil.getProperties().getProperty(PropertiesUtil.AS4_PMODE_FILEPATH);
        FileUtils.deleteQuietly(new File(pmodeFilePath));
    }

    /**
     * Tests the creation of a partner for the AS4 protocol. The call to the Reload servlet is mocked
     *
     * @throws Exception
     */
    @Test
    public void testCreatePartner() throws Exception {
        DefaultHttpClient defaultHttpClient = PowerMockito.mock(DefaultHttpClient.class);
        PowerMockito.whenNew(DefaultHttpClient.class).withNoArguments().thenReturn(defaultHttpClient);
        PowerMockito.when(defaultHttpClient.execute(Mockito.isA(HttpGet.class))).thenReturn(null);
        AS4GatewayInterface gw = new AS4GatewayInterface();
        gw.createPartner("TSTGW3", "TSTGW2", "EPO", "FORM_B", "http://test");

        // verify that the PMode file has been correctly updated
        PowerMockito.verifyNew(DefaultHttpClient.class).withNoArguments();
        AS4PModeService as4PModeService = new AS4PModeService();
        as4PModeService.initPModePool();
        Assert.assertNotNull(as4PModeService.getProducer("TSTGW3_GW"));
        Assert.assertNotNull(as4PModeService.getBinding("TSTGW3_TSTGW2_EPO_FORM_B"));
        Assert.assertNotNull(as4PModeService.getUserService("EPO_FORM_B_TSTGW2_GW"));
    }
}
