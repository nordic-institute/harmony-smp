package eu.europa.ec.cipa.bdmsl.dao.entity;

import javax.persistence.*;

/**
 * Created by feriaad on 15/06/2015.
 */
@Entity
@Table(name = "BDMSL_CONFIGURATION")
public class ConfigurationEntity {
    @Id
    @Column(name = "property")
    private String property;

    @Basic
    @Column(name = "value")
    private String value;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigurationEntity)) return false;

        ConfigurationEntity that = (ConfigurationEntity) o;

        if (property != null ? !property.equals(that.property) : that.property != null) return false;
        return !(value != null ? !value.equals(that.value) : that.value != null);

    }

    @Override
    public int hashCode() {
        int result = property != null ? property.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
