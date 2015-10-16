package eu.domibus.common.exceptions;

import org.apache.axis2.AxisFault;

/**
 * This is the implementation of a ebMS3 Error Message
 */
public class EbMS3Exception extends AxisFault {

    private static final String ORIGIN_EBMS = "ebMS";
    private static final String ORIGIN_RELIABILITY = "reliability";
    private static final String ORIGIN_SECURITY = "security";

    private enum Categories {
        CONTENT, UNPACKAGING, PROCESSING, COMMUNICATION;
    }

    private enum Severity {
        WARNING, FAILURE;
    }

    private enum ErrorCode {
        EBMS_0001, EBMS_0002, EBMS_0003, EBMS_0004, EBMS_0005, EBMS_0006, EBMS_0007,
        EBMS_0008, EBMS_0009, EBMS_0010, EBMS_0011, EBMS_0101, EBMS_0102, EBMS_0103,
        EBMS_0201, EBMS_0202, EBMS_0301, EBMS_0302, EBMS_0303, EBMS_0020, EBMS_0021,
        EBMS_0022, EBMS_0023, EBMS_0030, EBMS_0031, EBMS_0040, EBMS_0041, EBMS_0042,
        EBMS_0043, EBMS_0044, EBMS_0045, EBMS_0046, EBMS_0047, EBMS_0048, EBMS_0049,
        EBMS_0050, EBMS_0051, EBMS_0052, EBMS_0053, EBMS_0054, EBMS_0055, EBMS_0060;
    }

    /**
     * TODO: not sure about the origin for the following errors:
     * <ul>
     * <li>EBMS_0021</li>
     * <li>EBMS_0022</li>
     * <li>EBMS_0023</li>
     * <li>EBMS_0030</li>
     * <li>EBMS_0031</li>
     * <li>EBMS_0040</li>
     * <li>EBMS_0041</li>
     * <li>EBMS_0042</li>
     * <li>EBMS_0043</li>
     * <li>EBMS_0044</li>
     * <li>EBMS_0045</li>
     * <li>EBMS_0046</li>
     * <li>EBMS_0047</li>
     * <li>EBMS_0048</li>
     * <li>EBMS_0049</li>
     * <li>EBMS_0050</li>
     * <li>EBMS_0051</li>
     * <li>EBMS_0052</li>
     * <li>EBMS_0053</li>
     * <li>EBMS_0054</li>
     * <li>EBMS_0055</li>
     * <li>EBMS_0060</li>
     * </ul>
     */
    private enum OriginErrorCode {

