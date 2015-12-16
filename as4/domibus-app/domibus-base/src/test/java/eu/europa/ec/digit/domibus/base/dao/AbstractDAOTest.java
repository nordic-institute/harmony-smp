package eu.europa.ec.digit.domibus.base.dao;


import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.digit.domibus.base.config.domibus.DomibusBaseConfiguration;

/**
 * Abstract class for DAO integration tests
 *
 */
@RunWith (SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    loader = AnnotationConfigContextLoader.class,
    classes = {DomibusBaseConfiguration.class})
@ActiveProfiles ("testing")
@TransactionConfiguration (defaultRollback=true)
@Transactional
public abstract class AbstractDAOTest extends AbstractTransactionalJUnit4SpringContextTests {

    /* ---- Constants ---- */

    /* ---- Instance Variables ---- */

	@Autowired
	private JdbcTemplate jdbcTemplate = null;

    /* ---- Constructors ---- */

    /* ---- Business Methods ---- */

    /**
     * Executes a sql script before the start of any test. Script does
     * not run if file name is not provided.
     */
    @Before
    public void runScriptBeforeStart() {
        executeScript(getRunSqlScriptBeforeStart());
    }

    /**
     * Executes a sql script after a test has been executed. Script does
     * not run if file name is not provided.
     */
    @After
    public void runScriptAfterStart() {
        executeScript(getRunSqlScriptAfterEnd());
    }

    /**
     * Execute a series of SQL scripts.
     *
     * @param fileNames - list of SQL scripts
     */
    private void executeScript(List<String> fileNames) {
        if (fileNames != null) {
            for (String fname : fileNames) {
                if (fname != null) {
                    runScript(new ClassPathResource(fname));
                }
            }
        }
    }

    /**
     * Returns the fileName of the sql-script to run before
     * each test.
     *
     * @return String the fileName
     */
    public List<String> getRunSqlScriptBeforeStart() {
        return new ArrayList<String>(0);
    }

    /**
     * Returns the fileName of the sql-script to run after
     * each test.
     *
     * @return String the fileName
     */
    public List<String> getRunSqlScriptAfterEnd() {
        return new ArrayList<String>(0);
    }

    /**
     * Run the actual script using the simpleJdbcTemplate.
     *
     * @param resource the sql script
     */
    private void runScript(Resource resource) {
        JdbcTestUtils.executeSqlScript(
            jdbcTemplate,
            resource,
            Boolean.FALSE);
    }
}
