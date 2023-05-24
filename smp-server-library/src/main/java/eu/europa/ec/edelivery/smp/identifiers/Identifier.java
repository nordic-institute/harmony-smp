package eu.europa.ec.edelivery.smp.identifiers;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Identifier {
    String value;
    String scheme;

    public Identifier() {
    }

    public Identifier(String value, String scheme) {
        this.value = value;
        this.scheme = scheme;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String sheme) {
        this.scheme = sheme;
    }

    @Override
    public String toString() {
        return "Identifier{" +
                "value='" + value + '\'' +
                ", scheme='" + scheme + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Identifier that = (Identifier) o;

        return new EqualsBuilder().append(value, that.value).append(scheme, that.scheme).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(value).append(scheme).toHashCode();
    }
}
