package eu.europa.ec.cipa.smp.server.data.dbms.model;

/**
 * Created by gutowpa on 01/02/2017.
 */
public class CommonColumnsLengths {
    public static final String DNS_HASHED_IDENTIFIER_PREFIX = "B-";
    public static final int MAX_IDENTIFIER_SCHEME_LENGTH = 25;
    public static final int MAX_PARTICIPANT_IDENTIFIER_VALUE_LENGTH = 50;
    public static final int MAX_DOCUMENT_TYPE_IDENTIFIER_VALUE_LENGTH = 500;
    public static final int MAX_PROCESS_IDENTIFIER_VALUE_LENGTH = 200;
    public static final String PARTICIPANT_IDENTIFIER_SCHEME_REGEX = "[a-z0-9]+-actorid-[a-z0-9]+";
    public static final String DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME = "iso6523-actorid-upis";
    public static final String DEFAULT_DOCUMENT_TYPE_IDENTIFIER_SCHEME = "busdox-docid-qns";
    public static final String DEFAULT_PROCESS_IDENTIFIER_SCHEME = "cenbii-procid-ubl";
    public static final String DEFAULT_PROCESS_IDENTIFIER_NOPROCESS = "busdox:noprocess";
    public static final String URL_SCHEME_VALUE_SEPARATOR = "::";
}
