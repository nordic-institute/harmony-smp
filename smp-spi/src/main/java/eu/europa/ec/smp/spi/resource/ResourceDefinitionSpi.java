package eu.europa.ec.smp.spi.resource;


import java.util.List;

/**
 *
 * SMP Service provider interface (SPI) for implementing resource handling
 * This SPI interface is intended to allow support for various resource types as for example Oasis SMP 1.0 document
 * CPP documents
 *
 *  @author Joze Rihtarsic
 *  @since 5.0
 */
public interface ResourceDefinitionSpi {

    /**
     * Unique identifier of the resource definition. When upgrading to the newer version the indenter must stay the same else the definition
     * is handled as new resource definition.
     *
     * @return resource definition unique identifier
     */
    String identifier();

    /**
     * Default URL path segment for the resource. The DomiSMP can override the url segment for the domain!
     * @return default url segment
     */
    String defaultUrlSegment();

    String name();
    String description();

    /**
     * Mimetype of the resource
     * @return
     */
    String mimeType();

    /**
     * All subresouce types for the resource
     * @return
     */

    List<SubresourceDefinitionSpi> getSuresourceSpiList();

    // resource handle for validating, reading and storing the resource
    ResourceHandlerSpi getResourceHandler();


}
