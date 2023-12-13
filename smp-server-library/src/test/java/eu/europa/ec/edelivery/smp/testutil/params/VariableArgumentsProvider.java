/*-
 * #%L
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
 * #L%
 */
package eu.europa.ec.edelivery.smp.testutil.params;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public class VariableArgumentsProvider
        implements ArgumentsProvider, AnnotationConsumer<VariableSource> {

    private String variableName;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return context.getTestClass()
                .map(this::getField)
                .map(this::getValue)
                .orElseThrow(() ->
                        new IllegalArgumentException("Failed to load test arguments"));
    }

    @Override
    public void accept(VariableSource variableSource) {
        variableName = variableSource.value();
    }

    private Field getField(Class<?> clazz) {
        try {
            return clazz.getDeclaredField(variableName);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Stream<Arguments> getValue(Field field) {
        Object value = null;
        try {
            value = field.get(null);
        } catch (Exception ignored) {}

        return value == null ? null : (Stream<Arguments>) value;
    }
}
