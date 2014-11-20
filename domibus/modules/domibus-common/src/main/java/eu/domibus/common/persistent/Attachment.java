package eu.domibus.common.persistent;


import eu.domibus.common.util.FileUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
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
            this.setContentType(mimeType);
        }
    }

    public Attachment(final String file, final String cid) {
        this(file);
        if ((cid != null) && !"".equals(cid.trim())) {
            this.setContentID(cid);
        }
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public String getContentID() {
        return this.contentID;
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
        return this.filePath;
    }

    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

}