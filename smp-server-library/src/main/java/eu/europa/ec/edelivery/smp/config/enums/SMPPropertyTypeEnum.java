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
package eu.europa.ec.edelivery.smp.config.enums;

/**
 * DomiSMP application properties types
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public enum SMPPropertyTypeEnum {
    STRING("^.{0,2000}$", "error.invalid.property.string"),
    DATETIME(".{0,2000}", "error.invalid.property.datetime"),
    LIST_STRING(".{0,2000}", "error.invalid.property.list.string"),
    MAP_STRING(".{0,2000}", "error.invalid.property.map.string"),
    INTEGER("^\\d{0,12}$", "error.invalid.property.integer"),
    BOOLEAN("true|false", "error.invalid.property.boolean"),
    REGEXP("^.{0,2000}$", "error.invalid.property.regexp"),
    CRON_EXPRESSION("^([\\d\\*\\/\\-,]+\\s){5}[\\d\\*\\/\\-,]+$", "error.invalid.property.cron.expression"),
    EMAIL("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+$", "error.invalid.property.email"),
    FILENAME(".{0,2000}", "error.invalid.property.filename"),
    PATH(".{0,2000}", "error.invalid.property.path"),
    URL("^([A-Za-z]+)://[^\\s/$.?#].[^\\s]*$", "error.invalid.property.url"),
    CERTIFICATE(".{0,4000}", "error.invalid.property.certificate"),
    ;

    final String defValidationRegExp;
    final String errorMessageCode;

    SMPPropertyTypeEnum(String defValidationRegExp, String errorMessageCode) {
        this.defValidationRegExp = defValidationRegExp;
        this.errorMessageCode = errorMessageCode;
    }

    public String getErrorMessageCode() {
        return errorMessageCode;
    }
}
