package eu.europa.ec.edelivery.smp.ui.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils;
import eu.europa.ec.edelivery.smp.test.testutils.X509CertificateTestUtils;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SmpTestWebAppConfig.class})
@Sql(scripts = {
        "/cleanup-database.sql",
        "/webapp_integration_test_data.sql"})
public class DomainResourceIntegrationTest {
    private static final String PATH = ResourceConstants.CONTEXT_PATH_PUBLIC_DOMAIN;

    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    DomainDao domainDao;
    @Autowired
    private ConfigurationDao configurationDao;


    private MockMvc mvc;

    @Before
    public void setup() throws IOException {
        X509CertificateTestUtils.reloadKeystores();
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED, "true", null);
        configurationDao.reloadPropertiesFromDatabase();
        mvc = MockMvcUtils.initializeMockMvc(webAppContext);
        configurationDao.contextRefreshedEvent();
    }

    @Test
    public void geDomainPublicList() throws Exception {

        // given when
        MvcResult result = mvc.perform(get(PATH)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        //then
        ObjectMapper mapper = new ObjectMapper();
        ServiceResult res = mapper.readValue(result.getResponse().getContentAsString(), ServiceResult.class);


        assertNotNull(res);
        assertEquals(2, res.getServiceEntities().size());
        res.getServiceEntities().forEach(sgMap -> {
            DomainRO sgro = mapper.convertValue(sgMap, DomainRO.class);
            assertNotNull(sgro.getDomainCode());
            assertNotNull(sgro.getSmlSubdomain());
            // for public endpoint all other data must be null!
            assertNull(sgro.getDomainId());
            assertNull(sgro.getSmlSmpId());
            assertNull(sgro.getSignatureKeyAlias());
        });
    }
}
