package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyTypeEnum;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PropertyUtils {

    private  static UrlValidator urlValidator =  new UrlValidator(new String[]{"http", "https"}, UrlValidator.ALLOW_LOCAL_URLS);


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
                return f.exists() && f.isDirectory();
            }
            // nothing to validate
            case URL:
                return urlValidator.isValid(value);
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
