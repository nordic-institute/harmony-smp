package eu.europa.ec.digit.domibus.client.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.europa.ec.digit.domibus.client.AbstractTestClient;
import eu.europa.ec.digit.domibus.common.aggregate.components.Acknowledgement;
import eu.europa.ec.digit.domibus.common.aggregate.components.AddressInfoType;
import eu.europa.ec.digit.domibus.common.aggregate.components.GatewayBody;
import eu.europa.ec.digit.domibus.common.aggregate.components.GatewayHeader;
import eu.europa.ec.digit.domibus.common.aggregate.components.MessageContentType;
import eu.europa.ec.digit.domibus.common.aggregate.components.MessageInfoType;
import eu.europa.ec.digit.domibus.common.aggregate.components.ReceiverType;
import eu.europa.ec.digit.domibus.common.aggregate.components.SenderType;


public class GatewayBasicEndpointTest extends AbstractTestClient {

	/* ---- Constants ---- */
	public final static String CORRELATION_ID = "COR-12345";
	public final static String MESSAGE_ID = "MES_12345";

	/* ---- Instance Variables ---- */

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */
	@Test
	public void testGatewayBasicEndpoint() throws Exception {

		GatewayHeader header = new GatewayHeader();
		header.setMessageInfo(messageInfo());
		header.setAddressInfo(addressInfo());
		GatewayBody body = new GatewayBody();
		MessageContentType messageContent = new MessageContentType();
		//String message = "<message>Eureka</message>";
		messageContent.setAny(getMessage());

		body.setMessageContent(messageContent);
		Acknowledgement ack = getDomibusBasicInterface().submit(header, body);

		assertNotNull(ack);
		assertEquals(ack.getMessageInfo().getCorrelationId(), CORRELATION_ID);

	}

	private Element getMessage() throws Exception {
		String message = "<message><abc>Eureka!</abc></message>";
		InputStream stream  = new ByteArrayInputStream(message.getBytes(Charset.forName("UTF-8")));
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(false);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(stream);
		return document.getDocumentElement();
	}

	private MessageInfoType messageInfo() {
		MessageInfoType messageInfo = new MessageInfoType();
		messageInfo.setCorrelationId(CORRELATION_ID);
		return messageInfo;
	}

	private AddressInfoType addressInfo() {
		AddressInfoType addressInfo = new AddressInfoType();
		SenderType sender = new SenderType();
		sender.setId("instanceAId1");
		addressInfo.setSender(sender);
		ReceiverType receiver = new ReceiverType();
		receiver.setId("instanceBId1");
		addressInfo.setReceiver(receiver);
		return addressInfo;

	}

	/* ---- Getters and Setters ---- */

}
