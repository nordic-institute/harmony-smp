package eu.europa.ec.edelivery.smp.ui.internal;


import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.config.PropertiesTestConfig;
import eu.europa.ec.edelivery.smp.config.SmpAppConfig;
import eu.europa.ec.edelivery.smp.config.SmpWebAppConfig;
import eu.europa.ec.edelivery.smp.config.SpringSecurityConfig;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.error.UIErrorControllerAdvice;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import eu.europa.ec.edelivery.smp.testutils.X509CertificateTestUtils;
import eu.europa.ec.edelivery.smp.ui.UserResourceTest;
import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Ignore;
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
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_INTERNAL_TRUSTSTORE;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_PUBLIC_TRUSTSTORE;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PropertiesTestConfig.class,
        SmpAppConfig.class,
        SmpWebAppConfig.class,
        SpringSecurityConfig.class,
        UIErrorControllerAdvice.class})
@WebAppConfiguration
@SqlConfig(encoding = "UTF-8")
@Sql(scripts = {"classpath:cleanup-database.sql",
        "classpath:webapp_integration_test_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TruststoreAdminResourceTest {
    private static final String PATH_INTERNAL = CONTEXT_PATH_INTERNAL_TRUSTSTORE;
    private static final String PATH_PUBLIC = CONTEXT_PATH_PUBLIC_TRUSTSTORE;

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private UITruststoreService uiTruststoreService;

    private MockMvc mvc;
    private static final RequestPostProcessor SYSTEM_CREDENTIALS = httpBasic("sys_admin", "test123");
    private static final RequestPostProcessor ADMIN_CREDENTIALS = httpBasic("smp_admin", "test123");
    private static final RequestPostProcessor SG_ADMIN_CREDENTIALS = httpBasic("sg_admin", "test123");

    @Before
    public void setup() throws IOException {
        X509CertificateTestUtils.reloadKeystores();

        mvc = MockMvcBuilders.webAppContextSetup(webAppContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();


        initServletContext();
        uiTruststoreService.refreshData();
    }

    private void initServletContext() {
        MockServletContext sc = new MockServletContext("");
        ServletContextListener listener = new ContextLoaderListener(webAppContext);
        ServletContextEvent event = new ServletContextEvent(sc);
    }

    @Test
    public void validateInvalidCertificate() throws Exception {
        byte[] buff = (new String("Not a certficate :) ")).getBytes();

        // given when
        mvc.perform(post(PATH_PUBLIC + "/1098765430/validate-certificate")
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .content(buff))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string(CoreMatchers.containsString(" The certificate is not valid")));
    }

    @Test
    public void validateCertificateSystemAdmin() throws Exception {
        byte[] buff = IOUtils.toByteArray(UserResourceTest.class.getResourceAsStream("/SMPtest.crt"));

        // given when
        MvcResult result = mvc.perform(post(PATH_PUBLIC + "/1098765430/validate-certificate")
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .content(buff))
                .andExpect(status().isOk()).andReturn();

        //then
        ObjectMapper mapper = new ObjectMapper();
        CertificateRO res = mapper.readValue(result.getResponse().getContentAsString(), CertificateRO.class);

        assertNotNull(res);
        assertEquals("CN=Intermediate CA,O=DIGIT,C=BE", res.getIssuer());
        assertEquals("1.2.840.113549.1.9.1=#160c736d7040746573742e636f6d,CN=SMP test,O=DIGIT,C=BE", res.getSubject());
        assertEquals("3", res.getSerialNumber());
        assertEquals("CN=SMP test,O=DIGIT,C=BE:0000000000000003", res.getCertificateId());
        assertEquals("sno=3&subject=1.2.840.113549.1.9.1%3D%23160c736d7040746573742e636f6d%2CCN%3DSMP+test%2CO%3DDIGIT%2CC%3DBE&validfrom=May+22+20%3A59%3A00+2018+GMT&validto=May+22+20%3A56%3A00+2019+GMT&issuer=CN%3DIntermediate+CA%2CO%3DDIGIT%2CC%3DBE", res.getBlueCoatHeader());
    }

    @Test
    public void validateCertificateIdWithEmailSerialNumberInSubjectCertIdTest() throws Exception {
        String subject = "CN=common name,emailAddress=CEF-EDELIVERY-SUPPORT@ec.europa.eu,serialNumber=1,O=org,ST=My town,postalCode=2151, L=GreatTown,street=My Street. 20, C=BE";
        String serialNumber = "1234321";
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(serialNumber, subject);
        byte[] buff = certificate.getEncoded();
        // given when
        MvcResult result = mvc.perform(post(PATH_PUBLIC + "/1098765430/validate-certificate")
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .content(buff))
                .andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        CertificateRO res = mapper.readValue(result.getResponse().getContentAsString(), CertificateRO.class);

        assertEquals("CN=common name,O=org,C=BE:0000000001234321", res.getCertificateId());
    }

    @Test
    public void uploadCertificateInvalidUser() throws Exception {
        byte[] buff = IOUtils.toByteArray(UserResourceTest.class.getResourceAsStream("/SMPtest.crt"));
        // id and logged user not match
        // given when
        mvc.perform(post(PATH_PUBLIC + "/34556655/validate-certificate")
                .with(ADMIN_CREDENTIALS)
                .with(csrf())
                .content(buff))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    public void getCertificateList() throws Exception {
        // given when
        int countStart = uiTruststoreService.getCertificateROEntriesList().size();
        MvcResult result = mvc.perform(get(PATH_INTERNAL)
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
    public void deleteCertificateSystemAdmin() throws Exception {
        byte[] buff = IOUtils.toByteArray(UserResourceTest.class.getResourceAsStream("/SMPtest.crt"));

        int countStart = uiTruststoreService.getNormalizedTrustedList().size();
        MvcResult prepRes = mvc.perform(post(PATH_INTERNAL + "/3/upload-certificate")
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .content(buff))
                .andExpect(status().isOk()).andReturn();

        // given when
        ObjectMapper mapper = new ObjectMapper();
        CertificateRO res = mapper.readValue(prepRes.getResponse().getContentAsString(), CertificateRO.class);
        assertNotNull(res);
        uiTruststoreService.refreshData();
        assertEquals(countStart + 1, uiTruststoreService.getNormalizedTrustedList().size());

        // then
        MvcResult result = mvc.perform(delete(PATH_INTERNAL + "/3/delete/" + res.getAlias())
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .content(buff))
                .andExpect(status().isOk()).andReturn();
        uiTruststoreService.refreshData();
        assertEquals(countStart, uiTruststoreService.getNormalizedTrustedList().size());

    }

/*
    @Test
    public void uploadKeystoreOK() throws Exception {

        int countStart = uiTruststoreService.getCertificateROEntriesList().size();
        // given when
        MvcResult result = mvc.perform(post(PATH+"/3/upload/JKS/test123")
                .with(SYSTEM_CREDENTIALS)
                .content(Files.readAllBytes(keystore)) )
                .andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        KeystoreImportResult res = mapper.readValue(result.getResponse().getContentAsString(), KeystoreImportResult.class);

        assertNotNull(res);
        assertNull(res.getErrorMessage());
        assertEquals(countStart+1, uiTruststoreService.getCertificateROEntriesList().size());
    }*/
/*
    @Test
    public void deleteKeystoreEntryOK() throws Exception {

        int countStart = uiTruststoreService.getKeystoreEntriesList().size();
        // given when
        MvcResult result = mvc.perform(delete(PATH+"/3/delete/second_domain_alias")
                .with(SYSTEM_CREDENTIALS)
                .content(Files.readAllBytes(keystore)) )
                .andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        KeystoreImportResult res = mapper.readValue(result.getResponse().getContentAsString(), KeystoreImportResult.class);

        assertNotNull(res);
        assertNull(res.getErrorMessage());
        assertEquals(countStart-1, uiTruststoreService.getKeystoreEntriesList().size());
    }
*/

    public List<CertificateRO> getCertificateFromEndpointList() throws Exception {
        // given when
        MvcResult result = mvc.perform(get(PATH_INTERNAL).with(SYSTEM_CREDENTIALS)).
                andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        ServiceResult res = mapper.readValue(result.getResponse().getContentAsString(), ServiceResult.class);


        List<CertificateRO> list = new ArrayList<>();
        res.getServiceEntities().forEach(sgMap -> {
            CertificateRO cert = mapper.convertValue(sgMap, CertificateRO.class);
            list.add(cert);
            assertNotNull(cert.getAlias());
        });
        return list;
    }
}