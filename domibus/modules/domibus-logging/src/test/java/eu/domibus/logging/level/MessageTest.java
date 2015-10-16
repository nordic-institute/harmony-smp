package eu.domibus.logging.level;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The class <code>MessageTest</code> contains tests for the class <code>{@link Message}</code>.
 *
 * @author cheny01
 * @version $Revision: 1.0 $
 * @generatedBy CodePro at 05.10.12 13:49
 */
public class MessageTest {
    private static final int FALLBACK_INT = Level.INFO_INT;
    private static final int FALLBACK_SYSLOG_EQUIVALENT = 6;
    private static final String FALLBACK_LEVEL = "INFO";

    private static final int MESSAGE_INT = FALLBACK_INT + 10;
    private static final int MESSAGE_SYSLOG_EQUIVALENT = 6;


    /**
     * Run the Message(int,String,int) constructor test.
     *
     * @throws Exception
     * @generatedBy CodePro at 05.10.12 13:49
     */
    @Test
    public void testMessage_1() throws Exception {
        final int level = 1;
        final String levelStr = "MESSAGE";
        final int syslogEquivalent = 1;

        final Message result = new Message(level, levelStr, syslogEquivalent);

        assertNotNull(result);
        assertEquals("MESSAGE", result.toString());
        assertEquals(1, result.toInt());
        assertEquals(1, result.getSyslogEquivalent());
    }

    /**
     * Run the Level toLevel(int) method test.
     *
     * @throws Exception
     * @generatedBy CodePro at 05.10.12 13:49
     */
    @Test
    public void testToLevel_1() throws Exception {
        final int value = MESSAGE_INT;

        final Level result = Message.toLevel(value);

        assertNotNull(result);
        assertEquals("MESSAGE", result.toString());
        assertEquals(value, result.toInt());
        assertEquals(MESSAGE_SYSLOG_EQUIVALENT, result.getSyslogEquivalent());
    }

    /**
     * Run the Level toLevel(int) method test.
     *
     * @throws Exception
     * @generatedBy CodePro at 05.10.12 13:49
     */
    @Test
    public void testToLevel_2() throws Exception {
        final int value = 1;

        final Level result = Message.toLevel(value);

        assertNotNull(result);
        assertEquals(FALLBACK_LEVEL, result.toString());
        assertEquals(FALLBACK_INT, result.toInt());
        assertEquals(FALLBACK_SYSLOG_EQUIVALENT, result.getSyslogEquivalent());
    }

    /**
     * Run the Level toLevel(String) method test.
     *
     * @throws Exception
     * @generatedBy CodePro at 05.10.12 13:49
     */
    @Test
    public void testToLevel_3() throws Exception {
        final String level = "x";

        final Level result = Message.toLevel(level);
        assertNotNull(result);
        assertEquals(FALLBACK_LEVEL, result.toString());
        assertEquals(FALLBACK_INT, result.toInt());
        assertEquals(FALLBACK_SYSLOG_EQUIVALENT, result.getSyslogEquivalent());
    }

    /**
     * Run the Level toLevel(String) method test.
     *
     * @throws Exception
     * @generatedBy CodePro at 05.10.12 13:49
     */
    @Test
    public void testToLevel_4() throws Exception {
        final String level = null;

        final Level result = Message.toLevel(level);

        assertNotNull(result);
        assertEquals(FALLBACK_LEVEL, result.toString());
        assertEquals(FALLBACK_INT, result.toInt());
        assertEquals(FALLBACK_SYSLOG_EQUIVALENT, result.getSyslogEquivalent());
    }

    /**
     * Run the Level toLevel(String) method test.
     *
     * @throws Exception
     * @generatedBy CodePro at 05.10.12 13:49
     */
    @Test
    public void testToLevel_5() throws Exception {
        final String level = "MESSAGe";
        final Level result = Message.toLevel(level);

        assertNotNull(result);
        assertEquals("MESSAGE", result.toString());
        assertEquals(MESSAGE_INT, result.toInt());
        assertEquals(MESSAGE_SYSLOG_EQUIVALENT, result.getSyslogEquivalent());
    }

