package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyTypeEnum;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.security.authentication.AuthenticationProvider;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.*;

public class PropertyUtils {


    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(PropertyUtils.class);
    private static final String REG_EXP_SEPARATOR="\\|";

    private static UrlValidator urlValidator =  new UrlValidator(new String[]{"http", "https"}, UrlValidator.ALLOW_LOCAL_URLS);


    public static Object parseProperty(SMPPropertyEnum prop, String value, File rootFolder) {
        if (StringUtils.isBlank(value)) {
            // empty/ null value is invalid
            if (prop.isMandatory()) {
                throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Empty mandatory property: " + prop.getProperty());
            }
            ;
            return null;
        }

        SMPPropertyTypeEnum type = prop.getPropertyType();
        return parsePropertyType(type, value, rootFolder);
    }

    public static boolean isValidProperty(SMPPropertyEnum prop, String value) {
        if (StringUtils.isBlank(value)) {
            // empty/ null value is invalid
            return !prop.isMandatory();
        }
        SMPPropertyTypeEnum type = prop.getPropertyType();
        return isValidPropertyType(type, value);
    }

    public static boolean isValidPropertyType(SMPPropertyTypeEnum type, String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }

        switch (type) {
            case BOOLEAN:
                return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
            case EMAIL:
                return EmailValidator.getInstance().isValid(value);
            case REGEXP:
                try {
                    Pattern.compile(value);
                    return true;
                } catch (PatternSyntaxException exception) {
                    return false;
                }
            case INTEGER:
                try {
                    Integer.parseInt(value);
                    return true;
                } catch (NumberFormatException exception) {
                    return false;
                }
            case PATH: {
                File f = new File(value);
                if (!f.exists()) {
                    LOG.warn("Folder {} not exists. Try to create the folder.", f.getAbsolutePath());
                    if (f.mkdirs()){
                        LOG.info("Folder {} created.", f.getAbsolutePath());
                    };
                }
                return f.exists() && f.isDirectory();
            }
            // nothing to validate
            case URL:
                return urlValidator.isValid(value);
            case LIST_STRING:
            case MAP_STRING:
            case FILENAME:
            case STRING:
                return true;
        }
        // property va

        return true;
    }

    public static Object parsePropertyType(SMPPropertyTypeEnum type, String value, File rootFolder) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        switch (type) {
            case BOOLEAN:
                return Boolean.valueOf(value.trim());
            case REGEXP:
                try {
                    return Pattern.compile(value);
                } catch (PatternSyntaxException exception) {
                    throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Invalid regular expression: " + value);
                }
            case INTEGER:
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException exception) {
                    throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Invalid integer: " + value);
                }
            case LIST_STRING: {
                return Arrays.asList(value.split(REG_EXP_SEPARATOR));
            }case MAP_STRING: {
                return Arrays.asList(value.split(REG_EXP_SEPARATOR)).stream().collect(Collectors.toMap(
                        val -> trim(substringBefore(val,":")), val-> trim(substringAfter(val,":"))));
            }
            case PATH: {
                return new File(value);
            }
            // nothing to validate
            case FILENAME:
                return new File(rootFolder, value);
            case EMAIL:
                String trimVal = value.trim();
                if (EmailValidator.getInstance().isValid(trimVal)) {
                    return trimVal;
                } else {
                    throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Invalid email address: " + value);
                }
            case URL:
                try {
                    return new URL(value.trim());
                } catch (MalformedURLException e) {
                    throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Invalid URL address: " + value);
                }
            case STRING:
                return value;
        }
        // property va

        return null;
    }
}