        EBMS_0001(ORIGIN_EBMS, ErrorCode.EBMS_0001),
        EBMS_0002(ORIGIN_EBMS, ErrorCode.EBMS_0002),
        EBMS_0003(ORIGIN_EBMS, ErrorCode.EBMS_0003),
        EBMS_0004(ORIGIN_EBMS, ErrorCode.EBMS_0004),
        EBMS_0005(ORIGIN_EBMS, ErrorCode.EBMS_0005),
        EBMS_0006(ORIGIN_EBMS, ErrorCode.EBMS_0006),
        EBMS_0007(ORIGIN_EBMS, ErrorCode.EBMS_0007),
        EBMS_0008(ORIGIN_EBMS, ErrorCode.EBMS_0008),
        EBMS_0009(ORIGIN_EBMS, ErrorCode.EBMS_0009),
        EBMS_0010(ORIGIN_EBMS, ErrorCode.EBMS_0010),
        EBMS_0301(ORIGIN_EBMS, ErrorCode.EBMS_0301),
        EBMS_0302(ORIGIN_EBMS, ErrorCode.EBMS_0302),
        EBMS_0303(ORIGIN_EBMS, ErrorCode.EBMS_0303),
        EBMS_0011(ORIGIN_EBMS, ErrorCode.EBMS_0011),
        EBMS_0020(ORIGIN_EBMS, ErrorCode.EBMS_0020),
        EBMS_0021(ORIGIN_EBMS, ErrorCode.EBMS_0021),
        EBMS_0022(ORIGIN_EBMS, ErrorCode.EBMS_0022),
        EBMS_0023(ORIGIN_EBMS, ErrorCode.EBMS_0023),
        EBMS_0030(ORIGIN_EBMS, ErrorCode.EBMS_0030),
        EBMS_0031(ORIGIN_EBMS, ErrorCode.EBMS_0031),
        EBMS_0040(ORIGIN_EBMS, ErrorCode.EBMS_0040),
        EBMS_0041(ORIGIN_EBMS, ErrorCode.EBMS_0041),
        EBMS_0042(ORIGIN_EBMS, ErrorCode.EBMS_0042),
        EBMS_0043(ORIGIN_EBMS, ErrorCode.EBMS_0043),
        EBMS_0044(ORIGIN_EBMS, ErrorCode.EBMS_0044),
        EBMS_0045(ORIGIN_EBMS, ErrorCode.EBMS_0045),
        EBMS_0046(ORIGIN_EBMS, ErrorCode.EBMS_0046),
        EBMS_0047(ORIGIN_EBMS, ErrorCode.EBMS_0047),
        EBMS_0048(ORIGIN_EBMS, ErrorCode.EBMS_0048),
        EBMS_0049(ORIGIN_EBMS, ErrorCode.EBMS_0049),
        EBMS_0050(ORIGIN_EBMS, ErrorCode.EBMS_0050),
        EBMS_0051(ORIGIN_EBMS, ErrorCode.EBMS_0051),
        EBMS_0052(ORIGIN_EBMS, ErrorCode.EBMS_0052),
        EBMS_0053(ORIGIN_EBMS, ErrorCode.EBMS_0053),
        EBMS_0054(ORIGIN_EBMS, ErrorCode.EBMS_0054),
        EBMS_0055(ORIGIN_EBMS, ErrorCode.EBMS_0055),
        EBMS_0060(ORIGIN_EBMS, ErrorCode.EBMS_0060),
        EBMS_0101(ORIGIN_SECURITY, ErrorCode.EBMS_0101),
        EBMS_0102(ORIGIN_SECURITY, ErrorCode.EBMS_0102),
        EBMS_0103(ORIGIN_SECURITY, ErrorCode.EBMS_0103),
        EBMS_0201(ORIGIN_RELIABILITY, ErrorCode.EBMS_0201),
        EBMS_0202(ORIGIN_RELIABILITY, ErrorCode.EBMS_0202);


        /**
         * "This OPTIONAL attribute identifies the functional module within which the
         * error occurred. This module could be the the ebMS Module, the Reliability Module,
         * or the Security Module. Possible values for this attribute include "ebMS",
         * "reliability", and "security". The use of other modules, and thus their
         * corresponding @origin values, may be specified elsewhere, such as in a
         * forthcoming Part 2 of this specification."
         * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
         */
        private final ErrorCode errorCode;

        /**
         * "This REQUIRED attribute is a unique identifier for the type of error."
         * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
         */
        private final String origin;

        private OriginErrorCode(String origin, ErrorCode errorCode) {
            this.origin = origin;
            this.errorCode = errorCode;
        }

        private ErrorCode getErrorCode() {
            return this.errorCode;
        }

        private String getOrigin() {
            return this.origin;
        }
    }


    public enum EbMS3ErrorCode {

