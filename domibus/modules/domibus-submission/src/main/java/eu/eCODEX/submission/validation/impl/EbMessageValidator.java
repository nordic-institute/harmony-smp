package eu.eCODEX.submission.validation.impl;

import eu.domibus.ebms3.persistent.PartInfo;
import eu.domibus.ebms3.persistent.ReceivedUserMsg;
import eu.domibus.ebms3.persistent.UserMsgToPush;
import eu.domibus.ebms3.submit.EbMessage;
import eu.eCODEX.submission.validation.Validator;
import eu.eCODEX.submission.validation.exception.ValidationException;

public class EbMessageValidator implements Validator<EbMessage> {

	@Override
	public void validate(EbMessage message) throws ValidationException {

		if(message instanceof ReceivedUserMsg){
			ReceivedUserMsg msg = (ReceivedUserMsg) message;
			if(msg.getRawXMLMessage()==null)
				throw new ValidationException ("There isn't a XML associated with the EbMessage message");
			if(msg.getMsgInfo()==null)
				throw new ValidationException ("There isn't a message info set associated with the EbMessage message");
			if(msg.getMessageContext()==null)
				throw new ValidationException ("There isn't an Axis message context associated with the EbMessage message");
			if(msg.getMsgInfo().getParts()==null || msg.getMsgInfo().getParts().size()<1)
				throw new ValidationException ("There isn't a message info set associated with the EbMessage message");

			boolean bodyLoad = false;
			for (final PartInfo p : msg.getMsgInfo().getParts()) {
	            if (p.isBody()) {
	            	bodyLoad = true;
	            	break;
	            } 
	        }
			if(!bodyLoad)
				throw new ValidationException ("There isn't a bodyLoad associated with the EbMessage message");
			
			for (final PartInfo p : msg.getMsgInfo().getParts()) {
				if(p.getPayloadData()==null)
					throw new ValidationException ("No payload data for the Part Info");
	        }
			
			if(msg.getMsgInfo().getToRole()==null || 
					msg.getMsgInfo().getFromRole()==null || msg.getMsgInfo().getService()==null
					|| msg.getMsgInfo().getAction()==null || msg.getMsgInfo().getMpc()==null)
				throw new ValidationException ("No Party Info associated with EbMessage");
		}
		else if(message instanceof UserMsgToPush){
			UserMsgToPush msg = (UserMsgToPush) message;
			if(msg.getMsgInfoSet()==null)
				throw new ValidationException ("There isn't a message info set associated with the EbMessage message");
			if(msg.getMessageContext()==null)
				throw new ValidationException ("There isn't an Axis message context associated with the EbMessage message");
			if(msg.getId()==null || msg.getMsgInfoSet().getMessageId()==null)
				throw new ValidationException ("There aren't identifiers assigned to the EbMessage message");
			if(msg.getPmode()==null)
				throw new ValidationException ("There aren't identifiers assigned to the EbMessage message");
			else if(msg.getMep()==null)
				throw new ValidationException ("The isn't MEP for the PMode assigned to the EbMessage message");
			if(msg.getMsgInfoSet().getBodyPayload()==null)
				throw new ValidationException ("The EbMessage message hasn't Bodyload");
			if(msg.getMsgInfoSet().getProducer()==null || msg.getMsgInfoSet().getFromParties()==null)
				throw new ValidationException ("No From party info associated with EbMessage");	
		}
	}
}
