package eu.europa.ec.edelivery.smp.services.spi.data;

import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.InputStream;

/**
 *  The resource metadata.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class SpiRequestData implements RequestData {

    String domainCode;

    ResourceIdentifier resourceIdentifier;
    ResourceIdentifier subresourceIdentifier;

    InputStream resourceInputStream;


    public SpiRequestData(String domainCode, ResourceIdentifier resourceIdentifier, InputStream inputStream) {
        this(domainCode, resourceIdentifier, null, inputStream);
    }

    public SpiRequestData(String domainCode, ResourceIdentifier resourceIdentifier, ResourceIdentifier subresourceIdentifier,InputStream inputStream) {
        this.domainCode = domainCode;
        this.resourceIdentifier = resourceIdentifier;
        this.subresourceIdentifier = subresourceIdentifier;
        this.resourceInputStream = inputStream;
    }

    @Override
    public String getDomainCode() {
        return domainCode;
    }

    @Override
    public ResourceIdentifier getResourceIdentifier() {
        return resourceIdentifier;
    }

    @Override
    public ResourceIdentifier getSubresourceIdentifier() {
        return subresourceIdentifier;
    }

    @Override
    public InputStream getResourceInputStream() {
        return resourceInputStream;
    }

    @Override
    public String toString() {
        return "ResourceData{" +
                "domainCode='" + domainCode + '\'' +
                ", resourceIdentifier=" + resourceIdentifier +
                ", subresourceIdentifier=" + subresourceIdentifier +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SpiRequestData that = (SpiRequestData) o;

        return new EqualsBuilder().append(domainCode, that.domainCode).append(resourceIdentifier, that.resourceIdentifier).append(subresourceIdentifier, that.subresourceIdentifier).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(domainCode).append(resourceIdentifier).append(subresourceIdentifier).toHashCode();
    }
}
