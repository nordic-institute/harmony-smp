/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.identifiers;

import eu.europa.ec.dynamicdiscovery.model.identifiers.AbstractIdentifierFormatter;
import eu.europa.ec.dynamicdiscovery.model.identifiers.types.AbstractFormatterType;
import eu.europa.ec.dynamicdiscovery.model.identifiers.types.FormatterType;

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

        AbstractFormatterType defaultFormatter;

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

        public void setDefaultFormatter(AbstractFormatterType defaultFormatter) {
            this.defaultFormatter = defaultFormatter;
        }

        public IdentifierFormatter build(){
            IdentifierFormatter identifierFormatter = new IdentifierFormatter();
            identifierFormatter.setSchemeMandatory(schemeMandatory);
            identifierFormatter.setCaseSensitiveSchemas(caseSensitiveSchemas);
            identifierFormatter.setSchemeValidationPattern(schemeValidationPattern);
            identifierFormatter.addFormatterTypes(formatterTypes);
            if (defaultFormatter != null) {
                identifierFormatter.setDefaultFormatter(defaultFormatter);
            }
            return identifierFormatter;
        }
    }
}
