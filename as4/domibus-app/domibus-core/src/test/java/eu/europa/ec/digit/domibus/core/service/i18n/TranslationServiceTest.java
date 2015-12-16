package eu.europa.ec.digit.domibus.core.service.i18n;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import eu.europa.ec.digit.domibus.core.AbstractCoreTest;

public class TranslationServiceTest extends AbstractCoreTest {

	/* ---- Constants ---- */

	public final static String MESSAGE_CODE = "message.code.sample.001";
	public final static String MESSAGE = "Eureka, it is working";
	public final static String[] MESSAGE_PARAMETER = { "parameter" };

	/* ---- Instance Variables ---- */

	@Autowired
	private TranslationService translationService = null;

	@Autowired
	private MessageSource messageSource = null;

	/* ---- Constructors ---- */

	/* ---- Test Methods ---- */

	@Before
	public void setUp() {

	}

	@Test
	public void testTranslationService() {
		// Configure EasyMock
		expect(messageSource.getMessage(MESSAGE_CODE, MESSAGE_PARAMETER, Locale.ENGLISH)).andReturn(MESSAGE);

	    // Activate EasyMock
	    replay(messageSource);

		String translation = translationService.translate(MESSAGE_CODE, Locale.ENGLISH, MESSAGE_PARAMETER);

		assertNotNull(translation);
		assertEquals(MESSAGE, translation);

		// Check EasyMock usage
		verify(messageSource);
	}

	/* ---- Getters and Setters ---- */

	public TranslationService getTranslationService() {
		return translationService;
	}

	public void setTranslationService(TranslationService translationService) {
		this.translationService = translationService;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
