/*
 * Copyright 2015 e-CODEX Project
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl5
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.domibus.common.exception;


import eu.domibus.common.MSHRole;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;

import javax.xml.ws.WebFault;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This is the implementation of a ebMS3 Error Message
 */
@WebFault(name = "ebMS3Error")
public class EbMS3Exception extends Exception {

    /**
     * Default locale for error messages
     */
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    /**
     * Default ResourceBundle name for error messages
     */
    public static final String RESOURCE_BUNDLE_NAME = "messages.ebms3.codes.MessagesBundle";
    /**
     * Default ResourceBundle for error messages
     */
    public static final ResourceBundle DEFAULT_MESSAGES = ResourceBundle.getBundle(EbMS3Exception.RESOURCE_BUNDLE_NAME, EbMS3Exception.DEFAULT_LOCALE);
    public static final String SEVERITY_FAILURE = "failure";
    public static final String SEVERITY_WARNING = "warning";
    private static final String ORIGIN_EBMS = "ebMS";
    private static final String ORIGIN_RELIABILITY = "reliability";
    private static final String ORIGIN_SECURITY = "security";
    private final EbMS3Exception.EbMS3ErrorCode errorCode;
    /**
     * "This OPTIONAL attribute provides a short description of the error that can be reported in a log, in order to facilitate readability."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     */
    private String errorDetail;
    private String refToMessageId;
    private MSHRole mshRole;
    private boolean recoverable = true;
    private String signalMessageId;

    /**
     * "This OPTIONAL element provides a narrative description of the error in the language
     * defined by the xml:lang attribute. The content of this element is left to
     * implementation-specific decisions."
     * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
     */
    //private final String description;
    public EbMS3Exception(final EbMS3Exception.EbMS3ErrorCode errorCode, final String errorDetail, final String refToMessageId, final String signalMessageId, final Throwable cause, final MSHRole mshRole) {
        super(cause);
        this.errorCode = errorCode;
        this.errorDetail = errorDetail;
        this.refToMessageId = refToMessageId;
        this.mshRole = mshRole;
        this.signalMessageId = signalMessageId;

    }

    public EbMS3Exception(final EbMS3Exception.EbMS3ErrorCode errorCode, final String errorDetail, final String refToMessageId, final Throwable cause, final MSHRole mshRole) {
        this(errorCode, errorDetail, refToMessageId, null, cause, mshRole);
    }

    public EbMS3Exception(final EbMS3Exception.EbMS3ErrorCode errorCode, final String refToMessageId, final Throwable cause, final MSHRole mshRole) {
        this(errorCode, "", refToMessageId, cause, mshRole);
    }

    public EbMS3Exception(final EbMS3Exception.EbMS3ErrorCode errorCode, final Throwable cause, final MSHRole mshRole) {
        this(errorCode, "", cause, mshRole);
    }

    public boolean isRecoverable() {
        return this.recoverable;
    }

    public void setRecoverable(final boolean recoverable) {
        this.recoverable = recoverable;
    }

    public Description getDescription() {
        return this.getDescription(EbMS3Exception.DEFAULT_MESSAGES);
    }

    public Description getDescription(final ResourceBundle bundle) {
        final Description description = new Description();
        description.setValue(bundle.getString(this.errorCode.code.name()));
        description.setLang(bundle.getLocale().getLanguage());

        return description;
    }

    public Description getDescription(final Locale locale) {
        return this.getDescription(ResourceBundle.getBundle(EbMS3Exception.RESOURCE_BUNDLE_NAME, locale));
    }

    public String getErrorDetail() {
        return this.errorDetail;
    }

