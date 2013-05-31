/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
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
