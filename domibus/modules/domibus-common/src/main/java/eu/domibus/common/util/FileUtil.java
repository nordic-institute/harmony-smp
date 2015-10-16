package eu.domibus.common.util;


import eu.domibus.common.exceptions.EbMS3Exception;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileUtil {

    private static final Logger log = Logger.getLogger(FileUtil.class);

    private static MimetypesFileTypeMap mimeTypes;

    public static String mimeType(final String fileName) {
        if ((fileName == null) || "".equals(fileName.trim())) {
            return null;
        }
        final int dot = fileName.lastIndexOf(".");
        if (dot < 0) {
            return null;
        }
        final String extension = fileName.substring(dot + 1);

        if ("wmv".equalsIgnoreCase(extension)) {
            return "video/x-ms-wmv";
        }
        if ("mp4".equalsIgnoreCase(extension)) {
            return "video/mp4";
        }
        if ("mov".equalsIgnoreCase(extension)) {
            return "video/quicktime";
        }
        if ("hqx".equalsIgnoreCase(extension)) {
            return "application/mac-binhex40";
        }
        if ("cpt".equalsIgnoreCase(extension)) {
            return "application/mac-compactpro";
        }
        if ("doc".equalsIgnoreCase(extension)) {
            return "application/msword";
        }
        if ("pdf".equalsIgnoreCase(extension)) {
            return "application/pdf";
        }
        if ("ai".equalsIgnoreCase(extension) ||
            "eps".equalsIgnoreCase(extension) ||
            "ps".equalsIgnoreCase(extension)) {
            return "application/postscript";
        }
        if ("rtf".equalsIgnoreCase(extension)) {
            return "application/rtf";
        }
        if ("bcpio".equalsIgnoreCase(extension)) {
            return "application/x-bcpio";
        }
        if ("bz2".equalsIgnoreCase(extension)) {
            return "application/x-bzip2";
        }
        if ("csh".equalsIgnoreCase(extension)) {
            return "application/x-csh";
        }
        if ("gtar".equalsIgnoreCase(extension)) {
            return "application/x-gtar";
        }
        if ("tgz".equalsIgnoreCase(extension)) {
            return "application/x-gzip";
        }
        if ("gz".equalsIgnoreCase(extension) || "gzip".equalsIgnoreCase(extension)) {
            return "application/gzip";
        }
        if ("kwd".equalsIgnoreCase(extension) || "kwt".equalsIgnoreCase(extension)) {
            return "application/x-kword";
        }
        if ("ksp".equalsIgnoreCase(extension)) {
            return "application/x-kspread";
        }
        if ("kpr".equalsIgnoreCase(extension) || "kpt".equalsIgnoreCase(extension)) {
            return "application/x-kpresenter";
        }
        if ("chrt".equalsIgnoreCase(extension)) {
            return "application/x-kchart";
        }
        if ("latex".equalsIgnoreCase(extension)) {
            return "application/x-latex";
        }
        if ("sh".equalsIgnoreCase(extension)) {
            return "application/x-sh";
        }
        if ("shar".equalsIgnoreCase(extension)) {
            return "application/x-shar";
        }
        if ("swf".equalsIgnoreCase(extension)) {
            return "application/x-shockwave-flash";
        }
        if ("tar".equalsIgnoreCase(extension)) {
            return "application/x-tar";
        }
        if ("tcl".equalsIgnoreCase(extension)) {
            return "application/x-tcl";
        }
        if ("tex".equalsIgnoreCase(extension)) {
            return "application/x-tex";
        }
        if ("texinfo".equalsIgnoreCase(extension) || "texi".equalsIgnoreCase(extension)) {
            return "application/x-texinfo";
        }
        if ("t".equalsIgnoreCase(extension) ||
            "tr".equalsIgnoreCase(extension) ||
            "roff".equalsIgnoreCase(extension)) {
            return "application/x-troff";
        }
        if ("man".equalsIgnoreCase(extension)) {
            return "application/x-troff-man";
        }
        if ("zip".equalsIgnoreCase(extension)) {
            return "application/zip";
        }
        if ("mpga".equalsIgnoreCase(extension) ||
            "mp2".equalsIgnoreCase(extension) ||
            "mp3".equalsIgnoreCase(extension)) {
            return "audio/mpeg";
        }
        if ("aif".equalsIgnoreCase(extension) ||
            "aiff".equalsIgnoreCase(extension) ||
            "aifc".equalsIgnoreCase(extension)) {
            return "audio/x-aiff";
        }
        if ("wav".equalsIgnoreCase(extension)) {
            return "audio/x-wav";
        }
        if ("gif".equalsIgnoreCase(extension)) {
            return "image/gif";
        }
        if ("ief".equalsIgnoreCase(extension)) {
            return "image/ief";
        }
        if ("jpeg".equalsIgnoreCase(extension) ||
            "jpg".equalsIgnoreCase(extension) ||
            "jpe".equalsIgnoreCase(extension)) {
            return "image/jpeg";
        }
        if ("png".equalsIgnoreCase(extension)) {
            return "image/png";
        }
        if ("tif".equalsIgnoreCase(extension) || "tiff".equalsIgnoreCase(extension)) {
            return "image/tiff";
        }
        if ("txt".equalsIgnoreCase(extension) || "asc".equalsIgnoreCase(extension)) {
            return "text/plain";
        }
        if ("rtf".equalsIgnoreCase(extension)) {
            return "text/rtf";
        }
        if ("sgml".equalsIgnoreCase(extension) || "sgm".equalsIgnoreCase(extension)) {
            return "text/sgml";
        }
        if ("xml".equalsIgnoreCase(extension)) {
            return "text/xml";
        }
        if ("mpeg".equalsIgnoreCase(extension) ||
            "mpg".equalsIgnoreCase(extension) ||
            "mpe".equalsIgnoreCase(extension)) {
            return "video/mpeg";
        }
        if ("avi".equalsIgnoreCase(extension)) {
            return "video/x-msvideo";
        }
        if ("html".equalsIgnoreCase(extension) || "htm".equalsIgnoreCase(extension)) {
            return "text/html";
        }
        if ("rtx".equalsIgnoreCase(extension)) {
            return "text/richtext";
        }
        if ("sct".equalsIgnoreCase(extension)) {
            return "text/scriptlet";
        }
        if ("tsv".equalsIgnoreCase(extension)) {
            return "text/tab-separated-values";
        }
        if ("css".equalsIgnoreCase(extension)) {
            return "text/css";
        }
        if ("pfx".equalsIgnoreCase(extension)) {
            return "application/x-pkcs12";
        }
        if ("dll".equalsIgnoreCase(extension)) {
            return "application/x-msdownload";
        }
        if ("js".equalsIgnoreCase(extension)) {
            return "application/x-javascript";
        }
        if ("iii".equalsIgnoreCase(extension)) {
            return "application/x-iphone";
        }
        if ("dvi".equalsIgnoreCase(extension)) {
            return "application/x-dvi";
        }
        if ("xla".equalsIgnoreCase(extension)) {
            return "application/vnd.ms-excel";
        }
        if ("bin".equalsIgnoreCase(extension)) {
            return "application/octet-stream";
        }
        if ("fif".equalsIgnoreCase(extension)) {
            return "application/fractals";
        }
        if ("ppt".equalsIgnoreCase(extension)) {
            return "application/vnd.ms-powerpoint";
        }
        if ("wps".equalsIgnoreCase(extension)) {
            return "application/vnd.ms-works";
        }
        if ("mdb".equalsIgnoreCase(extension)) {
            return "application/x-msaccess";
        }
        if ("pub".equalsIgnoreCase(extension)) {
            return "application/x-mspublisher";
        }
        if ("der".equalsIgnoreCase(extension)) {
            return "application/x-x509-ca-cert";
        }
        if ("ra".equalsIgnoreCase(extension)) {
            return "audio/x-pn-realaudio";
        }
        if ("svg".equalsIgnoreCase(extension)) {
            return "image/svg+xml";
        }
        if ("ico".equalsIgnoreCase(extension)) {
            return "image/x-icon";
        }

        return null;
    }

    public static MimetypesFileTypeMap getMimeTypes() {
        if (FileUtil.mimeTypes != null) {
            return FileUtil.mimeTypes;
        }
        FileUtil.mimeTypes = new MimetypesFileTypeMap();

        FileUtil.mimeTypes.addMimeTypes("image/png png");
        FileUtil.mimeTypes.addMimeTypes("video/mp4 mp4");
        FileUtil.mimeTypes.addMimeTypes("application/mac-binhex40 hqx");
        FileUtil.mimeTypes.addMimeTypes("application/mac-compactpro cpt");
        FileUtil.mimeTypes.addMimeTypes("application/msword doc");
        FileUtil.mimeTypes.addMimeTypes("application/pdf pdf");
        FileUtil.mimeTypes.addMimeTypes("application/postscript ai eps ps");
        FileUtil.mimeTypes.addMimeTypes("application/rtf rtf");
        FileUtil.mimeTypes.addMimeTypes("application/x-bcpio bcpio");
        FileUtil.mimeTypes.addMimeTypes("application/x-bzip2 bz2");
        FileUtil.mimeTypes.addMimeTypes("application/x-csh csh");
        FileUtil.mimeTypes.addMimeTypes("application/x-gtar gtar");
        FileUtil.mimeTypes.addMimeTypes("application/x-gzip tgz");
        FileUtil.mimeTypes.addMimeTypes("application/gzip gz");
        FileUtil.mimeTypes.addMimeTypes("application/x-kword kwd kwt");
        FileUtil.mimeTypes.addMimeTypes("application/x-kspread ksp");
        FileUtil.mimeTypes.addMimeTypes("application/x-kpresenter kpr kpt");
        FileUtil.mimeTypes.addMimeTypes("application/x-kchart chrt");
        FileUtil.mimeTypes.addMimeTypes("application/x-latex latex");
        FileUtil.mimeTypes.addMimeTypes("application/x-sh sh");
        FileUtil.mimeTypes.addMimeTypes("application/x-shar shar");
        FileUtil.mimeTypes.addMimeTypes("application/x-shockwave-flash swf");
        FileUtil.mimeTypes.addMimeTypes("application/x-tar tar");
        FileUtil.mimeTypes.addMimeTypes("application/x-tcl tcl");
        FileUtil.mimeTypes.addMimeTypes("video/quicktime mov");

        FileUtil.mimeTypes.addMimeTypes("text/richtext rtx");
        FileUtil.mimeTypes.addMimeTypes("text/scriptlet sct");
        FileUtil.mimeTypes.addMimeTypes("text/tab-separated-values tsv");
        FileUtil.mimeTypes.addMimeTypes("text/css css");
        FileUtil.mimeTypes.addMimeTypes("application/x-pkcs12 pfx");
        FileUtil.mimeTypes.addMimeTypes("application/x-msdownload dll");
        FileUtil.mimeTypes.addMimeTypes("application/x-javascript js");
        FileUtil.mimeTypes.addMimeTypes("application/x-iphone iii");
        FileUtil.mimeTypes.addMimeTypes("application/x-dvi dvi");
        FileUtil.mimeTypes.addMimeTypes("application/vnd.ms-excel xla");
        FileUtil.mimeTypes.addMimeTypes("application/octet-stream bin");
        FileUtil.mimeTypes.addMimeTypes("application/fractals fif");
        FileUtil.mimeTypes.addMimeTypes("application/vnd.ms-powerpoint ppt");
        FileUtil.mimeTypes.addMimeTypes("application/vnd.ms-works wps");
        FileUtil.mimeTypes.addMimeTypes("application/x-msaccess mdb");
        FileUtil.mimeTypes.addMimeTypes("application/x-mspublisher pub");
        FileUtil.mimeTypes.addMimeTypes("application/x-x509-ca-cert der");
        FileUtil.mimeTypes.addMimeTypes("audio/x-pn-realaudio ra");
        FileUtil.mimeTypes.addMimeTypes("image/svg+xml svg");
        FileUtil.mimeTypes.addMimeTypes("image/x-icon ico");
        FileUtil.mimeTypes.addMimeTypes("video/x-ms-wmv wmv");
        FileUtil.mimeTypes.addMimeTypes("video/x-ms-asf asf");
        FileUtil.mimeTypes.addMimeTypes("audio/mp3 mp3");

        return FileUtil.mimeTypes;
    }

    public static FileTypeMap getMimeTypes(final String mimetype) {
        return new FileTypeMap() {

            @Override
            public String getContentType(final String filename) {
                return mimetype;
            }

            @Override
            public String getContentType(final File file) {
                return mimetype;
            }
        };
    }

    public static String getFileExtension(final String mimeType) {
        if ((mimeType == null) || "".equals(mimeType.trim())) {
            return null;
        }

        if ("video/mp4".equalsIgnoreCase(mimeType)) {
            return "mp4";
        }
        if ("audio/mp3".equalsIgnoreCase(mimeType)) {
            return "mp3";
        }
        if ("audio/x-mp3".equalsIgnoreCase(mimeType)) {
            return "mp3";
        }
        if ("audio/mpeg".equalsIgnoreCase(mimeType)) {
            return "mp3";
        }
        if ("video/x-ms-asf".equalsIgnoreCase(mimeType)) {
            return "asf";
        }
        if ("video/x-ms-wmv".equalsIgnoreCase(mimeType)) {
            return "wmv";
        }
        if ("video/quicktime".equalsIgnoreCase(mimeType)) {
            return "mov";
        }
        if ("application/mac-binhex40".equalsIgnoreCase(mimeType)) {
            return "hqx";
        }
        if ("application/mac-compactpro".equalsIgnoreCase(mimeType)) {
            return "cpt";
        }
        if ("application/msword".equalsIgnoreCase(mimeType)) {
            return "doc";
        }
        if ("application/pdf".equalsIgnoreCase(mimeType)) {
            return "pdf";
        }
        if ("application/postscript".equalsIgnoreCase(mimeType)) {
            return "ps";
        }
        if ("application/rtf".equalsIgnoreCase(mimeType)) {
            return "rtf";
        }
        if ("application/x-bcpio".equalsIgnoreCase(mimeType)) {
            return "bcpio";
        }
        if ("application/x-bzip2".equalsIgnoreCase(mimeType)) {
            return "bz2";
        }
        if ("application/x-csh".equalsIgnoreCase(mimeType)) {
            return "csh";
        }
        if ("application/x-gtar".equalsIgnoreCase(mimeType)) {
            return "gtar";
        }
        if ("application/x-gzip".equalsIgnoreCase(mimeType) || "application/gzip".equalsIgnoreCase(mimeType)) {
            return "gz";
        }
        if ("application/x-kword".equalsIgnoreCase(mimeType)) {
            return "kwd";
        }
        if ("application/x-kspread".equalsIgnoreCase(mimeType)) {
            return "ksp";
        }
        if ("application/x-kpresenter".equalsIgnoreCase(mimeType)) {
            return "kpr";
        }
        if ("application/x-kchart".equalsIgnoreCase(mimeType)) {
            return "chrt";
        }
        if ("application/x-latex".equalsIgnoreCase(mimeType)) {
            return "latex";
        }
        if ("application/x-sh".equalsIgnoreCase(mimeType)) {
            return "sh";
        }
        if ("application/x-shar".equalsIgnoreCase(mimeType)) {
            return "shar";
        }
        if ("application/x-shockwave-flash".equalsIgnoreCase(mimeType)) {
            return "swf";
        }
        if ("application/x-tar".equalsIgnoreCase(mimeType)) {
            return "tar";
        }
        if ("application/x-tcl".equalsIgnoreCase(mimeType)) {
            return "tcl";
        }
        if ("application/x-tex".equalsIgnoreCase(mimeType)) {
            return "tex";
        }
        if ("application/x-texinfo".equalsIgnoreCase(mimeType)) {
            return "texi";
        }
        if ("application/x-troff".equalsIgnoreCase(mimeType)) {
            return "roff";
        }
        if ("application/x-troff-man".equalsIgnoreCase(mimeType)) {
            return "man";
        }
        if ("application/zip".equalsIgnoreCase(mimeType)) {
            return "zip";
        }
        if ("audio/x-aiff".equalsIgnoreCase(mimeType)) {
            return "aif";
        }
        if ("audio/x-wav".equalsIgnoreCase(mimeType)) {
            return "wav";
        }
        if ("image/gif".equalsIgnoreCase(mimeType)) {
            return "gif";
        }
        if ("image/ief".equalsIgnoreCase(mimeType)) {
            return "ief";
        }
        if ("image/jpeg".equalsIgnoreCase(mimeType)) {
            return "jpg";
        }
        if ("image/png".equalsIgnoreCase(mimeType)) {
            return "png";
        }
        if ("image/tiff".equalsIgnoreCase(mimeType)) {
            return "tif";
        }
        if ("text/plain".equalsIgnoreCase(mimeType)) {
            return "txt";
        }
        if ("text/rtf".equalsIgnoreCase(mimeType)) {
            return "rtf";
        }
        if ("text/sgml".equalsIgnoreCase(mimeType)) {
            return "sgml";
        }
        if ("text/xml".equalsIgnoreCase(mimeType)) {
            return "xml";
        }
        if ("video/mpeg".equalsIgnoreCase(mimeType)) {
            return "mpeg";
        }
        if ("video/x-msvideo".equalsIgnoreCase(mimeType)) {
            return "avi";
        }
        if ("text/html".equalsIgnoreCase(mimeType)) {
            return "html";
        }
        if ("text/richtext".equalsIgnoreCase(mimeType)) {
            return "rtx";
        }
        if ("text/scriptlet".equalsIgnoreCase(mimeType)) {
            return "sct";
        }
        if ("text/tab-separated-values".equalsIgnoreCase(mimeType)) {
            return "tsv";
        }
        if ("text/css".equalsIgnoreCase(mimeType)) {
            return "css";
        }
        if ("application/x-pkcs12".equalsIgnoreCase(mimeType)) {
            return "pfx";
        }
        if ("application/x-msdownload".equalsIgnoreCase(mimeType)) {
            return "dll";
        }
        if ("application/x-javascript".equalsIgnoreCase(mimeType)) {
            return "js";
        }
        if ("application/x-iphone".equalsIgnoreCase(mimeType)) {
            return "iii";
        }
        if ("application/x-dvi".equalsIgnoreCase(mimeType)) {
            return "dvi";
        }
        if ("application/vnd.ms-excel".equalsIgnoreCase(mimeType)) {
            return "xla";
        }
        if ("application/octet-stream".equalsIgnoreCase(mimeType)) {
            return "bin";
        }
        if ("application/fractals".equalsIgnoreCase(mimeType)) {
            return "fif";
        }
        if ("application/vnd.ms-powerpoint".equalsIgnoreCase(mimeType)) {
            return "ppt";
        }
        if ("application/vnd.ms-works".equalsIgnoreCase(mimeType)) {
            return "wps";
        }
        if ("application/x-msaccess".equalsIgnoreCase(mimeType)) {
            return "mdb";
        }
        if ("application/x-mspublisher".equalsIgnoreCase(mimeType)) {
            return "pub";
        }
        if ("application/x-x509-ca-cert".equalsIgnoreCase(mimeType)) {
            return "der";
        }
        if ("audio/x-pn-realaudio".equalsIgnoreCase(mimeType)) {
            return "ra";
        }
        if ("image/svg+xml".equalsIgnoreCase(mimeType)) {
            return "svg";
        }
        if ("image/x-icon".equalsIgnoreCase(mimeType)) {
            return "ico";
        }

        return null;
    }

    public static void writeDataHandlerToFile(final DataHandler dataHandler, final File file) {
        if ((dataHandler == null) || (file == null)) {
            return;
        }
        try {
            file.getParentFile().mkdirs();
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            dataHandler.writeTo(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException ex) {
            FileUtil.log.error("Data could be written to file", ex);
        } catch (IOException e) {
            FileUtil.log.error("I/O exception during write process occured", e);
        }
    }

    // not tested yet...
    public static void writeToFile(final File file, final InputStream fileIs) {
        if ((file == null) || (fileIs == null)) {
            return;
        }
        file.getParentFile().mkdirs();
        try {
            final WritableByteChannel channel = new FileOutputStream(file).getChannel();
            final int size = 1024 * 1024;
            final ByteBuffer buf = ByteBuffer.allocateDirect(size);
            final byte[] bytes = new byte[size];
            int count = 0;
            int index = 0;
            while (count >= 0) {
                if (index == count) {
                    count = fileIs.read(bytes);
                    index = 0;
                }
                while ((index < count) && buf.hasRemaining()) {
                    index++;
                    buf.put(bytes[index]);

                }
                buf.flip();
                //int numWritten =
                channel.write(buf);
                if (buf.hasRemaining()) {
                    buf.compact();
                } else {
                    buf.clear();
                }
            }
            channel.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void doCompressFile(final String inFileName) {
        if ((inFileName == null) || "".equals(inFileName.trim())) {
            return;
        }
        try {
            final File file = new File(inFileName);
            //System.out.println(" you are going to gzip the  : " + file + "file");
            final FileOutputStream fos = new FileOutputStream(file + ".gz");
            //System.out.println(" Now the name of this gzip file is  : " + file + ".gz" );
            final GZIPOutputStream gzos = new GZIPOutputStream(fos);
            //System.out.println(" opening the input stream");
            final FileInputStream fin = new FileInputStream(file);
            final BufferedInputStream in = new BufferedInputStream(fin);
            //System.out.println("Transferring file from" + inFileName + " to " + file + ".gz");
            final byte[] buffer = new byte[1024];
            int i;
            while ((i = in.read(buffer)) >= 0) {
                gzos.write(buffer, 0, i);
            }
            //System.out.println(" file is in now gzip format");
            in.close();
            gzos.close();
        } catch (IOException e) {
            FileUtil.log.error("I/O exception during write process occured", e);
        }
    }

    /**
     * Same as {@link eu.domibus.common.util.FileUtil#doCompress(String, String)} but with only one parameter (sourceFileName).
     * Creates a compressed file with prefix .gz in the same directory with the same filename as the source.
     *
     * @param sourceFileName
     * @throws IOException
     */
    public static void doCompress(final String sourceFileName) throws IOException {
        doCompress(sourceFileName, sourceFileName + ".gz");
    }


    /**
     * Compresses a given file with GZIP [RFC1952] and creates a compressed
     * file with prefix .gz at a specified location
     *
     * @param sourceFileName
     * @param targetFileName
     * @throws IOException
     */
    public static void doCompress(final String sourceFileName, final String targetFileName) throws IOException {

        if (sourceFileName == null || "".equals(sourceFileName)) {
            log.error("Given filename of the source file was null or empty");
            throw new NullPointerException("Given filename of the source file was null or empty");
        }

        if (targetFileName == null || "".equals(targetFileName)) {
            log.error("Given filename of the target file was null or empty");
            throw new NullPointerException("Given filename of the target file was null or empty");
        }

        try {
            final File sourceFileObject = new File(sourceFileName);
            log.debug("Inputfile: " + sourceFileName);

            final FileInputStream fin = new FileInputStream(sourceFileObject);
            final BufferedInputStream sourceStream = new BufferedInputStream(fin);
            log.debug("Creating BufferedInputStream for " + sourceFileName);


            final File targetFileObject = new File(targetFileName);
            log.debug("Outputfile: " + targetFileName);

            final FileOutputStream fos = new FileOutputStream(targetFileObject);
            log.debug("Creating FileOutputStream for: " + targetFileName);

            final GZIPOutputStream targetStream = new GZIPOutputStream(fos);
            log.debug("Creating GZIP OutputStream");


            doCompress(sourceStream, targetStream);

        } catch (FileNotFoundException e) {
            log.error("File not found or no premission to write", e);
            throw e;
        } catch (IOException e) {
            log.error("Error during compression");
            throw e;
        }
    }

    /**
     * Compress given Stream via GZIP [RFC1952].
     *
     * @param sourceStream Stream of uncompressed data
     * @param targetStream Stream of compressed data
     * @throws IOException
     */
    public static void doCompress(final InputStream sourceStream, final GZIPOutputStream targetStream)
            throws IOException {

        final byte[] buffer = new byte[1024];

        try {
            int i;
            while ((i = sourceStream.read(buffer)) > 0) {
                targetStream.write(buffer, 0, i);
            }

            sourceStream.close();

            targetStream.finish();
            targetStream.close();

            log.debug("doCompress finished");

        } catch (IOException e) {
            log.error("I/O exception during gzip compression. method: doCompress(Inputstream, GZIPOutputStream)", e);
            throw e;
        }
    }

    /**
     * Decompress given file
     *
     * @param sourceFileName name of the file that needs to be decompressed
     * @param targetFileName name of the resulting decompressed file
     * @throws IOException
     */
    public static void doDecompress(String sourceFileName, String targetFileName) throws IOException {

        if (sourceFileName == null || "".equals(sourceFileName)) {
            log.error("Given filename of the source file was null or empty");
            throw new NullPointerException("Given filename of the source file was null or empty");
        }

        if (targetFileName == null || "".equals(targetFileName)) {
            log.error("Given filename of the target file was null or empty");
            throw new NullPointerException("Given filename of the target file was null or empty");
        }

        try {
            final File sourceFileObject = new File(sourceFileName);
            log.debug("Inputfile: " + sourceFileName);

            final FileInputStream fin = new FileInputStream(sourceFileObject);
            final GZIPInputStream sourceStream = new GZIPInputStream(fin);
            log.debug("Creating GZIP InputStream for " + sourceFileName);


            final File targetFileObject = new File(targetFileName);
            log.debug("Outputfile: " + targetFileName);

            final FileOutputStream targetStream = new FileOutputStream(targetFileObject);
            log.debug("Creating FileOutputStream for: " + targetFileName);


            doDecompress(sourceStream, targetStream);

        } catch (FileNotFoundException e) {
            log.error("File not found or no premission to write", e);
            throw e;
        } catch (IOException e) {
            log.error("Error during compression");
            throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0303);
        }
    }

    /**
     * Decompress given GZIP Stream. Separated from {@link eu.domibus.common.util.FileUtil#doCompress(java.io.InputStream, java.util.zip.GZIPOutputStream)}
     * just for a better overview even though they share the same logic (except finish() ).
     *
     * @param sourceStream
     * @param targetStream
     * @throws IOException
     */
    public static void doDecompress(final GZIPInputStream sourceStream, final OutputStream targetStream)
            throws IOException {

        final byte[] buffer = new byte[1024];

        try {
            int i;
            while ((i = sourceStream.read(buffer)) > 0) {
                targetStream.write(buffer, 0, i);
            }

            sourceStream.close();
            targetStream.close();

        } catch (IOException e) {
            log.error("I/O exception during gzip compression. method: doDecompress(GZIPInputStream, OutputStream");
            throw e;
        }
    }

    /**
     * Compresses the given byte[]. If an error occures a {@link eu.domibus.common.exceptions.EbMS3Exception} is thrown
     *
     * @param uncompressed the byte[] to compress
     * @return the compressed byte[]
     * @throws EbMS3Exception if an error occures a {@link eu.domibus.common.exceptions.EbMS3Exception}
     *          with {@link eu.domibus.common.exceptions.EbMS3Exception.EbMS3ErrorCode#EBMS_0303} is thrown
     */
    public static byte[] doCompress(byte[] uncompressed) throws EbMS3Exception {
        if(uncompressed == null) {
            throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0303);
        }

        ByteArrayOutputStream compressedContent = new ByteArrayOutputStream();

        try {
            doCompress(new ByteArrayInputStream(uncompressed), new GZIPOutputStream(compressedContent));
        } catch (IOException e) {
            FileUtil.log.error(e);
            throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0303, e.getMessage());
        }

        return compressedContent.toByteArray();
    }


    public static byte[] doDecompress(byte[] compressed) throws EbMS3Exception {
        if(compressed == null) {
            throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0303);
        }

        ByteArrayInputStream compressedContent = new ByteArrayInputStream(compressed);
        ByteArrayOutputStream decompressedContent = new ByteArrayOutputStream();

        try {
            doDecompress(new GZIPInputStream(compressedContent), decompressedContent);
        } catch (IOException e) {
            FileUtil.log.error(e);
            throw new EbMS3Exception(EbMS3Exception.EbMS3ErrorCode.EBMS_0303);
        }

        return decompressedContent.toByteArray();
    }
}
