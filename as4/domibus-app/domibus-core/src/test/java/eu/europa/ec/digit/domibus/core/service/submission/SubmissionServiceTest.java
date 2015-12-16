package eu.europa.ec.digit.domibus.core.service.submission;

import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import eu.europa.ec.digit.domibus.core.AbstractCoreTest;
import eu.europa.ec.digit.domibus.core.service.message.MessageSubmissionService;

public class SubmissionServiceTest extends AbstractCoreTest {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */

	@Autowired
	private MessageSubmissionService messageSubmissionService = null;

	/* ---- Constructors ---- */

	/* ---- Test Methods ---- */

	/**
	 * If this test succeeds, the actual configuration of this module
	 * is correct.
	 */
	@Test
	public void testCoreServicesConfiguration() {
		assertTrue(Boolean.TRUE);
	}

	/* ---- Getters and Setters ---- */

	public MessageSubmissionService getMessageSubmissionService() {
		return messageSubmissionService;
	}

	public void setMessageSubmissionService(MessageSubmissionService messageSubmissionService) {
		this.messageSubmissionService = messageSubmissionService;
	}

}
