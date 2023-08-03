package eu.europa.ec.edelivery.smp.identifiers;

import eu.europa.ec.edelivery.smp.identifiers.types.FormatterType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Formatter for the IdentifierFormatter with default null split regular expression and
 * '::' as split separator. For details see the {@link AbstractIdentifierFormatter}
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class IdentifierFormatter extends AbstractIdentifierFormatter<Identifier> {


    @Override
    protected String getSchemeFromObject(Identifier object) {
        return object != null ? object.getScheme() : null;
    }

    @Override
    protected String getIdentifierFromObject(Identifier object) {
        return object != null ? object.getValue() : null;
    }

    @Override
    protected Identifier createObject(String scheme, String identifier) {
        Identifier identifierObject = new Identifier();
        identifierObject.setScheme(scheme);
        identifierObject.setValue(identifier);
        return identifierObject;
    }

    @Override
    protected void updateObject(Identifier identifierObject, String scheme, String identifier) {
        identifierObject.setScheme(scheme);
        identifierObject.setValue(identifier);
    }

    public static class Builder{
        public static Builder create(){
            return new Builder();
        }

        private Builder() {
        }

        boolean schemeMandatory = false;
        Pattern schemeValidationPattern;
        List<String> caseSensitiveSchemas;
        FormatterType[] formatterTypes = null;

        FormatterType defaultFormatter;

        public Builder schemeMandatory(boolean schemeMandatory) {
            this.schemeMandatory = schemeMandatory;
            return this;
        }

        public Builder setSchemeValidationPattern(Pattern schemeValidationPattern) {
            this.schemeValidationPattern = schemeValidationPattern;
            return this;
        }

        public Builder addCaseSensitiveSchemas(String ... caseSensitiveSchemas) {
            if (this.caseSensitiveSchemas == null) {
                this.caseSensitiveSchemas = new ArrayList<>();
            }
            this.caseSensitiveSchemas.addAll(Arrays.asList(caseSensitiveSchemas));
            return this;
        }

        public Builder addFormatterTypes(FormatterType ... formatterTypes) {
            this.formatterTypes = formatterTypes;
            return this;
        }

        public void setDefaultFormatter(FormatterType defaultFormatter) {
            this.defaultFormatter = defaultFormatter;
        }

        public IdentifierFormatter build(){
            IdentifierFormatter identifierFormatter = new IdentifierFormatter();
            identifierFormatter.setSchemeMandatory(schemeMandatory);
            identifierFormatter.setCaseSensitiveSchemas(caseSensitiveSchemas);
            identifierFormatter.setSchemeValidationPattern(schemeValidationPattern);
            identifierFormatter.addFormatterTypes(formatterTypes);
            identifierFormatter.setDefaultFormatter(defaultFormatter);
            return identifierFormatter;
        }
    }
}
