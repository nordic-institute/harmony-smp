package eu.europa.ec.cipa.sml.server.management;

import java.io.StringWriter;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FaultHandler implements  javax.xml.ws.handler.soap.SOAPHandler<SOAPMessageContext> {
	
	private static final Logger s_aLogger = LoggerFactory.getLogger (AddSignatureHandler.class);
	
	@Override
	public void close(MessageContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		 Boolean outbound = (Boolean)
	      context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		 System.out.println("Entered the soap handler" +outbound);
		 s_aLogger.info ("Converting " + outbound + " to DOMSource");
		if (outbound){
		
			SOAPMessage sm= context.getMessage();
	         SOAPFault fault;
			try {
				fault = sm.getSOAPBody().getFault();
				System.out.println(fault.getFaultCode());
				System.out.println(fault.getFaultString());
			} catch (SOAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
		}
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		return true;
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

}
