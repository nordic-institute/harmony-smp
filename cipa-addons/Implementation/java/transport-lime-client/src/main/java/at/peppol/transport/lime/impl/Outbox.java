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
package at.peppol.transport.lime.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2009._02.ws_tra.Create;
import org.w3._2009._02.ws_tra.CreateResponse;
import org.w3._2009._02.ws_tra.Put;
import org.w3._2009._02.ws_tra.Resource;
import org.w3._2009._02.ws_tra.ResourceCreated;
import org.w3c.dom.Element;

import at.peppol.commons.utils.IReadonlyUsernamePWCredentials;
import at.peppol.commons.wsaddr.W3CEndpointReferenceUtils;
import at.peppol.transport.IMessageMetadata;
import at.peppol.transport.MessageMetadata;
import at.peppol.transport.MessageMetadataHelper;
import at.peppol.transport.lime.CLimeIdentifiers;
import at.peppol.transport.lime.IEndpointReference;
import at.peppol.transport.lime.IMessage;
import at.peppol.transport.lime.IOutbox;
import at.peppol.transport.lime.MessageException;

import com.phloc.commons.string.StringHelper;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.developer.WSBindingProvider;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class Outbox implements IOutbox {
  private static final Logger s_aLogger = LoggerFactory.getLogger (Outbox.class);

  private static void _validateCredentials (@Nonnull final IReadonlyUsernamePWCredentials credentials) throws MessageException {
    if (credentials == null)
      throw new MessageException ("Credentials can not be a null value");

    if (StringHelper.hasNoTextAfterTrim (credentials.getUsername ()) ||
        StringHelper.hasNoTextAfterTrim (credentials.getPassword ())) {
      throw new MessageException ("Credentials are invalid, username=" +
                                  credentials.getUsername () +
                                  " password=" +
                                  credentials.getPassword ());
    }
  }

  @Nonnull
  private static EndpointReferenceWithMessageID _createEndpointReferenceDocument (final CreateResponse createResponse) {
    ResourceCreated resourceCreated = (ResourceCreated) createResponse.getAny ();
    if (resourceCreated == null) {
      resourceCreated = createResponse.getResourceCreated ();
      if (resourceCreated == null)
        throw new IllegalStateException ("No content of create response!");
    }

    final W3CEndpointReference aEndpointReference = resourceCreated.getEndpointReference ().get (0);

    // Extract address, channel ID and message ID
    final EndpointReferenceWithMessageID ret = new EndpointReferenceWithMessageID ();
    ret.setAddress (W3CEndpointReferenceUtils.getAddress (aEndpointReference));
    for (final Element e : W3CEndpointReferenceUtils.getReferenceParameters (aEndpointReference)) {
      if (CLimeIdentifiers.CHANNELID.equals (e.getLocalName ()))
        ret.setChannelID (e.getTextContent ());
      else
        if (CLimeIdentifiers.MESSAGEID.equals (e.getLocalName ()))
          ret.setMessageID (e.getTextContent ());
        else
          s_aLogger.warn ("EndpointReference contains illegal element " + e.getLocalName ());
    }
    return ret;
  }

  /*
   * Send a new message and return the created message ID
   */
  public String sendMessage (final IReadonlyUsernamePWCredentials aCredentials,
                             final IMessage aMessage,
                             final IEndpointReference aEndpointReference) throws MessageException {
    _validateCredentials (aCredentials);

    try {
      // Create metadata (everything except messageID)
      final IMessageMetadata aMetadata = new MessageMetadata (null,
                                                              aEndpointReference.getChannelID (),
                                                              aMessage.getSender (),
                                                              aMessage.getReceiver (),
                                                              aMessage.getDocumentType (),
                                                              aMessage.getProcessType () != null
                                                                                                ? aMessage.getProcessType ()
                                                                                                : CLimeIdentifiers.MESSAGEUNDELIVERABLE_PROCESS);

      // Create "create" port
      Resource aPort = LimeHelper.createServicePort (aEndpointReference.getAddress (), aCredentials);
      List <Header> aHeaders = MessageMetadataHelper.createHeadersFromMetadata (aMetadata);
      ((WSBindingProvider) aPort).setOutboundHeaders (aHeaders);

      // Perform "create" action
      final CreateResponse createResponse = aPort.create (new Create ());

      // Evaluate "create" response
      final EndpointReferenceWithMessageID aEndpointDoc = _createEndpointReferenceDocument (createResponse);

      // Create "put" port
      aPort = LimeHelper.createServicePort (aEndpointDoc.getAddress (), aCredentials);
      aHeaders = MessageMetadataHelper.createHeadersFromMetadata (aEndpointDoc);
      ((WSBindingProvider) aPort).setOutboundHeaders (aHeaders);

      // Perform "put" action (no real response expected)
      final Put put = new Put ();
      put.getAny ().add (aMessage.getDocument ().getDocumentElement ());
      aPort.put (put);

      return aEndpointDoc.getMessageID ();
    }
    catch (final Exception e) {
      s_aLogger.warn ("Outbox error", e);
      throw new MessageException (e);
    }
  }
}
