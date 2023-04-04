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
