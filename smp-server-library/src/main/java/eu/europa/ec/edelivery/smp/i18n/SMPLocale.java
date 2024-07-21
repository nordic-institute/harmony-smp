package eu.europa.ec.edelivery.smp.i18n;

import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;

public enum SMPLocale {

    EN_US ("en", "English"),

    RO_RO ("ro", "Romanian");

    private final String code;
    private final String language;

    SMPLocale(String code, String language) {
        this.code = code;
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public String getLanguage() {
        return language;
    }

    public static SMPLocale fromCodeDefaultingToEnglish(String code) {
        return EnumSet.allOf(SMPLocale.class).stream()
                .filter(locale -> StringUtils.equalsIgnoreCase(locale.getCode(), code))
                .findAny()
                .orElse(EN_US);
    }
}
