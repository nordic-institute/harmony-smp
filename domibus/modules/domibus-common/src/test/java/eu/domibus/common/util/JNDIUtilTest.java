package eu.domibus.common.util;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by feriaad on 01/12/2014.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JNDIUtil.class})
public class JNDIUtilTest {

    /**
     * Tests the method JNDIUtil.getBooleanEnvironmentParameter
     */
    @Test
    public void getBooleanEnvironmentParameterTest() {
        PowerMock.mockStaticPartial(JNDIUtil.class, "getEnvironmentParameter");
        EasyMock.expect(JNDIUtil.getEnvironmentParameter("returnTrue")).andReturn(Boolean.TRUE);
        EasyMock.expect(JNDIUtil.getEnvironmentParameter(null)).andReturn(Boolean.FALSE);
        EasyMock.expect(JNDIUtil.getEnvironmentParameter("returnFalse")).andReturn(Boolean.FALSE);
        EasyMock.expect(JNDIUtil.getEnvironmentParameter("")).andReturn(Boolean.FALSE);
        EasyMock.expect(JNDIUtil.getEnvironmentParameter("returnObject")).andReturn(new Object());
        EasyMock.expect(JNDIUtil.getEnvironmentParameter("returnInteger")).andReturn(123);
        EasyMock.expect(JNDIUtil.getEnvironmentParameter("returnTrueString")).andReturn("true");
        EasyMock.expect(JNDIUtil.getEnvironmentParameter("returnFalseString")).andReturn("false");
        EasyMock.expect(JNDIUtil.getEnvironmentParameter("returnFALSEString")).andReturn("FALSE");
        EasyMock.expect(JNDIUtil.getEnvironmentParameter("returnTRUEString")).andReturn("TRUE");
        PowerMock.replay(JNDIUtil.class);
        Assert.assertEquals(Boolean.TRUE, JNDIUtil.getBooleanEnvironmentParameter("returnTrue"));
        Assert.assertEquals(Boolean.FALSE, JNDIUtil.getBooleanEnvironmentParameter("returnFalse"));
        Assert.assertEquals(Boolean.FALSE, JNDIUtil.getBooleanEnvironmentParameter(null));
        Assert.assertEquals(Boolean.FALSE, JNDIUtil.getBooleanEnvironmentParameter(""));
        Assert.assertEquals(Boolean.FALSE, JNDIUtil.getBooleanEnvironmentParameter("returnObject"));
        Assert.assertEquals(Boolean.FALSE, JNDIUtil.getBooleanEnvironmentParameter("returnInteger"));
        Assert.assertEquals(Boolean.TRUE, JNDIUtil.getBooleanEnvironmentParameter("returnTrueString"));
        Assert.assertEquals(Boolean.FALSE, JNDIUtil.getBooleanEnvironmentParameter("returnFalseString"));
        Assert.assertEquals(Boolean.FALSE, JNDIUtil.getBooleanEnvironmentParameter("returnFALSEString"));
        Assert.assertEquals(Boolean.TRUE, JNDIUtil.getBooleanEnvironmentParameter("returnTRUEString"));
    }

}
