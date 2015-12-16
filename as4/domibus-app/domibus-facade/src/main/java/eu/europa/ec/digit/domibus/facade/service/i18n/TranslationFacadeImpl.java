package eu.europa.ec.digit.domibus.facade.service.i18n;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europa.ec.digit.domibus.core.service.i18n.TranslationService;

@Service
public class TranslationFacadeImpl implements TranslationFacade {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */

	@Autowired
	private TranslationService translationService = null;

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */

	@Override
	public String translate(String code, Locale locale, Object... params) {
		return translationService.translate(code, locale, params);
	}

	/* ---- Getters and Setters ---- */

	public TranslationService getTranslationService() {
		return translationService;
	}

	public void setTranslationService(TranslationService translationService) {
		this.translationService = translationService;
	}

}
