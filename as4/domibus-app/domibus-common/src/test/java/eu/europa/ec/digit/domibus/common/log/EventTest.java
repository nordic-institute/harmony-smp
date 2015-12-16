package eu.europa.ec.digit.domibus.common.log;

import static org.junit.Assert.*;
import static eu.europa.ec.digit.domibus.common.log.LogEvent.*;
import org.junit.Test;


public class EventTest {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */

	@Test
	public void eventTest() {
		String message = BUS_SEND_MESSAGE_REQUEST.format();
		assertEquals(BUS_SEND_MESSAGE_REQUEST.getMessage(), message);

		message = BUS_NOTIFY_MESSAGE_FAILED.format("eureka");
		assertEquals("Notifying the message failed, eureka", message);

		message = BUS_CONVERSION_FAILED.format("one", "two", "three");
		assertEquals("Conversion of one to two failed due to: three", message);
	}



	/* ---- Getters and Setters ---- */

}
