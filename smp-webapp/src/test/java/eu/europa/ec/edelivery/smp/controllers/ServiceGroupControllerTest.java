/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.controllers;

import eu.europa.ec.edelivery.smp.config.SmpAppConfig;
import eu.europa.ec.edelivery.smp.config.SmpWebAppConfig;
import eu.europa.ec.edelivery.smp.config.SpringSecurityConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static eu.europa.ec.edelivery.smp.ServiceGroupBodyUtil.getSampleServiceGroupBodyWithScheme;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gutowpa on 02/08/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        SmpAppConfig.class,
        SmpWebAppConfig.class,
        SpringSecurityConfig.class})
@WebAppConfiguration
@Transactional
@Rollback(true)
@Sql("classpath:/webapp_integration_test_data.sql")
public class ServiceGroupControllerTest {

    private static final String PARTICIPANT_SCHEME = "ehealth-participantid-qns";
    private static final String PARTICIPANT_ID = "urn:poland:ncpb";
    private static final String URL_PATH = format("/%s::%s", PARTICIPANT_SCHEME, PARTICIPANT_ID);
    private final String SERVICE_GROUP_INPUT_BODY = getSampleServiceGroupBodyWithScheme(PARTICIPANT_SCHEME);

    private static final RequestPostProcessor ADMIN_CREDENTIALS = httpBasic("test_admin", "gutek123");

    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mvc;

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
        listener.contextInitialized(event);
    }

    @Test
    public void notFoundIsReturnedWhenServiceGroupDoesNotExtist() throws Exception {
        mvc.perform(get(URL_PATH))
                .andExpect(status().isNotFound());
    }

    @Test
    public void adminCanCreateServiceGroup() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content(SERVICE_GROUP_INPUT_BODY))
                .andExpect(status().isCreated());
    }

    @Test
    public void adminCanUpdateServiceGroup() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content(SERVICE_GROUP_INPUT_BODY))
                .andExpect(status().isCreated());

        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content(SERVICE_GROUP_INPUT_BODY))
                .andExpect(status().isOk());
    }

    @Test
    public void existingServiceGroupCanBeRetrievedByEverybody() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content(SERVICE_GROUP_INPUT_BODY))
                .andExpect(status().isCreated());

        mvc.perform(get(URL_PATH))
                .andExpect(content().xml(SERVICE_GROUP_INPUT_BODY));
    }

    @Test
    public void anonymousUserCannotCreateServiceGroup() throws Exception {
        mvc.perform(put(URL_PATH)
                .contentType(APPLICATION_XML_VALUE)
                .content(SERVICE_GROUP_INPUT_BODY))
                .andExpect(status().isUnauthorized());

        mvc.perform(get(URL_PATH))
                .andExpect(status().isNotFound());
    }

    @Test
    public void adminCanDeleteServiceGroup() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content(SERVICE_GROUP_INPUT_BODY))
                .andExpect(status().isCreated());

        mvc.perform(delete(URL_PATH)
                .with(ADMIN_CREDENTIALS))
                .andExpect(status().isOk());

        mvc.perform(get(URL_PATH))
                .andExpect(status().isNotFound());
    }

    @Test
    public void malformedInputReturnsBadRequest() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content("malformed input XML"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidParticipantSchemeReturnsBadRequest() throws Exception {

        String scheme = "length-exceeeeeeds-25chars";
        String urlPath = format("/%s::%s", scheme, PARTICIPANT_ID);

        mvc.perform(put(urlPath)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content(getSampleServiceGroupBodyWithScheme(scheme)))
                .andExpect(status().isBadRequest());
    }
}