    /**
     * Run the Level toLevel(int,Level) method test.
     *
     * @throws Exception
     * @generatedBy CodePro at 05.10.12 13:49
     */
    @Test
    public void testToLevel_6() throws Exception {
        final int value = MESSAGE_INT;
        final Level defaultLevel = Level.toLevel(1);

        final Level result = Message.toLevel(value, defaultLevel);

        assertNotNull(result);
        assertEquals("MESSAGE", result.toString());
        assertEquals(MESSAGE_INT, result.toInt());
        assertEquals(MESSAGE_SYSLOG_EQUIVALENT, result.getSyslogEquivalent());
    }

    /**
     * Run the Level toLevel(int,Level) method test.
     *
     * @throws Exception
     * @generatedBy CodePro at 05.10.12 13:49
     */
    @Test
    public void testToLevel_7() throws Exception {
        final int value = FALLBACK_INT;
        final Level defaultLevel = Level.toLevel(FALLBACK_INT);

        final Level result = Message.toLevel(value, defaultLevel);


        assertNotNull(result);
        assertEquals(FALLBACK_LEVEL, result.toString());
        assertEquals(FALLBACK_INT, result.toInt());
        assertEquals(FALLBACK_SYSLOG_EQUIVALENT, result.getSyslogEquivalent());
    }

    /**
     * Run the Level toLevel(String,Level) method test.
     *
     * @throws Exception
     * @generatedBy CodePro at 05.10.12 13:49
     */
    @Test
    public void testToLevel_8() throws Exception {
        final String level = "MESSAGe";
        final Level defaultLevel = Level.toLevel(FALLBACK_INT);

        final Level result = Message.toLevel(level, defaultLevel);

        assertNotNull(result);
        assertEquals("MESSAGE", result.toString());
        assertEquals(MESSAGE_INT, result.toInt());
        assertEquals(FALLBACK_SYSLOG_EQUIVALENT, result.getSyslogEquivalent());
    }

    /**
     * Run the Level toLevel(String,Level) method test.
     *
     * @throws Exception
     * @generatedBy CodePro at 05.10.12 13:49
     */
    @Test
    public void testToLevel_9() throws Exception {
        final String level = null;
        final Level defaultLevel = Level.toLevel(FALLBACK_INT);

        final Level result = Message.toLevel(level, defaultLevel);

        assertNotNull(result);
        assertEquals(FALLBACK_LEVEL, result.toString());
        assertEquals(FALLBACK_INT, result.toInt());
        assertEquals(FALLBACK_SYSLOG_EQUIVALENT, result.getSyslogEquivalent());
    }

    /**
     * Run the Level toLevel(String,Level) method test.
     *
     * @throws Exception
     * @generatedBy CodePro at 05.10.12 13:49
     */
    @Test
    public void testToLevel_10() throws Exception {
        final String level = "";
        final Level defaultLevel = Level.toLevel(FALLBACK_INT);

        final Level result = Message.toLevel(level, defaultLevel);

        assertNotNull(result);
        assertEquals(FALLBACK_LEVEL, result.toString());
        assertEquals(FALLBACK_INT, result.toInt());
        assertEquals(FALLBACK_SYSLOG_EQUIVALENT, result.getSyslogEquivalent());
    }

    /**
     * Perform pre-test initialization.
     *
     * @throws Exception if the initialization fails for some reason
     * @generatedBy CodePro at 05.10.12 13:49
     */
    @Before
    public void setUp() throws Exception {

    }

    /**
     * Perform post-test clean-up.
     *
     * @throws Exception if the clean-up fails for some reason
     * @generatedBy CodePro at 05.10.12 13:49
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Launch the test.
     *
     * @param args the command line arguments
     * @generatedBy CodePro at 05.10.12 13:49
     */
    public static void main(final String[] args) {
        new org.junit.runner.JUnitCore().run(MessageTest.class);
    }
}