        EBMS_0301(OriginErrorCode.EBMS_0301, "MissingReceipt", Severity.FAILURE, Categories.COMMUNICATION),
        EBMS_0302(OriginErrorCode.EBMS_0302, "InvalidReceipt", Severity.FAILURE, Categories.COMMUNICATION),
        EBMS_0303(OriginErrorCode.EBMS_0303, "DecompressionFailure", Severity.FAILURE, Categories.COMMUNICATION),
        EBMS_0001(OriginErrorCode.EBMS_0001, "ValueNotRecognized", Severity.FAILURE, Categories.CONTENT),
        EBMS_0002(OriginErrorCode.EBMS_0002, "FeatureNotSupported", Severity.WARNING, Categories.CONTENT),
        EBMS_0003(OriginErrorCode.EBMS_0003, "ValueInconsistent", Severity.FAILURE, Categories.CONTENT),
        EBMS_0004(OriginErrorCode.EBMS_0004, "Other", Severity.FAILURE, Categories.CONTENT),
        EBMS_0005(OriginErrorCode.EBMS_0005, "ConnectionFailure", Severity.FAILURE, Categories.COMMUNICATION),
        EBMS_0006(OriginErrorCode.EBMS_0006, "EmptyMessagePartitionChannel", Severity.WARNING,
                  Categories.COMMUNICATION),
        EBMS_0007(OriginErrorCode.EBMS_0007, "MimeInconsistency", Severity.FAILURE, Categories.UNPACKAGING),
        EBMS_0008(OriginErrorCode.EBMS_0008, "FeatureNotSupported", Severity.FAILURE, Categories.UNPACKAGING),
        EBMS_0009(OriginErrorCode.EBMS_0009, "InvalidHeader", Severity.FAILURE, Categories.UNPACKAGING),
        EBMS_0010(OriginErrorCode.EBMS_0010, "ProcessingModeMismatch", Severity.FAILURE, Categories.PROCESSING),
        EBMS_0011(OriginErrorCode.EBMS_0011, "ExternalPayloadError", Severity.FAILURE, Categories.CONTENT),
        EBMS_0101(OriginErrorCode.EBMS_0101, "FailedAuthentication", Severity.FAILURE, Categories.PROCESSING),
        EBMS_0102(OriginErrorCode.EBMS_0102, "FailedDecryption", Severity.FAILURE, Categories.PROCESSING),
        EBMS_0103(OriginErrorCode.EBMS_0103, "PolicyNoncompliance", Severity.FAILURE, Categories.PROCESSING),
        EBMS_0201(OriginErrorCode.EBMS_0201, "DysfunctionalReliability", Severity.FAILURE, Categories.PROCESSING),
        EBMS_0202(OriginErrorCode.EBMS_0202, "DeliveryFailure", Severity.FAILURE, Categories.COMMUNICATION),
        EBMS_0020(OriginErrorCode.EBMS_0020, "RoutingFailure", Severity.FAILURE, Categories.PROCESSING),
        EBMS_0021(OriginErrorCode.EBMS_0021, "MPCCapacityExceeded", Severity.FAILURE, Categories.PROCESSING),
        EBMS_0022(OriginErrorCode.EBMS_0022, "MessagePersistenceTimeout", Severity.FAILURE, Categories.PROCESSING),
        EBMS_0023(OriginErrorCode.EBMS_0023, "MessageExpired", Severity.WARNING, Categories.PROCESSING),
        EBMS_0030(OriginErrorCode.EBMS_0030, "BundlingError", Severity.FAILURE, Categories.CONTENT),
        EBMS_0031(OriginErrorCode.EBMS_0031, "RelatedMessageFailed", Severity.FAILURE, Categories.PROCESSING),
        EBMS_0040(OriginErrorCode.EBMS_0040, "BadFragmentGroup", Severity.FAILURE, Categories.CONTENT),
        EBMS_0041(OriginErrorCode.EBMS_0041, "DuplicateMessageSize", Severity.FAILURE, Categories.CONTENT),
        EBMS_0042(OriginErrorCode.EBMS_0042, "DuplicateFragmentCount", Severity.FAILURE, Categories.CONTENT),
        EBMS_0043(OriginErrorCode.EBMS_0043, "DuplicateMessageHeader", Severity.FAILURE, Categories.CONTENT),
        EBMS_0044(OriginErrorCode.EBMS_0044, "DuplicateAction", Severity.FAILURE, Categories.CONTENT),
        EBMS_0045(OriginErrorCode.EBMS_0045, "DuplicateCompressionInfo", Severity.FAILURE, Categories.CONTENT),
        EBMS_0046(OriginErrorCode.EBMS_0046, "DuplicateFragment", Severity.FAILURE, Categories.CONTENT),
        EBMS_0047(OriginErrorCode.EBMS_0047, "BadFragmentStructure", Severity.FAILURE, Categories.UNPACKAGING),
        EBMS_0048(OriginErrorCode.EBMS_0048, "BadFragmentNum", Severity.FAILURE, Categories.CONTENT),
        EBMS_0049(OriginErrorCode.EBMS_0049, "BadFragmentCount", Severity.FAILURE, Categories.CONTENT),
        EBMS_0050(OriginErrorCode.EBMS_0050, "FragmentSizeExceeded", Severity.WARNING, Categories.UNPACKAGING),
        EBMS_0051(OriginErrorCode.EBMS_0051, "ReceiveIntervalExceeded", Severity.FAILURE, Categories.UNPACKAGING),
        EBMS_0052(OriginErrorCode.EBMS_0052, "BadProperties", Severity.WARNING, Categories.UNPACKAGING),
        EBMS_0053(OriginErrorCode.EBMS_0053, "HeaderMismatch", Severity.FAILURE, Categories.UNPACKAGING),
        EBMS_0054(OriginErrorCode.EBMS_0054, "OutOfStorageSpace", Severity.FAILURE, Categories.UNPACKAGING),
        EBMS_0055(OriginErrorCode.EBMS_0055, "DecompressionError", Severity.FAILURE, Categories.PROCESSING),
        EBMS_0060(OriginErrorCode.EBMS_0060, "ResponseUsing-AlternateMEP", Severity.WARNING, Categories.PROCESSING);


