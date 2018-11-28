package eu.europa.ec.edelivery.smp.ui;


import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.config.PropertiesTestConfig;
import eu.europa.ec.edelivery.smp.config.SmpAppConfig;
import eu.europa.ec.edelivery.smp.config.SmpWebAppConfig;
import eu.europa.ec.edelivery.smp.config.SpringSecurityConfig;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
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

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PropertiesTestConfig.class,
        SmpAppConfig.class,
        SmpWebAppConfig.class,
        SpringSecurityConfig.class})
@WebAppConfiguration
@Sql("classpath:/cleanup-database.sql")
@Sql("classpath:/webapp_integration_test_data.sql")
@SqlConfig(encoding = "UTF-8")
@TestPropertySource(properties = {
        "smp.artifact.name=TestApplicationSmpName",
        "smp.artifact.version=TestApplicationVersion",
        "smp.artifact.build.time=2018-11-27 00:00:00",
        "bdmsl.integration.enabled=true"})

public class KeystoreResourceTest {
    private static final String PATH = "/ui/rest/keystore";

    Path resourceDirectory = Paths.get("src", "test", "resources",  "keystores", "smp-keystore.jks");

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private UIKeystoreService uiKeystoreService;

    private MockMvc mvc;
    private static final RequestPostProcessor SYSTEM_CREDENTIALS = httpBasic("sys_admin", "test123");

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(webAppContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();


        initServletContext();
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
        MvcResult result = mvc.perform(get(PATH).with(SYSTEM_CREDENTIALS)).
                andExpect(status().isOk()).andReturn();

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
                .content("invalid keystore")).
                andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        KeystoreImportResult res = mapper.readValue(result.getResponse().getContentAsString(), KeystoreImportResult.class);

        assertNotNull(res);
        assertEquals("IOException occurred while reading the keystore: Invalid keystore format", res.getErrorMessage());
    }

    @Test
    public void uploadKeystoreInvalidPassword() throws Exception {

        // given when
        MvcResult result = mvc.perform(post(PATH+"/3/upload/JKS/NewPassword1234")
                .with(SYSTEM_CREDENTIALS)
                .content(Files.readAllBytes(resourceDirectory)) )
                .andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        KeystoreImportResult res = mapper.readValue(result.getResponse().getContentAsString(), KeystoreImportResult.class);

        assertNotNull(res);
        assertEquals("IOException occurred while reading the keystore: Keystore was tampered with, or password was incorrect", res.getErrorMessage());
    }

    @Test
    public void uploadKeystoreOK() throws Exception {

        int countStart = uiKeystoreService.getKeystoreEntriesList().size();
        // given when
        MvcResult result = mvc.perform(post(PATH+"/3/upload/JKS/test123")
                .with(SYSTEM_CREDENTIALS)
                .content(Files.readAllBytes(resourceDirectory)) )
                .andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        KeystoreImportResult res = mapper.readValue(result.getResponse().getContentAsString(), KeystoreImportResult.class);

        assertNotNull(res);
        assertNull(res.getErrorMessage());
        assertEquals(countStart+1, uiKeystoreService.getKeystoreEntriesList().size());
    }



}