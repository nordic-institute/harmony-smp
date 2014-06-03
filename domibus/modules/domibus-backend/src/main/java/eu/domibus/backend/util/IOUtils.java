/*
 * 
 */
package eu.domibus.backend.util;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * The Class IOUtils.
 */
public class IOUtils extends org.apache.commons.io.IOUtils {
    /**
     * Creates the temp dir.
     *
     * @return the file
     */
    public static File createTempDir() {
        final File baseDir = new File(System.getProperty("java.io.tmpdir"));
        final String baseName = UUID.randomUUID().toString();
        final File tempDir = new File(baseDir, baseName);
        tempDir.mkdirs();
        return tempDir;

    }

    /**
     * To byte array.
     *
     * @param dataHandler the data handler
     * @return the byte[]
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static byte[] toByteArray(final DataHandler dataHandler) throws IOException {
        final InputStream in = dataHandler.getInputStream();

        return toByteArray(in);
    }

    /**
     * Removes the directory.
     *
     * @param directory the directory
     * @return true, if successful
     */
    public static boolean removeDirectory(final File directory) {
        if (directory == null) {
            return false;
        }
        if (!directory.exists()) {
            return false;
        }
        if (!directory.isDirectory()) {
            return false;
        }
        final String[] list = directory.list();
        // Some JVMs return null for File.list() when the
        // directory is empty.
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                final File entry = new File(directory, list[i]);
                if (entry.isDirectory()) {
                    if (!removeDirectory(entry)) {
                        return false;
                    }
                } else {
                    if (!entry.delete()) {
                        return false;
                    }
                }
            }
        }
        return directory.delete();
    }

    public static long getDataSize(final DataHandler dh) {
        long dataSize = -1L;
        try {
            final DataSource ds = dh.getDataSource();
            dataSize = 0;
            final InputStream in = ds.getInputStream();
            final byte[] readbuf = new byte[64 * 1024];
            int bytesread;
            do {
                bytesread = in.read(readbuf);
                if (bytesread > 0) {
                    dataSize += bytesread;
                }
            } while (bytesread > -1);
            if (in.markSupported()) {
                in.reset();
            } else {
                in.close();
            }
        } catch (Exception e) {
        }
        return dataSize;
    }

    public static long getDataSize(final InputStream in) {
        long dataSize = -1L;
        try {
            final byte[] readbuf = new byte[64 * 1024];
            int bytesread;
            do {
                bytesread = in.read(readbuf);
                if (bytesread > 0) {
                    dataSize += bytesread;
                }
            } while (bytesread > -1);

            if (in.markSupported()) {
                in.reset();
            } else {
                in.close();
            }
        } catch (Exception e) {
        }
        return dataSize;
    }
}
