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
package eu.europa.ec.edelivery.smp.exceptions;

import eu.europa.ec.smp.spi.exceptions.IErrorCodeType;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.*;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorMessageArgument.*;

/**
 * Error message types with i18n keys and default EN messages
 *
 * @since 5.2
 * @author Joze Rihtarsic
 */
public enum ErrorMessageType implements IErrorCodeType {
    CERTIFICATE_ERROR_GENERIC(CERTIFICATE_ERROR, "error.certificate.cannot.decode", "Certificate error: [{{error}}]!", CERTIFICATE, ERROR),
    CERTIFICATE_CANNOT_DECODE(CERTIFICATE_ERROR, "error.certificate.cannot.decode", "Certificate error: cannot decode certificate  [{{certificate}}]. Error: [{{error}}]!", CERTIFICATE, ERROR),
    CERTIFICATE_CANNOT_ENCODE(CERTIFICATE_ERROR, "error.certificate.cannot.encode", "Certificate error: cannot encode certificate [{{certificate}}]. Error: [{{error}}]!", CERTIFICATE, ERROR),
    CERTIFICATE_CANNOT_GET_POLICY_IDENTIFIER(CERTIFICATE_ERROR, "error.certificate.cannot.get.policy.identifier", "Certificate error: cannot retrieve policy identifiers for [{{certificate}}]. Error: [{{error}}]!", CERTIFICATE, ERROR),
    CERTIFICATE_CANNOT_UPLOAD_DUPLICATE(CERTIFICATE_ERROR, "error.certificate.cannot.upload.duplicate", "Certificate error: duplicate certificate. The certificate you are trying to upload already exists under the [{{alias}}] entry!", ALIAS),
    CERTIFICATE_CRL_CANNOT_DOWNLOAD(CERTIFICATE_ERROR, "error.certificate.crl.cannot.download", "Certificate error: cannot download CRL [{{crlURL}}]. Error: [{{error}}]!", CRL_URL, ERROR),
    CERTIFICATE_CRL_CANNOT_READ(CERTIFICATE_ERROR, "error.certificate.crl.cannot.read", "Certificate error: CRL cannot be read [{{crlURL}}]. Error: [{{error}}]!", CRL_URL, ERROR),
    CERTIFICATE_CRL_DOWNLOAD_ISSUE(CERTIFICATE_ERROR, "error.certificate.crl.download.issue", "Certificate error: error occurred while downloading CRL [{{crlURL}}]. Error: [{{error}}]!", CRL_URL, ERROR),
    CERTIFICATE_CRL_NOT_SUPPORTED(CERTIFICATE_ERROR, "error.certificate.crl.not.supported", "Certificate error: CRL list is not supported [{{crlURL}}]. Error: [{{error}}]!", CRL_URL, ERROR),
    CONFIGURATION(CONFIGURATION_ERROR,"error.configuration", "Configuration error: [{{error}}]!", ERROR),
    CONFIGURATION_TRUSTSTORE_INVALID(CONFIGURATION_ERROR,"error.configuration.truststore.invalid", "Can not access truststore! (Check the truststore configuration or read/write permissions)!", ERROR),
    CONFIGURATION_KEYSTORE_INVALID(CONFIGURATION_ERROR,"error.configuration.keystore.invalid", "Can not access keystore! (Check the keystore configuration or read/write permissions)!", ERROR),
    CONFIGURATION_PROPERTY(CONFIGURATION_ERROR,"error.configuration.property", "Configuration property [{{propertyName}}] has error: [{{errorMessageCode}}]!", ERROR),
    CONFIGURATION_FOLDER_CREATION(CONFIGURATION_ERROR,"error.configuration.folder.creation", "Configuration error: configuration folder does not exists and cannot be created! Value: [{{path}}] (Absolute path [{{absolutePath}}])!", PATH, ABSOLUTE_PATH),
    CONFIGURATION_FOLDER_NOT_DIRECTORY(CONFIGURATION_ERROR,"error.configuration.folder.not.directory", "Configuration error: configuration folder is not a folder! Value: [{{path}}] (Absolute path [{{absolutePath}}])!", PATH, ABSOLUTE_PATH),
    CONFIGURATION_DATASOURCE(CONFIGURATION_ERROR,"error.configuration.datasource", "Configuration error: invalid datasource configuration. Both JNDI and JDBC URLs are empty!"),
    CONFIGURATION_DOMISML_INTEGRATION_DISABLED(CONFIGURATION_ERROR,"error.configuration.domisml.integration.disabled", "Configuration error: DomiSML integration is not enabled!"),
    CONFIGURATION_EMPTY_KEYSTORE(CONFIGURATION_ERROR,"error.configuration.empty.keystore", "Configuration error: could not retrieve key [{{alias}}] from empty keystore: [{{keystoreFile}}]!", ALIAS, KEYSTORE_FILE),
    CONFIGURATION_ENCRYPTED_DOMAIN_PROPERTY_NOT_ALLOWED(CONFIGURATION_ERROR,"error.configuration.encrypted.domain.property.not.allowed", "Configuration error: encrypted domain properties are not supported! Cannot parse [{{propertyName}}]!", PROPERTY_NAME),
    CONFIGURATION_ENCRYPTION_FILE_NOT_FILE(CONFIGURATION_ERROR,"error.configuration.encryption.file.not.file", "Configuration error: encryption file does not exists or is not a file! Value: [{{absolutePath}}]!", ABSOLUTE_PATH),
    CONFIGURATION_FILE_NOT_EXISTS(CONFIGURATION_ERROR,"error.configuration.file.not.exists", "Configuration error: The file [{{absolutePath}}] does not exist.", ABSOLUTE_PATH),
    CONFIGURATION_FILE_NOT_FILE(CONFIGURATION_ERROR,"error.configuration.file.not.file", "Configuration error: [{{absolutePath}}] must be a file.", ABSOLUTE_PATH),
    CONFIGURATION_FILE_OR_PATH_DOMAIN_PROPERTY_NOT_ALLOWED(CONFIGURATION_ERROR,"error.configuration.file.or.path.domain.property.not.allowed", "Configuration error: path or filename domain properties are not supported! Cannot parse [{{propertyName}}]!", PROPERTY_NAME),
    CONFIGURATION_INVALID_BOOLEAN(CONFIGURATION_ERROR,"error.configuration.invalid.boolean", "Configuration error: invalid boolean value [{{propertyValue}}]! Error: only {true, false} are allowed!", PROPERTY_VALUE),
    CONFIGURATION_INVALID_DATETIME(CONFIGURATION_ERROR,"error.configuration.invalid.datetime", "Configuration error: invalid datetime value [{{propertyValue}}]!", PROPERTY_VALUE),
    CONFIGURATION_INVALID_CRON_EXPRESSION(CONFIGURATION_ERROR,"error.configuration.invalid.cron.expression", "Configuration error: invalid cron expression [{{propertyValue}}]! Error: [{{error}}]!", PROPERTY_VALUE, ERROR),
    CONFIGURATION_INVALID_EMAIL(CONFIGURATION_ERROR,"error.configuration.invalid.email", "Configuration error: invalid email address [{{propertyValue}}]!", PROPERTY_VALUE),
    CONFIGURATION_INVALID_FILENAME(CONFIGURATION_ERROR,"error.configuration.invalid.filename", "Configuration error: invalid filename [{{propertyValue}}]!", PROPERTY_VALUE),
    CONFIGURATION_INVALID_FOLDER(CONFIGURATION_ERROR,"error.configuration.invalid.folder", "Configuration error: folder [{{propertyValue}}] does not exists and cannot be created!", PROPERTY_VALUE),
    CONFIGURATION_INVALID_FOLDER_NOT_DIRECTORY(CONFIGURATION_ERROR,"error.configuration.invalid.folder.not.directory", "Configuration error: path [{{propertyValue}}] is not a folder!", PROPERTY_VALUE),
    CONFIGURATION_INVALID_INTEGER(CONFIGURATION_ERROR,"error.configuration.invalid.integer", "Configuration error: invalid integer [{{propertyValue}}]! Error: [{{error}}]!", PROPERTY_VALUE, ERROR),
    CONFIGURATION_INVALID_LENGTH(CONFIGURATION_ERROR,"error.configuration.invalid.length", "Configuration error: invalid property value! Error: Value to long. Max. allowed size 2000 characters!"),
    CONFIGURATION_INVALID_MAP(CONFIGURATION_ERROR,"error.configuration.invalid.map", "Configuration error: invalid map [{{propertyValue}}]! Map must have at least one key:value entry!", PROPERTY_VALUE),
    CONFIGURATION_INVALID_REGULAR_EXPRESSION(CONFIGURATION_ERROR,"error.configuration.invalid.regular.expression", "Configuration error: invalid regular expression [{{propertyValue}}]! Error: [{{error}}]!", PROPERTY_VALUE, ERROR),
    CONFIGURATION_INVALID_URL(CONFIGURATION_ERROR,"error.configuration.invalid.url", "Configuration error: invalid URL address [{{propertyValue}}]! Error: [{{error}}]!", PROPERTY_VALUE, ERROR),
    CONFIGURATION_LOCALE_FOLDER_CREATION(CONFIGURATION_ERROR,"error.configuration.locale.folder.creation", "Configuration error: locale folder does not exists and cannot be created! Value: [{{path}}] (Absolute path [{{absolutePath}}])!", PATH, ABSOLUTE_PATH),
    CONFIGURATION_LOCALE_FOLDER_NOT_DIRECTORY(CONFIGURATION_ERROR,"error.configuration.locale.folder.not.directory", "Configuration error: locale folder is not a folder! Value: [{{path}}] (Absolute path [{{absolutePath}}])!", PATH, ABSOLUTE_PATH),
    CONFIGURATION_MANDATORY_ENCRYPTION_FILE_NAME(CONFIGURATION_ERROR,"error.configuration.mandatory.encryption.file.name", "Configuration error: empty configuration folder. Property [{{propertyName}}] is mandatory!", PROPERTY_NAME),
    CONFIGURATION_MANDATORY_PROPERTY(CONFIGURATION_ERROR,"error.configuration.mandatory.property", "Configuration error: empty mandatory property [{{propertyName}}]!", PROPERTY_NAME),
    CONFIGURATION_MISSING_KEYPAIR_OR_WRONG_ALIAS(CONFIGURATION_ERROR,"error.configuration.missing.keypair.or.wrong.alias", "Configuration error: wrong configuration, missing key pair from keystore or wrong alias [{{alias}}]!", ALIAS),
    CONFIGURATION_MISSING_PROPERTY(CONFIGURATION_ERROR,"error.configuration.missing.property", "Configuration error: Missing property [{{propertyName}}].", PROPERTY_NAME),
    CONFIGURATION_NO_DOMAINS(CONFIGURATION_ERROR,"error.configuration.no.domains", "Configuration error: no domain is configured for the DomiSMP instance!"),
    CONFIGURATION_NO_RESOURCE_DEFINITION_FOR_DOMAIN(CONFIGURATION_ERROR,"error.configuration.no.resource.definition.for.domain", "Configuration error: no resource def [{{headerParameter}}] is registered for the domain [{{domainCode}}]!", HEADER_PARAMETER, DOMAIN_CODE),
    CONFIGURATION_NO_RESOURCES(CONFIGURATION_ERROR,"error.configuration.no.resources", "Configuration error: no resource type is registered for the domain!"),
    CONFIGURATION_PROPERTY_DECRYPTION(CONFIGURATION_ERROR,"error.configuration.property.decryption", "Configuration error: cannot decrypt the property [{{propertyName}}]! Error: [{{error}}]!", PROPERTY_NAME, ERROR),
    CONFIGURATION_PROPERTY_ENCRYPTION(CONFIGURATION_ERROR,"error.configuration.property.encryption", "Configuration error: cannot encrypt the property [{{propertyName}}]! Error: [{{error}}]!", PROPERTY_NAME, ERROR),
    CONFIGURATION_RESOURCEDEF_MULTIPLE_ENTRIES(CONFIGURATION_ERROR,"error.configuration.resourcedef.multiple.entries", "Configuration error: more than one resource type is registered for the name!"),
    CONFIGURATION_VAULT_CLASSNAME_EMPTY(CONFIGURATION_ERROR,"error.configuration.vault.classname.empty", "Null or empty Vault implementation class name/identifier!"),
    DOMAIN_DOMAIN_CODE_ALREADY_EXISTS(INVALID_DOMAIN_DATA, "error.domain.domain.code.already.exists", "Invalid domain data! Domain with code [{{domainCode}}] already exists!", DOMAIN_CODE),
    DOMAIN_DOMAIN_CODE_EMPTY(INVALID_DOMAIN_DATA, "error.domain.domain.code.empty", "Invalid domain data! Domain code must not be empty!"),
    DOMAIN_GROUP_NOT_EXISTS(GROUP_NOT_EXISTS, "error.domain.group.not.exists", "Invalid group [{{groupName}}]", GROUP_NAME),
    DOMAIN_ILLEGAL_STATE_MULTIPLE_ENTRIES(INVALID_DOMAIN_DATA, "error.domain.illegal.state.multiple.entries", "More than one domain entry (domain: [{{domainCode}}]) is defined in database!", DOMAIN_CODE),

