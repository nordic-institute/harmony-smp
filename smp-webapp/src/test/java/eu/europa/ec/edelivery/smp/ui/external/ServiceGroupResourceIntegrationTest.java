package eu.europa.ec.edelivery.smp.ui.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.data.dao.ResourceDao;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupValidationRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
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

import java.io.IOException;
import java.util.Arrays;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.*;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SmpTestWebAppConfig.class})
@Sql(scripts = {
        "classpath:/cleanup-database.sql",
        "classpath:/webapp_integration_test_data.sql"},
        executionPhase = BEFORE_TEST_METHOD)
public class ServiceGroupResourceIntegrationTest {

    @Autowired
    ResourceDao serviceGroupDao;

    private static final String PATH_PUBLIC = ResourceConstants.CONTEXT_PATH_PUBLIC_SERVICE_GROUP;

    private static final String IDENTIFIER_VALUE = "urn:australia:ncpb";
    private static final String IDENTIFIER_SCHEME = "ehealth-actorid-qns";

    private String validExtension = null;

    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mvc;
    @Before
    public void setup() throws IOException {
        mvc = MockMvcUtils.initializeMockMvc(webAppContext);
        validExtension = new String(IOUtils.toByteArray(ServiceGroupResourceIntegrationTest.class.getResourceAsStream("/input/extensionMarshal.xml")));
    }

    @Test
    public void getServiceGroupListForSMPAdmin() throws Exception {
        // given when
        MockHttpSession sessionAdmin = loginWithUserGroupAdmin(mvc);
        MvcResult result = mvc.perform(get(PATH_PUBLIC)
                .session(sessionAdmin).with(csrf())
        ).andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        ServiceResult res = mapper.readValue(result.getResponse().getContentAsString(), ServiceResult.class);


        assertNotNull(res);
        assertEquals(2, res.getServiceEntities().size());
        res.getServiceEntities().forEach(sgMap -> {
            ServiceGroupRO sgro = mapper.convertValue(sgMap, ServiceGroupRO.class);
            assertNotNull(sgro.getId());
            assertNotNull(sgro.getParticipantScheme());
            assertNotNull(sgro.getParticipantIdentifier());
            assertEquals(1, sgro.getUsers().size());
            assertNotEquals("smp_admin", sgro.getUsers().get(0).getUsername());
        });
    }

    @Test
    public void getServiceGroupListForServiceGroupAdmin() throws Exception {
        // given when
        MockHttpSession sessionAdmin = loginWithUserGroupAdmin(mvc);
        MvcResult result = mvc.perform(get(PATH_PUBLIC)
                .session(sessionAdmin).with(csrf())
        ).andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        ServiceResult res = mapper.readValue(result.getResponse().getContentAsString(), ServiceResult.class);

        assertNotNull(res);
        assertFalse(res.getServiceEntities().isEmpty());
        res.getServiceEntities().forEach(sgMap -> {
            ServiceGroupRO sgro = mapper.convertValue(sgMap, ServiceGroupRO.class);
            assertNotNull(sgro.getId());
            assertNotNull(sgro.getParticipantScheme());
            assertNotNull(sgro.getParticipantIdentifier());
            assertTrue(Arrays.asList("urn:australia:ncpb","urn:brazil:ncpb").contains(sgro.getParticipantIdentifier()));
            assertEquals(1, sgro.getUsers().size());
            assertNotNull(sgro.getUsers().get(0).getUserId());
        });
    }

    @Test
    public void getServiceGroupById() throws Exception {
        // given when
        MockHttpSession sessionAdmin = loginWithUserGroupAdmin(mvc);
        MvcResult result = mvc.perform(get(PATH_PUBLIC + "/100000")
                .session(sessionAdmin).with(csrf())).
                andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        ServiceGroupRO res = mapper.readValue(result.getResponse().getContentAsString(), ServiceGroupRO.class);

        assertNotNull(res);
        assertEquals(100000, res.getId().intValue());
        assertEquals(IDENTIFIER_VALUE, res.getParticipantIdentifier());
        assertEquals(IDENTIFIER_SCHEME, res.getParticipantScheme());
        assertEquals(1, res.getUsers().size());
        assertNotNull(res.getUsers().get(0).getUserId());

        assertEquals(1, res.getServiceGroupDomains().size());
        assertEquals(1, res.getServiceMetadata().size());
        assertEquals("doc_7", res.getServiceMetadata().get(0).getDocumentIdentifier());
        assertEquals(res.getServiceGroupDomains().get(0).getId(), res.getServiceMetadata().get(0).getServiceGroupDomainId());
    }

    @Test
    public void getExtensionServiceGroupById() throws Exception {

        DBResource sg = serviceGroupDao.findServiceGroup(IDENTIFIER_VALUE, IDENTIFIER_SCHEME).get();
        sg.setExtension(validExtension.getBytes());
        serviceGroupDao.update(sg);

        // given when
        MockHttpSession sessionAdmin = loginWithUserGroupAdmin(mvc);
        MvcResult result = mvc.perform(get(PATH_PUBLIC + "/100000/extension")
                .session(sessionAdmin).with(csrf()))
                .andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        ServiceGroupValidationRO res = mapper.readValue(result.getResponse().getContentAsString(), ServiceGroupValidationRO.class);

        assertNotNull(res);
        assertEquals(100000, res.getServiceGroupId().longValue());
        assertEquals(IDENTIFIER_VALUE, res.getParticipantIdentifier());
        assertEquals(IDENTIFIER_SCHEME, res.getParticipantScheme());
        assertEquals(new String(sg.getExtension()), res.getExtension());
    }

    @Test
    public void getExtensionServiceGroupByIdNotAuthorizedBasicAuthentication() throws Exception {
        // given when
        MvcResult result = mvc.perform(get(PATH_PUBLIC + "/100000/extension")
                .with(getHttpBasicSMPAdminCredentials()).with(csrf()))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    public void testValidateInvalid() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ServiceGroupValidationRO validate = new ServiceGroupValidationRO();
        validate.setExtension(validExtension + "<ADFA>sdfadsf");

        // given when
        MockHttpSession sessionAdmin = loginWithUserGroupAdmin(mvc);
        MvcResult result = mvc.perform(post(PATH_PUBLIC + "/extension/validate")
                .session(sessionAdmin)
                .header("Content-Type","application/json")
                    .content(mapper.writeValueAsString(validate))
                .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        //then
        ServiceGroupValidationRO res = mapper.readValue(result.getResponse().getContentAsString(), ServiceGroupValidationRO.class);

        assertNotNull(res);
        assertNotNull(res.getErrorMessage());
    }


}
