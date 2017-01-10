package eu.europa.ec.digit.domibus.core.service.i18n;

import java.util.Locale;

/**
 * @author Vincent Dijkstra
 */
public interface TranslationService {

	/**
	 * Translates give error code into a localised message with injected parameters.
	 *
	 * @param code error code
	 * @param locale of the message
	 * @param params parameters to inject in message
	 * @return translated message.
	 */
	public String translate(String code, Locale locale, Object...params);

}