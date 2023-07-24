package eu.europa.ec.edelivery.smp.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils;
import eu.europa.ec.edelivery.smp.test.testutils.X509CertificateTestUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;


@RunWith(SpringRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = {SmpTestWebAppConfig.class})
@Sql(scripts = {
        "classpath:/cleanup-database.sql",
        "classpath:/webapp_integration_test_data.sql"})
abstract public class AbstractControllerTest {
    protected MockMvc mvc;
    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private ConfigurationDao configurationDao;

    public void setup() throws IOException {
        X509CertificateTestUtils.reloadKeystores();
        mvc = MockMvcUtils.initializeMockMvc(webAppContext);
        configurationDao.reloadPropertiesFromDatabase();
    }

    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
