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
package eu.europa.ec.edelivery.smp.ui;

import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.dao.CredentialDao;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils;
import eu.europa.ec.edelivery.smp.test.testutils.X509CertificateTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SmpTestWebAppConfig.class})
@Sql(scripts = {
        "classpath:/cleanup-database.sql",
        "classpath:/webapp_integration_test_data.sql"},
        executionPhase = BEFORE_TEST_METHOD)
class AuthenticationResourceIT {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationResourceIT.class);

    private static final String PATH = ResourceConstants.CONTEXT_PATH_PUBLIC_SECURITY + "/authentication";

    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private UIKeystoreService uiKeystoreService;

    @Autowired
    private CredentialDao credentialDao;

    @Autowired
    private ConfigurationDao configurationDao;

    private MockMvc mvc;

    @BeforeEach
    public void setup() throws IOException {
        X509CertificateTestUtils.reloadKeystores();
        mvc = MockMvcUtils.initializeMockMvc(webAppContext);
        uiKeystoreService.refreshData();

    }

    @Test
    void authenticateSuccessTest() throws Exception {
        // given when
        HttpSession session = mvc.perform(post(PATH)
                        .header("Content-Type", "application/json")
                        .content("{\"username\":\"smp_admin\",\"password\":\"test123\"}"))
                .andExpect(status().isOk()).andReturn()
                .getRequest()
                .getSession();

        assertNotNull(session);
    }


    @Test
    void authenticateInvalidPasswordTest() throws Exception {
        // given when then
        mvc.perform(post(PATH)
                        .header("Content-Type", "application/json")
                        .content("{\"username\":\"smp_admin\",\"password\":\"test1235\"}"))
                .andExpect(status().isUnauthorized()).andReturn()
                .getRequest()
                .getSession();
    }

    @Test
    void authenticateInvalidUsernameTest() throws Exception {

        // given when
        mvc.perform(post(PATH)
                        .header("Content-Type", "application/json")
                        .content("{\"username\":\"smp_admin1\",\"password\":\"test123\"}"))
                .andExpect(status().isUnauthorized()).andReturn()
                .getRequest()
                .getSession();
    }
}
