package eu.europa.ec.smp.spi.handler;

import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * The abstract class with common methods for implementation of the  ResourceHandlerSpi.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public abstract class AbstractOasisSMP10Handler implements ResourceHandlerSpi {
    public byte[] readFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public ResourceIdentifier getResourceIdentifier(RequestData resourceData) throws ResourceException {
        if (resourceData == null || resourceData.getResourceIdentifier() == null || StringUtils.isEmpty(resourceData.getResourceIdentifier().getValue())) {
            throw new ResourceException(ResourceException.ErrorCode.INVALID_PARAMETERS, "Missing resource identifier for the resource ServiceGroup");
        }
        return resourceData.getResourceIdentifier();
    }

    public ResourceIdentifier getSubresourceIdentifier(RequestData resourceData) throws ResourceException {
        if (resourceData == null || resourceData.getSubresourceIdentifier() == null || StringUtils.isEmpty(resourceData.getSubresourceIdentifier().getValue())) {
            throw new ResourceException(ResourceException.ErrorCode.INVALID_PARAMETERS, "Missing sub-resource identifier for the resource service metadata!");
        }
        return resourceData.getSubresourceIdentifier();
    }
}
