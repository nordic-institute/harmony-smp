package eu.europa.ec.digit.domibus.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.europa.ec.digit.domibus.core.service.i18n.TranslationServiceTest;

/**
 * Root TestSuite. This class marks all the individual common tests. Running this
 * suite causes all unit tests to be run.
 *
 * @author Vincent Dijkstra
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	TranslationServiceTest.class
})
public class AllTests {

}

