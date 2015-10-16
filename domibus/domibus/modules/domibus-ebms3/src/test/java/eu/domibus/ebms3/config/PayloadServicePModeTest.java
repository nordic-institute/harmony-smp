package eu.domibus.ebms3.config;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;

import java.io.File;
import java.net.URL;
import static org.junit.Assert.*;

/**
 * This test reads a pmode file with new PayloadService element and checks whether it is mapped correctly into
 * an java entity or not.
 *
 * @author muell16
 */
public class PayloadServicePModeTest {

    private static final String SERVICE = "COMPRESSION";
    private static final String ACTION_APPLICATION_GZIP = "APPLICATION_GZIP";
    private static final String ACTION_EMPTY = "EMPTY";

    private static final String COMPRESSIONTYPE_ENABLED = "application/gzip";
    private static final String COMPRESSIONTYPE_DISABLED = "";

    @Rule
    public ExpectedException correspondingPModeNotFoundException = ExpectedException.none();

    /**
     * Positive test with pmode with enabled compression
     *
     * <ol>
     *  <li>Read file PMode_compression_enabled_correct.xml from test resources and map it into a java representation via simpleframework.</li>
     *  <li>Check if the correct pmode was loaded</li>
     *  <li>Check if PayloadService and CompressionType element is mapped</li>
     *  <li>Check if content of PayloadService.CompressionType equals application/gzip</li>
     * </ol>
     */
    @Test
    public void testCompressionEnabledPositive() throws Exception {
        URL testPMode_url = Thread.currentThread().getContextClassLoader()
                                   .getResource("compression/test_pmodes/PMode_compression_enabled_correct.xml");

        assertNotNull(testPMode_url);

        File testPMode_file = new File(testPMode_url.getFile());

        final Serializer serializer = new Persister(new AnnotationStrategy());
        PModePool pool = serializer.read(PModePool.class, testPMode_file);

        assertNotNull(pool);
        assertTrue(pool.getUserServices().size() == 1);
        assertEquals(pool.getUserServices().get(0).getCollaborationInfo().getService().getValue(), SERVICE);
        assertEquals(pool.getUserServices().get(0).getCollaborationInfo().getAction(), ACTION_APPLICATION_GZIP);

        PayloadService actual = pool.getBinding("COMPRESSION_APPLICATION_GZIP").getMep().getLegByNumber(1).getPayloadService();
        assertNotNull(actual);
        assertEquals(actual.getCompressionType(), COMPRESSIONTYPE_ENABLED);


    }

    /**
     * Positive test with pmode with compression disabled via empty CompressionType element
     *
     * <ol>
     *  <li>Read file PMode_compression_disabled_empty_correct.xml from test resources and map it into a java representation via simpleframework.</li>
     *  <li>Check if the correct pmode was loaded</li>
     *  <li>Check if PayloadService and CompressionType element is mapped</li>
     *  <li>Check if content of PayloadService.CompressionType empty</li>
     * </ol>
     */
    @Test
    public void testCompressionDisabledEmptyPositive() throws Exception {
        URL testPMode_url = Thread.currentThread().getContextClassLoader()
                                   .getResource("compression/test_pmodes/PMode_compression_disabled_empty_correct.xml");

        assertNotNull(testPMode_url);

        File testPMode_file = new File(testPMode_url.getFile());

        final Serializer serializer = new Persister(new AnnotationStrategy());
        PModePool pool = serializer.read(PModePool.class, testPMode_file);

        assertNotNull(pool);
        assertTrue(pool.getUserServices().size() == 1);
        assertEquals(pool.getUserServices().get(0).getCollaborationInfo().getService().getValue(), SERVICE);
        assertEquals(pool.getUserServices().get(0).getCollaborationInfo().getAction(), ACTION_EMPTY);

        PayloadService actual = pool.getBinding("COMPRESSION_EMPTY").getMep().getLegByNumber(1).getPayloadService();
        assertNotNull(actual);
        assertEquals(actual.getCompressionType(), COMPRESSIONTYPE_DISABLED);
    }
}
