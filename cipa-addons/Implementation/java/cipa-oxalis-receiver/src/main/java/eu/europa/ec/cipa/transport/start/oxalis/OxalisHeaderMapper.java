package eu.europa.ec.cipa.transport.start.oxalis;

import eu.europa.ec.cipa.transport.IMessageMetadata;
import eu.peppol.start.identifier.ChannelId;
import eu.peppol.start.identifier.MessageId;
import eu.peppol.start.identifier.ParticipantId;
import eu.peppol.start.identifier.PeppolDocumentTypeId;
import eu.peppol.start.identifier.PeppolMessageHeader;
import eu.peppol.start.identifier.PeppolProcessTypeId;

public class OxalisHeaderMapper {

		public static PeppolMessageHeader mapHeader (IMessageMetadata header){
			PeppolMessageHeader oxalisHeader = new PeppolMessageHeader();
			oxalisHeader.setChannelId(new ChannelId(header.getChannelID()));
			oxalisHeader.setDocumentTypeIdentifier(PeppolDocumentTypeId.valueOf(header.getDocumentTypeID().getValue()));
			oxalisHeader.setMessageId(new MessageId(header.getMessageID()));
			oxalisHeader.setPeppolProcessTypeId(PeppolProcessTypeId.valueOf( header.getProcessID().getValue()));
			oxalisHeader.setRecipientId(new ParticipantId(header.getRecipientID().getValue()));
			oxalisHeader.setSenderId(new ParticipantId(header.getSenderID().getValue()));
			return oxalisHeader;
		}
}
