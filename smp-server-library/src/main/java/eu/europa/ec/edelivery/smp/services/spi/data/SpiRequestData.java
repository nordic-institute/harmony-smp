/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.services.spi.data;

import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.InputStream;
import java.util.Map;

/**
 *  The resource metadata.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class SpiRequestData implements RequestData {

    String domainCode;

    protected final ResourceIdentifier resourceIdentifier;
    protected final ResourceIdentifier subresourceIdentifier;

    protected final InputStream resourceInputStream;
    protected final Map<String, String> documentAttributes;


    public SpiRequestData(String domainCode,
                          Map<String, String> documentAttributes,
                          ResourceIdentifier resourceIdentifier,
                          ResourceIdentifier subresourceIdentifier,
                          InputStream inputStream) {
        this.domainCode = domainCode;
        this.resourceIdentifier = resourceIdentifier;
        this.subresourceIdentifier = subresourceIdentifier;
        this.resourceInputStream = inputStream;
        this.documentAttributes = documentAttributes;
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

    public Map<String, String> getDocumentAttributes() {
        return documentAttributes;
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
