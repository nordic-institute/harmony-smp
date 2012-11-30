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
package eu.europa.ec.cipa.sml.server;

import java.util.List;

import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.servicemetadata.locator._1.PublisherEndpointType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;

import eu.europa.ec.cipa.busdox.identifier.IReadonlyParticipantIdentifier;
import eu.europa.ec.cipa.sml.server.exceptions.InternalErrorException;
import eu.europa.ec.cipa.sml.server.exceptions.NotFoundException;
import eu.europa.ec.cipa.sml.server.exceptions.UnauthorizedException;
import eu.europa.ec.cipa.sml.server.exceptions.UnknownUserException;

/**
 * This interface is used by the web service to access the underlying data.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public interface IGenericDataHandler extends IRequestAuthenticationHandler {
  ServiceMetadataPublisherServiceType getSMPDataOfParticipant (IReadonlyParticipantIdentifier recipientBusinessIdentifier) throws NotFoundException,
                                                                                                                          InternalErrorException;

  /*
   * Utility methods used by the DNS scheme
   */
  PublisherEndpointType getSMPEndpointAddressOfSMPID (String sSMPID) throws InternalErrorException, NotFoundException;

  List <String> getAllSMPIDs () throws InternalErrorException;

  ParticipantIdentifierPageType listParticipantIdentifiers (String pageId, String sSMPID) throws NotFoundException,
                                                                                         UnauthorizedException,
                                                                                         UnknownUserException,
                                                                                         InternalErrorException;
}
