package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PropertyUtils {

    public static boolean isValidProperty(SMPPropertyEnum prop, String value) {
        if (StringUtils.isBlank(value)) {
            // empty/ null value is invalid
            return !prop.isMandatory();
        }
        SMPPropertyTypeEnum type = prop.getPropertyType();
        return isValidPropertyType(type, value);
    }

    public static boolean isValidPropertyType(SMPPropertyTypeEnum type, String value) {
        if (StringUtils.isEmpty(value)) {
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
            case FILENAME:
            case STRING:
                return true;
        }
        // property va

        return true;
    }
}
