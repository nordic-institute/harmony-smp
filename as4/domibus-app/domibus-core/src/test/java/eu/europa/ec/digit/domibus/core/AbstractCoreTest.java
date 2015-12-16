package eu.europa.ec.digit.domibus.core;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import eu.europa.ec.digit.domibus.core.config.DomibusCoreTestConfiguration;


@RunWith (SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    loader = AnnotationConfigContextLoader.class,
    classes = {
    	DomibusCoreTestConfiguration.class
    }
)
@ActiveProfiles ("testing")
public abstract class AbstractCoreTest extends AbstractJUnit4SpringContextTests {

    /* ---- Constants ---- */

    /* ---- Instance Variables ---- */

    /* ---- Constructors ---- */

    /* ---- Business Methods ---- */

}
