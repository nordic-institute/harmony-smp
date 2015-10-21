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
package eu.europa.ec.cipa.sml.server.dns.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.xbill.DNS.Record;

import com.helger.commons.string.ToStringGenerator;

import eu.europa.ec.cipa.sml.server.dns.ISMLDNSClient;

/**
 * Dummy implementation..
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class DoNothingDNSClient implements ISMLDNSClient {
  public void createIdentifier (final ParticipantIdentifierType pi, final String sSMPID) {
    // Do nothing...
  }

  public void createIdentifiers (final List <ParticipantIdentifierType> list, final String sSMPID) {
    // Do nothing...
  }

  public void deleteIdentifier (final ParticipantIdentifierType pi) {
    // Do nothing...
  }

  public void deleteIdentifiers (final List <ParticipantIdentifierType> list) {
    // Do nothing...
  }

  public void createPublisherAnchor (final String sSMPID, final String host) {
    // Do nothing...
  }

  public void deletePublisherAnchor (final String sSMPID) {
    // Do nothing...
  }

  @Nullable
  public String lookupDNSRecord (final String dnsName) {
    return null;
  }

  @Nullable
  public String lookupPeppolPublisherById (final String publisherId) {
    return null;
  }

  @Nonnull
  public List <Record> getAllRecords () {
    return new ArrayList <Record> ();
  }

  @Nullable
  public String getDNSZoneName () {
    return null;
  }

  @Nullable
  public String getSMLZoneName () {
    return null;
  }

  @Nullable
  public String getServer () {
    return null;
  }

  @Nullable
  public ParticipantIdentifierType getIdentifierFromDnsName (final String name) {
    return null;
  }

  @Nullable
  public String getDNSNameOfParticipant (final ParticipantIdentifierType pi) {
    return null;
  }

  @Nullable
  public String getPublisherAnchorFromDnsName (final String name) {
    return null;
  }

  public boolean isHandledZone (final String name) {
    return false;
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).toString ();
  }
}
