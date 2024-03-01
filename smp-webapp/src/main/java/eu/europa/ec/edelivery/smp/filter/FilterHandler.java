/*-
 * #START_LICENSE#
 * smp-webapp
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
package eu.europa.ec.edelivery.smp.filter;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.net.URLDecoder;
import java.text.ParseException;
import java.util.*;

/**
 * A handler that parses filter values by decoding their tokens that have been URI-encoded in the front end.
 *
 * @author Sebastian-Ion TINCU
 * @since 5.1
 */
public class FilterHandler implements AnnotationFormatterFactory<Filter> {

    private static final Set<Class<?>> FIELD_TYPES;

    static {
        Set<Class<?>> fieldTypes = new HashSet<>();
        fieldTypes.add(String.class);
        FIELD_TYPES = Collections.unmodifiableSet(fieldTypes);
    }

    @Override
    public Set<Class<?>> getFieldTypes() {
        return FIELD_TYPES;
    }

    @Override
    public Printer<?> getPrinter(Filter annotation, Class<?> fieldType) {
        return getFormatter(annotation);
    }

    @Override
    public Parser<?> getParser(Filter annotation, Class<?> fieldType) {
        return getFormatter(annotation);
    }

    private Formatter<String> getFormatter(Filter annotation) {
        return new Formatter<String>() {
            @Override
            public String print(String object, Locale locale) {
                return object;
            }

            @Override
            public String parse(String encoded, Locale locale) throws ParseException {
                return URLDecoder.decode(encoded);
            }
        };
    }
}
