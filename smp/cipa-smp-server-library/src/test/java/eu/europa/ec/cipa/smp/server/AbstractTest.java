package eu.europa.ec.cipa.smp.server;

import com.helger.commons.scopes.mock.ScopeTestRule;
import eu.europa.ec.cipa.smp.server.data.IDataManager;
import eu.europa.ec.cipa.smp.server.data.dbms.DBMSDataManager;
import eu.europa.ec.cipa.smp.server.hook.DoNothingRegistrationHook;
import org.junit.ClassRule;
import org.junit.rules.TestRule;

/**
 * Created by rodrfla on 23/01/2017.
 */
public class AbstractTest {

    protected static IDataManager s_aDataMgr = null;

    private static final class SMPTestRule extends ScopeTestRule {
        @Override
        public void before() {
            super.before();
            if (s_aDataMgr == null) {
                // Do it only once :)
                // SMPEntityManagerFactory.getInstance ();
                s_aDataMgr = new DBMSDataManager(new DoNothingRegistrationHook());
            }
        }
    }

    @ClassRule
    public static TestRule s_aTestRule = new AbstractTest.SMPTestRule();
}
