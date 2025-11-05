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

/**
 * Error message types with i18n keys and default EN messages
 *
 * @since 5.2
 * @author Joze Rihtarsic
 */
public enum ErrorMessageArgument {
    ABSOLUTE_PATH("absolutePath"),
    ACTION("action"),
    ALIAS("alias"),
    CERTIFICATE("certificate"),
    CERTIFICATE_CREDENTIAL_ID("certificateCredentialId"),
    CLIENT_CERT_ENABLED("clientCertEnabled"),
    CRL_URL("crlURL"),
    DNS_LOOKUP_TYPE("dnsLookupType"),
    DOCUMENT_IDENTIFIER("documentIdentifier"),
    DOCUMENT_SCHEME("documentScheme"),
    DOMAIN_CODE("domainCode"),
    DOMAIN_GROUP("domainGroup"),
    DOMAIN_ID("domainId"),
    ERROR("error"),
    GROUP_ID("groupId"),
    GROUP_NAME("groupName"),
    HEADER_PARAMETER("headerParameter"),
    HTTP_METHOD("httpMethod"),
    IDENTIFIER("identifier"),
    IDENTIFIERS("identifiers"),
    JNDI_DATASOURCE_NAME("jndiDatasourceName"),
    KEYSTORE_FILE("keystoreFile"),
    LOCALE("locale"),
    MIGRATION_DATE("migrationDate"),
    PATH("path"),
    PATH_PARAMS("pathParams"),
    PATTERN("pattern"),
    PROPERTY_NAME("propertyName"),
    PROPERTY_VALUE("propertyValue"),
    RESOURCE("resource"),
    RESOURCES_COUNT("resourcesCount"),
    SCHEME("scheme"),
    SCOPE("scope"),
    SML_CLIENT_KEY_CHANGE_ALIAS("smlClientKeyChangeAlias"),
    URL_SEGMENT("urlSegment"),
    USER("user"),
    USER_ID("userId"),
    USER_ROLE("userRole"),
    USERNAME("username"),
    VALIDATION_MESSAGE("validationMessage"),
    // special arguments for error messages which is code for i18n translation messages and need to be translated. The value
    // is set back to argument map with code 'error'
    ERROR_MESSAGE_CODE("errorMessageCode"),
    ;
    private final String argumentName;
    ErrorMessageArgument(String argumentName) {
        this.argumentName = argumentName;
    }
    public String getArgumentName() {
        return argumentName;
    }


}