    public void setErrorDetail(final String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public String getOrigin() {
        return this.errorCode.getCode().getOrigin();
    }

    public String getErrorCode() {
        return this.errorCode.getCode().getErrorCode().name();
    }

    public ErrorCode getErrorCodeObject() {
        return this.errorCode.getCode().getErrorCode();
    }

    public String getShortDescription() {
        return this.errorCode.getShortDescription();
    }

    public String getSeverity() {
        return this.errorCode.getSeverity();
    }

    public String getCategory() {
        return this.errorCode.getCategory().name();
    }

    public Error getFaultInfo() {

        final Error ebMS3Error = new Error();

        ebMS3Error.setOrigin(this.errorCode.getCode().getOrigin());
        ebMS3Error.setErrorCode(this.errorCode.getCode().getErrorCode().getErrorCodeName());
        ebMS3Error.setSeverity(this.errorCode.getSeverity());
        ebMS3Error.setErrorDetail((this.errorDetail != null ? this.errorDetail : ""));
        ebMS3Error.setCategory(this.errorCode.getCategory().name());
        ebMS3Error.setRefToMessageInError(this.refToMessageId);
        ebMS3Error.setShortDescription(this.getShortDescription());
        ebMS3Error.setDescription(this.getDescription());


        return ebMS3Error;
    }

    public String getRefToMessageId() {
        return this.refToMessageId;
    }

    public void setRefToMessageId(final String refToMessageId) {
        this.refToMessageId = refToMessageId;
    }

    public MSHRole getMshRole() {
        return this.mshRole;
    }

    public void setMshRole(final MSHRole mshRole) {
        this.mshRole = mshRole;
    }

    public String getSignalMessageId() {
        return this.signalMessageId;
    }

    public void setSignalMessageId(final String signalMessageId) {
        this.signalMessageId = signalMessageId;
    }

    public enum Categories {
        CONTENT, UNPACKAGING, PROCESSING, COMMUNICATION;
    }


    private enum OriginErrorCode {

        EBMS_0001(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0001),
        EBMS_0002(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0002),
        EBMS_0003(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0003),
        EBMS_0004(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0004),
        EBMS_0005(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0005),
        EBMS_0006(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0006),
        EBMS_0007(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0007),
        EBMS_0008(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0008),
        EBMS_0009(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0009),
        EBMS_0010(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0010),
        EBMS_0301(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0301),
        EBMS_0302(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0302),
        EBMS_0303(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0303),
        EBMS_0011(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0011),
        EBMS_0020(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0020),
        EBMS_0021(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0021),
        EBMS_0022(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0022),
        EBMS_0023(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0023),
        EBMS_0030(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0030),
        EBMS_0031(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0031),
        EBMS_0040(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0040),
        EBMS_0041(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0041),
        EBMS_0042(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0042),
        EBMS_0043(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0043),
        EBMS_0044(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0044),
        EBMS_0045(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0045),
        EBMS_0046(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0046),
        EBMS_0047(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0047),
        EBMS_0048(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0048),
        EBMS_0049(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0049),
        EBMS_0050(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0050),
        EBMS_0051(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0051),
        EBMS_0052(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0052),
        EBMS_0053(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0053),
        EBMS_0054(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0054),
        EBMS_0055(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0055),
        EBMS_0060(EbMS3Exception.ORIGIN_EBMS, ErrorCode.EBMS_0060),
        EBMS_0101(EbMS3Exception.ORIGIN_SECURITY, ErrorCode.EBMS_0101),
        EBMS_0102(EbMS3Exception.ORIGIN_SECURITY, ErrorCode.EBMS_0102),
        EBMS_0103(EbMS3Exception.ORIGIN_SECURITY, ErrorCode.EBMS_0103),
        EBMS_0201(EbMS3Exception.ORIGIN_RELIABILITY, ErrorCode.EBMS_0201),
        EBMS_0202(EbMS3Exception.ORIGIN_RELIABILITY, ErrorCode.EBMS_0202);


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

        OriginErrorCode(final String origin, final ErrorCode errorCode) {
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

        EBMS_0301(EbMS3Exception.OriginErrorCode.EBMS_0301, "MissingReceipt", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.COMMUNICATION),
        EBMS_0302(EbMS3Exception.OriginErrorCode.EBMS_0302, "InvalidReceipt", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.COMMUNICATION),
        EBMS_0303(EbMS3Exception.OriginErrorCode.EBMS_0303, "DecompressionFailure", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.COMMUNICATION),
        EBMS_0001(EbMS3Exception.OriginErrorCode.EBMS_0001, "ValueNotRecognized", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.CONTENT),
        EBMS_0002(EbMS3Exception.OriginErrorCode.EBMS_0002, "FeatureNotSupported", EbMS3Exception.SEVERITY_WARNING, EbMS3Exception.Categories.CONTENT),
        EBMS_0003(EbMS3Exception.OriginErrorCode.EBMS_0003, "ValueInconsistent", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.CONTENT),
        EBMS_0004(EbMS3Exception.OriginErrorCode.EBMS_0004, "Other", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.CONTENT),
        EBMS_0005(EbMS3Exception.OriginErrorCode.EBMS_0005, "ConnectionFailure", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.COMMUNICATION),
        EBMS_0006(EbMS3Exception.OriginErrorCode.EBMS_0006, "EmptyMessagePartitionChannel", EbMS3Exception.SEVERITY_WARNING,
                EbMS3Exception.Categories.COMMUNICATION),
        EBMS_0007(EbMS3Exception.OriginErrorCode.EBMS_0007, "MimeInconsistency", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.UNPACKAGING),
        EBMS_0008(EbMS3Exception.OriginErrorCode.EBMS_0008, "FeatureNotSupported", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.UNPACKAGING),
        EBMS_0009(EbMS3Exception.OriginErrorCode.EBMS_0009, "InvalidHeader", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.UNPACKAGING),
        EBMS_0010(EbMS3Exception.OriginErrorCode.EBMS_0010, "ProcessingModeMismatch", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.PROCESSING),
        EBMS_0011(EbMS3Exception.OriginErrorCode.EBMS_0011, "ExternalPayloadError", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.CONTENT),
        EBMS_0101(EbMS3Exception.OriginErrorCode.EBMS_0101, "FailedAuthentication", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.PROCESSING),
        EBMS_0102(EbMS3Exception.OriginErrorCode.EBMS_0102, "FailedDecryption", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.PROCESSING),
        EBMS_0103(EbMS3Exception.OriginErrorCode.EBMS_0103, "PolicyNoncompliance", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.PROCESSING),
        EBMS_0201(EbMS3Exception.OriginErrorCode.EBMS_0201, "DysfunctionalReliability", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.PROCESSING),
        EBMS_0202(EbMS3Exception.OriginErrorCode.EBMS_0202, "DeliveryFailure", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.COMMUNICATION),
        EBMS_0020(EbMS3Exception.OriginErrorCode.EBMS_0020, "RoutingFailure", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.PROCESSING),
        EBMS_0021(EbMS3Exception.OriginErrorCode.EBMS_0021, "MPCCapacityExceeded", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.PROCESSING),
        EBMS_0022(EbMS3Exception.OriginErrorCode.EBMS_0022, "MessagePersistenceTimeout", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.PROCESSING),
        EBMS_0023(EbMS3Exception.OriginErrorCode.EBMS_0023, "MessageExpired", EbMS3Exception.SEVERITY_WARNING, EbMS3Exception.Categories.PROCESSING),
        EBMS_0030(EbMS3Exception.OriginErrorCode.EBMS_0030, "BundlingError", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.CONTENT),
        EBMS_0031(EbMS3Exception.OriginErrorCode.EBMS_0031, "RelatedMessageFailed", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.PROCESSING),
        EBMS_0040(EbMS3Exception.OriginErrorCode.EBMS_0040, "BadFragmentGroup", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.CONTENT),
        EBMS_0041(EbMS3Exception.OriginErrorCode.EBMS_0041, "DuplicateMessageSize", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.CONTENT),
        EBMS_0042(EbMS3Exception.OriginErrorCode.EBMS_0042, "DuplicateFragmentCount", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.CONTENT),
        EBMS_0043(EbMS3Exception.OriginErrorCode.EBMS_0043, "DuplicateMessageHeader", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.CONTENT),
        EBMS_0044(EbMS3Exception.OriginErrorCode.EBMS_0044, "DuplicateAction", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.CONTENT),
        EBMS_0045(EbMS3Exception.OriginErrorCode.EBMS_0045, "DuplicateCompressionInfo", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.CONTENT),
        EBMS_0046(EbMS3Exception.OriginErrorCode.EBMS_0046, "DuplicateFragment", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.CONTENT),
        EBMS_0047(EbMS3Exception.OriginErrorCode.EBMS_0047, "BadFragmentStructure", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.UNPACKAGING),
        EBMS_0048(EbMS3Exception.OriginErrorCode.EBMS_0048, "BadFragmentNum", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.CONTENT),
        EBMS_0049(EbMS3Exception.OriginErrorCode.EBMS_0049, "BadFragmentCount", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.CONTENT),
        EBMS_0050(EbMS3Exception.OriginErrorCode.EBMS_0050, "FragmentSizeExceeded", EbMS3Exception.SEVERITY_WARNING, EbMS3Exception.Categories.UNPACKAGING),
        EBMS_0051(EbMS3Exception.OriginErrorCode.EBMS_0051, "ReceiveIntervalExceeded", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.UNPACKAGING),
        EBMS_0052(EbMS3Exception.OriginErrorCode.EBMS_0052, "BadProperties", EbMS3Exception.SEVERITY_WARNING, EbMS3Exception.Categories.UNPACKAGING),
        EBMS_0053(EbMS3Exception.OriginErrorCode.EBMS_0053, "HeaderMismatch", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.UNPACKAGING),
        EBMS_0054(EbMS3Exception.OriginErrorCode.EBMS_0054, "OutOfStorageSpace", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.UNPACKAGING),
        EBMS_0055(EbMS3Exception.OriginErrorCode.EBMS_0055, "DecompressionError", EbMS3Exception.SEVERITY_FAILURE, EbMS3Exception.Categories.PROCESSING),
        EBMS_0060(EbMS3Exception.OriginErrorCode.EBMS_0060, "ResponseUsing-AlternateMEP", EbMS3Exception.SEVERITY_WARNING, EbMS3Exception.Categories.PROCESSING);


        private final EbMS3Exception.OriginErrorCode code;

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
        private final String severity;

        /**
         * "This OPTIONAL attribute identifies the type of error related to a particular
         * origin. For example: Content, Packaging, UnPackaging, Communication, InternalProcess."
         * (OASIS ebXML Messaging Services Version 3.0: Part 1, Core Features, 1 October 2007)
         */
        private final EbMS3Exception.Categories category;


        EbMS3ErrorCode(final EbMS3Exception.OriginErrorCode code, final String shortDescription, final String severity, final EbMS3Exception.Categories category) {
            this.code = code;
            this.shortDescription = shortDescription;
            this.severity = severity;
            this.category = category;
        }

        public static EbMS3Exception.EbMS3ErrorCode findErrorCodeBy(final String originErrorCode) {
            for (final EbMS3Exception.EbMS3ErrorCode errorCode : EbMS3Exception.EbMS3ErrorCode.values()) {
                if (errorCode.getCode().getErrorCode().equals(ErrorCode.findBy(originErrorCode))) {
                    return errorCode;
                }
            }

            throw new IllegalArgumentException("No EbMS3ErrorCode found for OriginErrorCode: " + originErrorCode);
        }

        public EbMS3Exception.OriginErrorCode getCode() {
            return this.code;
        }

        public String getShortDescription() {
            return this.shortDescription;
        }

        public String getSeverity() {
            return this.severity;
        }

        public EbMS3Exception.Categories getCategory() {
            return this.category;
        }


        @Override
        public String toString() {
            return "EbMS3ErrorCode{" +
                    "code=" + this.code +
                    ", shortDescription='" + this.shortDescription + '\'' +
                    ", severity=" + this.severity +
                    ", category=" + this.category +
                    '}' + super.toString();
        }
    }
}

