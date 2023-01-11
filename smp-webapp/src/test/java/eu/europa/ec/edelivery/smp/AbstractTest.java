package eu.europa.ec.edelivery.smp;

import eu.europa.ec.edelivery.smp.test.testutils.X509CertificateTestUtils;
import org.junit.Before;

import java.io.IOException;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
public abstract class AbstractTest {

    @Before
    public void setup() throws IOException {
        X509CertificateTestUtils.reloadKeystores();
    }


}
