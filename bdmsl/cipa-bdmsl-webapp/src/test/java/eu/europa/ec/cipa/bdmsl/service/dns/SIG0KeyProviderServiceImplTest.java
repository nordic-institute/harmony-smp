package eu.europa.ec.cipa.bdmsl.service.dns;

import eu.europa.ec.cipa.bdmsl.AbstractTest;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by feriaad on 24/06/2015.
 */
public class SIG0KeyProviderServiceImplTest extends AbstractTest {

    @Autowired
    private ISIG0KeyProviderService sig0KeyProviderService;

    @Test
    public void getPrivateSIG0KeyTest() throws TechnicalException {
        Assert.assertNotNull(sig0KeyProviderService.getPrivateSIG0Key());
    }
}