    DOMAIN_ILLEGAL_STATE_MULTIPLE_GROUP_ENTRIES(INVALID_DOMAIN_DATA, "error.domain.illegal.state.multiple.group.entries", "More than one group for domain entry (group: [{{groupName}}], domain: [{{domainId}}]) is defined in database!", GROUP_NAME, DOMAIN_ID),
    DOMAIN_INVALID_DOMAIN_CODE(INVALID_DOMAIN_CODE, "error.domain.invalid.domain.code", "Provided domain code [{{domainCode}}] does not match required pattern: [{{pattern}}]", DOMAIN_CODE, PATTERN),
    DOMAIN_NONE_CONFIGURED(INVALID_DOMAIN_CODE, "error.domain.none.configured", "No domain configured on SMP, at least one domain is mandatory!"),
    DOMAIN_NOT_EXISTS(ErrorCode.DOMAIN_NOT_EXISTS, "error.domain.not.exists", "Invalid domain [{{domainCode}}]!", DOMAIN_CODE),
    DOMAIN_NOT_EXISTS_ID(ErrorCode.DOMAIN_NOT_EXISTS, "error.domain.not.exists", "Invalid domain id!", DOMAIN_CODE),
    DOMAIN_CONFIGURATION_ENCRYPTION_ERROR(CONFIGURATION_ERROR, "error.domain.configuration.encrypted", "Encrypted domain Properties are not supported! Can not parse [{{propertyName}}] !", PROPERTY_NAME),
    DOMAIN_CONFIGURATION_PATH_ERROR(CONFIGURATION_ERROR, "error.domain.configuration.path", "Path or filename domain properties are not supported! Can not parse [{{propertyName}}] !", PROPERTY_NAME),
    DOMAIN_DOC_TEMPLATE_NOT_EXISTS_ID(ErrorCode.INVALID_DOMAIN_DATA, "error.domain.document.template.not.exists", "Domain document template does not exists!", DOMAIN_CODE),
    DOMISML_INTEGRATION(SML_INTEGRATION_EXCEPTION, "error.domisml.integration", "SML integration error! Error: [{{error}}]", ERROR),
    DOMISML_INTEGRATION_CERTIFICATE_ALREADY_PREPARED_FOR_CHANGE(SML_INTEGRATION_EXCEPTION, "error.domisml.integration.certificate.already.prepared.for.change", "SML integration error! Error: There is already a certificate alias prepared to change [{{smlClientKeyChangeAlias}}] for domain [{{domainCode}}]!", SML_CLIENT_KEY_CHANGE_ALIAS, DOMAIN_CODE),
    DOMISML_INTEGRATION_CERTIFICATE_CHANGE_NOT_PREPARED(SML_INTEGRATION_EXCEPTION,"error.domisml.integration.certificate.change.not.prepared", "SML integration error! Error: The certificate change has not yet been prepared for domain [{{domainCode}}]!", DOMAIN_CODE),
    DOMISML_INTEGRATION_CERTIFICATE_MIGRATION_DATE_NOT_FUTURE(SML_INTEGRATION_EXCEPTION,"error.domisml.integration.certificate.migration.date.not.future", "SML integration error! Error: The migration date for the SML certificate change is in the past [{{migrationDate}}]!", MIGRATION_DATE),
    DOMISML_INTEGRATION_CERTOIFICATE_MIGRATION_DATE_UNDEFINED(SML_INTEGRATION_EXCEPTION,"error.domisml.integration.certoificate.migration.date.undefined", "SML integration error! Error: The migration date is not defined for domain [{{domainCode}}]!", DOMAIN_CODE),
    EXTENSION_ILLEGAL_STATE_MULTIPLE_ENTRIES(CONFIGURATION_ERROR, "error.extension.illegal.state.multiple.entries", "More than one extension entry (identifier: [{{identifier}}]) is defined in database!", IDENTIFIER),
    XML_RESPONSE_SIGNING(ErrorCode.XML_SIGNING_EXCEPTION, "error.xml.signing.response", "Error occurred while signing response!"),
    INTERNAL(INTERNAL_ERROR, "error.internal", "Internal error occurred!"),
    INTERNAL_CANNOT_FIND_RESOURCE_DEFINITION_FOR_IDENTIFIER(ErrorCode.INTERNAL_ERROR,"error.internal.cannot.find.resource.definition.for.identifier", "Internal error occurred because could not find resource definition for identifier [{{identifier}}]). Registered resource IDs [{{identifiers}}]!", IDENTIFIER, IDENTIFIERS),
    INTERNAL_CANNOT_FIND_SUBRESOURCE_DEFINITION_FOR_IDENTIFIER(ErrorCode.INTERNAL_ERROR, "error.internal.cannot.find.subresource.definition.for.identifier", "Internal error occurred because could not find subresource definition for identifier [{{identifier}}]). Registered subresource IDs [{{identifiers}}]!", IDENTIFIER, IDENTIFIERS),
    INTERNAL_CANNOT_READ_MAIL_TEMPLATE(ErrorCode.INTERNAL_ERROR, "error.internal.cannot.read.mail.template", "Internal error occurred while reading the mail template. Error: [{{error}}]!", ERROR),
    INTERNAL_CANNOT_READ_SUBRESOURCE(ErrorCode.INTERNAL_ERROR, "error.internal.cannot.read.subresource", "Internal error occurred while reading the subresource!"),
    INTERNAL_CONVERSION_FROM_DATABASE_ENTITY_TO_VALUE_OBJECT(INTERNAL_ERROR, "error.internal.conversion.from.database.entity.to.value.object", "Internal error occurred because of a conversion from a database entity to a value object. Error: [{{error}}]!", ERROR),
    INTERNAL_CONVERSION_FROM_VALUE_OBJECT_TO_DATABASE_ENTITY(INTERNAL_ERROR, "error.internal.conversion.from.value.object.to.database.entity", "Internal error occurred because of a conversion from a a value object to a database entity. Error: [{{error}}]!", ERROR),
    INTERNAL_DATABASE_LIST_QUERY(INTERNAL_ERROR, "error.internal.database.list.query", "Internal error occurred because of database list query exception. Error: [{{error}}]!", ERROR),
    INTERNAL_DETAILED(INTERNAL_ERROR,"error.internal.detailed", "Internal error [{{scope}}]. Error: [{{error}}]!", SCOPE, ERROR),
    INTERNAL_INVALID_JNDI_DATASOURCE(INTERNAL_ERROR, "error.internal.invalid.jndi.datasource", "Internal error occurred because of an invalid JNDI datasource: [{{jndiDatasourceName}}]. Error: [{{error}}]!", JNDI_DATASOURCE_NAME, ERROR),
    INTERNAL_RESOURCEDEF_LOOKUP_BY_IDENTIFIER_ILLEGAL_STATE_MULTIPLE_ENTRIES(INTERNAL_ERROR, "error.internal.resourcedef.lookup.by.identifier.illegal.state.multiple.entries", "Internal error occurred because of more than one result for ResourceDef with identifier [{{identifier}}]", IDENTIFIER),
    INTERNAL_RESOURCEDEF_LOOKUP_BY_URL_AND_DOMAIN_CODE_ILLEGAL_STATE_MULTIPLE_ENTRIES(INTERNAL_ERROR, "error.internal.resourcedef.lookup.by.url.and.domain.code.illegal.state.multiple.entries", "Internal error occurred because of more than one result for ResourceDef with url context [{{urlSegment}}] and domain code [{{domainCode}}]", URL_SEGMENT, DOMAIN_CODE),
    INTERNAL_RESOURCEDEF_LOOKUP_BY_IDENTIFIER_AND_DOMAIN_CODE_ILLEGAL_STATE_MULTIPLE_ENTRIES(INTERNAL_ERROR, "error.internal.resourcedef.lookup.by.url.and.domain.code.illegal.state.multiple.entries", "Internal error occurred because of more than one result for ResourceDef with url identifier [{{identifier}}] and domain code [{{domainCode}}]", IDENTIFIER, DOMAIN_CODE),
    INTERNAL_RESOURCEDEF_LOOKUP_BY_URL_ILLEGAL_STATE_MULTIPLE_ENTRIES(INTERNAL_ERROR, "error.internal.resourcedef.lookup.by.url.illegal.state.multiple.entries", "Internal error occurred because of more than one result for ResourceDef with url context: [{{urlSegment}}]", URL_SEGMENT),
    INTERNAL_RESOURCE_READING(ErrorCode.INTERNAL_ERROR, "error.internal.resource.reading", "Internal error occurred while reading the resource!"),
    INTERNAL_RESOURCE_READING_UNKNOWN_DOMAIN(INTERNAL_ERROR, "error.internal.resource.reading.unknown.domain", "Internal error occurred because could not resolve resource for unknown domain!"),
    INTERNAL_SETTING_CLIENT_CERT_FEATURE_ENABLED(INTERNAL_ERROR,"error.internal.setting.clientcert.feature.enabled", "Internal error occurred while setting the ClientCert feature (enable [{{clientCertEnabled}}]). Error: [{{error}}]!", CLIENT_CERT_ENABLED, ERROR),
    INTERNAL_SETTING_SSLCLIENTCERT_FEATURE_ENABLED(INTERNAL_ERROR,"error.internal.setting.sslclientcert.feature.enabled", "Internal error occurred while setting the SSLClientCert feature (enable [{{clientCertEnabled}}]). Error: [{{error}}]!", CLIENT_CERT_ENABLED, ERROR),
    INTERNAL_SUBRESOURCEDEF_LOOKUP_BY_IDENTIFIER_ILLEGAL_STATE_MULTIPLE_ENTRIES(INTERNAL_ERROR, "error.internal.subresourcedef.lookup.by.identifier.illegal.state.multiple.entries", "Internal error occurred because of more than one result for SubresourceDef with identifier [{{identifier}}]", IDENTIFIER),
    INTERNAL_SUBRESOURCEDEF_LOOKUP_BY_URL_ILLEGAL_STATE_MULTIPLE_ENTRIES(INTERNAL_ERROR, "error.internal.subresourcedef.lookup.by.url.illegal.state.multiple.entries", "Internal error occurred because of more than one result for SubresourceDef with url context: [{{urlSegment}}]", URL_SEGMENT),
    INTERNAL_SUBRESOURCE_READING(ErrorCode.INTERNAL_ERROR, "error.internal.subresource.reading", "Internal error occurred while reading the subresource!"),
    INTERNAL_USER_UNAUTHORIZED_FOR_INVALID_RESOURCE_ACTION(ErrorCode.INTERNAL_ERROR, "error.internal.user.unauthorized.for.unknown.resource.action", "Internal error occurred because of user [{{user}}] uses invalid resource action: [{{action}}]!", USER, ACTION),
    INVALID_PROPERTY_ALERT_ACCESS_TOKEN_BEFORE_EXPIRATION_LEVEL(PROPERTY_VALIDATION_ERROR, "error.invalid.property.alert.access.token.before.expiration.level", "Allowed values are: LOW, MEDIUM, HIGH"),
    INVALID_PROPERTY_ALERT_ACCESS_TOKEN_EXPIRED_LEVEL(PROPERTY_VALIDATION_ERROR, "error.invalid.property.alert.access.token.expired.level", "Allowed values are: LOW, MEDIUM, HIGH"),
    INVALID_PROPERTY_ALERT_CERTIFICATE_BEFORE_EXPIRATION_LEVEL(PROPERTY_VALIDATION_ERROR, "error.invalid.property.alert.certificate.before.expiration.level", "Allowed values are: LOW, MEDIUM, HIGH"),
    INVALID_PROPERTY_ALERT_CERTIFICATE_EXPIRED_LEVEL(PROPERTY_VALIDATION_ERROR, "error.invalid.property.alert.certificate.expired.level", "Allowed values are: LOW, MEDIUM, HIGH"),
    INVALID_PROPERTY_ALERT_PASSWORD_BEFORE_EXPIRATION_LEVEL(PROPERTY_VALIDATION_ERROR, "error.invalid.property.alert.password.before.expiration.level", "Allowed values are: LOW, MEDIUM, HIGH"),
    INVALID_PROPERTY_ALERT_PASSWORD_EXPIRED_LEVEL(PROPERTY_VALIDATION_ERROR, "error.invalid.property.alert.password.expired.level", "Allowed values are: LOW, MEDIUM, HIGH"),
    INVALID_PROPERTY_ALERT_SYSTEM_CERTIFICATE_BEFORE_EXPIRATION_LEVEL(PROPERTY_VALIDATION_ERROR, "error.invalid.property.alert.system.certificate.before.expiration.level", "Allowed values are: LOW, MEDIUM, HIGH"),
    INVALID_PROPERTY_ALERT_SYSTEM_CERTIFICATE_EXPIRED_LEVEL(PROPERTY_VALIDATION_ERROR, "error.invalid.property.alert.system.certificate.expired.level", "Allowed values are: LOW, MEDIUM, HIGH"),
    INVALID_PROPERTY_ALERT_USER_CREATED_LEVEL(PROPERTY_VALIDATION_ERROR, "error.invalid.property.alert.user.created.level", "Allowed values are: LOW, MEDIUM, HIGH"),
    INVALID_PROPERTY_ALERT_USER_LOGIN_FAILURE_LEVEL(PROPERTY_VALIDATION_ERROR, "error.invalid.property.alert.user.login.failure.level", "Allowed values are: LOW, MEDIUM, HIGH"),
    INVALID_PROPERTY_ALERT_USER_SUSPENDED_LEVEL(PROPERTY_VALIDATION_ERROR, "error.invalid.property.alert.user.suspended.level", "Allowed values are: LOW, MEDIUM, HIGH"),
    INVALID_PROPERTY_ALERT_USER_SUSPENDED_MOMENT(PROPERTY_VALIDATION_ERROR, "error.invalid.property.alert.user.suspended.moment", "Allowed values are: AT_LOGON,WHEN_BLOCKED"),
    INVALID_PROPERTY_ALERT_USER_UPDATED_LEVEL(PROPERTY_VALIDATION_ERROR, "error.invalid.property.alert.user.updated.level", "Allowed values are: LOW, MEDIUM, HIGH"),
    INVALID_PROPERTY_BOOLEAN(PROPERTY_VALIDATION_ERROR, "error.invalid.property.boolean", "Property value: [{{propertyValue}}] is not valid Boolean type!", ErrorMessageArgument.PROPERTY_NAME),
    INVALID_PROPERTY_CRON_EXPRESSION(PROPERTY_VALIDATION_ERROR, "error.invalid.property.cron.expression", "Property value: [{{propertyValue}}] is not valid Cron Expression type!", ErrorMessageArgument.PROPERTY_NAME),
    INVALID_PROPERTY_DATETIME(PROPERTY_VALIDATION_ERROR, "error.invalid.property.datetime", "Property value must be less than 2000 characters", ErrorMessageArgument.PROPERTY_NAME),
    INVALID_PROPERTY_EMAIL(PROPERTY_VALIDATION_ERROR, "error.invalid.property.email", "Property value: [{{propertyValue}}] is not valid Email address type!", ErrorMessageArgument.PROPERTY_NAME),
    INVALID_PROPERTY_FILENAME(PROPERTY_VALIDATION_ERROR, "error.invalid.property.filename", "Property value: [{{propertyValue}}] is not valid Filename type or it does not exists", ErrorMessageArgument.PROPERTY_NAME),
    INVALID_PROPERTY_INTEGER(PROPERTY_VALIDATION_ERROR, "error.invalid.property.integer", "Property value: [{{propertyValue}}] is not valid Integer and it must have less than 10 digits", ErrorMessageArgument.PROPERTY_NAME),
    INVALID_PROPERTY_LIST_STRING(PROPERTY_VALIDATION_ERROR, "error.invalid.property.list.string", "Property value: [{{propertyValue}}] is not valid LIST_STRING type", ErrorMessageArgument.PROPERTY_NAME),
    INVALID_PROPERTY_MAP_STRING(PROPERTY_VALIDATION_ERROR, "error.invalid.property.map.string", "Property value: [{{propertyValue}}] is not valid MAP_STRING type", ErrorMessageArgument.PROPERTY_NAME),
    INVALID_PROPERTY_MISSING(CONFIGURATION_ERROR, "error.invalid.property.missing", "Property [{{propertyName}}] is mandatory and must not be NULL OR empty", ErrorMessageArgument.PROPERTY_NAME),
    INVALID_PROPERTY_PATH(PROPERTY_VALIDATION_ERROR, "error.invalid.property.path", "Property value: [{{propertyValue}}] is not valid Path type or it does not exists", ErrorMessageArgument.PROPERTY_NAME),
    INVALID_PROPERTY_REGEXP(PROPERTY_VALIDATION_ERROR, "error.invalid.property.regexp", "Property value: [{{propertyValue}}] is not valid Regular Expression type", ErrorMessageArgument.PROPERTY_NAME),
    INVALID_PROPERTY_STRING(PROPERTY_VALIDATION_ERROR, "error.invalid.property.string", "Property value must be less than 2000 characters!", ErrorMessageArgument.PROPERTY_NAME),
    INVALID_PROPERTY_UNKNOWN(CONFIGURATION_ERROR,"error.invalid.property.unknown", "Property [{{propertyName}}] is not SMP property!", ErrorMessageArgument.PROPERTY_NAME),
    INVALID_PROPERTY_URL(PROPERTY_VALIDATION_ERROR, "error.invalid.property.url", "Property value: [{{propertyValue}}] is not valid URL!", ErrorMessageArgument.PROPERTY_NAME),
    INVALID_REQUEST_GENERIC(INVALID_REQUEST, "error.invalid.request", "Invalid request!"),
    INVALID_REQUEST_CREATE_USER_CREDENTIALS(ErrorCode.INVALID_REQUEST, "error.invalid.request.create.user.credentials", "Invalid request [UserId]. Error: cannot find user identifier to update!"),
    INVALID_REQUEST_DNS_LOOKUP_UNKNOWN(ErrorCode.INVALID_REQUEST,"error.invalid.request.dns.lookup.unknown", "Invalid request [DNSLookup]. Error: unknown DNS lookup type: [{{dnsLookupType}}]!", DNS_LOOKUP_TYPE),
    INVALID_REQUEST_DOCUMENT_GENERATION(INVALID_REQUEST, "error.invalid.request.document.generation.validation", "Invalid request [GenerateDocument]. Error: [{{error}}]!", ERROR),
    INVALID_REQUEST_DOCUMENT_SEARCH_IDENTIFIERS_MISMATCH(INVALID_REQUEST, "error.invalid.request.document.search.identifiers.mismatch", "Invalid request [ResourceMismatch]. Error: the resource identifier does not match the subresource's resource identifier!"),
    INVALID_REQUEST_DOCUMENT_STORE_RESOURCE_VALIDATION(INVALID_REQUEST, "error.invalid.request.document.store.resource.validation", "Invalid request [StoreResourceValidation]. Error: [{{error}}]!", ERROR),
    INVALID_REQUEST_DOCUMENT_STORE_SUBRESOURCE_VALIDATION(INVALID_REQUEST, "error.invalid.request.document.store.subresource.validation", "Invalid request [StoreSubresourceValidation]. Error: [{{error}}]!", ERROR),
    INVALID_REQUEST_DOCUMENT_VALIDATION_DOCUMENT_ID_MISMATCH_RESOURCE_TAG(INVALID_REQUEST, "error.invalid.request.document.validation.document.id.mismatch.resource.tag", "Invalid request [DocumentIdMismatch]. Error: document identifier does not match the resource document identifier!"),
    INVALID_REQUEST_DOCUMENT_VALIDATION_DOCUMENT_VERSION_CANNOT_DELETE_LAST_VERSION(INVALID_REQUEST, "error.invalid.request.document.validation.document.version.cannot.delete.last.version", "Invalid request [DocumentVersionCannotDeleteLastVersion]. Error: cannot delete the last/only document version!"),
    INVALID_REQUEST_DOCUMENT_VALIDATION_DOCUMENT_ID_MISMATCH_SUBRESOURCE_TAG(INVALID_REQUEST, "error.invalid.request.document.validation.document.id.mismatch.subresource.tag", "Invalid request [DocumentIdMismatch]. Error: document identifier does not match the subresource document identifier!"),
    INVALID_REQUEST_DOCUMENT_VALIDATION_DOCUMENT_REFERENCE_ALREADY_REFERENCED(INVALID_REQUEST, "error.invalid.request.document.validation.document.reference.already.referenced", "Invalid request [DocumentReferenceNotValid]. Error: cannot reference to a document that already has a reference!"),
    INVALID_REQUEST_DOCUMENT_VALIDATION_DOCUMENT_REFERENCE_HAVING_NO_SHARING(INVALID_REQUEST, "error.invalid.request.document.validation.document.reference.having.no.sharing", "Invalid request [DocumentReferenceNotValid]. Error: cannot reference to not shared document!"),
    INVALID_REQUEST_DOCUMENT_VALIDATION_DOCUMENT_REFERENCE_NOT_ALLOWED(INVALID_REQUEST, "error.invalid.request.document.validation.document.reference.not.allowed", "Invalid request [DocumentReferenceNotAllowed]. Error: document reference cannot be the same as the document identifier!"),
    INVALID_REQUEST_DOCUMENT_VALIDATION_DOCUMENT_REFERENCE_NOT_FOUND(INVALID_REQUEST, "error.invalid.request.document.validation.document.reference.not.found", "Invalid request [DocumentReferenceNotFound]. Error: document reference not found!"),
    INVALID_REQUEST_DOCUMENT_VALIDATION_DOCUMENT_REVIEW_ACTION_NOT_ALLOWED(INVALID_REQUEST, "error.invalid.request.document.validation.document.review.action.not.allowed", "Invalid request [DocumentReviewActionNotAllowed]. Error: document review action is not allowed for the document!"),
    INVALID_REQUEST_DOCUMENT_VALIDATION_DOCUMENT_REVIEW_NOT_ALLOWED(INVALID_REQUEST, "error.invalid.request.document.validation.document.review.not.allowed", "Invalid request [DocumentReviewNotAllowed]. Error: document review is not allowed for the document!"),
    INVALID_REQUEST_DOCUMENT_VALIDATION_DOCUMENT_REVIEW_NOT_ENABLED(INVALID_REQUEST, "error.invalid.request.document.validation.document.review.not.enabled", "Invalid request [DocumentReviewNotEnabled]. Error: document review is not enabled for the document!"),
    INVALID_REQUEST_DOCUMENT_VALIDATION_DOCUMENT_SHARING_NOT_ALLOWED(INVALID_REQUEST, "error.invalid.request.document.validation.document.sharing.not.allowed", "Invalid request [DocumentSharingNotAllowed]. Error: document sharing is not allowed for the document with reference document!"),
    INVALID_REQUEST_DOCUMENT_VALIDATION_DOCUMENT_VERSION_ALREADY_PUBLISHED(INVALID_REQUEST, "error.invalid.request.document.validation.document.version.already.published", "Invalid request [DocumentVersionAlreadyPublished]. Error: document version has wrong status!"),
    INVALID_REQUEST_DOCUMENT_VALIDATION_DOCUMENT_VERSION_WRONG_STATUS(INVALID_REQUEST, "error.invalid.request.document.validation.document.version.published", "Invalid request. Error: document version has wrong status for action!"),
    INVALID_REQUEST_DOCUMENT_VALIDATION_DOCUMENT_VERSION_NOT_FOUND(INVALID_REQUEST, "error.invalid.request.document.validation.document.version.not.found.tag", "Invalid request [DocumentVersionNotFound]. Error: document version not found!"),
    INVALID_REQUEST_DOCUMENT_VALIDATION_RESOURCE(INVALID_REQUEST, "error.invalid.request.document.validation.resource", "Invalid request [ResourceValidation]. Error: [{{error}}]!", ERROR),
    INVALID_REQUEST_DOCUMENT_VALIDATION_SUBRESOURCE(INVALID_REQUEST, "error.invalid.request.document.validation.subresource", "Invalid request [SubresourceValidation]. Error: [{{error}}]!", ERROR),
    INVALID_REQUEST_DOMAIN_REMOVE_RESOURCE_DEF(INVALID_REQUEST, "error.invalid.request.domain.unregister.type.contains.resources", "Can not remove resource definition [{{identifier}}] from domain [{{domainCode}}], because it has resources. Resource count [{{count}}]!", IDENTIFIER,  DOMAIN_CODE, COUNT),
    INVALID_REQUEST_DOMAIN_DELETE_CONTAINS_RESOURCES(INVALID_REQUEST, "error.invalid.request.domain.delete.contains.resources", "Can not delete domain because it has resources [{{count}}]! Delete resources first!", COUNT),
    INVALID_REQUEST_DOMAIN_MEMBERSHIP_ADD_USER_ALREADY_MEMBER(INVALID_REQUEST, "error.invalid.request.domain.membership.add.user.already.member", "Invalid request [AddMembership]. Error: user [{{username}}] is already a member!", USERNAME),
    INVALID_REQUEST_DOMAIN_MEMBERSHIP_ADD_USER_NOT_EXISTS(INVALID_REQUEST, "error.invalid.request.domain.membership.add.user.not.exists", "Invalid request [AddMembership]. Error: user [{{username}}] does not exists!", USERNAME),
    INVALID_REQUEST_DOMAIN_MEMBERSHIP_REMOVE_USER_NOT_MEMBER(INVALID_REQUEST, "error.invalid.request.domain.membership.remove.user.not.member", "Invalid request [RemoveMembership]. Error: membership does not exists!"),
    INVALID_REQUEST_DOMAIN_MEMBERSHIP_REMOVE_USER_NOT_PART_OF_DOMAIN(INVALID_REQUEST, "error.invalid.request.domain.membership.remove.user.not.part.of.domain", "Invalid request [RemoveMembership]. Error: membership does not belong to domain!"),
    INVALID_REQUEST_GET_DOMAIN_GROUPS(INVALID_REQUEST, "error.invalid.request.get.domain.groups", "Invalid request [GetGroups]. Error: unknown parameter type [{{userRole}}]!", USER_ROLE),
    INVALID_REQUEST_GET_GROUP_RESOURCES(INVALID_REQUEST, "error.invalid.request.get.group.resources", "Invalid request [ResourcesForGroups]. Error: unknown parameter type [{{userRole}}]!", USER_ROLE),
    INVALID_REQUEST_GET_USER_DOMAINS(INVALID_REQUEST, "error.invalid.request.get.user.domains", "Invalid request [GetDomains]. Error: unknown parameter type [{{userRole}}]!", USER_ROLE),
    INVALID_REQUEST_GROUP_CREATE_ALREADY_EXISTS(INVALID_REQUEST, "error.invalid.request.group.create.already.exists", "Invalid request [CreateGroup]. Error: group with name [{{groupName}}] already exists!", GROUP_NAME),
    INVALID_REQUEST_GROUP_DELETE_CONTAINS_RESOURCES(INVALID_REQUEST, "error.invalid.request.group.delete.contains.resources", "Invalid request [DeleteGroup]. Error: group has resources [{{resourcesCount}}] and cannot be deleted!", RESOURCES_COUNT),
    INVALID_REQUEST_GROUP_DELETE_NOT_EXISTS(INVALID_REQUEST, "error.invalid.request.group.delete.not.exists", "Invalid request [DeleteGroup]. Error: cannot find group to delete!"),
    INVALID_REQUEST_GROUP_DELETE_NOT_PART_OF_DOMAIN(INVALID_REQUEST, "error.invalid.request.group.delete.not.part.of.domain", "Invalid request [DeleteGroup]. Error: group does not belong to domain!"),
    INVALID_REQUEST_GROUP_MEMBERSHIP_ADD_MEMBER_GROUP_NOT_EXISTS(INVALID_REQUEST,"error.invalid.request.group.membership.add.member.group.not.exists", "Invalid request [AddMemberToGroup]. Error: group does not exists!"),
    INVALID_REQUEST_GROUP_MEMBERSHIP_ADD_MEMBER_GROUP_NOT_PART_OF_DOMAIN(INVALID_REQUEST,"error.invalid.request.group.membership.add.member.group.not.part.of.domain", "Invalid request [AddMemberToGroup]. Error: group does not belong to given domain!"),
    INVALID_REQUEST_GROUP_MEMBERSHIP_ADD_USER_ALREADY_MEMBER(INVALID_REQUEST,"error.invalid.request.group.membership.add.user.already.member", "Invalid request [AddMembership]. Error: user [{{username}}] is already a member!", USERNAME),
    INVALID_REQUEST_GROUP_MEMBERSHIP_ADD_USER_NOT_EXISTS(INVALID_REQUEST,"error.invalid.request.group.membership.add.user.not.exists", "Invalid request [AddMembership]. Error: user [{{username}}] does not exists!", USERNAME),
    INVALID_REQUEST_GROUP_MEMBERSHIP_GET_MEMBERS_GROUP_NOT_EXISTS(INVALID_REQUEST, "error.invalid.request.group.membership.get.members.group.not.exists", "Invalid request [GetGroupMembers]. Error: group does not exists!"),
    INVALID_REQUEST_GROUP_MEMBERSHIP_GET_MEMBERS_GROUP_NOT_PART_OF_DOMAIN(INVALID_REQUEST,"error.invalid.request.group.membership.get.members.group.not.part.of.domain", "Invalid request [GetGroupMembers]. Error: group does not belong to given domain!"),
    INVALID_REQUEST_GROUP_MEMBERSHIP_REMOVE_MEMBER_GROUP_NOT_EXISTS(INVALID_REQUEST,"error.invalid.request.group.membership.remove.member.group.not.exists", "Invalid request [DeleteMemberFromGroup]. Error: group does not exists!"),
    INVALID_REQUEST_GROUP_MEMBERSHIP_REMOVE_MEMBER_GROUP_NOT_PART_OF_DOMAIN(INVALID_REQUEST,"error.invalid.request.group.membership.remove.member.group.not.part.of.domain", "Invalid request [DeleteMemberFromGroup]. Error: group does not belong to given domain!"),
    INVALID_REQUEST_GROUP_MEMBERSHIP_REMOVE_USER_NOT_MEMBER(INVALID_REQUEST, "error.invalid.request.group.membership.remove.user.not.member", "Invalid request [RemoveMembership]. Error: membership does not exists!"),
    INVALID_REQUEST_GROUP_MEMBERSHIP_REMOVE_USER_NOT_PART_OF_GROUP(INVALID_REQUEST,"error.invalid.request.group.membership.remove.user.not.part.of.group", "Invalid request [RemoveMembership]. Error: membership does not belong to group!"),
    INVALID_REQUEST_GROUP_UPDATE_BLANK_NAME(INVALID_REQUEST, "error.invalid.request.group.update.blank.name", "Invalid request [UpdateGroup]. Error: group name must not be blank!"),
    INVALID_REQUEST_GROUP_UPDATE_NOT_EXISTS(INVALID_REQUEST, "error.invalid.request.group.update.not.exists", "Invalid request [UpdateGroup]. Error: group with [{{groupId}}] does not exists!", GROUP_ID),
    INVALID_REQUEST_HTTP_REQUEST_INPUT_STREAM(INVALID_REQUEST, "error.invalid.request.http.request.input.stream", "Invalid request [HttpRequest]. Error: cannot read input stream!"),
    INVALID_REQUEST_HTTP_REQUEST_METHOD(INVALID_REQUEST, "error.invalid.request.http.request.method", "Invalid request [HttpRequest]. Error: missing or invalid HTTP request method [{{httpMethod}}]!", HTTP_METHOD),
    INVALID_REQUEST_HTTP_REQUEST_MISSING_ACTION(INVALID_REQUEST, "error.invalid.request.missing.action", "Invalid request [HttpRequest]. Error: action cannot be null!"),
    INVALID_REQUEST_HTTP_REQUEST_URI_VARIABLE(INVALID_REQUEST, "error.invalid.request.http.request.uri.variable", "Invalid request [HttpRequest]. Error: at least one path parameter must be provided!"),
    INVALID_REQUEST_HTTP_REQUEST_URI_VARIABLES_RESOURCE_FIRST_MATCH(INVALID_REQUEST, "error.invalid.request.http.request.uri.variables.resource.first.match", "Invalid request [HttpRequest]. Error: not enough path parameters provided to locate the resource (the first match the domain) [{{pathParams}}]!", PATH_PARAMS),
    INVALID_REQUEST_HTTP_REQUEST_URI_VARIABLES_RESOURCE_FIRST_REMAINING_MATCHES(INVALID_REQUEST, "error.invalid.request.http.request.uri.variables.resource.first.remaining.matches", "Invalid request [HttpRequest]. Error: invalid number of remaining subresource path parameters provided to locate the resource (expected only subresourceDef and subresource identifier) [{{pathParams}}]!", PATH_PARAMS),
    INVALID_REQUEST_HTTP_REQUEST_URI_VARIABLES_RESOURCE_FIRST_TWO_MATCHES(INVALID_REQUEST, "error.invalid.request.http.request.uri.variables.resource.first.two.matches", "Invalid request [HttpRequest]. Error: not enough path parameters provided to locate the resource (the first two match the domain and the resource type) [{{pathParams}}]!", PATH_PARAMS),
    INVALID_REQUEST_HTTP_REQUEST_URI_VARIABLES_RESOURCE_NO_MATCHES(INVALID_REQUEST, "error.invalid.request.http.request.uri.variables.resource.no.matches", "Invalid request [HttpRequest]. Error: no path parameters provided to locate the resource!"),
    INVALID_REQUEST_HTTP_REQUEST_URI_VARIABLES_RESOURCE_SUBRESOURCE_MATCH(INVALID_REQUEST, "error.invalid.request.http.request.uri.variables.resource.subresource.match", "Invalid request [HttpRequest]. Error: subresource [{{urlSegment}}] does not exist for resource type [{{resource}}]!", URL_SEGMENT, RESOURCE),
    INVALID_REQUEST_HTTP_REQUEST_URI_VARIABLES_RESOURCE_TOO_MANY_MATCHES(INVALID_REQUEST, "error.invalid.request.http.request.uri.variables.resource.too.many.matches", "Invalid request [HttpRequest]. Error: more than the maximum count of 5 path parameters provided to locate the resource [{{pathParams}}]!", PATH_PARAMS),
    INVALID_REQUEST_HTTP_RESPONSE_OUTPUT_STREAM(INVALID_REQUEST,  "error.invalid.request.http.response.output.stream", "Invalid request [HttpResponse]. Error: cannot open output stream!"),
    INVALID_REQUEST_RESOURCE_CREATE_GROUP_NOT_EXISTS(INVALID_REQUEST, "error.invalid.request.resource.create.group.not.exists", "Invalid request [CreateResourceForGroup]. Error: group does not exist!"),
    INVALID_REQUEST_RESOURCE_CREATE_GROUP_NOT_PART_OF_DOMAIN(INVALID_REQUEST, "error.invalid.request.resource.create.group.not.part.of.domain", "Invalid request [CreateResourceForGroup]. Error: group does not belong to the given domain!"),
    INVALID_REQUEST_RESOURCE_CREATE_RESOURCE_ALREADY_EXISTS(INVALID_REQUEST, "error.invalid.request.resource.create.resource.already.exists", "Invalid request [CreateResourceForGroup]. Error: resource definition (val: [{{identifier}}] scheme: [{{scheme}}]) already exists for the domain!", IDENTIFIER, SCHEME),
    INVALID_REQUEST_RESOURCE_CREATE_RESOURCE_NOT_EXISTS(INVALID_REQUEST, "error.invalid.request.resource.create.resource.not.exists", "Invalid request [CreateResourceForGroup]. Error: resource definition [{{identifier}}] does not exist!", IDENTIFIER),
    INVALID_REQUEST_RESOURCE_CREATE_RESOURCE_NOT_PART_OF_DOMAIN(INVALID_REQUEST, "error.invalid.request.resource.create.resource.not.part.of.domain", "Invalid request [CreateResourceForGroup]. Error: resource definition [{{identifier}}] is not registered for domain!", IDENTIFIER),
    INVALID_REQUEST_RESOURCE_LIST_GROUP_NOT_EXISTS(INVALID_REQUEST, "error.invalid.request.resource.list.group.not.exists", "Invalid request [GetResourceListForGroup]. Error: group does not exist!"),
    INVALID_REQUEST_RESOURCE_LIST_USER_NOT_EXISTS(INVALID_REQUEST, "error.invalid.request.resource.list.user.not.exists", "Invalid request [GetResourceListForGroup]. Error: user does not exist!"),
    INVALID_REQUEST_RESOURCE_MEMBERSHIP_ADD_MEMBER_GROUP_NOT_PART_OF_DOMAIN(INVALID_REQUEST, "error.invalid.request.resource.membership.add.member.group.not.part.of.domain", "Invalid request [AddMemberToResource]. Error: group does not belong to given domain!"),
    INVALID_REQUEST_RESOURCE_MEMBERSHIP_ADD_MEMBER_RESOURCE_NOT_EXISTS(INVALID_REQUEST, "error.invalid.request.resource.membership.add.member.resource.not.exists", "Invalid request [AddMemberToResource]. Error: resource does not exists!"),
    INVALID_REQUEST_RESOURCE_MEMBERSHIP_ADD_USER_ALREADY_MEMBER(INVALID_REQUEST,"error.invalid.request.resource.membership.add.user.already.member", "Invalid request [AddMembership]. Error: user [{{username}}] is already a member!", USERNAME),
    INVALID_REQUEST_RESOURCE_MEMBERSHIP_ADD_USER_NOT_EXISTS(INVALID_REQUEST,"error.invalid.request.resource.membership.add.user.not.exists", "Invalid request [AddMembership]. Error: user [{{username}}] does not exists!", USERNAME),
    INVALID_REQUEST_RESOURCE_MEMBERSHIP_GET_MEMBERS_GROUP_NOT_PART_OF_DOMAIN(INVALID_REQUEST,"error.invalid.request.resource.membership.get.members.group.not.part.of.domain", "Invalid request [GetResourceMembers]. Error: group does not belong to given domain!"),
    INVALID_REQUEST_RESOURCE_MEMBERSHIP_GET_MEMBERS_RESOURCE_NOT_EXISTS(INVALID_REQUEST,"error.invalid.request.resource.membership.get.members.resource.not.exists", "Invalid request [GetResourceMembers]. Error: resource does not exists!"),
    INVALID_REQUEST_RESOURCE_MEMBERSHIP_REMOVE_MEMBER_GROUP_NOT_PART_OF_DOMAIN(INVALID_REQUEST,"error.invalid.request.resource.membership.remove.member.group.not.part.of.domain", "Invalid request [DeleteMemberFromResource]. Error: group does not belong to given domain!"),
    INVALID_REQUEST_RESOURCE_MEMBERSHIP_REMOVE_MEMBER_RESOURCE_NOT_EXISTS(INVALID_REQUEST,"error.invalid.request.resource.membership.remove.member.resource.not.exists", "Invalid request [DeleteMemberFromResource]. Error: resource does not exists!"),
    INVALID_REQUEST_RESOURCE_MEMBERSHIP_REMOVE_USER_NOT_MEMBER(INVALID_REQUEST,"error.invalid.request.resource.membership.remove.user.not.member", "Invalid request [RemoveMembership]. Error: membership does not exists!"),
    INVALID_REQUEST_RESOURCE_MEMBERSHIP_REMOVE_USER_NOT_PART_OF_RESOURCE(INVALID_REQUEST,"error.invalid.request.resource.membership.remove.user.not.part.of.resource", "Invalid request [RemoveMembership]. Error: membership does not belong to resource!"),
    INVALID_REQUEST_RESOURCE_REMOVE_GROUP_NOT_PART_OF_DOMAIN(INVALID_REQUEST, "error.invalid.request.resource.remove.group.not.part.of.domain", "Invalid request [DeleteResourceFromGroup]. Error: group does not belong to the given domain!"),
    INVALID_REQUEST_RESOURCE_REMOVE_RESOURCE_NOT_EXISTS(INVALID_REQUEST, "error.invalid.request.resource.remove.resource.not.exists", "Invalid request [DeleteResourceFromGroup]. Error: resource does not exist!"),
    INVALID_REQUEST_RESOURCE_REMOVE_RESOURCE_NOT_PART_OF_GROUP(INVALID_REQUEST, "error.invalid.request.resource.remove.resource.not.part.of.group", "Invalid request [DeleteResourceFromGroup]. Error: resource does not belong to the group!"),
    INVALID_REQUEST_RESOURCE_UPDATE_GROUP_NOT_EXISTS(INVALID_REQUEST, "error.invalid.request.resource.update.group.not.exists", "Invalid request [UpdateResource]. Error: group does not exist!"),
    INVALID_REQUEST_RESOURCE_UPDATE_GROUP_NOT_PART_OF_DOMAIN(INVALID_REQUEST,"error.invalid.request.resource.update.group.not.part.of.domain", "Invalid request [UpdateResource]. Error: group does not belong to the given domain!"),
    INVALID_REQUEST_RESOURCE_UPDATE_GROUP_RESOURCE_NOT_EXISTS(INVALID_REQUEST,"error.invalid.request.resource.update.group.resource.not.exists", "Invalid request [UpdateResource]. Error: resource definition [{{identifier}}] does not exist!", IDENTIFIER),
    INVALID_REQUEST_RESOURCE_UPDATE_GROUP_RESOURCE_NOT_PART_OF_DOMAIN(INVALID_REQUEST,"error.invalid.request.resource.update.group.resource.not.part.of.domain", "Invalid request [UpdateResource]. Error: resource definition [{{identifier}}] is not registered for domain!", IDENTIFIER),
    INVALID_REQUEST_RESOURCEDEF_LOOKUP_FOR_DOMAIN_BY_IDENTIFIER(INVALID_REQUEST, "error.invalid.request.resourcedef.lookup.by.identifier", "Invalid request: Resource def [{{identifier}}] does not exist for [{{domainCode}}]", IDENTIFIER, DOMAIN_CODE),
    INVALID_REQUEST_DOC_TEMPLATE_ALREADY_EXISTS_FOR_RESOURCEDEF_AND_DOMAIN(INVALID_REQUEST, "error.invalid.request.domain.doc.tmpl.already.exists", "Invalid request: Document template for resource def [{{identifier}}] and domain [{{domainCode}}] already exists", IDENTIFIER, DOMAIN_CODE),
    INVALID_REQUEST_SUBRESOURCE_CREATE_RESOURCE_NOT_EXISTS(INVALID_REQUEST,"error.invalid.request.subresource.create.resource.not.exists", "Invalid request [CreateSubresourceForResource]. Error: resource does not exist!"),
    INVALID_REQUEST_SUBRESOURCE_CREATE_SUBRESOURCE_ALREADY_EXISTS(INVALID_REQUEST,"error.invalid.request.subresource.create.subresource.already.exists", "Invalid request [CreateSubresourceForResource]. Error: subresource definition (val: [{{identifier}}] scheme: [{{scheme}}]) already exists for the resource!", IDENTIFIER, SCHEME),
    INVALID_REQUEST_SUBRESOURCE_CREATE_SUBRESOURCE_NOT_EXISTS(INVALID_REQUEST,"error.invalid.request.subresource.create.subresource.not.exists", "Invalid request [CreateSubresourceForResource]. Error: subresource definition [{{identifier}}] does not exist!", IDENTIFIER),
    INVALID_REQUEST_SUBRESOURCE_DELETE_RESOURCE_NOT_EXISTS(INVALID_REQUEST,"error.invalid.request.subresource.delete.resource.not.exists", "Invalid request [DeleteSubresourceFromResource]. Error: resource does not exist!"),
    INVALID_REQUEST_SUBRESOURCE_DELETE_SUBRESOURCE_NOT_EXISTS(INVALID_REQUEST,"error.invalid.request.subresource.delete.subresource.not.exists", "Invalid request [DeleteSubresourceFromResource]. Error: subresource does not exist!"),
    INVALID_REQUEST_SUBRESOURCE_DELETE_SUBRESOURCE_NOT_PART_OF_RESOURCE(INVALID_REQUEST,"error.invalid.request.subresource.delete.subresource.not.part.of.resource", "Invalid request [DeleteSubresourceFromResource]. Error: subresource does not belong to the resource!"),
    INVALID_REQUEST_USER_CREATE_USER_ALREADY_EXISTS(INVALID_REQUEST,"error.invalid.request.user.create.user.already.exists", "Invalid request [CreateUser]. Error: user with username [{{username}}] already exists!", USERNAME),
    INVALID_REQUEST_USER_CREDENTIALS_CERTIFICATE_NOT_EXISTS(INVALID_REQUEST,"error.invalid.request.user.credentials.certificate.not.exists", "Invalid request [CertificateCredentials]. Error: certificate is not given for certificate credential!"),
    INVALID_REQUEST_USER_CREDENTIALS_NOT_EXISTS(INVALID_REQUEST,"error.invalid.request.user.credentials.not.exists", "Invalid request [Credentials]. Error: credentials with [{{certificateCredentialId}}] does not exist!", CERTIFICATE_CREDENTIAL_ID),
    INVALID_REQUEST_USER_NOT_EXISTS(INVALID_REQUEST,"error.invalid.request.user.not.exists", "Invalid request [UserId]. Error: user with given id does not exist!", USER_ID),
    INVALID_REQUEST_USER_PASSWORD_CHANGE(INVALID_REQUEST,"error.invalid.request.user.password.change", "Invalid request [PasswordChange]. Error: [{{error}}]!", ERROR),
    INVALID_REQUEST_VALIDATE_PAYLOAD(INVALID_REQUEST, "error.invalid.request.validate.payload", "Invalid request [UploadPayload]. Error: content validation failed!"),
    INVALID_REQUEST_VALIDATE_PROPERTY(INVALID_REQUEST, "error.invalid.request.validate.property", "Invalid request [ValidateProperty]. Error: property name is empty!"),
    INVALID_REQUEST_PARAMETER(WRONG_FIELD, "error.invalid.request.parameter", "Invalid request parameter. Error: [{{error}}]! ", ERROR),
    MAIL_SUBMISSION(MAIL_SUBMISSION_ERROR, "error.mail.submission", "Mail submission error: [{{error}}]!", ERROR),
    SPI_VALIDATION_CANNOT_READ_PAYLOAD(SPI_GENERIC_ERROR, "error.spi.validation.payload.cannot.read", "Cannot read payload"),
    SPI_VALIDATION_PAYLOAD_STARTS_WITH_E(SPI_GENERIC_ERROR, "error.spi.validation.payload.starts.with.e", "This is invalid payload starting with E"),
    RESOURCE_DOCUMENT_MISSING(RESOURCE_DOCUMENT_ERROR, "error.resource.document.missing", "Empty document for the resource: (id: [{{identifier}}], sch.: [{{scheme}}])!", IDENTIFIER, SCHEME),
    RESOURCE_DOCUMENT_READING(RESOURCE_DOCUMENT_ERROR, "error.resource.document.reading", "Error occurred while reading the resource document: (id: [{{identifier}}], sch.: [{{scheme}}])! Error: multiple documents!", IDENTIFIER, SCHEME),
    RESOURCE_ILLEGAL_STATE_MULTIPLE_ENTRIES(INTERNAL_ERROR, "error.resource.illegal.state.multiple.entries", "More than one resource (part. id: [{{identifier}}], part. sch.: [{{scheme}}]) is defined in database for the same domain!", IDENTIFIER, SCHEME),
    RESOURCE_INVALID_EXTENSION(ErrorCode.INVALID_EXTENSION_FOR_SG, "error.resource.invalid.extension", "Invalid extension for resource (part. id: [{{identifier}}], part. sch.: [{{scheme}}]). Error: [{{error}}]!", IDENTIFIER, SCHEME, ERROR),
    RESOURCE_NOT_EXISTS(ErrorCode.RESOURCE_NOT_EXISTS, "error.resource.not.exists", "Resource not found (part. id: [{{identifier}}], part. sch.: [{{scheme}}])!", IDENTIFIER, SCHEME),
    SUBRESOURCE_ILLEGAL_STATE_MULTIPLE_ENTRIES(INTERNAL_ERROR, "error.subresource.illegal.state.multiple.entries", "More than one subresource (part. id: [{{identifier}}], part. sch.: [{{scheme}}], doc. id: [{{documentIdentifier}}], doc. sch.: [{{documentScheme}}]) is defined in database!", IDENTIFIER, SCHEME, DOCUMENT_IDENTIFIER, DOCUMENT_SCHEME),
    SUBRESOURCE_INVALID_XML(ErrorCode.INVALID_SMD_XML, "error.subresource.invalid.xml", "Invalid subresource. Error: [{{error}}]", ERROR),
    SUBRESOURCE_NOT_EXISTS(ErrorCode.SUBRESOURCE_NOT_EXISTS, "error.subresource.not.exists", "Subresource not found (part. id: [{{identifier}}], part. sch.: [{{scheme}}], doc. id: [{{documentIdentifier}}], doc. sch.: [{{documentScheme}}])!", IDENTIFIER, SCHEME, DOCUMENT_IDENTIFIER, DOCUMENT_SCHEME),
    SUBRESOURCE_DOCUMENT_MISSING(SUBRESOURCE_DOCUMENT_ERROR, "error.subresource.document.missing", "Empty document for the subresource: (docId: [{{documentIdentifier}}], docSch.: [{{documentScheme}}]) of the resource (id: [{{identifier}}], sch.: [{{scheme}}])", DOCUMENT_IDENTIFIER, DOCUMENT_SCHEME, IDENTIFIER, SCHEME),
    SUBRESOURCE_DOCUMENT_READING(SUBRESOURCE_DOCUMENT_ERROR, "error.subresource.document.reading", "Error occurred while reading the subresource document: : (docId: [{{documentIdentifier}}], docSch.: [{{documentScheme}}]] of the resource (id: [{{identifier}}], sch: [{{scheme}}])! Error: multiple documents for subresource!", DOCUMENT_IDENTIFIER, DOCUMENT_SCHEME, IDENTIFIER, SCHEME),
    UI_ACCESS_DENIED_EXCEPTION(UNAUTHORIZED, "error.ui.access.denied.exception", "Unexpected access denies error occurred."),
    UI_AUTHENTICATION_EXCEPTION(UNAUTHORIZED, "error.ui.authentication.exception", "Access not authorized. Invalid credentials or credential type."),
    UI_BAD_REQUEST_EXCEPTION(INVALID_REQUEST, "error.ui.bad.request.exception", "Unexpected bad request error occurred."),
    UI_BAD_REQUEST_WITH_ERROR(INVALID_REQUEST, "error.ui.bad.request.with.error", "Bad request. Error: {{error}}", ERROR),
    UI_INTERNAL_ERROR(INTERNAL_ERROR, "error.ui.internal.error", "Unexpected technical error occurred."),
    UI_MALFORMED_IDENTIFIER_EXCEPTION(INVALID_REQUEST, "error.ui.malformed.identifier.exception", "Unexpected malformed identifier error occurred. Error: [{{error}}].", ERROR),
    UI_SMP_RUNTIME_EXCEPTION(INTERNAL_ERROR, "error.ui.smp.runtime.exception", "Unexpected runtime error occurred."),
    UI_VALIDATION_LOCALE(INTERNAL_ERROR, "error.ui.validation.locale", "Invalid locale [{{locale}}].",LOCALE),
    UI_RESOURCE_INVALID_REFERENCE(VALIDATION_ERROR, "error.ui.resource.invalid.reference", "Resource has invalid reference. The referenced document is not accessible anymore and the reference own document is shown instead. Fix error in document editor"),
    UI_SUBRESOURCE_INVALID_REFERENCE(VALIDATION_ERROR, "error.ui.subresource.invalid.reference", "Subresource has invalid reference. The referenced document is not accessible anymore and the reference own document is shown instead. Fix error in document editor "),
    UNAUTHORIZED_CREDENTIAL_NOT_EXISTS(UNAUTHORIZED, "error.unauthorized.credential.not.exists", "Credential does not exist!"),
    UNAUTHORIZED_CREDENTIAL_NOT_OWNER(UNAUTHORIZED, "error.unauthorized.credential.not.owner", "User is not owner of the credential!"),
    UNAUTHORIZED_CREDENTIAL_WRONG_TYPE(UNAUTHORIZED, "error.unauthorized.credential.wrong.type", "Credentials are not expected credential type!"),
    UNAUTHORIZED_CREDENTIAL_WRONG_TARGET_TYPE(UNAUTHORIZED, "error.unauthorized.credential.wrong.target.type", "Credentials are not expected target type!"),
    UNAUTHORIZED_CREDENTIAL_SUSPENDED(UNAUTHORIZED, "error.unauthorized.credential.suspended", "The user credential is suspended. Please try again later or contact your administrator."),
    UNAUTHORIZED_INVALID_BEARER_TOKEN(UNAUTHORIZED, "error.unauthorized.invalid.bearer.token", "The bearer token is invalid or not active any more. Please try to regenerate your token."),
    UNAUTHORIZED_INVALID_RESET_TOKEN(UNAUTHORIZED, "error.unauthorized.invalid.reset.token", "The reset token it is invalid or not active any more. Please try to reset your password again."),
    UNAUTHORIZED_INVALID_USERNAME_PASSWORD(UNAUTHORIZED, "error.unauthorized.invalid.username.password", "Login failed; Invalid userID or password!"),
    UNAUTHORIZED_UNAUTHORIZED_INVALID_IDENTIFIER(UNAUTHORIZED, "error.unauthorized.unauthorized.invalid.identifier", "Invalid entity identifier! User not authorized to access the entity data"),
    UNAUTHORIZED_UNAUTHORIZED_INVALID_USER_IDENTIFIER(UNAUTHORIZED, "error.unauthorized.unauthorized.invalid.user.identifier", "Invalid user identifier! User not authorized."),
    UNAUTHORIZED_USER(UNAUTHORIZED,"error.unauthorized.user", "User not authorized!"),
    UNAUTHORIZED_USER_CHANGE_INVALID_NEW_CREDENTIAL(ErrorCode.USER_CHANGE_INVALID_NEW_CREDENTIAL, "error.unauthorized.user.change.invalid.new.credential", "Password change failed. [{{validationMessage}}]", VALIDATION_MESSAGE),
    UNAUTHORIZED_USER_CHANGE_INVALID_AUTHORIZATION_CREDENTIAL(ErrorCode.UNAUTHORIZED, "error.unauthorized.user.change.invalid.authorization.credential", "Password change failed. Invalid authorization password!"),
    UNAUTHORIZED_USER_FOR_GROUP(UNAUTHORIZED, "error.unauthorized.user.for.group", "User [{{username}}] is not authorized for group [{{domainGroup}}] in domain [{{domainCode}}]", USERNAME, DOMAIN_GROUP, DOMAIN_CODE),
    UNAUTHORIZED_USER_NOT_ADMIN(UNAUTHORIZED, "error.unauthorized.user.not.admin", "User [{{username}}] is not admin for any group in domain [{{domainCode}}]", USERNAME, DOMAIN_CODE),
    USER_ILLEGAL_STATE_USERNAME_MULTIPLE_ENTRIES(ILLEGAL_STATE_USERNAME_MULTIPLE_ENTRY, "error.user.illegal.state.username.multiple.entries", "More than one user entry (credential token: [{{identifier}}]) is defined in database!", IDENTIFIER),
    USER_INVALID_NO_IDENTIFIERS(INVALID_USER_NO_IDENTIFIERS,"error.user.invalid.no.identifiers", "Invalid user: no identifiers!"),
    USER_INVALID_OWNER(INVALID_OWNER,"error.user.invalid.owner", "Invalid owner id: [{{identifier}}]", IDENTIFIER),
    USER_NOT_EXISTS(ErrorCode.USER_NOT_EXISTS, "error.user.not.exists", "User does not exist or the password is wrong!"),
    ;

