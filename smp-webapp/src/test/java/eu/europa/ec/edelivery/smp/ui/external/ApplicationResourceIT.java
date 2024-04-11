/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.ui.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.ui.SmpConfigRO;
import eu.europa.ec.edelivery.smp.data.ui.SmpInfoRO;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.loginWithSystemAdmin;
import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.loginWithUserGroupAdmin;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SmpTestWebAppConfig.class})
@Sql(scripts = {
        "classpath:/cleanup-database.sql",
        "classpath:/webapp_integration_test_data.sql"},
        executionPhase = BEFORE_TEST_METHOD)
@TestPropertySource(properties = {
        "smp.artifact.name=TestApplicationSmpName",
        "smp.artifact.version=TestApplicationVersion",
        "smp.artifact.build.time=2018-11-27 00:00:00",
})
class ApplicationResourceIT {
    private static final String PATH = ResourceConstants.CONTEXT_PATH_PUBLIC_APPLICATION;

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private ApplicationController applicationResource;

    private MockMvc mvc;

    @BeforeEach
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
    void testGetName() throws Exception {
        String value = mvc.perform(get(PATH + "/name"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("\"TestApplicationSmpName\"", value);
    }

    @Test
    void getDisplayName() throws Exception {
        String value = applicationResource.getDisplayVersion();
        MatcherAssert.assertThat(value, startsWith("TestApplicationSmpName Version [TestApplicationVersion] Build-Time [2018-11-27 00:00:00"));
    }

    @Test
    void getApplicationInfoTest() throws Exception {
        String value = mvc.perform(get(PATH + "/info"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        SmpInfoRO info = mapper.readValue(value, SmpInfoRO.class);

        MatcherAssert.assertThat(info.getVersion(), startsWith("TestApplicationSmpName Version [TestApplicationVersion] Build-Time [2018-11-27 00:00:00"));
        assertEquals("/", info.getContextPath());
    }

    @Test
    void testGetApplicationConfigNotAuthorized() throws Exception {
        // when
        mvc.perform(get(PATH + "/config")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
    }

    @Test
    void testGetApplicationConfigAuthorized() throws Exception {

        //  User
        MockHttpSession sessionUser = loginWithUserGroupAdmin(mvc);
        String val = mvc.perform(get(PATH + "/config")
                        .session(sessionUser)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(val);
        // system admin
        MockHttpSession sessionSystem = loginWithSystemAdmin(mvc);
        val = mvc.perform(get(PATH + "/config")
                        .session(sessionSystem)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(val);
    }

    @Test
    void testGetApplicationConfigUser() throws Exception {
        // when
        MockHttpSession session = loginWithUserGroupAdmin(mvc);
        String value = mvc.perform(get(PATH + "/config")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        ObjectMapper mapper = new ObjectMapper();
        SmpConfigRO res = mapper.readValue(value, SmpConfigRO.class);

        assertNotNull(res);
        assertEquals("Participant scheme must start with:urn:oasis:names:tc:ebcore:partyid-type:(iso6523:|unregistered:) OR must be up to 25 characters long with form [domain]-[identifierArea]-[identifierType] (ex.: 'busdox-actorid-upis') and may only contain the following characters: [a-z0-9].", res.getParticipantSchemaRegExpMessage());
        assertEquals("^$|^(?!^.{26})([a-z0-9]+-[a-z0-9]+-[a-z0-9]+)$|^urn:oasis:names:tc:ebcore:partyid-type:(iso6523|unregistered)(:.+)?$", res.getParticipantSchemaRegExp());
        assertEquals(SMPPropertyEnum.PARTC_EBCOREPARTYID_CONCATENATE.getDefValue(), String.valueOf(res.isConcatEBCorePartyId()));
        assertFalse(res.isSmlIntegrationOn());
    }
}
