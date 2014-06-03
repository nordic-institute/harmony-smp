package eu.domibus.ebms3.handlers;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.module.AS4ReliabilityUtil;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.persistent.ErrorMessage;
import eu.domibus.ebms3.persistent.ErrorMessageDAO;
import eu.domibus.ebms3.persistent.ReceiptTracking;
import eu.domibus.ebms3.persistent.ReceiptTrackingDAO;

import javax.xml.namespace.QName;
import java.util.Date;
import java.util.List;

/**
 * This handler runs both on the client and server side but only during the
 * IN_FLOW and its job is to simply log any received ebms3 error signals.
 *
 * @author Hamid Ben Malek
 */
public class ErrorLogger extends AbstractHandler {
    private static final Logger log = Logger.getLogger(ErrorLogger.class.getName());
    private final ErrorMessageDAO emd = new ErrorMessageDAO();
    private final ReceiptTrackingDAO rtd = new ReceiptTrackingDAO();

    @Override
    public InvocationResponse invoke(final MessageContext msgCtx) throws AxisFault {
        final int flow = msgCtx.getFLOW();
        if (flow != MessageContext.IN_FLOW && flow != MessageContext.IN_FAULT_FLOW) {
            return InvocationResponse.CONTINUE;
        }

        final OMElement messaging = EbUtil.getMessaging(msgCtx);
        if (messaging == null) {
            return InvocationResponse.CONTINUE;
        }

        ((SOAPHeaderBlock) messaging).setProcessed();

        final List<OMElement> errors = XMLUtil.getGrandChildrenNameNS(messaging, Constants.ERROR, Constants.NS);
        if (errors == null || errors.size() == 0) {
            return InvocationResponse.CONTINUE;
        }

        for (final OMElement error : errors) {
            XMLUtil.error(log, "Received the following ebms3 Error: ", error);

            final String origin = error.getAttributeValue(new QName("origin"));
            final String category = error.getAttributeValue(new QName("category"));
            final String errorCode = error.getAttributeValue(new QName("errorCode"));
            final String severity = error.getAttributeValue(new QName("severity"));
            final String refToMessageInError = error.getAttributeValue(new QName("refToMessageInError"));
            final String shortDescription = error.getAttributeValue(new QName("shortDescription"));

            final OMElement descriptionElement = XMLUtil.getFirstChildWithNameNS(error, "Description", Constants.NS);
            final String description = descriptionElement == null ? null : descriptionElement.getText();

            final OMElement errorDetailElement = XMLUtil.getFirstChildWithNameNS(error, "ErrorDetail", Constants.NS);
            final String errorDetail = errorDetailElement == null ? null : errorDetailElement.getText();

            final ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setOrigin(origin);
            errorMessage.setCategory(category);
            errorMessage.setErrorCode(errorCode);
            errorMessage.setSeverity(severity);
            errorMessage.setRefToMessageInError(refToMessageInError);
            errorMessage.setShortDescription(shortDescription);
            errorMessage.setDescription(description);
            errorMessage.setErrorDetail(errorDetail);
            errorMessage.setRemote();
            errorMessage.setInFlow();
            errorMessage.setAppearance(new Date());
            errorMessage.setDelivered(false);
            errorMessage.setToURL(null);
            emd.persist(errorMessage);

            // Stop sending MSH time schedule due to an unrecoverable error
            rtd.updateTrackingStatus(ReceiptTracking.STATUS_UNRECOVERABLE_ERROR, refToMessageInError);
            AS4ReliabilityUtil.removeAttachedFiles(refToMessageInError);
        }

        return InvocationResponse.CONTINUE;
    }
}