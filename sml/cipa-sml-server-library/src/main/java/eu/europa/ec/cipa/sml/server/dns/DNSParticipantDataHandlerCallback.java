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
package eu.europa.ec.cipa.sml.server.dns;

import java.io.IOException;
import java.util.List;

import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.cipa.sml.server.IParticipantDataHandlerCallback;
import eu.europa.ec.cipa.sml.server.exceptions.BadRequestException;
import eu.europa.ec.cipa.sml.server.exceptions.DNSErrorException;
import eu.europa.ec.cipa.sml.server.exceptions.IllegalHostnameException;
import eu.europa.ec.cipa.sml.server.exceptions.IllegalIdentifierSchemeException;
import eu.europa.ec.cipa.sml.server.exceptions.InternalErrorException;

/**
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class DNSParticipantDataHandlerCallback implements IParticipantDataHandlerCallback {
  private static final Logger s_aLogger = LoggerFactory.getLogger (DNSParticipantDataHandlerCallback.class);

  public void identifiersCreated (final ParticipantIdentifierPageType identifiers) throws BadRequestException,
                                                                                  InternalErrorException {
    try {
    	long timer = System.currentTimeMillis();
      s_aLogger.info ("Start identifier creation in DNS");
      final List <ParticipantIdentifierType> aPIs = identifiers.getParticipantIdentifier ();
      final String sSMPID = identifiers.getServiceMetadataPublisherID ();
      DNSClientFactory.getInstance ().createIdentifiers (aPIs, sSMPID);
      s_aLogger.info ("Identifiers created in DNS" +(System.currentTimeMillis() - timer) +" ms");
      s_aLogger.info ("DNS Linked Participants " + aPIs + " to " + sSMPID);
    }
    catch (final IOException e) {
      s_aLogger.error ("DNSClient failed to create BusinessIdentifier", e);
      throw new DNSErrorException (e);
    }
    catch (final IllegalIdentifierSchemeException e) {
      s_aLogger.error ("Illegal Identifier : ", e);
      throw new BadRequestException (e.getMessage ());
    }
    catch (final IllegalHostnameException e) {
      s_aLogger.error ("Illegal PublisherID : ", e);
      throw new BadRequestException (e.getMessage ());
    }
  }

  public void identifiersDeleted (final List <ParticipantIdentifierType> aPIs) throws BadRequestException,
                                                                              InternalErrorException {
    try {
      DNSClientFactory.getInstance ().deleteIdentifiers (aPIs);
      s_aLogger.info ("DNS Deleted Participants " + aPIs);
    }
    catch (final IOException e) {
      s_aLogger.error ("DNSClient Failed to delete BusinessIdentifier", e);
      throw new InternalErrorException ("DNSClient Failed to delete BusinessIdentifier", e);
    }
    catch (final IllegalIdentifierSchemeException e) {
      s_aLogger.error ("Illegal Identifier : ", e);
      throw new BadRequestException (e.getMessage ());
    }
  }
}
