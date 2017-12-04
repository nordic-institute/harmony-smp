package eu.europa.ec.cipa.busdox.util;

import org.junit.Assert;
import org.junit.Test;

/*
 *
 *@author Flavio Santos
 *
 * */
public class CommonUtilTest {

    @Test
    public void testGetTimezoneFromDate() {
        String result = CommonUtil.getTimezoneFromDate("2003-01-01T00:00:00-11:00");
        Assert.assertEquals("2003-01-01T00:00:00-11:00", result);
    }

    @Test
    public void testTimezoneFromDate() {
        String result = CommonUtil.getTimezoneFromDate("2003-01-01T00:00:00");
        Assert.assertEquals("2003-01-01T00:00:00Z", result);
    }

    @Test
    public void testTimezoneFromDateZ() {
        String result = CommonUtil.getTimezoneFromDate("2003-01-01T00:00:00Z");
        Assert.assertEquals("2003-01-01T00:00:00Z", result);
    }

    @Test
    public void testTimezoneFromDateWithWrongFormat() {
        String result = CommonUtil.getTimezoneFromDate("wrong format");
        Assert.assertEquals("wrong format", result);
    }
}
