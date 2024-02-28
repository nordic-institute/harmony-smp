/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.services.mail;


import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.services.mail.prop.TestMailProperties;
import eu.europa.ec.edelivery.smp.testutil.MockAlertBeans;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@ContextConfiguration(classes = {MockAlertBeans.class, MailService.class})
public class MailServiceTest extends AbstractServiceIntegrationTest {


    @Autowired
    JavaMailSenderImpl mockJavaMailSender;

    @Autowired
    MailService testInstance;

    @Test
    public void testSendMail() {

        Mockito.doNothing().when(mockJavaMailSender).send((MimeMessage) Mockito.any());
        Map<String, Object> props = new HashMap<>();

        props.put(TestMailProperties.SERVER_NAME.name(), "server name");
        props.put(TestMailProperties.USERNAME.name(), "username");
        props.put(TestMailProperties.USER_MAIL.name(), "test@test-receiver-mail.eu");

        MailDataModel data = new MailDataModel("en", AlertTypeEnum.TEST_ALERT, props);

        testInstance.sendMail(data, "test@test-sender-mail.eu", "test@test-receiver-mail.eu");

        Mockito.verify(mockJavaMailSender, Mockito.times(1)).send((MimeMessage) Mockito.any());
    }
}
