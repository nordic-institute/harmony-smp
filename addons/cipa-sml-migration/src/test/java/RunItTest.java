import eu.europa.ec.cipa.sml.server.dns.RunIt;
import org.junit.Test;

/**
 * Created by feriaad on 02/03/2015.
 */
public class RunItTest {

    @Test
    public void testMain() {
        String[] args = new String[]{"smk.peppolcentral.org.dns"};
        RunIt.main(args);
    }
}
