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
package eu.europa.ec.edelivery.smp.services.mail;

import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.mail.prop.DocumentActionProperties;
import eu.europa.ec.edelivery.smp.services.mail.prop.MailDocumentActionType;
import eu.europa.ec.edelivery.smp.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Joze Rihtarsic
 * @since 5.2
 */
@Service
public class DocumentMailService {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentMailService.class);

    final ConfigurationService configurationService;
    final MailService mailService;
    final UserDao userDao;

    public DocumentMailService(ConfigurationService configurationService, MailService mailService, UserDao userDao) {
        this.configurationService = configurationService;
        this.mailService = mailService;
        this.userDao = userDao;
    }

    public void sendDocumentActionNotification(DBResource resource, DBSubresource dbSubresource, MailDocumentActionType actionType, int version, String documentName, SMPUserDetails user) {

        List<DBUser> recipients;
        if (resource.isReviewEnabled()) {
            // send notification to all reviewers
            recipients = actionType == MailDocumentActionType.REVIEW_REQUESTED ?
                    userDao.getResourceReviewUsers(resource) :
                    userDao.getResourceAdminAndReviewUsers(resource);
        } else {
            recipients = userDao.getResourceAdminUsers(resource);
        }
        sendDocumentActionNotification(resource, dbSubresource, actionType, version, documentName, user, recipients);
    }

    public void sendDocumentActionNotification(DBResource resource, DBSubresource dbSubresource, MailDocumentActionType actionType, int version, String documentName, SMPUserDetails user, List<DBUser> recipients) {
        if (user == null || user.getUser() == null) {
            LOG.warn("Unknown user, cannot send resource publish notification for resource [{}]", resource);
            return;
        }
        DBUser dbUser = user.getUser();
        Map<String, Object> data = new HashMap<>();
        // add common mail data
        data.put(MailDataModel.CommonProperties.SMP_INSTANCE_NAME.name(), configurationService.getSMPInstanceName());

        // generate mail data
        data.put(DocumentActionProperties.DOCUMENT_NAME.name(), documentName);
        data.put(DocumentActionProperties.DOCUMENT_VERSION.name(), version);
        data.put(DocumentActionProperties.DOCUMENT_TYPE.name(), resource.getDomainResourceDef().getResourceDef().getName());

        data.put(DocumentActionProperties.RESOURCE_IDENTIFIER.name(), resource.getIdentifierValue());
        data.put(DocumentActionProperties.RESOURCE_SCHEME.name(), resource.getIdentifierScheme());
        if (dbSubresource != null) {
            data.put(DocumentActionProperties.SUBRESOURCE_IDENTIFIER.name(), dbSubresource.getIdentifierValue());
            data.put(DocumentActionProperties.SUBRESOURCE_SCHEME.name(), dbSubresource.getIdentifierScheme());
        }
        data.put(DocumentActionProperties.DOMAIN.name(), resource.getDomainResourceDef().getDomain().getDomainCode());
        data.put(DocumentActionProperties.ACTION.name(), actionType);
        data.put(DocumentActionProperties.ACTION_BY.name(), StringUtils.isBlank(dbUser.getFullName()) ? dbUser.getUsername() : dbUser.getFullName() + " (" + dbUser.getUsername() + ")");
        OffsetDateTime now = OffsetDateTime.now();
        AlertTypeEnum alertType = getAlertType(dbSubresource != null, actionType);
        for (DBUser mailRecipients : recipients) {
            data.put(MailDataModel.CommonProperties.CURRENT_DATETIME.name(), DateTimeUtils.formatOffsetDateTimeWithLocal(now, mailRecipients.getSmpLocale()));
            MailDataModel mailDataModel = new MailDataModel(mailRecipients.getSmpLocale(),
                    alertType,
                    data);

            // send mails to all resource admins
            mailService.sendMail(mailDataModel, configurationService.getAlertEmailFrom(), mailRecipients.getEmailAddress());
        }
    }

    /**
     * Get alert type for document action
     *
     * @param isSubresource true if the document is for subresource, false if for resource
     * @param actionType    action type
     * @return alert type
     */
    private AlertTypeEnum getAlertType(boolean isSubresource, MailDocumentActionType actionType) {
        return switch (actionType) {
            case REVIEW_REQUESTED, REVIEW_APPROVED, REVIEW_REJECTED ->
                    isSubresource ? AlertTypeEnum.SUBRESOURCE_DOCUMENT_REVIEW_ACTION : AlertTypeEnum.RESOURCE_DOCUMENT_REVIEW_ACTION;
            default ->
                    isSubresource ? AlertTypeEnum.SUBRESOURCE_DOCUMENT_ACTION : AlertTypeEnum.RESOURCE_DOCUMENT_ACTION;
        };
    }
}

