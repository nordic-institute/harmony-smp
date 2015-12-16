package eu.europa.ec.digit.domibus.base.dao.i18n;

import static org.junit.Assert.*;
import java.util.Locale;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import eu.europa.ec.digit.domibus.base.dao.AbstractDAOTest;

public class MessageSourceTest extends AbstractDAOTest {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */

	@Autowired
	private MessageSource messageSource = null;


	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */

	@Test
	public void testBusinessExceptionMessage() {
		String message = messageSource.getMessage(
			"message.domibus.program.error.ebms.003",
			null,
			Locale.ENGLISH);
		assertNotNull(message);
	}

	/* ---- Getters and Setters ---- */

	public MessageSource getMessageSource() {
		return messageSource;
	}


	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
