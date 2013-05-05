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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.busdox.servicemetadata.locator._1.PublisherEndpointType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.cipa.sml.server.ISMPDataHandlerCallback;
import eu.europa.ec.cipa.sml.server.exceptions.BadRequestException;
import eu.europa.ec.cipa.sml.server.exceptions.DNSErrorException;
import eu.europa.ec.cipa.sml.server.exceptions.IllegalHostnameException;
import eu.europa.ec.cipa.sml.server.exceptions.IllegalIdentifierSchemeException;
import eu.europa.ec.cipa.sml.server.exceptions.InternalErrorException;


/**
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class DNSSMPDataHandlerCallback implements ISMPDataHandlerCallback {
  private static final Logger s_aLogger = LoggerFactory.getLogger (DNSSMPDataHandlerCallback.class);

  public void serviceMetadataCreated (final ServiceMetadataPublisherServiceType registryService) throws BadRequestException,
                                                                                                InternalErrorException {
    final PublisherEndpointType aJAXBEndpoint = registryService.getPublisherEndpoint ();
    final String sSMPID = registryService.getServiceMetadataPublisherID ();
    final String sLogicalAddress = aJAXBEndpoint.getLogicalAddress ();
    try {
      final URL url = new URL (sLogicalAddress);
      DNSClientFactory.getInstance ().createPublisherAnchor (sSMPID, url.getHost ());
      s_aLogger.info ("DNS Created SMP " + sSMPID + " pointing to " + url.getHost ());
    }
    catch (final MalformedURLException e) {
      s_aLogger.warn ("PublisherEndpoint does not have a valid host : " + sLogicalAddress + " for id " + sSMPID);
      throw new BadRequestException ("PublisherEndpoint does not have a valid host : " + sLogicalAddress);
    }
    catch (final IllegalHostnameException e) {
      s_aLogger.error ("Illegal hostname : ", e);
      throw new BadRequestException (e.getMessage ());
    }
    catch (final IOException e) {
      s_aLogger.error ("DNSClient Failed to create MetadataPublisher : " + sSMPID, e);
      throw new DNSErrorException (e);
    }
  }

  public void serviceMetadataUpdated (final ServiceMetadataPublisherServiceType aSMPData) throws BadRequestException,
                                                                                         InternalErrorException {
    // reuse 'serviceMetadataCreated'
    serviceMetadataCreated (aSMPData);
  }

  public void serviceMetadataDeleted (final String sSMPID,
                                      final Collection <ParticipantIdentifierType> aParticipantIdentifiers) throws InternalErrorException,
                                                                                                           BadRequestException {
    for (final ParticipantIdentifierType aParticipantIdentifier : aParticipantIdentifiers) {
      try {
        DNSClientFactory.getInstance ().deleteIdentifier (aParticipantIdentifier);
        s_aLogger.info ("DNS Deleted Participant " + aParticipantIdentifier);
      }
      catch (final IOException e) {
        s_aLogger.error ("DNSClient Failed to bulk delete BusinessIdentifiers for Publisher : " + sSMPID, e);
      }
      catch (final IllegalIdentifierSchemeException e) {
        s_aLogger.error ("Illegal Identifier : ", e);
        throw new BadRequestException (e.getMessage ());
      }
    }

    try {
      DNSClientFactory.getInstance ().deletePublisherAnchor (sSMPID);
      s_aLogger.info ("DNS Deleted SMP " + sSMPID);
    }
    catch (final IOException e) {
      s_aLogger.error ("DNSClient Failed to delete MetadataPublisher : " + sSMPID, e);
      throw new DNSErrorException (e);
    }
  }
}
