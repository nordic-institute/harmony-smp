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

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * DomiSMP domain specific properties
 *
 * @author Joze Rihtarsic
 * @since 5.1
 */
public enum SMPDomainPropertyEnum {


    RESOURCE_SCH_VALIDATION_REGEXP(SMPPropertyEnum.RESOURCE_SCH_VALIDATION_REGEXP, false),
    RESOURCE_SCH_REGEXP_MSG(SMPPropertyEnum.RESOURCE_SCH_REGEXP_MSG, false),
    RESOURCE_SCH_MANDATORY(SMPPropertyEnum.RESOURCE_SCH_MANDATORY, false),
    RESOURCE_IDENTIFIER_TMPL_MATCH_REGEXP(SMPPropertyEnum.RESOURCE_IDENTIFIER_TMPL_MATCH_REGEXP, false),
    RESOURCE_IDENTIFIER_TMPL_SPLIT_REGEXP(SMPPropertyEnum.RESOURCE_IDENTIFIER_TMPL_SPLIT_REGEXP, false),
    RESOURCE_IDENTIFIER_TMPL_CONCATENATE(SMPPropertyEnum.RESOURCE_IDENTIFIER_TMPL_CONCATENATE, false),
    RESOURCE_IDENTIFIER_TMPL_CONCATENATE_NULL_SCHEME(SMPPropertyEnum.RESOURCE_IDENTIFIER_TMPL_CONCATENATE_NULL_SCHEME, false),
    RESOURCE_CASE_SENSITIVE_SCHEMES(SMPPropertyEnum.RESOURCE_CASE_SENSITIVE_SCHEMES, false),
    SUBRESOURCE_CASE_SENSITIVE_SCHEMES(SMPPropertyEnum.SUBRESOURCE_CASE_SENSITIVE_SCHEMES, false),
    ;
    // System equivalent property
    private final SMPPropertyEnum propertyEnum;
    /**
     * If true, only system admin can change/view the value for the domain
     */
    private final boolean systemAdminOnly;

    SMPDomainPropertyEnum(SMPPropertyEnum propertyEnum, boolean systemAdminOnly) {
        this.propertyEnum = propertyEnum;
        this.systemAdminOnly = systemAdminOnly;
    }

    public boolean isSystemAdminOnly() {
        return systemAdminOnly;
    }

    public boolean isNotSystemAdminOnly() {
        return !systemAdminOnly;
    }

    public String getProperty() {
        return propertyEnum.getProperty();
    }

    public String getDefValue() {
        return propertyEnum.getDefValue();
    }

    public String getDesc() {
        return propertyEnum.getDesc();
    }

    public SMPPropertyTypeEnum getPropertyType() {
        return propertyEnum.getPropertyType();
    }

    public static Optional<SMPDomainPropertyEnum> getByProperty(String key) {
        String keyTrim = StringUtils.trimToNull(key);
        if (keyTrim == null) {
            return Optional.empty();
        }
        return Arrays.stream(values()).filter(val -> val.getProperty().equalsIgnoreCase(keyTrim)).findAny();
    }

    public Pattern getValuePattern() {
        return propertyEnum.getValuePattern();
    }

    public String getErrorValueMessage() {
        return propertyEnum.getErrorValueMessage();
    }

    public SMPPropertyEnum getPropertyEnum() {
        return propertyEnum;
    }
}


