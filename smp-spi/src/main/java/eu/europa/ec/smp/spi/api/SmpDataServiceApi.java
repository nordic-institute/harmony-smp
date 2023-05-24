package eu.europa.ec.smp.spi.api;


import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;

import java.util.List;

/**
 * Class contains useful utils for retrieving data from the DomiSMP
 */
public interface SmpDataServiceApi {

    /**
     * Return subresource identifiers with subresource definition and resource
     *
     * @param identifier of the resource
     * @param subresourceDefinitionIdentifier identifier of the subresource
     * @return list of subresource identifiers
     */
    List<ResourceIdentifier> getSubResourceIdentifiers(ResourceIdentifier identifier, String subresourceDefinitionIdentifier);


    /**
     * The request returns requestor URL with only root context
     *
     * @return
     */
    String getResourceUrl();

    String getURIPathSegmentForSubresource(String name);

}
