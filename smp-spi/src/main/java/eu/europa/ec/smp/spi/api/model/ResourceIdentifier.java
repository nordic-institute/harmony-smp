package eu.europa.ec.smp.spi.api.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *  The resource identifier entity for resource and subresource identifier .
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class ResourceIdentifier {
    String value;
    String scheme;

    public ResourceIdentifier(String identifierValue) {
        this(identifierValue, null);
    }

    public ResourceIdentifier(String identifierValue, String scheme) {
        this.value = identifierValue;
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

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @Override
    public String toString() {
        return "UrlIdentifier{" +
                "value='" + value + '\'' +
                ", scheme='" + scheme + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ResourceIdentifier that = (ResourceIdentifier) o;

        return new EqualsBuilder().append(value, that.value).append(scheme, that.scheme).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(value).append(scheme).toHashCode();
    }
}
