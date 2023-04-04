package eu.europa.ec.smp.spi.resource;


/**
 * @author Joze Rihtarsic
 * @since 5.0
 *
 * SMP Service provider interface (SPI) for implementing resource handling
 * This SPI interface is intended to allow support for various resource types as for example Oasis SMP 1.0 document
 * CPP documents
 */
public interface SubresourceDefinitionSpi {

    String identifier();
    String urlSegment();
    String name();
    String description();
    String mimeType();

    ResourceHandlerSpi getResourceHandler();

}
