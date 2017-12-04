package eu.europa.ec.cipa.busdox.util;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.Calendar;

/*
 *
 *@author Flavio Santos
 *
 * */
public class CommonUtilTest {

    @Test
    public void testAddTimezoneIfNotPresent1() throws DatatypeConfigurationException {
        Calendar result = CommonUtil.addTimezoneIfNotPresent("2003-01-01T00:00:00-11:00");
        Assert.assertEquals("GMT-11:00", result.getTimeZone().getDisplayName());
    }

    @Test
    public void testAddTimezoneIfNotPresent2() throws DatatypeConfigurationException {
        Calendar result = CommonUtil.addTimezoneIfNotPresent("2003-01-01T00:00:00");
        Assert.assertEquals("GMT+00:00", result.getTimeZone().getDisplayName());
    }
}
