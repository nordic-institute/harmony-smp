package eu.europa.ec.edelivery.smp.config.enums;


/**
 * Enumeration constants. The purpose of the constants is to make enumeration configurations more transparent/readable
 * ex:
 * This is  (see the boolean values)
 *   OUTPUT_CONTEXT_PATH("contextPath.output", "true", "This property controls pattern of URLs produced by SMP in GET ServiceGroup responses.",
 *   true, false, true, BOOLEAN),
 * changed to:
 *   OUTPUT_CONTEXT_PATH("contextPath.output", "true", "This property controls pattern of URLs produced by SMP in GET ServiceGroup responses.",
 *   MANDATORY, NOT_ENCRYPTED, RESTART_NEEDED, BOOLEAN),
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class SMPEnumConstants {

    public static final boolean MANDATORY = true;
    public static final boolean OPTIONAL = !MANDATORY;
    public static final boolean ENCRYPTED = true;
    public static final boolean NOT_ENCRYPTED = !ENCRYPTED;
    public static final boolean RESTART_NEEDED = true;
    public static final boolean NO_RESTART_NEEDED = !RESTART_NEEDED;
}
