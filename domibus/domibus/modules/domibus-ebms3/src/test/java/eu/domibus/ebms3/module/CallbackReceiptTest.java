package eu.domibus.ebms3.module;

import eu.domibus.common.exceptions.ConfigurationException;
import eu.domibus.ebms3.config.PModePool;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CallbackReceiptTest {

    private final static Logger LOG = Logger.getLogger(CallbackReceiptTest.class);

    //Map<Search Parameter, Expected PMode>
    private final static Map<String, String> SEARCH_RESULT_MAP = new HashMap<String, String>();

    private final static String SINGLE_PMODE_WITH_NO_CORRESPONDING_PMODE =
            "TESTGW1_TESTGW2_TEST_GW2GW_unreliable_maxPayloadMin";

    @Rule
    public ExpectedException correspondingPModeNotFoundException = ExpectedException.none();


    @BeforeClass
    public static void setup() {
        try {

            //URL testPModeURL_1 = CallbackReceiptTest.class.getResource("/test_pmodes/TESTGW1toTESTGW2.pmode.xml");
            //URL testPModeURL_2 = CallbackReceiptTest.class.getResource("/test_pmodes/TESTGW2toTESTGW1.pmode.xml");
            URL testPModeURL_1 = Thread.currentThread().getContextClassLoader()
                                       .getResource("test_pmodes/TESTGW1toTESTGW2.pmode.xml");
            URL testPModeURL_2 = Thread.currentThread().getContextClassLoader()
                                       .getResource("test_pmodes/TESTGW2toTESTGW1.pmode.xml");
            assertNotNull(testPModeURL_1);
            LOG.info("1st PMode File used for this test: " + testPModeURL_1.getFile());
            assertNotNull(testPModeURL_2);
            LOG.info("2nd PMode File used for this test: " + testPModeURL_2.getFile());

            PModePool pmodePool_1 = PModePool.load(testPModeURL_1.getFile());
            PModePool pmodePool_2 = PModePool.load(testPModeURL_2.getFile());

            Configuration.addPModePool(pmodePool_1);
            Configuration.addPModePool(pmodePool_2);

            URL testSecurityConfigURL = Thread.currentThread().getContextClassLoader()
                                              .getResource("test_security-config/security-config.xml");
            assertNotNull(testSecurityConfigURL);

            URL testPolicyURL = Thread.currentThread().getContextClassLoader().getResource("test_policies");
            assertNotNull(testPolicyURL);

            eu.domibus.security.module.Configuration
                    .loadSecurityConfigFileIfModified(new File(testSecurityConfigURL.getFile()),
                                                      testPolicyURL.getFile());

            assertNotNull(Constants.pmodes);

        } catch (Exception e) {
            LOG.error("Exception while loading testressources", e);
        }

        SEARCH_RESULT_MAP.put("TESTGW1_TESTGW2_TEST_GW2GW_unreliable_sign_encrypt",
                              "TESTGW2_TESTGW1_TEST_GW2GW_unreliable_sign_encrypt");
        SEARCH_RESULT_MAP.put("TESTGW1_TESTGW2_TEST_GW2GW_as4_response_sign_encrypt",
                              "TESTGW2_TESTGW1_TEST_GW2GW_as4_response_sign_encrypt");
        SEARCH_RESULT_MAP.put("TESTGW1_TESTGW2_TEST_GW2GW_as4_callback_sign_encrypt",
                              "TESTGW2_TESTGW1_TEST_GW2GW_as4_callback_sign_encrypt");
        SEARCH_RESULT_MAP.put("TESTGW1_TESTGW2_TEST_GW2GW_unreliable", "TESTGW2_TESTGW1_TEST_GW2GW_unreliable");
        SEARCH_RESULT_MAP.put("TESTGW1_TESTGW2_TEST_GW2GW_as4_response", "TESTGW2_TESTGW1_TEST_GW2GW_as4_response");
        SEARCH_RESULT_MAP.put("TESTGW1_TESTGW2_TEST_GW2GW_as4_callback", "TESTGW2_TESTGW1_TEST_GW2GW_as4_callback");
        SEARCH_RESULT_MAP.put("TESTGW1_TESTGW2_TEST_SubmissionAcceptance", "TESTGW2_TESTGW1_TEST_SubmissionAcceptance");
        SEARCH_RESULT_MAP.put("TESTGW1_TESTGW2_TEST_RelayMD", "TESTGW2_TESTGW1_TEST_RelayMD");
        SEARCH_RESULT_MAP.put("TESTGW1_TESTGW2_TEST_Delivery", "TESTGW2_TESTGW1_TEST_Delivery");
        SEARCH_RESULT_MAP.put("TESTGW1_TESTGW2_TEST_Retrieval", "TESTGW2_TESTGW1_TEST_Retrieval");
    }


    /**
     * Positive test of getCorrespondingReplyPMode(). This test iterates through all pmodes except one and tries to find a corresponding pmode for it.
     *
     * @throws Exception
     */
    @Test
    public void testGetCorrespondingReplyPModeOverList_Positive() throws Exception {

        for (Map.Entry<String, String> entry : SEARCH_RESULT_MAP.entrySet()) {
            String methodResult = Configuration.getCorrespondingReplyPMode(entry.getKey());
            assertEquals(entry.getValue(), methodResult);

            methodResult = Configuration.getCorrespondingReplyPMode(entry.getKey());
            assertEquals(methodResult, entry.getValue());
        }
    }

    /**
     * Negative test of getCorrespondingReplyPMode(). This test calls the method with the name of a pmode which does not have a corresponding pmode and therefore expects an exception
     *
     * @throws Exception
     */
    @Test
    public void testGetCorrespondingReplyPModeSinglePmode_Negative() throws Exception {

        this.correspondingPModeNotFoundException.expect(ConfigurationException.class);
        this.correspondingPModeNotFoundException
                .expectMessage("No corresponding PMode for " + SINGLE_PMODE_WITH_NO_CORRESPONDING_PMODE + " found.");

        Configuration.getCorrespondingReplyPMode(SINGLE_PMODE_WITH_NO_CORRESPONDING_PMODE);
    }
}
