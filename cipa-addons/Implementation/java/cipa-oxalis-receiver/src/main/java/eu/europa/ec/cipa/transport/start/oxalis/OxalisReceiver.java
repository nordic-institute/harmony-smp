package eu.europa.ec.cipa.transport.start.oxalis;

import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2009._02.ws_tra.Create;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.lang.ServiceLoaderUtils;
import com.phloc.commons.state.impl.SuccessWithValue;

import eu.europa.ec.cipa.transport.IMessageMetadata;
import eu.europa.ec.cipa.transport.start.server.AccessPointReceiveError;
import eu.europa.ec.cipa.transport.start.server.IAccessPointServiceReceiverSPI;
import eu.peppol.start.persistence.MessageRepository;

public class OxalisReceiver implements IAccessPointServiceReceiverSPI {

  private static final Logger s_aLogger = LoggerFactory.getLogger (OxalisReceiver.class);
  private static final String INIT_PARAMETER_REAL_PATH = "userfolder";

  private static final List <MessageRepository> oxalisReceivers;

  static {
    oxalisReceivers = ContainerHelper.newUnmodifiableList (ServiceLoaderUtils.getAllSPIImplementations (MessageRepository.class));
    if (oxalisReceivers.isEmpty ()) {
      s_aLogger.error ("No implementation of the Oxalis Message repository" +
                       MessageRepository.class +
                       " found! Incoming documents will be discarded!");
    }
  }

  @Override
  public SuccessWithValue <AccessPointReceiveError> receiveDocument (final WebServiceContext aWebServiceContext,
                                                                     final IMessageMetadata aMetadata,
                                                                     final Create aBody) {

    System.out.println ("Receiving document for Oxalis");
    final AccessPointReceiveError aErrs = new AccessPointReceiveError ();
    final List <Object> objects = aBody.getAny ();
    if (ContainerHelper.getSize (objects) == 1) {
      // It must be an Element
      final Element aMessageElement = (Element) objects.iterator ().next ();

      // Get the surrounding XML DOM document
      final Document aMessageDocument = aMessageElement.getOwnerDocument ();

      // Get ServletContext for later real path determination
      final ServletContext aServletContext = (ServletContext) aWebServiceContext.getMessageContext ()
                                                                                .get (MessageContext.SERVLET_CONTEXT);
      final String sRealPath = aServletContext.getInitParameter (INIT_PARAMETER_REAL_PATH);

      try {
        for (final MessageRepository repo : oxalisReceivers) {
          repo.saveInboundMessage (sRealPath, OxalisHeaderMapper.mapHeader (aMetadata), aMessageDocument);
        }
        return SuccessWithValue.createSuccess (aErrs);
      }
      catch (final Exception ex) {
        aErrs.error ("Failed to save document", ex);
        s_aLogger.error ("Failed to save document", ex);
      }
    }
    else {
      aErrs.error ("The received message contains more than one element!");
      s_aLogger.error ("The received message contains more than one element!");
    }

    return SuccessWithValue.createFailure (aErrs);
  }

}
