package eu.domibus.common.persistent;

import javax.persistence.*;

/**
 * TODO: Insert Description here
 *
 * @author muell16
 */
@Entity
@NamedQueries({@NamedQuery(name = "TempStore.findByGroup", query = "SELECT t FROM TempStore t WHERE t.group = :GROUP"),
               @NamedQuery(name = "TempStore.findByGroupAndArtifact",
                           query = "SELECT t FROM TempStore t WHERE t.group = :GROUP AND t.artifact = :ARTIFACT"),
               @NamedQuery(name = "TempStore.deleteAttachments",
                           query = "DELETE FROM TempStore t WHERE t.group = :GROUP")})

@Table(name = "TB_TEMP_STORE")
public class TempStore extends AbstractBaseEntity {

    @Column(name = "BINARY_DATA")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private byte[] bytes;

    @Column(name = "ATTACHMENT_SET")
    private String group;

    @Column(name = "ARTIFACT")
    private String artifact;

    public TempStore() {

    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getArtifact() {
        return this.artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }
}
