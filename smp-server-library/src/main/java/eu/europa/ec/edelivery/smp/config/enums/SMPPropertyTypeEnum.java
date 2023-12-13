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
package eu.europa.ec.edelivery.smp.config.enums;
/**
 * DomiSMP application properties types
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public enum SMPPropertyTypeEnum {
    STRING (".{0,2000}","Property value [%s] must be less than 2000 characters!"),
    LIST_STRING(".{0,2000}","Property [%s] is not valid LIST_STRING type!"),
    MAP_STRING(".{0,2000}","Property [%s] is not valid MAP_STRING type!"),
    INTEGER("\\d{0,12}","Property [%s] is not valid Integer!"),
    BOOLEAN("true|false","Property [%s] is not valid Boolean type!"),
    REGEXP(".{0,2000}", "Property [%s] is not valid Regular Expression type!"),
    CRON_EXPRESSION(".{0,2000}","Property [%s] is not valid Cron Expression type!"),
    EMAIL(".{0,2000}","Property [%s] is not valid Email address type!"),
    FILENAME(".{0,2000}","Property [%s] is not valid Filename type or it does not exists!"),
    PATH(".{0,2000}","Property [%s] is not valid Path type or it does not exists!"),
    URL(".{0,2000}","Property [%s] is not valid URL!"),
    ;

    String errorTemplate;
    String defValidationRegExp;

    SMPPropertyTypeEnum(String defValidationRegExp, String errorTemplate ) {
        this.defValidationRegExp = defValidationRegExp;
        this.errorTemplate =errorTemplate;

    }

    public String getErrorMessage(String property) {
        return String.format(errorTemplate, property);
    }
}
