package eu.europa.ec.edelivery.smp.data.enums;

/**
 * Specifies
 *
 * Specifies resource, group or domain visibility .
 * If the enumerated type is not specified or the Enumerated annotation is not used, the EnumType value is assumed to be PUBLIC.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public enum VisibilityType {
    /**
     * Resource, group of domain is marked as PUBLIC.
     */
    PUBLIC,
    /**
     * Access to the resource is within the domain/group. Users must be authenticated and must be members of the domain/group/resource in order to read it.
     */
    INTERNAL,
    /**
     *  Access to the domain, group or  resource is possible only if you are only direct or un-direct   member of the domain, group or resource
     */
    PRIVATE
}
