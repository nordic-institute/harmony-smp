package eu.europa.ec.edelivery.smp.data.ui.enums;

public enum SMPPropertyTypeEnum {
    STRING (".*","Property [%s] is not valid String type!"),
    LIST_STRING(".*","Property [%s] is not valid LIST_STRING type!"),
    MAP_STRING(".*","Property [%s] is not valid MAP_STRING type!"),
    INTEGER("\\d*","Property [%s] is not valid Integer!"),
    BOOLEAN("true|false","Property [%s] is not valid Boolean type!"),
    REGEXP(".*", "Property [%s] is not valid Regular Expression type!"),
    CRON_EXPRESSION(".*","Property [%s] is not valid Cron Expression type!"),
    EMAIL(".*","Property [%s] is not valid Email address type!"),
    FILENAME(".*","Property [%s] is not valid Filename type or it does not exists!"),
    PATH(".*","Property [%s] is not valid Path type or it does not exists!"),
    URL(".*","Property [%s] is not valid URL type or it does not exists!"),
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
