package eu.europa.ec.edelivery.smp;

import eu.europa.ec.edelivery.smp.testutils.X509CertificateTestUtils;
import org.junit.Before;

import java.io.IOException;

public abstract class AbstractTest {


    @Before
    public void setup() throws IOException {
        X509CertificateTestUtils.reloadKeystores();
    }


}
