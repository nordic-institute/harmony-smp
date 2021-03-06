package eu.europa.ec.edelivery.smp.data.ui.enums;

public enum SMPPropertyTypeEnum {
    STRING ("Property [%s] is not valid String type!"),
    LIST_STRING("Property [%s] is not valid LIST_STRING type!"),
    INTEGER("Property [%s] is not valid Integer!"),
    BOOLEAN("Property [%s] is not valid Boolean type!"),
    REGEXP("Property [%s] is not valid Regular Expression type!n"),
    EMAIL("Property [%s] is not valid Email address type!"),
    FILENAME("Property [%s] is not valid Filename type or it does not exists!"),
    PATH("Property [%s] is not valid Path type or it does not exists!"),
    URL("Property [%s] is not valid URL type or it does not exists!"),
    ;

    String errorTemplate;

    SMPPropertyTypeEnum(String errorTemplate) {
        this.errorTemplate =errorTemplate;
    }

    public String getErrorMessage(String property) {
        return String.format(errorTemplate, property);
    }
}
