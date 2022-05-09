package eu.europa.ec.edelivery.smp.config;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import static org.junit.Assert.*;

public class DatabaseConfigTest {

    DatabaseConfig testInstance = new DatabaseConfig(){{
        // test properties from persistence-test-h2.properties
        jndiDatasourceName =null;
        driver = "org.h2.Driver";
        url = "jdbc:h2:file:./target/myDb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE;AUTO_SERVER=TRUE";
        username = "smp-dev";
        password = "smp-dev";
    }};

    @Test
    public void getDataSource() {

        DataSource result = testInstance.getDataSource();

        Assert.assertNotNull(result);
        Assert.assertEquals(DriverManagerDataSource.class, result.getClass());
    }
    @Test
    public void jpaVendorAdapter() {
        JpaVendorAdapter result = testInstance.jpaVendorAdapter();

        Assert.assertNotNull(result);
    }

    @Test
    public void smpEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean result = testInstance.smpEntityManagerFactory(testInstance.getDataSource(), testInstance.jpaVendorAdapter());
        Assert.assertNotNull(result);
    }

    @Test
    public void smpTransactionManager() {
        EntityManagerFactory entityManagerFactory = testInstance.smpEntityManagerFactory(testInstance.getDataSource(), testInstance.jpaVendorAdapter()).getObject();
        PlatformTransactionManager result = testInstance.smpTransactionManager(entityManagerFactory);
        Assert.assertNotNull(result);
    }


}