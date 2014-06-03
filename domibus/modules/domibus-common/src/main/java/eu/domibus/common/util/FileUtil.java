package eu.domibus.common.util;


import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.zip.GZIPOutputStream;

public class FileUtil {

    private static final Logger log = Logger.getLogger(FileUtil.class);

    private static MimetypesFileTypeMap mimeTypes = null;

    public static String mimeType(final String fileName) {
        if (fileName == null || fileName.trim().equals("")) {
            return null;
        }
        final int dot = fileName.lastIndexOf(".");
        if (dot < 0) {
            return null;
        }
        final String extension = fileName.substring(dot + 1);

        if (extension.equalsIgnoreCase("wmv")) {
            return "video/x-ms-wmv";
        }
        if (extension.equalsIgnoreCase("mp4")) {
            return "video/mp4";
        }
        if (extension.equalsIgnoreCase("mov")) {
            return "video/quicktime";
        }
        if (extension.equalsIgnoreCase("hqx")) {
            return "application/mac-binhex40";
        }
        if (extension.equalsIgnoreCase("cpt")) {
            return "application/mac-compactpro";
        }
        if (extension.equalsIgnoreCase("doc")) {
            return "application/msword";
        }
        if (extension.equalsIgnoreCase("pdf")) {
            return "application/pdf";
        }
        if (extension.equalsIgnoreCase("ai") ||
            extension.equalsIgnoreCase("eps") ||
            extension.equalsIgnoreCase("ps")) {
            return "application/postscript";
        }
        if (extension.equalsIgnoreCase("rtf")) {
            return "application/rtf";
        }
        if (extension.equalsIgnoreCase("bcpio")) {
            return "application/x-bcpio";
        }
        if (extension.equalsIgnoreCase("bz2")) {
            return "application/x-bzip2";
        }
        if (extension.equalsIgnoreCase("csh")) {
            return "application/x-csh";
        }
        if (extension.equalsIgnoreCase("gtar")) {
            return "application/x-gtar";
        }
        if (extension.equalsIgnoreCase("tgz")) {
            return "application/x-gzip";
        }
        if (extension.equalsIgnoreCase("gz") || extension.equalsIgnoreCase("gzip")) {
            return "application/gzip";
        }
        if (extension.equalsIgnoreCase("kwd") || extension.equalsIgnoreCase("kwt")) {
            return "application/x-kword";
        }
        if (extension.equalsIgnoreCase("ksp")) {
            return "application/x-kspread";
        }
        if (extension.equalsIgnoreCase("kpr") || extension.equalsIgnoreCase("kpt")) {
            return "application/x-kpresenter";
        }
        if (extension.equalsIgnoreCase("chrt")) {
            return "application/x-kchart";
        }
        if (extension.equalsIgnoreCase("latex")) {
            return "application/x-latex";
        }
        if (extension.equalsIgnoreCase("sh")) {
            return "application/x-sh";
        }
        if (extension.equalsIgnoreCase("shar")) {
            return "application/x-shar";
        }
        if (extension.equalsIgnoreCase("swf")) {
            return "application/x-shockwave-flash";
        }
        if (extension.equalsIgnoreCase("tar")) {
            return "application/x-tar";
        }
        if (extension.equalsIgnoreCase("tcl")) {
            return "application/x-tcl";
        }
        if (extension.equalsIgnoreCase("tex")) {
            return "application/x-tex";
        }
        if (extension.equalsIgnoreCase("texinfo") || extension.equalsIgnoreCase("texi")) {
            return "application/x-texinfo";
        }
        if (extension.equalsIgnoreCase("t") ||
            extension.equalsIgnoreCase("tr") ||
            extension.equalsIgnoreCase("roff")) {
            return "application/x-troff";
        }
        if (extension.equalsIgnoreCase("man")) {
            return "application/x-troff-man";
        }
        if (extension.equalsIgnoreCase("zip")) {
            return "application/zip";
        }
        if (extension.equalsIgnoreCase("mpga") ||
            extension.equalsIgnoreCase("mp2") ||
            extension.equalsIgnoreCase("mp3")) {
            return "audio/mpeg";
        }
        if (extension.equalsIgnoreCase("aif") ||
            extension.equalsIgnoreCase("aiff") ||
            extension.equalsIgnoreCase("aifc")) {
            return "audio/x-aiff";
        }
        if (extension.equalsIgnoreCase("wav")) {
            return "audio/x-wav";
        }
        if (extension.equalsIgnoreCase("gif")) {
            return "image/gif";
        }
        if (extension.equalsIgnoreCase("ief")) {
            return "image/ief";
        }
        if (extension.equalsIgnoreCase("jpeg") ||
            extension.equalsIgnoreCase("jpg") ||
            extension.equalsIgnoreCase("jpe")) {
            return "image/jpeg";
        }
        if (extension.equalsIgnoreCase("png")) {
            return "image/png";
        }
        if (extension.equalsIgnoreCase("tif") || extension.equalsIgnoreCase("tiff")) {
            return "image/tiff";
        }
        if (extension.equalsIgnoreCase("txt") || extension.equalsIgnoreCase("asc")) {
            return "text/plain";
        }
        if (extension.equalsIgnoreCase("rtf")) {
            return "text/rtf";
        }
        if (extension.equalsIgnoreCase("sgml") || extension.equalsIgnoreCase("sgm")) {
            return "text/sgml";
        }
        if (extension.equalsIgnoreCase("xml")) {
            return "text/xml";
        }
        if (extension.equalsIgnoreCase("mpeg") ||
            extension.equalsIgnoreCase("mpg") ||
            extension.equalsIgnoreCase("mpe")) {
            return "video/mpeg";
        }
        if (extension.equalsIgnoreCase("avi")) {
            return "video/x-msvideo";
        }
        if (extension.equalsIgnoreCase("html") || extension.equalsIgnoreCase("htm")) {
            return "text/html";
        }
        if (extension.equalsIgnoreCase("rtx")) {
            return "text/richtext";
        }
        if (extension.equalsIgnoreCase("sct")) {
            return "text/scriptlet";
        }
        if (extension.equalsIgnoreCase("tsv")) {
            return "text/tab-separated-values";
        }
        if (extension.equalsIgnoreCase("css")) {
            return "text/css";
        }
        if (extension.equalsIgnoreCase("pfx")) {
            return "application/x-pkcs12";
        }
        if (extension.equalsIgnoreCase("dll")) {
            return "application/x-msdownload";
        }
        if (extension.equalsIgnoreCase("js")) {
            return "application/x-javascript";
        }
        if (extension.equalsIgnoreCase("iii")) {
            return "application/x-iphone";
        }
        if (extension.equalsIgnoreCase("dvi")) {
            return "application/x-dvi";
        }
        if (extension.equalsIgnoreCase("xla")) {
            return "application/vnd.ms-excel";
        }
        if (extension.equalsIgnoreCase("bin")) {
            return "application/octet-stream";
        }
        if (extension.equalsIgnoreCase("fif")) {
            return "application/fractals";
        }
        if (extension.equalsIgnoreCase("ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (extension.equalsIgnoreCase("wps")) {
            return "application/vnd.ms-works";
        }
        if (extension.equalsIgnoreCase("mdb")) {
            return "application/x-msaccess";
        }
        if (extension.equalsIgnoreCase("pub")) {
            return "application/x-mspublisher";
        }
        if (extension.equalsIgnoreCase("der")) {
            return "application/x-x509-ca-cert";
        }
        if (extension.equalsIgnoreCase("ra")) {
            return "audio/x-pn-realaudio";
        }
        if (extension.equalsIgnoreCase("svg")) {
            return "image/svg+xml";
        }
        if (extension.equalsIgnoreCase("ico")) {
            return "image/x-icon";
        }

        return null;
    }

    public static MimetypesFileTypeMap getMimeTypes() {
        if (mimeTypes != null) {
            return mimeTypes;
        }
        mimeTypes = new MimetypesFileTypeMap();

        mimeTypes.addMimeTypes("image/png png");
        mimeTypes.addMimeTypes("video/mp4 mp4");
        mimeTypes.addMimeTypes("application/mac-binhex40 hqx");
        mimeTypes.addMimeTypes("application/mac-compactpro cpt");
        mimeTypes.addMimeTypes("application/msword doc");
        mimeTypes.addMimeTypes("application/pdf pdf");
        mimeTypes.addMimeTypes("application/postscript ai eps ps");
        mimeTypes.addMimeTypes("application/rtf rtf");
        mimeTypes.addMimeTypes("application/x-bcpio bcpio");
        mimeTypes.addMimeTypes("application/x-bzip2 bz2");
        mimeTypes.addMimeTypes("application/x-csh csh");
        mimeTypes.addMimeTypes("application/x-gtar gtar");
        mimeTypes.addMimeTypes("application/x-gzip tgz");
        mimeTypes.addMimeTypes("application/gzip gz");
        mimeTypes.addMimeTypes("application/x-kword kwd kwt");
        mimeTypes.addMimeTypes("application/x-kspread ksp");
        mimeTypes.addMimeTypes("application/x-kpresenter kpr kpt");
        mimeTypes.addMimeTypes("application/x-kchart chrt");
        mimeTypes.addMimeTypes("application/x-latex latex");
        mimeTypes.addMimeTypes("application/x-sh sh");
        mimeTypes.addMimeTypes("application/x-shar shar");
        mimeTypes.addMimeTypes("application/x-shockwave-flash swf");
        mimeTypes.addMimeTypes("application/x-tar tar");
        mimeTypes.addMimeTypes("application/x-tcl tcl");
        mimeTypes.addMimeTypes("video/quicktime mov");

        mimeTypes.addMimeTypes("text/richtext rtx");
        mimeTypes.addMimeTypes("text/scriptlet sct");
        mimeTypes.addMimeTypes("text/tab-separated-values tsv");
        mimeTypes.addMimeTypes("text/css css");
        mimeTypes.addMimeTypes("application/x-pkcs12 pfx");
        mimeTypes.addMimeTypes("application/x-msdownload dll");
        mimeTypes.addMimeTypes("application/x-javascript js");
        mimeTypes.addMimeTypes("application/x-iphone iii");
        mimeTypes.addMimeTypes("application/x-dvi dvi");
        mimeTypes.addMimeTypes("application/vnd.ms-excel xla");
        mimeTypes.addMimeTypes("application/octet-stream bin");
        mimeTypes.addMimeTypes("application/fractals fif");
        mimeTypes.addMimeTypes("application/vnd.ms-powerpoint ppt");
        mimeTypes.addMimeTypes("application/vnd.ms-works wps");
        mimeTypes.addMimeTypes("application/x-msaccess mdb");
        mimeTypes.addMimeTypes("application/x-mspublisher pub");
        mimeTypes.addMimeTypes("application/x-x509-ca-cert der");
        mimeTypes.addMimeTypes("audio/x-pn-realaudio ra");
        mimeTypes.addMimeTypes("image/svg+xml svg");
        mimeTypes.addMimeTypes("image/x-icon ico");
        mimeTypes.addMimeTypes("video/x-ms-wmv wmv");
        mimeTypes.addMimeTypes("video/x-ms-asf asf");
        mimeTypes.addMimeTypes("audio/mp3 mp3");

        return mimeTypes;
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
        if (mimeType == null || mimeType.trim().equals("")) {
            return null;
        }

        if (mimeType.equalsIgnoreCase("video/mp4")) {
            return "mp4";
        }
        if (mimeType.equalsIgnoreCase("audio/mp3")) {
            return "mp3";
        }
        if (mimeType.equalsIgnoreCase("audio/x-mp3")) {
            return "mp3";
        }
        if (mimeType.equalsIgnoreCase("audio/mpeg")) {
            return "mp3";
        }
        if (mimeType.equalsIgnoreCase("video/x-ms-asf")) {
            return "asf";
        }
        if (mimeType.equalsIgnoreCase("video/x-ms-wmv")) {
            return "wmv";
        }
        if (mimeType.equalsIgnoreCase("video/quicktime")) {
            return "mov";
        }
        if (mimeType.equalsIgnoreCase("application/mac-binhex40")) {
            return "hqx";
        }
        if (mimeType.equalsIgnoreCase("application/mac-compactpro")) {
            return "cpt";
        }
        if (mimeType.equalsIgnoreCase("application/msword")) {
            return "doc";
        }
        if (mimeType.equalsIgnoreCase("application/pdf")) {
            return "pdf";
        }
        if (mimeType.equalsIgnoreCase("application/postscript")) {
            return "ps";
        }
        if (mimeType.equalsIgnoreCase("application/rtf")) {
            return "rtf";
        }
        if (mimeType.equalsIgnoreCase("application/x-bcpio")) {
            return "bcpio";
        }
        if (mimeType.equalsIgnoreCase("application/x-bzip2")) {
            return "bz2";
        }
        if (mimeType.equalsIgnoreCase("application/x-csh")) {
            return "csh";
        }
        if (mimeType.equalsIgnoreCase("application/x-gtar")) {
            return "gtar";
        }
        if (mimeType.equalsIgnoreCase("application/x-gzip") || mimeType.equalsIgnoreCase("application/gzip")) {
            return "gz";
        }
        if (mimeType.equalsIgnoreCase("application/x-kword")) {
            return "kwd";
        }
        if (mimeType.equalsIgnoreCase("application/x-kspread")) {
            return "ksp";
        }
        if (mimeType.equalsIgnoreCase("application/x-kpresenter")) {
            return "kpr";
        }
        if (mimeType.equalsIgnoreCase("application/x-kchart")) {
            return "chrt";
        }
        if (mimeType.equalsIgnoreCase("application/x-latex")) {
            return "latex";
        }
        if (mimeType.equalsIgnoreCase("application/x-sh")) {
            return "sh";
        }
        if (mimeType.equalsIgnoreCase("application/x-shar")) {
            return "shar";
        }
        if (mimeType.equalsIgnoreCase("application/x-shockwave-flash")) {
            return "swf";
        }
        if (mimeType.equalsIgnoreCase("application/x-tar")) {
            return "tar";
        }
        if (mimeType.equalsIgnoreCase("application/x-tcl")) {
            return "tcl";
        }
        if (mimeType.equalsIgnoreCase("application/x-tex")) {
            return "tex";
        }
        if (mimeType.equalsIgnoreCase("application/x-texinfo")) {
            return "texi";
        }
        if (mimeType.equalsIgnoreCase("application/x-troff")) {
            return "roff";
        }
        if (mimeType.equalsIgnoreCase("application/x-troff-man")) {
            return "man";
        }
        if (mimeType.equalsIgnoreCase("application/zip")) {
            return "zip";
        }
        if (mimeType.equalsIgnoreCase("audio/x-aiff")) {
            return "aif";
        }
        if (mimeType.equalsIgnoreCase("audio/x-wav")) {
            return "wav";
        }
        if (mimeType.equalsIgnoreCase("image/gif")) {
            return "gif";
        }
        if (mimeType.equalsIgnoreCase("image/ief")) {
            return "ief";
        }
        if (mimeType.equalsIgnoreCase("image/jpeg")) {
            return "jpg";
        }
        if (mimeType.equalsIgnoreCase("image/png")) {
            return "png";
        }
        if (mimeType.equalsIgnoreCase("image/tiff")) {
            return "tif";
        }
        if (mimeType.equalsIgnoreCase("text/plain")) {
            return "txt";
        }
        if (mimeType.equalsIgnoreCase("text/rtf")) {
            return "rtf";
        }
        if (mimeType.equalsIgnoreCase("text/sgml")) {
            return "sgml";
        }
        if (mimeType.equalsIgnoreCase("text/xml")) {
            return "xml";
        }
        if (mimeType.equalsIgnoreCase("video/mpeg")) {
            return "mpeg";
        }
        if (mimeType.equalsIgnoreCase("video/x-msvideo")) {
            return "avi";
        }
        if (mimeType.equalsIgnoreCase("text/html")) {
            return "html";
        }
        if (mimeType.equalsIgnoreCase("text/richtext")) {
            return "rtx";
        }
        if (mimeType.equalsIgnoreCase("text/scriptlet")) {
            return "sct";
        }
        if (mimeType.equalsIgnoreCase("text/tab-separated-values")) {
            return "tsv";
        }
        if (mimeType.equalsIgnoreCase("text/css")) {
            return "css";
        }
        if (mimeType.equalsIgnoreCase("application/x-pkcs12")) {
            return "pfx";
        }
        if (mimeType.equalsIgnoreCase("application/x-msdownload")) {
            return "dll";
        }
        if (mimeType.equalsIgnoreCase("application/x-javascript")) {
            return "js";
        }
        if (mimeType.equalsIgnoreCase("application/x-iphone")) {
            return "iii";
        }
        if (mimeType.equalsIgnoreCase("application/x-dvi")) {
            return "dvi";
        }
        if (mimeType.equalsIgnoreCase("application/vnd.ms-excel")) {
            return "xla";
        }
        if (mimeType.equalsIgnoreCase("application/octet-stream")) {
            return "bin";
        }
        if (mimeType.equalsIgnoreCase("application/fractals")) {
            return "fif";
        }
        if (mimeType.equalsIgnoreCase("application/vnd.ms-powerpoint")) {
            return "ppt";
        }
        if (mimeType.equalsIgnoreCase("application/vnd.ms-works")) {
            return "wps";
        }
        if (mimeType.equalsIgnoreCase("application/x-msaccess")) {
            return "mdb";
        }
        if (mimeType.equalsIgnoreCase("application/x-mspublisher")) {
            return "pub";
        }
        if (mimeType.equalsIgnoreCase("application/x-x509-ca-cert")) {
            return "der";
        }
        if (mimeType.equalsIgnoreCase("audio/x-pn-realaudio")) {
            return "ra";
        }
        if (mimeType.equalsIgnoreCase("image/svg+xml")) {
            return "svg";
        }
        if (mimeType.equalsIgnoreCase("image/x-icon")) {
            return "ico";
        }

        return null;
    }

    public static void writeDataHandlerToFile(final DataHandler dataHandler, final File file) {
        if (dataHandler == null || file == null) {
            return;
        }
        try {
            file.getParentFile().mkdirs();
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            dataHandler.writeTo(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException ex) {
            log.error("Data could be written to file", ex);
        } catch (IOException e) {
            log.error("I/O exception during write process occured", e);
        }
    }

    // not tested yet...
    public static void writeToFile(final File file, final InputStream fileIs) {
        if (file == null || fileIs == null) {
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
                while (index < count && buf.hasRemaining()) {
                    buf.put(bytes[index++]);
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
        if (inFileName == null || inFileName.trim().equals("")) {
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
            log.error("I/O exception during write process occured", e);
        }
    }

}
