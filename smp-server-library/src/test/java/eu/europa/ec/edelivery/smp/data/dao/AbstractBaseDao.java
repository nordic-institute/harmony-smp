package eu.europa.ec.edelivery.smp.data.dao;


import eu.europa.ec.edelivery.smp.config.H2JPATestConfiguration;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {H2JPATestConfiguration.class,
        ServiceGroupDao.class, ServiceMetadataDao.class, DomainDao.class, UserDao.class})
@Sql(scripts = "classpath:cleanup-database.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig
        (transactionMode = SqlConfig.TransactionMode.ISOLATED,
                transactionManager = "transactionManager",
                dataSource = "h2DataSource"))
public abstract class AbstractBaseDao {


}
