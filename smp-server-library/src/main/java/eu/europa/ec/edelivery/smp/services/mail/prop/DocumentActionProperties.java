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
package eu.europa.ec.edelivery.smp.services.mail.prop;

/**
 * Properties available for document action mail templates.
 * @author Joze Rihtarsic
 * @since 5.2
 */
public enum DocumentActionProperties {
    ACTION,
    DOCUMENT_NAME,
    DOCUMENT_VERSION,
    DOCUMENT_TYPE,
    RESOURCE_IDENTIFIER,
    RESOURCE_SCHEME,
    SUBRESOURCE_IDENTIFIER,
    SUBRESOURCE_SCHEME,
    DOMAIN,
    ACTION_BY,
}
