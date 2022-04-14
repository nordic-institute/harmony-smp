package eu.europa.ec.edelivery.smp.ui.internal;


import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.config.PropertiesTestConfig;
import eu.europa.ec.edelivery.smp.config.SmpAppConfig;
import eu.europa.ec.edelivery.smp.config.SmpWebAppConfig;
import eu.europa.ec.edelivery.smp.config.WSSecurityConfigurerAdapter;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.testutils.X509CertificateTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_INTERNAL_KEYSTORE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PropertiesTestConfig.class,
        SmpAppConfig.class,
        SmpWebAppConfig.class,
        WSSecurityConfigurerAdapter.class})
@WebAppConfiguration
@SqlConfig(encoding = "UTF-8")
@Sql(scripts = {"classpath:cleanup-database.sql",
        "classpath:webapp_integration_test_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class KeystoreResourceTest {
    private static final String PATH = CONTEXT_PATH_INTERNAL_KEYSTORE;
    Path keystore = Paths.get("src", "test", "resources",  "keystores", "smp-keystore.jks");

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private UIKeystoreService uiKeystoreService;

    private MockMvc mvc;
    private static final RequestPostProcessor SYSTEM_CREDENTIALS = httpBasic("sys_admin", "test123");

    @Before
    public void setup() throws IOException {
        X509CertificateTestUtils.reloadKeystores();

        mvc = MockMvcBuilders.webAppContextSetup(webAppContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        initServletContext();
        uiKeystoreService.refreshData();
    }

    private void initServletContext() {
        MockServletContext sc = new MockServletContext("");
        ServletContextListener listener = new ContextLoaderListener(webAppContext);
        ServletContextEvent event = new ServletContextEvent(sc);
    }

    @Test
    public void getKeyCertificateList() throws Exception {
        // given when
        int countStart = uiKeystoreService.getKeystoreEntriesList().size();
        MvcResult result = mvc.perform(get(PATH)
                .with(SYSTEM_CREDENTIALS)
                .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        ServiceResult res = mapper.readValue(result.getResponse().getContentAsString(), ServiceResult.class);


        assertNotNull(res);
        assertEquals(countStart, res.getServiceEntities().size());
        res.getServiceEntities().forEach(sgMap -> {
            CertificateRO cert = mapper.convertValue(sgMap, CertificateRO.class);
            assertNotNull(cert.getAlias());
            assertNotNull(cert.getCertificateId());
            assertNotNull(cert.getBlueCoatHeader());
            assertNull(cert.getEncodedValue()); // submit only metadata
        });
    }

    @Test
    public void uploadKeystoreFailed() throws Exception {
        // given when
        MvcResult result = mvc.perform(post(PATH+"/3/upload/JKS/test123")
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .content("invalid keystore")).
                andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        KeystoreImportResult res = mapper.readValue(result.getResponse().getContentAsString(), KeystoreImportResult.class);

        assertNotNull(res);
        assertEquals("java.io.IOException occurred while reading the keystore: Invalid keystore format", res.getErrorMessage());
    }

    @Test
    public void uploadKeystoreInvalidPassword() throws Exception {

        // given when
        MvcResult result = mvc.perform(post(PATH+"/3/upload/JKS/NewPassword1234")
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .content(Files.readAllBytes(keystore)) )
                .andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        KeystoreImportResult res = mapper.readValue(result.getResponse().getContentAsString(), KeystoreImportResult.class);

        assertNotNull(res);
        assertEquals("java.io.IOException occurred while reading the keystore: Keystore was tampered with, or password was incorrect", res.getErrorMessage());
    }

    @Test
    public void uploadKeystoreOK() throws Exception {

        int countStart = uiKeystoreService.getKeystoreEntriesList().size();
        // given when
        MvcResult result = mvc.perform(post(PATH+"/3/upload/JKS/test123")
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .content(Files.readAllBytes(keystore)) )
                .andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        KeystoreImportResult res = mapper.readValue(result.getResponse().getContentAsString(), KeystoreImportResult.class);

        assertNotNull(res);
        assertNull(res.getErrorMessage());
        assertEquals(countStart+1, uiKeystoreService.getKeystoreEntriesList().size());
    }

    @Test
    public void deleteKeystoreEntryOK() throws Exception {

        int countStart = uiKeystoreService.getKeystoreEntriesList().size();
        // given when
        MvcResult result = mvc.perform(delete(PATH+"/3/delete/second_domain_alias")
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .content(Files.readAllBytes(keystore)) )
                .andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        KeystoreImportResult res = mapper.readValue(result.getResponse().getContentAsString(), KeystoreImportResult.class);

        assertNotNull(res);
        assertNull(res.getErrorMessage());
        assertEquals(countStart-1, uiKeystoreService.getKeystoreEntriesList().size());
    }

}