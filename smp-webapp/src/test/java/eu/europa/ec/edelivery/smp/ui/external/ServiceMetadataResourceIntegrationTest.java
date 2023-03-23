package eu.europa.ec.edelivery.smp.ui.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataValidationRO;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import javax.ws.rs.core.MediaType;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.*;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_PUBLIC_SERVICE_METADATA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        mvc = MockMvcUtils.initializeMockMvc(webAppContext);
    }

    @Test
    public void getServiceGroupMetadataById() throws Exception {
        // given when
        MockHttpSession sessionAdmin = loginWithUserGroupAdmin(mvc);
        MvcResult result = mvc.perform(get(CONTEXT_PATH_PUBLIC_SERVICE_METADATA + "/" + SERVICE_METADATA_ID)
                .session(sessionAdmin).with(csrf())
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
        MockHttpSession session = loginWithUser2(mvc);
        MvcResult result = mvc.perform(get(CONTEXT_PATH_PUBLIC_SERVICE_METADATA + "/" + SERVICE_METADATA_ID)
                .session(session).with(csrf())
        ).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    public void getServiceGroupMetadataByIdNotAuthorizedForBasicAuthentication() throws Exception {
        // given when
        MvcResult result = mvc.perform(get(CONTEXT_PATH_PUBLIC_SERVICE_METADATA + "/" + SERVICE_METADATA_ID)
                .with(getHttpBasicSMPAdminCredentials()).with(csrf())
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
    @Ignore
    public void validateServiceMetadata() throws Exception {
        ServiceMetadataValidationRO smv = new ServiceMetadataValidationRO();
        smv.setDocumentIdentifier("documentId");
        smv.setDocumentIdentifierScheme("documentScheme");
        smv.setParticipantIdentifier("partId");
        smv.setParticipantScheme("partSch");
        smv.setXmlContent("Invalid content");

        MockHttpSession session = loginWithUserGroupAdmin(mvc);

        MvcResult result = mvc.perform(post(CONTEXT_PATH_PUBLIC_SERVICE_METADATA + "/validate")
                .session(session)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(smv))
        ).andExpect(status().isOk()).andReturn();


        ServiceMetadataValidationRO res = mapper.readValue(result.getResponse().getContentAsString(),
                ServiceMetadataValidationRO.class);

        assertEquals("SAXParseException: Content is not allowed in prolog.", res.getErrorMessage());
    }
}
