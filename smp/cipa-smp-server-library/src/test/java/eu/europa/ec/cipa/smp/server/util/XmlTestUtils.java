package eu.europa.ec.cipa.smp.server.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gutowpa on 05/01/2017.
 */
public class XmlTestUtils {

    private static final String UTF_8 = "UTF-8";

    public static String loadDocumentAsString(String docResourcePath) throws IOException {
        InputStream inputStream = XmlTestUtils.class.getResourceAsStream(docResourcePath);

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(UTF_8);
    }
}
