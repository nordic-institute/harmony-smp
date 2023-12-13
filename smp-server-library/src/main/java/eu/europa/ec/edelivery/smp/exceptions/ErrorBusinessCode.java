/*-
 * #%L
 * smp-webapp
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
 * #L%
 */

package eu.europa.ec.edelivery.smp.exceptions;

/**
 * Created by migueti on 16/01/2017.
 */
public enum ErrorBusinessCode {
    XSD_INVALID,
    MISSING_FIELD,
    WRONG_FIELD,
    OUT_OF_RANGE,
    UNAUTHOR_FIELD,
    FORMAT_ERROR,
    OTHER_ERROR,
    UNAUTHORIZED,
    NOT_FOUND,
    USER_NOT_FOUND,
    INVALID_INPUT_DATA,
    TECHNICAL
}
