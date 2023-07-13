package eu.europa.ec.edelivery.smp.testutil;

import java.time.OffsetDateTime;

import static org.junit.Assert.assertEquals;

public class SMPAssert {

    private SMPAssert() {
    }

    public static void assertEqualDates(OffsetDateTime expected, OffsetDateTime actual) {
        if (expected == actual) {
            return;
        }
        // if one of them is null, but not both check in previous if
        if (expected == null || actual == null) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
        // compare instant
        assertEquals(expected.toInstant(), actual.toInstant());
    }
}
