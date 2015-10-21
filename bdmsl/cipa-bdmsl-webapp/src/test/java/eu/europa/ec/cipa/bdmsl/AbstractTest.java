package eu.europa.ec.cipa.bdmsl;

import eu.europa.ec.cipa.bdmsl.mock.DnsMessageSenderServiceMock;
import eu.europa.ec.cipa.bdmsl.service.dns.IDnsMessageSenderService;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Created by feriaad on 29/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testApplicationContext.xml")
@DirtiesContext
@TransactionConfiguration(defaultRollback = true)
public abstract class AbstractTest {

    private static boolean initialized;

    @Autowired
    protected DataSource dataSource;

    @Autowired
    private IDnsMessageSenderService dnsMessageSenderService;

    @Before
    public void init() throws Exception {
        if (!initialized) {
            Class.forName("org.h2.Driver");
            Connection conn = dataSource.getConnection();
            Liquibase liquibase = new Liquibase("liquibase/db.changelog-master.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(conn));
            liquibase.update("");
            liquibase = new Liquibase("liquibase/db.changelog-test-data.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(conn));
            liquibase.update("");
            initialized = true;
        }
        ((DnsMessageSenderServiceMock)dnsMessageSenderService).reset();
    }
}
