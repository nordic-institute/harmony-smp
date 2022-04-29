package eu.europa.ec.edelivery.smp.ui.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataValidationRO;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_PUBLIC_SERVICE_METADATA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SmpTestWebAppConfig.class})
@Sql(scripts = {
        "classpath:/cleanup-database.sql",
        "classpath:/webapp_integration_test_data.sql"},
        executionPhase = BEFORE_TEST_METHOD)
public class ServiceMetadataResourceIntegrationTest {


    // For the following test data see the: webapp_integration_test_data.sql
    private static final Long SERVICE_METADATA_ID = 1000L;
    private static final String DOC_IDENTIFIER = "doc_7";
    private static final String DOC_SCHEME = "busdox-docid-qns";


    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mvc;
    private static final RequestPostProcessor SMP_ADMIN_CREDENTIALS = httpBasic("smp_admin", "test123");
    private static final RequestPostProcessor SG_USER2_CREDENTIALS = httpBasic("test_user_hashed_pass", "test123");
    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() throws IOException {
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
    public void getServiceGroupMetadataById() throws Exception {
        // given when
        MvcResult result = mvc.perform(get(CONTEXT_PATH_PUBLIC_SERVICE_METADATA + "/" + SERVICE_METADATA_ID)
                .with(SMP_ADMIN_CREDENTIALS).with(csrf())
        ).andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        ServiceMetadataRO res = mapper.readValue(result.getResponse().getContentAsString(), ServiceMetadataRO.class);

        assertNotNull(res);
        assertNotNull(res.getXmlContent());
        assertEquals(SERVICE_METADATA_ID, res.getId());
        assertEquals(DOC_IDENTIFIER, res.getDocumentIdentifier());
        assertEquals(DOC_SCHEME, res.getDocumentIdentifierScheme());
    }

    @Test
    public void getServiceGroupMetadataByIdNotAuthorized() throws Exception {
        // given when
        MvcResult result = mvc.perform(get(CONTEXT_PATH_PUBLIC_SERVICE_METADATA + "/" + SERVICE_METADATA_ID)
                .with(SG_USER2_CREDENTIALS).with(csrf())
        ).andExpect(status().isUnauthorized()).andReturn();

    }

    @Test
    public void validateServiceMetadataUnauthorized() throws Exception {
        ServiceMetadataValidationRO smv = new ServiceMetadataValidationRO();
        smv.setDocumentIdentifier("documentId");
        smv.setDocumentIdentifierScheme("documentScheme");
        smv.setParticipantIdentifier("partId");
        smv.setParticipantScheme("partSch");
        smv.setXmlContent("Invalid content");

        mvc.perform(post(CONTEXT_PATH_PUBLIC_SERVICE_METADATA + "/validate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(smv))
        ).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    public void validateServiceMetadata() throws Exception {
        ServiceMetadataValidationRO smv = new ServiceMetadataValidationRO();
        smv.setDocumentIdentifier("documentId");
        smv.setDocumentIdentifierScheme("documentScheme");
        smv.setParticipantIdentifier("partId");
        smv.setParticipantScheme("partSch");
        smv.setXmlContent("Invalid content");

        MvcResult result = mvc.perform(post(CONTEXT_PATH_PUBLIC_SERVICE_METADATA + "/validate")
                .with(SG_USER2_CREDENTIALS)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(smv))
        ).andExpect(status().isOk()).andReturn();

        ServiceMetadataValidationRO res = mapper.readValue(result.getResponse().getContentAsString(),
                ServiceMetadataValidationRO.class);

        assertEquals("SAXParseException: Content is not allowed in prolog.", res.getErrorMessage());
    }
}