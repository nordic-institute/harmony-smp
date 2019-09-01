package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyTypeEnum;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyTypeEnum.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class PropertyUtilsTest {

    private static final Object[] testValues() {
        return new Object[][]{
                {STRING, "this is a string", true},
                {INTEGER, "1345", true},
                {INTEGER, " 1e345", false},
                {BOOLEAN, "true", true},
                {BOOLEAN, "false", true},
                {BOOLEAN, "fALse", true},
                {BOOLEAN, "fale ", false},
                {REGEXP, ".*", true},
                {REGEXP, ".*(**]", false},
                {EMAIL, "test@mail.com", true},
                {EMAIL, "test@2222.comsfs", false},
                {EMAIL, "test@coms.fs", false},
                {FILENAME, "myfilename.txt", true},
                {PATH, "./", true},
                {PATH, "./notexits-tst", false},
        };
    }

    @Test
    @Parameters(method = "testValues")
    public void testIsValidPropertyType(SMPPropertyTypeEnum propertyType, String value, boolean expected) {
        //when
        boolean result = PropertyUtils.isValidPropertyType(propertyType, value);

        //then
        assertEquals(expected, result);
    }


    @Test
    public void testDefaultValues() {

        for (SMPPropertyEnum prop : SMPPropertyEnum.values()) {
            assertTrue("Invalid: " + prop.getProperty() + " - " + prop.getDesc(), PropertyUtils.isValidProperty(prop, prop.getDefValue()));
        }
    }
}