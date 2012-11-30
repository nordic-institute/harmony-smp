package eu.europa.ec.cipa.sml.server.management;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FaultHandler implements javax.xml.ws.handler.soap.SOAPHandler <SOAPMessageContext> {
  private static final Logger s_aLogger = LoggerFactory.getLogger (AddSignatureHandler.class);

  @Override
  public void close (final MessageContext context) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean handleFault (final SOAPMessageContext context) {
    final Boolean aOutbound = (Boolean) context.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);
    System.out.println ("Entered the soap handler" + aOutbound);
    s_aLogger.info ("Converting " + aOutbound + " to DOMSource");
    if (aOutbound != null && aOutbound.booleanValue ()) {
      final SOAPMessage sm = context.getMessage ();
      SOAPFault fault;
      try {
        fault = sm.getSOAPBody ().getFault ();
        System.out.println (fault.getFaultCode ());
        System.out.println (fault.getFaultString ());
      }
      catch (final SOAPException e) {
        // TODO Auto-generated catch block
        e.printStackTrace ();
      }

    }
    return true;
  }

  @Override
  public boolean handleMessage (final SOAPMessageContext context) {
    return true;
  }

  @Override
  public Set <QName> getHeaders () {
    // TODO Auto-generated method stub
    return null;
  }
}
