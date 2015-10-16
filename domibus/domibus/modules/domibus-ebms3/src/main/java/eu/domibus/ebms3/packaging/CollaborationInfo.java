package eu.domibus.ebms3.packaging;

import eu.domibus.common.soap.Element;
import eu.domibus.ebms3.config.PMode;
import eu.domibus.ebms3.module.Constants;
import org.apache.axiom.util.UIDGenerator;

/**
 * @author Hamid Ben Malek
 */
public class CollaborationInfo extends Element {
    private static final long serialVersionUID = -6434646562915911782L;

    public CollaborationInfo(final String agreementRef, final String pModeId, final String service,
                             final String serviceType, final String action, final String conversationId) {
        super(Constants.COLLABORATION_INFO, Constants.NS, Constants.PREFIX);
        if ((agreementRef != null) && !"".equals(agreementRef.trim())) {
            final Element agr = this.addElement(Constants.AGREEMENT_REF, Constants.PREFIX);
            agr.setText(agreementRef);
            if ((pModeId != null) && !"".equals(pModeId.trim())) {
                final PMode pmode = Constants.pmodes.get(pModeId);
                if ((pmode == null) || pmode.getExplicit()) {
                    agr.addAttribute("pmode", pModeId);
                }
            }
        }
        if ((service != null) && !"".equals(service.trim())) {
            final Element srv = this.addElement(Constants.SERVICE, Constants.PREFIX);
            if ((serviceType != null) && !"".equals(serviceType.trim())) {
                srv.setText(service);
                srv.addAttribute("type", serviceType);
            } else {
                srv.setText(service);
                srv.setAttribute("type", Constants.ECODEX_SERVICE_URI_VALUE);
            }
        }
        if ((action != null) && !"".equals(action.trim())) {
            final Element act = this.addElement(Constants.ACTION, Constants.PREFIX);
            act.setText(action);
        }
        final Element conv = this.addElement(Constants.CONVERSATION_ID, Constants.PREFIX);
        if ((conversationId != null) && !"".equals(conversationId.trim())) {
            conv.setText(conversationId);
        } else {
            conv.setText(UIDGenerator.generateURNString());
        }
    }

    public String getAgreementRef() {
        return this.getGrandChildValue(Constants.AGREEMENT_REF);
    }

    public String getAction() {
        return this.getGrandChildValue(Constants.ACTION);
    }

    public String getConversationID() {
        return this.getGrandChildValue(Constants.CONVERSATION_ID);
    }

    public String getService() {
        return this.getGrandChildValue(Constants.SERVICE);
    }
}