    private static final Logger LOG = LoggerFactory.getLogger(ErrorMessageType.class);
    private final ErrorCode errorCode;
    private final String messageCode;
    private final String template;

    private final ErrorMessageArgument[] arguments;

    ErrorMessageType(ErrorCode errorCode, String  messageCode, String message, ErrorMessageArgument... arguments) {
        this.errorCode = errorCode;
        this.messageCode = messageCode;
        this.template = message;
        this.arguments = arguments;
    }

    public String getTemplate() {
        return template;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public ErrorMessageArgument[] getArguments() {
        return arguments;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessageTranslation(Map<ErrorMessageArgument, Object> arguments) {
        Map<String, Object> args = arguments.keySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        ErrorMessageArgument::getArgumentName,
                        arguments::get
                ));
        return ErrorMessageType.replacePlaceholder(getTemplate(), args);
    }

    public static String replacePlaceholder(String template, Map<String, Object> arguments) {
        String message = template;
        for (String placeholder : arguments.keySet()) {
            message = RegExUtils.replaceAll(message, "\\{\\{" + placeholder + "\\}\\}",
                    Matcher.quoteReplacement(Objects.toString(arguments.get(placeholder))));
        }
        return message;
    }

    public static ErrorMessageType getErrorMessageTypeByCode(String messageCode) {

        if (StringUtils.isBlank(messageCode)) {
            LOG.warn("The message code is blank so returning null.");
            return null;
        }
        return java.util.Arrays.stream(ErrorMessageType.values())
                .filter(type -> Strings.CI.equals(type.getMessageCode(), messageCode))
                .findFirst()
                .orElse(null);
    }

    public static Properties getAllErrorMessagesAsProperties(){
        Properties properties = new Properties();
        for(ErrorMessageType type : ErrorMessageType.values()){
            properties.put(type.getMessageCode(), type.getTemplate());
        }
        return properties;
    }



}
