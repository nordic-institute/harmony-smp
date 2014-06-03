package eu.domibus.ebms3.packaging;

import org.apache.axiom.util.UIDGenerator;
import eu.domibus.common.soap.Element;
import eu.domibus.ebms3.config.PMode;
import eu.domibus.ebms3.module.Constants;

/**
 * @author Hamid Ben Malek
 */
public class CollaborationInfo extends Element {
    private static final long serialVersionUID = -6434646562915911782L;

    public CollaborationInfo(final String agreementRef, final String pModeId, final String service,
                             final String serviceType, final String action, final String conversationId) {
        super(Constants.COLLABORATION_INFO, Constants.NS, Constants.PREFIX);
        if (agreementRef != null && !agreementRef.trim().equals("")) {
            final Element agr = addElement(Constants.AGREEMENT_REF, Constants.PREFIX);
            agr.setText(agreementRef);
            if (pModeId != null && !pModeId.trim().equals("")) {
                final PMode pmode = Constants.pmodes.get(pModeId);
                if (pmode == null || pmode.getExplicit()) {
                    agr.addAttribute("pmode", pModeId);
                }
            }
        }
        if (service != null && !service.trim().equals("")) {
            final Element srv = addElement(Constants.SERVICE, Constants.PREFIX);
            if (serviceType != null && !serviceType.trim().equals("")) {
                srv.setText(service);
                srv.addAttribute("type", serviceType);
            } else {
                srv.setText(service);
                srv.setAttribute("type", Constants.ECODEX_SERVICE_URI_VALUE);
            }
        }
        if (action != null && !action.trim().equals("")) {
            final Element act = addElement(Constants.ACTION, Constants.PREFIX);
            act.setText(action);
        }
        final Element conv = addElement(Constants.CONVERSATION_ID, Constants.PREFIX);
        if (conversationId != null && !conversationId.trim().equals("")) {
            conv.setText(conversationId);
        } else {
            conv.setText(UIDGenerator.generateURNString());
        }
    }

    public String getAgreementRef() {
        return getGrandChildValue(Constants.AGREEMENT_REF);
    }

    public String getAction() {
        return getGrandChildValue(Constants.ACTION);
    }

    public String getConversationID() {
        return getGrandChildValue(Constants.CONVERSATION_ID);
    }

    public String getService() {
        return getGrandChildValue(Constants.SERVICE);
    }
}