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

import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.xbill.DNS.Record;
import org.xbill.DNS.ZoneTransferException;

import eu.europa.ec.cipa.sml.server.exceptions.IllegalHostnameException;
import eu.europa.ec.cipa.sml.server.exceptions.IllegalIdentifierSchemeException;


/**
 * Interface for DNSClient used by ServiceMetadataLocator to maintain Publisher
 * hosts in DNS. The interface contains both a Factory and a Dummy
 * implementation.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public interface IDNSClient {
  /**
   * Administrative Zone configured for DNSClient.
   * 
   * @return zonename
   */
  String getDNSZoneName ();

  /**
   * SML Zone name configured used by Identifiers for DNSClient. Will be
   * prefixed on Zone
   * 
   * @return zonename
   */
  String getSMLZoneName ();

  /**
   * DNS Server handling Publisher hosts.
   * 
   * @return server
   */
  String getServer ();

  /**
   * Create an 'anchor'/CNAME for a Publisher which points to the host/server
   * running the MetadataPublisher. ParticipantIdentifier Registrations for the
   * Publisher will point to this 'anchor'
   * 
   * @param smpId
   *        unique id for Publisher
   * @param host
   *        hostname/server for publisher
   * @throws IOException
   * @throws IllegalHostnameException
   */
  void createPublisherAnchor (String smpId, String host) throws IOException, IllegalHostnameException;

  /**
   * Delete the 'anchor'/CNAME for a Publisher which points to the host/server
   * running the MetadataPublisher.
   * 
   * @param smpId
   *        unique id for Publisher
   * @throws IOException
   */
  void deletePublisherAnchor (String smpId) throws IOException;

  /**
   * Creates a DNS entry/CNAME for a ParticipantIdentifier pointing to the
   * 'anchor'/CNAME for the Publisher.
   * 
   * @param pi
   *        ParticipantIdentifier
   * @param smpId
   *        unique id for Publisher
   * @throws IOException
   * @throws IllegalIdentifierSchemeException
   * @throws IllegalHostnameException
   */
  void createIdentifier (ParticipantIdentifierType pi, String smpId) throws IOException,
                                                                    IllegalIdentifierSchemeException,
                                                                    IllegalHostnameException;

  /**
   * Creates a list of : DNS entry/CNAME for a ParticipantIdentifier.
   * 
   * @param list
   *        List of {@link ParticipantIdentifierType}
   * @throws IOException
   * @throws IllegalIdentifierSchemeException
   * @throws IllegalHostnameException
   */
  void createIdentifiers (List <ParticipantIdentifierType> list, String smpId) throws IOException,
                                                                              IllegalIdentifierSchemeException,
                                                                              IllegalHostnameException;

  /**
   * Deletes a DNS entry/CNAME for a ParticipantIdentifier.
   * 
   * @param pi
   *        {@link ParticipantIdentifierType}
   * @throws IOException
   * @throws IllegalIdentifierSchemeException
   */
  void deleteIdentifier (ParticipantIdentifierType pi) throws IOException, IllegalIdentifierSchemeException;

  /**
   * Deletes a list of : DNS entry/CNAME for a ParticipantIdentifier.
   * 
   * @param list
   *        list of {@link ParticipantIdentifierType}
   * @throws IOException
   * @throws IllegalIdentifierSchemeException
   */
  void deleteIdentifiers (List <ParticipantIdentifierType> list) throws IOException, IllegalIdentifierSchemeException;

  /**
   * Resolves a DNS Hostname.
   * 
   * @param dnsName
   *        name to resolve
   * @return host
   * @throws IOException
   */
  String lookupDNSRecord (String dnsName) throws IOException;

  /**
   * Run Zone Transfer and list all records.
   * 
   * @return List<org.xbill.DNS.Record>
   * @throws IOException
   * @throws ZoneTransferException
   */
  List <Record> getAllRecords () throws IOException, ZoneTransferException;

  /**
   * Find host/server registration for Publisher.
   * 
   * @param dnsPublisher
   * @return host
   * @throws IOException
   */
  String lookupPeppolPublisherById (String dnsPublisher) throws IOException;

  /**
   * Create ParticipantIdentifier from DNS Name.
   * 
   * @param name
   *        DNS Name
   * @return ParticipantIdentifier
   */
  ParticipantIdentifierType getIdentifierFromDnsName (String name);

  /**
   * Create DNS Name from ParticipantIdentifier
   * 
   * @param pi
   *        ParticipantIdentifier
   * @return DNS Name
   * @throws IllegalIdentifierSchemeException
   */
  String getDNSNameOfParticipant (ParticipantIdentifierType pi) throws IllegalIdentifierSchemeException;

  /**
   * Extract Publisher Anchor from DNS Name.
   * 
   * @param name
   * @return Publisher Anchor
   */
  String getPublisherAnchorFromDnsName (String name);

  /**
   * Checks if name is handled.
   * 
   * @param name
   * @return true or false
   */
  boolean isHandledZone (String name);
}
