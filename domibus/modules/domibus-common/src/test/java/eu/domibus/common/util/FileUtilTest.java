package eu.domibus.common.util;

import eu.domibus.common.exceptions.EbMS3Exception;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.*;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.junit.Assert.*;

/**
 * TODO: Insert Description here
 *
 * @author muell16
 */
public class FileUtilTest {

    private static int LOW = 1000;
    private static int HIGH = 9999;
    private static String GZIP_PREFIX = ".gz";

    private static final String TESTDIR_COMPRESSION = "compression";

    private static final String TESTDIR_TESTFILES = TESTDIR_COMPRESSION + "/testfiles";
    private static final String TESTDIR_EXPECTED = TESTDIR_COMPRESSION + "/expected";

    private static final String TESTDIR_COMPRESSED = "/compressed";
    private static final String TESTDIR_UNCOMPRESSED = "/uncompressed";

    private static final String TESTFILE_527KB = "/file_527kb.jar";
    private static final String TESTFILE_527KB_compressed = "/file_527kb.jar.gz";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Test
    public void testDoCompress_file_positive_500kb() throws Exception {

        URL url = Thread.currentThread().getContextClassLoader()
                        .getResource(TESTDIR_TESTFILES + TESTDIR_UNCOMPRESSED + TESTFILE_527KB);

        assertNotNull("URL is null: ", url);


        String resultFileName = randomResultFileNameGZIP(url);

        try {
            FileUtil.doCompress(url.getFile(), resultFileName);
        } catch (FileNotFoundException fnfe) {
            fail("File not found: " + fnfe.getMessage());
        }

        File resultFile = new File(resultFileName);

        assertTrue(isGZipped(new FileInputStream(resultFile)));
    }

    @Test
    public void testDoCompress_byte_source_byte_result_positive() throws IOException {
        URL url = Thread.currentThread().getContextClassLoader()
                        .getResource(TESTDIR_TESTFILES + TESTDIR_UNCOMPRESSED + TESTFILE_527KB);

        assertNotNull("URL is null: ", url);

        byte[] uncompressed = FileUtils.readFileToByteArray(new File(url.getFile()));

        byte[] compressed = FileUtil.doCompress(uncompressed);

        assertTrue(isGZipped(new ByteArrayInputStream(compressed)));
    }

    @Test
    public void testDoCompress_byte_source_null_negative() throws IOException {
        expectedException.expect(EbMS3Exception.class);
        expectedException.expectMessage(EbMS3Exception.EbMS3ErrorCode.EBMS_0303.getShortDescription());

        byte[] arr = null;

        FileUtil.doCompress(arr);
    }

    @Test
    public void testDoDecompress() throws IOException {
        URL url = Thread.currentThread().getContextClassLoader()
                        .getResource(TESTDIR_TESTFILES + TESTDIR_COMPRESSED + TESTFILE_527KB_compressed);

        assertNotNull("URL is null: ", url);

        byte[] compressed = FileUtils.readFileToByteArray(new File(url.getFile()));

        assertTrue("testresource is not zipped", isGZipped(new ByteArrayInputStream(compressed)));

        byte[] uncompressed = FileUtil.doDecompress(compressed);

        assertFalse("file is still zipped after deflation", isGZipped(new ByteArrayInputStream(uncompressed)));
    }


    private static boolean isGZipped(InputStream in) {
        if (!in.markSupported()) {
            in = new BufferedInputStream(in);
        }
        in.mark(2);
        int magic = 0;
        try {
            magic = in.read() & 0xff | ((in.read() << 8) & 0xff00);
            in.reset();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return false;
        }
        return magic == GZIPInputStream.GZIP_MAGIC;
    }

    private static String randomResultFileNameGZIP(URL sourceFile) {
        String result = sourceFile.getFile();
        int randomFileNamePart = (int) (Math.random() * (HIGH - LOW) + LOW);

        return result + "." + randomFileNamePart + GZIP_PREFIX;
    }
}
