package eu.europa.ec.cipa.smp.server;

import com.helger.commons.scopes.mock.ScopeTestRule;
import eu.europa.ec.cipa.smp.server.data.IDataManager;
import eu.europa.ec.cipa.smp.server.data.dbms.DBMSDataManager;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBUser;
import eu.europa.ec.cipa.smp.server.hook.DoNothingRegistrationHook;
import org.junit.After;
import org.junit.Before;
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

    @Before
    public void before() throws Throwable {
        createDBCertificated();
    }

    private static void createDBCertificated() throws Throwable {
        String[][] usernames = new String[][]{{"CN=SMP_1000000007,O=DG-DIGIT,C=BE:000000000123ABCD", "123ABCD"},
                {"CN=EHEALTH_SMP_1000000007,O=DG-DIGIT,C=BE:000000000123ABCD", "123ABCD"},
                {"CN=SMP_1000000007,O=DG-DIGIT,C=BE", "123ABCD"},
                {"CN=EHEALTH_SMP_1000000007,O=DG-DIGIT,C=BE", "123ABCD"},
                {"CN=SMP_1000000007,O=DG-DIGIT,C=BE:000000000123ABCD", "123ABCD"},
                {"CN=EHEALTH_SMP_EC/emailAddress\\=CEF-EDELIVERY-SUPPORT@ec.europa.eu,O=European Commission,C=BE:f71ee8b11cb3b787", "12345"}};

        for (int i = 0; i < usernames.length; i++) {
            DBUser aDBUser = s_aDataMgr.getCurrentEntityManager().find(DBUser.class, usernames[i][0]);
            if (aDBUser == null) {
                aDBUser = new DBUser();
                aDBUser.setUsername(usernames[i][0]);
                aDBUser.setPassword(usernames[i][1]);
                s_aDataMgr.getCurrentEntityManager().persist(aDBUser);
            } else {
                if (aDBUser.getPassword() != null && !aDBUser.getPassword().equals(usernames[i][1])) {
                    aDBUser.setPassword(usernames[i][1]);
                    s_aDataMgr.getCurrentEntityManager().merge(aDBUser);
                }
            }
        }
    }

    @After
    public final void after() throws Throwable {
        removeDBUser();
    }

    private static void removeDBUser() throws Throwable {
        String[] usernames = new String[]{"CN=SMP_1000000007,O=DG-DIGIT,C=BE:000000000123ABCD", "CN=EHEALTH_SMP_1000000007,O=DG-DIGIT,C=BE:000000000123ABCD"};
        for (int i = 0; i < usernames.length; i++) {
            DBUser aDBUser = s_aDataMgr.getCurrentEntityManager().find(DBUser.class, usernames[i]);
            if (aDBUser != null) {
                s_aDataMgr.getCurrentEntityManager().remove(aDBUser);
            }
        }
    }

}