        private final OriginErrorCode code;

        /**
         * "This OPTIONAL attribute provides a short description of the error
         * that can be reported in a log, in order to facilitate readability."
         * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
         */
        private final String shortDescription;

        /**
         * "This REQUIRED attribute indicates the severity of the error. Valid
         * values are: warning, failure.
         * The warning value indicates that a potentially disabling condition
         * has been detected, but no message processing and/or exchange has failed
         * so far. In particular, if the message was supposed to be delivered to
         * a consumer, it would be delivered even though a warning was issued.
         * Other related messages in the conversation or MEP can be generated and
         * exchanged in spite of this problem.
         * The failure value indicates that the processing of a message did not
         * proceed as expected, and cannot be considered successful. If, in spite
         * of this, the message payload is in a state of being delivered, the
         * default behavior is not to deliver it, unless an agreement states otherwise
         * (see OpCtx-ErrorHandling). This error does not presume the ability of the
         * MSH to process other messages, although the conversation or the MEP instance
         * this message was involved in is at risk of being invalid."
         * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
         */
        private final Severity severity;

        /**
         * "This OPTIONAL attribute identifies the type of error related to a particular
         * origin. For example: Content, Packaging, UnPackaging, Communication, InternalProcess."
         * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
         */
        private final Categories category;


        private EbMS3ErrorCode(OriginErrorCode code, String shortDescription, Severity severity, Categories category) {
            this.code = code;
            this.shortDescription = shortDescription;
            this.severity = severity;
            this.category = category;
        }


        public OriginErrorCode getCode() {
            return this.code;
        }

        public String getShortDescription() {
            return this.shortDescription;
        }

        public Severity getSeverity() {
            return this.severity;
        }

        public Categories getCategory() {
            return this.category;
        }
    }

    private final EbMS3ErrorCode errorCode;

    /**
     * "This REQUIRED attribute is a unique identifier for the type of error."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     */
    private final String errorDetail;

    /**
     * "This OPTIONAL element provides a narrative description of the error in the language
     * defined by the xml:lang attribute. The content of this element is left to
     * implementation-specific decisions."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     */
    private final String description;

    public EbMS3Exception(EbMS3ErrorCode errorCode, String description, String errorDetail) {
        super(errorCode.getShortDescription());
        this.errorCode = errorCode;
        this.errorDetail = errorDetail;
        this.description = description;
    }

    public EbMS3Exception(EbMS3ErrorCode errorCode, String errorDetail) {
        this(errorCode, null, errorDetail);
    }

    public EbMS3Exception(EbMS3ErrorCode errorCode) {
        this(errorCode, null, null);
    }


    public String getDescription() {
        return this.description;
    }

    public String getErrorDetail() {
        return this.errorDetail;
    }

    public String getOrigin() {
        return this.errorCode.getCode().getOrigin();
    }

    public String getErrorCode() {
        return this.errorCode.getCode().getErrorCode().name();
    }

    public String getShortDescription() {
        return this.errorCode.getShortDescription();
    }

    public String getSeverity() {
        return this.errorCode.getSeverity().name();
    }

    public String getCategory() {
        return this.errorCode.getCategory().name();
    }
}
