package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractBaseEntity;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "Property")
@Entity
@Table(name = "TB_PROPERTY")
public class Property extends AbstractBaseEntity {

    @Attribute
    @Column(name = "NAME")
    protected String name;
    @Text

    @Column(name = "VALUE")
    protected String value;

    public Property() {
    }

    public Property(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String v) {
        this.value = v;
    }
}