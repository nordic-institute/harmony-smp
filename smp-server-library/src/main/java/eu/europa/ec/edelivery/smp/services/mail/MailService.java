/*-
 * #START_LICENSE#
 * smp-server-library
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
package eu.europa.ec.edelivery.smp.services.mail;

import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;


/**
 * Mail service for mail for constitution and submission. The class was heavily inspired by Domibus
 * mail implementation
 *
 * @author Thomas Dussart
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Component
public class MailService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(MailService.class);

    private final JavaMailSenderImpl javaMailSender;
    private final MailTemplateService mailTemplateService;

    public MailService(JavaMailSenderImpl javaMailSender, MailTemplateService mailTemplateService) {
        this.javaMailSender = javaMailSender;
        this.mailTemplateService = mailTemplateService;
    }


    public void sendMail(final MailDataModel model, final String from, final String to) {
        if (StringUtils.isBlank(to)) {
            throw new IllegalArgumentException("The 'to' property cannot be null");
        }
        if (StringUtils.isBlank(from)) {
            throw new IllegalArgumentException("The 'from' property cannot be null");
        }


        MimeMessage message = javaMailSender.createMimeMessage();
        try {

            MimeMessageHelper helper = getMimeMessageHelper(message);
            String subject = mailTemplateService.getMailTitle(model);
            String html = mailTemplateService.getMailHtmlContent(model);

            // if to contains multiple emails, split them and send as anonymously as BCC
            if (to.contains(";")) {
                helper.setBcc(to.split(";"));
            } else {
                helper.setTo(to);
            }
            helper.setText(html, true);
            helper.setSubject(subject);
            helper.setFrom(from);
            LOG.info("Send mail to : [{}:{}]", javaMailSender.getHost(), javaMailSender.getPort());

            javaMailSender.send(message);
        } catch (MessagingException | MailException e) {
            LOG.error("Exception while sending mail from [{}] to [{}]", from, to, e);
            throw new SMPRuntimeException(ErrorCode.MAIL_SUBMISSION_ERROR, e, ExceptionUtils.getRootCauseMessage(e));
        }
    }

    MimeMessageHelper getMimeMessageHelper(MimeMessage message) throws MessagingException {
        return new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
    }

}
