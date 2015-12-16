package eu.europa.ec.digit.domibus.core.service.i18n;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class TranslationServiceImpl implements TranslationService {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */

	@Autowired
	private MessageSource messageSource = null;

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */

	@Override
	public String translate(String code, Locale locale, Object... params) {
		return messageSource.getMessage(code, params, locale);
	}

	/* ---- Getters and Setters ---- */

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
