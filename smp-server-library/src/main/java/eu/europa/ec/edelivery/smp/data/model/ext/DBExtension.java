package eu.europa.ec.edelivery.smp.data.model.ext;

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import eu.europa.ec.edelivery.smp.data.model.BaseEntity;
import eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

/**
 * Database table containing registered extensions data/description
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Entity
@Audited
@Table(name = "SMP_EXTENSION",
        indexes = {@Index(name = "SMP_EXT_UNIQ_NAME_IDX", columnList = "IMPLEMENTATION_NAME", unique = true)
})
@NamedQueries({
        @NamedQuery(name = QUERY_EXTENSION_ALL, query = "SELECT d FROM DBExtension d"),
        @NamedQuery(name = QUERY_EXTENSION_IMPLEMENTATION_NAME, query = "SELECT d FROM DBExtension d WHERE d.implementationName = :name"),
})
@org.hibernate.annotations.Table(appliesTo = "SMP_EXTENSION", comment = "SMP extension definitions")
public class DBExtension extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_EXTENSION_SEQ")
    @GenericGenerator(name = "SMP_EXTENSION_SEQ", strategy = "native")
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique extension id")
    Long id;

    @Column(name = "VERSION", length = CommonColumnsLengths.MAX_TEXT_LENGTH_128 )
    private String version;

    @Column(name = "NAME", length = CommonColumnsLengths.MAX_TEXT_LENGTH_128 )
    private String name;

    @Column(name = "DESCRIPTION", length = CommonColumnsLengths.MAX_TEXT_LENGTH_512 )
    private String description;

    @Column(name = "IMPLEMENTATION_NAME", length = CommonColumnsLengths.MAX_TEXT_LENGTH_256 )
    private String implementationName;

    @Column(name = "EXTENSION_TYPE", length = CommonColumnsLengths.MAX_TEXT_LENGTH_256 )
    private String extensionType;

    @OneToMany(mappedBy = "extension", cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<DBResourceDef> resourceDefs = new ArrayList<>();


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImplementationName() {
        return implementationName;
    }

    public void setImplementationName(String implementationName) {
        this.implementationName = implementationName;
    }

    public String getExtensionType() {
        return extensionType;
    }

    public void setExtensionType(String extensionType) {
        this.extensionType = extensionType;
    }

    public List<DBResourceDef> getResourceDefs() {
        if (resourceDefs == null) {
            resourceDefs = new ArrayList<>();
        }
        return resourceDefs;
    }
}
