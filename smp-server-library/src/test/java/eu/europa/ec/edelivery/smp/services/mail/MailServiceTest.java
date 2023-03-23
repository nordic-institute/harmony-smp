package eu.europa.ec.edelivery.smp.services.mail;


import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.services.mail.prop.TestMailProperties;
import eu.europa.ec.edelivery.smp.testutil.MockAlertBeans;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;

import javax.mail.internet.MimeMessage;

@Ignore
@ContextConfiguration(classes = {MockAlertBeans.class, MailService.class})
public class MailServiceTest extends AbstractServiceIntegrationTest {


    @Autowired
    JavaMailSenderImpl mockJavaMailSender;

    @Autowired
    MailService testInstance;

    @Test
    public void testSendMail() {

        Mockito.doNothing().when(mockJavaMailSender).send((MimeMessage) Mockito.any());

        PropertiesMailModel props = new PropertiesMailModel(AlertTypeEnum.TEST_ALERT.getTemplate(), "testMail");
        props.setProperty(TestMailProperties.SERVER_NAME.name(), "server name");
        props.setProperty(TestMailProperties.USERNAME.name(), "username");
        props.setProperty(TestMailProperties.USER_MAIL.name(), "test@test-receiver-mail.eu");
        testInstance.sendMail(props, "test@test-sender-mail.eu", "test@test-receiver-mail.eu");

        Mockito.verify(mockJavaMailSender, Mockito.times(1)).send((MimeMessage) Mockito.any());
    }
}
