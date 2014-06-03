package eu.domibus.common.persistent;


import eu.domibus.common.util.FileUtil;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.File;
import java.io.Serializable;


/**
 * @author Hamid Ben Malek
 */
@Entity
@Table(name = "TB_ATTACHMENTS")
public class Attachment extends AbstractBaseEntity implements Serializable {
    private static final long serialVersionUID = 9118287153028725539L;

    @Column(name = "CONTENT_TYPE")
    private String contentType;

    @Column(name = "CONTENT_ID")
    private String contentID;

    //@Column(name = "Encoding")
    //private String transferEncoding;

    @Column(name = "FILE_PATH")
    private String filePath;

    public Attachment() {
    }

    public Attachment(final String file) {
        this.filePath = file;
        final String mimeType = FileUtil.mimeType(file);
        if (mimeType != null) {
            setContentType(mimeType);
        }
    }

    public Attachment(final String file, final String cid) {
        this(file);
        if (cid != null && !cid.trim().equals("")) {
            setContentID(cid);
        }
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public String getContentID() {
        return contentID;
    }

    public void setContentID(final String contentID) {
        this.contentID = contentID;
    }
  /*
  public String getTransferEncoding() { return transferEncoding; }

  public void setTransferEncoding(String transferEncoding)
  {
    this.transferEncoding = transferEncoding;
  } */

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    public DataHandler getDataHandler(final String storageFolder) {
        if (filePath == null || filePath.trim().equals("")) {
            return null;
        }
        final File f = new File(filePath);
        if (f.exists()) {
            final FileDataSource fileDataSource = new FileDataSource(f);
            fileDataSource.setFileTypeMap(FileUtil.getMimeTypes());
            return new DataHandler(fileDataSource);
        } else if (storageFolder != null && !storageFolder.trim().equals("")) {
            final String path = storageFolder + File.separator +
                                f.getParentFile().getName() + File.separator +
                                f.getName();
            final File file = new File(path);
            if (file.exists()) {
                final FileDataSource fileDataSource = new FileDataSource(file);
                fileDataSource.setFileTypeMap(FileUtil.getMimeTypes());
                return new DataHandler(fileDataSource);
            }
        }
        return null;
    }
}