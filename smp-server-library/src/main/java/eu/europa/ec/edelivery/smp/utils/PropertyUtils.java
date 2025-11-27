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
package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.auth.enums.SMPAutomationAuthenticationTypes;
import eu.europa.ec.edelivery.smp.auth.enums.SMPUserAuthenticationTypes;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyTypeEnum;
import eu.europa.ec.edelivery.smp.exceptions.ErrorMessageArgument;
import eu.europa.ec.edelivery.smp.exceptions.ErrorMessageType;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.event.Level;
import org.springframework.scheduling.support.CronExpression;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorMessageArgument.*;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorMessageType.*;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Utility class for SMP properties parsing and validation
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class PropertyUtils {


    public static final String MASKED_VALUE = "*******";
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(PropertyUtils.class);
    private static final String REG_EXP_VALUE_SEPARATOR = "\\|";
    private static final String REG_EXP_MAP_SEPARATOR = ":";

    private PropertyUtils() {
        // private constructor
    }

    public static Object parseProperty(SMPPropertyEnum prop, String value, File rootFolder) {
        if (StringUtils.isBlank(value)) {
            // empty/ null value is invalid
            if (prop.isMandatory()) {
                throw new SMPRuntimeException(CONFIGURATION_MANDATORY_PROPERTY
                ).addParam(PROPERTY_NAME, prop.getProperty());
            }
            return null;
        }
        if (!prop.getValuePattern().matcher(value).matches()) {
            LOG.debug("Value [{}] for property [{}] does not match [{}]", value, prop.getProperty(), prop.getValuePattern().pattern());
            throw new SMPRuntimeException(ErrorMessageType.CONFIGURATION_PROPERTY)
                    .addParam(ERROR_MESSAGE_CODE, prop.getErrorMessageCode())
                    .addParam(PROPERTY_NAME, prop.getProperty())
                    .addParam(PROPERTY_VALUE,value);
        }

        SMPPropertyTypeEnum type = prop.getPropertyType();
        Object result;
        try {
            result = parsePropertyType(type, value, rootFolder);
        } catch (SMPRuntimeException ex) {
            LOG.debug("Invalid property value [{}] for property [{}]. Error: [{}]", value, prop.getProperty(), ExceptionUtils.getRootCauseMessage(ex));
            // rethrow with property specific error message code for consistency
            throw new SMPRuntimeException(ErrorMessageType.CONFIGURATION_PROPERTY, ex)
                    .addParam(ERROR_MESSAGE_CODE, prop.getErrorMessageCode())
                    .addParam(PROPERTY_NAME, prop.getProperty())
                    .addParam(PROPERTY_VALUE,value);
        }

        return switch (prop) {
            case AUTOMATION_AUTHENTICATION_TYPES, UI_AUTHENTICATION_TYPES ->
                    parseEnumListPropertyType(prop, (List<String>) result);
            default -> result;
        };
    }

    public static boolean isValidProperty(SMPPropertyEnum prop, String value, File confFolder) {
        if (StringUtils.isBlank(value)) {
            // empty/ null value is invalid
            return !prop.isMandatory();
        }

        if (!prop.getValuePattern().matcher(value).matches()) {
            LOG.debug("Value [{}] for property [{}] does not match [{}]", value, prop.getProperty(), prop.getValuePattern().pattern());
            throw new SMPRuntimeException(ErrorMessageType.CONFIGURATION_PROPERTY)
                    .addParam(ErrorMessageArgument.ERROR_MESSAGE_CODE, prop.getErrorMessageCode())
                    .addParam(PROPERTY_NAME, prop.getProperty());
        }
        SMPPropertyTypeEnum type = prop.getPropertyType();
        return isValidPropertyType(type, value, confFolder);
    }

    public static boolean isValidPropertyType(SMPPropertyTypeEnum type, String value, File confFolder) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        try {
            parsePropertyType(type, value, confFolder);
            return true;
        } catch (SMPRuntimeException ex) {
            LOG.debug("Invalid property value [{}] for type [{}]. Error: [{}]", value, type, ExceptionUtils.getRootCauseMessage(ex));
            return false;
        }
    }

    public static Object parsePropertyType(SMPPropertyTypeEnum type, String value, File rootFolder) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if (StringUtils.length(value) > 2000) {
            throw new SMPRuntimeException(CONFIGURATION_INVALID_LENGTH);
        }

        switch (type) {
            case BOOLEAN:
                if (Strings.CI.equalsAny(trim(value), "true", "false")) {
                    return Boolean.valueOf(value.trim());
                }
                throw new SMPRuntimeException(CONFIGURATION_INVALID_BOOLEAN)
                        .addParam(PROPERTY_VALUE, value);

            case DATETIME: {

                OffsetDateTime dateTime = DateTimeUtils.parseToOffsetDateTime(value);
                if (dateTime == null) {
                    throw new SMPRuntimeException(CONFIGURATION_INVALID_DATETIME)
                            .addParam(PROPERTY_VALUE, value);
                }
                return dateTime;
            }
            case REGEXP:
                try {
                    return Pattern.compile(value);
                } catch (PatternSyntaxException ex) {
                    throw new SMPRuntimeException(CONFIGURATION_INVALID_REGULAR_EXPRESSION, ex)
                            .addParam(PROPERTY_VALUE, value)
                            .addParam(ERROR, ExceptionUtils.getRootCauseMessage(ex));
                }
            case INTEGER:
                try {
                    if (!type.getDefValidationPattern().matcher(value).matches()) {
                        throw new SMPRuntimeException(CONFIGURATION_INVALID_INTEGER)
                                .addParam(PROPERTY_VALUE, value)
                                .addParam(ERROR_MESSAGE_CODE, type.getErrorMessageCode());
                    }
                    return Integer.parseInt(value);
                } catch (NumberFormatException ex) {
                    throw new SMPRuntimeException(CONFIGURATION_INVALID_INTEGER, ex)
                            .addParam(PROPERTY_VALUE, value)
                            .addParam(ERROR, ExceptionUtils.getRootCauseMessage(ex));
                }
            case LIST_STRING: {
                return Arrays.asList(value.split(REG_EXP_VALUE_SEPARATOR));
            }
            case MAP_STRING: {
                if (!value.contains(":")) {
                    throw new SMPRuntimeException(CONFIGURATION_INVALID_MAP)
                            .addParam(PROPERTY_VALUE, value);
                }
                return Arrays.stream(value.split(REG_EXP_VALUE_SEPARATOR)).collect(Collectors.toMap(
                        val -> trim(substringBefore(val, REG_EXP_MAP_SEPARATOR)), val -> trim(substringAfter(val, REG_EXP_MAP_SEPARATOR))));
            }
            case PATH: {
                File file = new File(rootFolder, value);
                if (!file.exists() && !file.mkdirs()) {
                    throw new SMPRuntimeException(CONFIGURATION_INVALID_FOLDER)
                            .addParam(PROPERTY_VALUE, value);
                }
                if (!file.isDirectory()) {
                    throw new SMPRuntimeException(CONFIGURATION_INVALID_FOLDER_NOT_DIRECTORY)
                            .addParam(PROPERTY_VALUE, value);
                }
                return new File(value);
            }
            // nothing to validate
            case FILENAME:
                if (isValidFilename(value)) {
                    File file = new File(rootFolder, value);
                    if (!file.exists()) {
                        LOG.warn("File: [{}] does not exist. Full path: [{}].", value, file.getAbsolutePath());
                    }
                    return file;
                } else {
                    throw new SMPRuntimeException(CONFIGURATION_INVALID_FILENAME)
                            .addParam(PROPERTY_VALUE, value);
                }


            case EMAIL:
                String trimVal = value.trim();
                if (EmailValidator.getInstance().isValid(trimVal)) {
                    return trimVal;
                } else {
                    throw new SMPRuntimeException(CONFIGURATION_INVALID_EMAIL)
                            .addParam(PROPERTY_VALUE, value);
                }
            case URL:
                try {
                    return new URL(value.trim());
                } catch (MalformedURLException ex) {
                    throw new SMPRuntimeException(CONFIGURATION_INVALID_URL, ex)
                            .addParam(PROPERTY_VALUE, value)
                            .addParam(ERROR, ExceptionUtils.getRootCauseMessage(ex));
                }
            case STRING:
                return value;
            case CRON_EXPRESSION:
                try {
                    return CronExpression.parse(value);
                } catch (IllegalArgumentException ex) {
                    throw new SMPRuntimeException(CONFIGURATION_INVALID_CRON_EXPRESSION, ex)
                            .addParam(PROPERTY_VALUE, value)
                            .addParam(ERROR, ExceptionUtils.getRootCauseMessage(ex));
                }
        }
        return null;
    }

    /**
     * Parse the property value for the given type.
     *
     * @param type  - property type
     * @param value - property value
     * @return parsed value
     */
    private static List<?> parseEnumListPropertyType(SMPPropertyEnum type, List<String> value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        return switch (type) {
            case AUTOMATION_AUTHENTICATION_TYPES -> value.stream()
                    .map(SMPAutomationAuthenticationTypes::fromString)
                    .filter(Objects::nonNull)
                    .toList();
            case UI_AUTHENTICATION_TYPES -> value.stream()
                    .map(SMPUserAuthenticationTypes::fromString)
                    .filter(Objects::nonNull)
                    .toList();
            default -> value;
        };
    }

    /**
     * Return true for properties with sensitive data. For example the property value must not be logged
     * or returned via WS!
     *
     * @param property - value to validate if contains sensitive data
     * @return true if data is sensitive, else return false
     */
    public static boolean isSensitiveData(String property) {
        Optional<SMPPropertyEnum> propOpt = SMPPropertyEnum.getByProperty(trim(property));
        if (propOpt.isPresent()) {
            return propOpt.get().isEncrypted() || property.toLowerCase().contains(".password.decrypted");
        }
        LOG.debug("Database property [{}] is not recognized by the SMP. Basic mask rule applied for masking!", property);
        return Strings.CI.contains(property, "passw");
    }

    /**
     * Method returns 'masked' value for sensitive property data
     *
     * @param property - property name
     * @param value    - property value
     * @return masked value for sensitive properties. Else it returns value!
     */
    public static String getMaskedData(String property, String value) {
        return isSensitiveData(property) ? getMaskedData(value) : value;
    }

    public static String getMaskedData(String value) {
        return isNotBlank(value) ? MASKED_VALUE : "Null/Empty/Blank";
    }

    public static void printProperties(Properties properties, Level loggingLevel) {
        if (properties != null) {
            LOG.debug("------ Print properties ------");
            properties.forEach((key, value) -> printProperty((String) key, (String) value, loggingLevel));
        }
    }

    public static void printProperty(String key, String value, Level loggingLevel) {
        String logValue = "\t[" + key + "] --> [" + getMaskedData(key, value) + "]";
        switch (loggingLevel) {
            case TRACE:
                LOG.trace(logValue);
                break;
            case DEBUG:
                LOG.debug(logValue);
                break;
            case INFO:
                LOG.info(logValue);
                break;
            case WARN:
                LOG.warn(logValue);
                break;
            case ERROR:
                LOG.error(logValue);
                break;
            default:
                LOG.debug(logValue);
        }
    }

    public static boolean isValidFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        // Check for invalid characters (common for most OS)
        String invalidChars = "[\\\\/:*?\"<>|]";
        if (filename.matches(".*" + invalidChars + ".*")) {
            return false;
        }
        // Optionally, check if file can be created
        File file = new File(filename);
        return !file.isDirectory();
    }
}
