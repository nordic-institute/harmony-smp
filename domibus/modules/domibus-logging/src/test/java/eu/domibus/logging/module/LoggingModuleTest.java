package eu.domibus.logging.module;

import eu.domibus.logging.persistent.LoggerEvent;
import eu.domibus.logging.persistent.LoggerEventDAO;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisBinding;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.log4j.Logger;
import org.apache.neethi.Assertion;
import org.apache.neethi.Policy;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * The class <code>LoggingModuleTest</code> contains tests for the class
 * <code>{@link LoggingModule}</code>.
 *
 * @author cheny01
 * @version $Revision: 1.0 $
 */
public class LoggingModuleTest {
    private final static Logger log = Logger.getLogger(LoggingModuleTest.class);

    /**
     * Run the LoggingModule() constructor test.
     */
    @Test
    public void testLoggingModule() throws Exception {
        final LoggingModule result = new LoggingModule();
        assertNotNull(result);
    }

    /**
     * Run the void applyPolicy(Policy,AxisDescription) method test.
     *
     * @throws Exception
     */
    @Test
    public void testApplyPolicy() throws Exception {
        final LoggingModule fixture = new LoggingModule();
        final Policy policy = new Policy();
        final AxisDescription axisDescription = new AxisBinding();

        fixture.applyPolicy(policy, axisDescription);
        // add additional test code here
    }

    /**
     * Run the boolean canSupportAssertion(Assertion) method test.
     *
     * @throws Exception
     */
    @Test
    public void testCanSupportAssertion() throws Exception {
        final LoggingModule fixture = new LoggingModule();
        final Assertion assertion = null;

        final boolean result = fixture.canSupportAssertion(assertion);

        assertTrue("Methode CanSupportAssertion liefert Boolean 'false' zurueck. ", result);
    }

    /**
     * Run the void engageNotify(AxisDescription) method test.
     *
     * @throws Exception
     */
    @Test
    public void testEngageNotify() throws Exception {
        final LoggingModule fixture = new LoggingModule();
        final AxisDescription axisDescription = new AxisBinding();

        fixture.engageNotify(axisDescription);
        // add additional test code here
    }

    /**
     * Run the String[] getPolicyNamespaces() method test.
     *
     * @throws Exception
     */
    @Test
    public void testGetPolicyNamespaces() throws Exception {
        final LoggingModule fixture = new LoggingModule();

        final String[] result = fixture.getPolicyNamespaces();

        assertNull("GetPolicyNamespaces liefert kein Null zur�ck.", result);
    }

    /**
     * Run the void init(ConfigurationContext,AxisModule) method test.
     *
     * @throws Exception
     */
    @Ignore
    @Test
    public void testInit() throws Exception {

        // System.out.println(getClass().getResource("/META-INF/persistence.xml"));
        // System.out.println(Class.forName("org.hibernate.ejb.HibernatePersistence"));
        // intial a Context
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
        final InitialContext ic = new InitialContext();

        ic.createSubcontext("java:");
        ic.createSubcontext("java:/comp");
        ic.createSubcontext("java:/comp/env");

        final String ds = "logging";
        //		set the appropriate extra properties in file "domibus.default.properties". Those properties override the one get through the peristence.xml file.
        //		If the persistence unit name is not found or does not match the Persistence Provider, null is returned This method is used in a non managed environment
        final String ds1 = getClass().getResource("/META-INF/domibus.default.properties").getPath();

        ic.bind("java:/comp/env/domibus.persistence.unit", ds);
        ic.bind("java:/comp/env/domibus.persistence.properties", ds1);
        ic.close();
        //      instance of new LoggingModule
        final LoggingModule fixture = new LoggingModule();

        final ConfigurationContext configContext = new ConfigurationContext(new AxisConfiguration());
        final AxisModule module = new AxisModule();

        try {
            fixture.init(configContext, module);
        } catch (Exception e) {
            log.error("Problem occured during initializing LoggingModule", e);
            fail("Failure by initial LoggingModule!");
        }
        //      now we have Constants.store
        final LoggerEventDAO led = new LoggerEventDAO();

        // new Object "LoggerEvent"
        final LoggerEvent loggerevent = new LoggerEvent();
        final String id = "9ac091593ed04172013ed0441922001f";
        loggerevent.setId(id);
        final Date lOGDate = new Date();
        loggerevent.setLOGDate(lOGDate);
        final String logger = "eu.domibus.backend.service.DownloadMessageService";
        loggerevent.setLogger(logger);
        led.persist(loggerevent);
        //final JpaUtil dbs = Constants.store;
        // find the loggerevent with Id="9ac091593ed04172013ed0441922001f";
        final String query = "from LoggerEvent lo";

        //TODO: fix it, this method has been removed
        //final List<LoggerEvent> result = dbs.findAll(query);
        //assertTrue(result.size() == 1);
    }

    /**
     * Run the void shutdown(ConfigurationContext) method test.
     *
     * @throws Exception
     */
    @Test
    public void testShutdown() throws Exception {
        final LoggingModule fixture = new LoggingModule();
        final ConfigurationContext configCtx = new ConfigurationContext(new AxisConfiguration());

        fixture.shutdown(configCtx);

    }

    /**
     * Perform pre-test initialization.
     *
     * @throws Exception if the initialization fails for some reason
     */
    @Before
    public void setUp() throws Exception {
        // Create initial context

        //log.log(eu.domibus.logging.level.Message.MESSAGE, "string");

    }

    /**
     * Perform post-test clean-up.
     *
     * @throws Exception if the clean-up fails for some reason
     */
    @After
    public void tearDown() throws Exception {

    }

    /**
     * Launch the test.
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        new org.junit.runner.JUnitCore().run(LoggingModuleTest.class);
    }
}
