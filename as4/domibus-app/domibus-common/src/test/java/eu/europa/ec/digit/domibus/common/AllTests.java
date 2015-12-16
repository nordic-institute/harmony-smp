package eu.europa.ec.digit.domibus.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.europa.ec.digit.domibus.common.log.EventTest;

/**
 * Root TestSuite. This class marks all the individual common tests. Running this
 * suite causes all unit tests to be run.
 *
 * @author Vincent Dijkstra
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	EventTest.class
})
public class AllTests {

}

