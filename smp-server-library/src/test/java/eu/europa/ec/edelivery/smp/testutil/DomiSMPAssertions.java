/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.testutil;

import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.SMPExceptionLanguageService;
import eu.europa.ec.smp.spi.exceptions.SMPException;
import eu.europa.ec.smp.spi.exceptions.TranslatedMessage;
import net.bytebuddy.implementation.bytecode.Throw;
import org.junit.jupiter.api.function.Executable;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesRegex;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Utility class for DomiSMP assertions.
 * Contains methods to assert conditions specific to DomiSMP.
 * <p>
 * This class is not meant to be instantiated.
 * </p>
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class DomiSMPAssertions {

    private DomiSMPAssertions() {
    }

    public static void assertDateEquals(OffsetDateTime expected, OffsetDateTime actual) {
        assertDateEquals(expected, actual, null);
    }

    public static void assertDateEquals(OffsetDateTime expected, OffsetDateTime actual, ChronoUnit precisionUnit) {
        if (expected == null && actual == null) {
            return; // both are null, so they are equal
        }
        if (expected == null || actual == null) {
            throw new AssertionError("One of the dates is null, expected: " + expected + ", actual: " + actual);
        }

        OffsetDateTime truncatedExpected = expected;
        OffsetDateTime truncatedActual = actual;
        if (precisionUnit != null) {
            truncatedExpected = expected.truncatedTo(precisionUnit);
            truncatedActual = actual.truncatedTo(precisionUnit);
        }
        if (!truncatedExpected.toInstant().equals(truncatedActual.toInstant())) {
            throw new AssertionError("Dates do not match. Expected: " + truncatedExpected + ", Actual: " + truncatedActual);
        }
    }

    public static void assertThrowsContainingMessages(Class<? extends Throwable> exceptionClass, Executable executable, String... messages) {
        Throwable exception = assertThrows(exceptionClass, executable);
        assertResultContainsExpected(exception.getMessage(), messages);
    }

    public static void assertThrowsContainingMessages(Class<? extends Throwable> exceptionClass, Executable executable, SMPExceptionLanguageService smpExceptionLanguageService, String... messages) {
        Throwable exception = assertThrows(exceptionClass, executable);
        if (!(exception instanceof TranslatedMessage)) {
            fail("Cannot translate exception message for unknown exception type: " + exception.getClass());
        }
        assertResultContainsExpected(smpExceptionLanguageService.getMessageTranslation(
                ((TranslatedMessage) exception).getMessageCode(), ((TranslatedMessage) exception).getMessageArgs()), messages);
    }

    public static void assertThrowsMatchingRegexExpressions(Class<? extends Throwable> exceptionClass, Executable executable, String... regexExpressions) {
        Throwable exception = assertThrows(exceptionClass, executable);
        assertResultMatchesExpected(exception.getMessage(), regexExpressions);
    }

    public static void assertThrowsMatchingRegexExpressions(Class<? extends Throwable> exceptionClass, Executable executable, SMPExceptionLanguageService smpExceptionLanguageService, String... regexExpressions) {
        Throwable exception = assertThrows(exceptionClass, executable);
        if (!(exception instanceof TranslatedMessage)) {
            fail("Cannot translate exception message for unknown exception type: " + exception.getClass());
        }
        assertResultMatchesExpected(smpExceptionLanguageService.getMessageTranslation(
                    ((TranslatedMessage) exception).getMessageCode(), ((TranslatedMessage) exception).getMessageArgs()), regexExpressions);
    }


    public static void assertResultContainsExpected(String result, String... expected) {
        Arrays.stream(expected).forEach(message -> assertThat(result, containsString(message)));
    }

    public static void assertResultMatchesExpected(String result, String... expected) {
        Arrays.stream(expected).forEach(message -> assertThat(result, matchesRegex(message)));
    }
}
