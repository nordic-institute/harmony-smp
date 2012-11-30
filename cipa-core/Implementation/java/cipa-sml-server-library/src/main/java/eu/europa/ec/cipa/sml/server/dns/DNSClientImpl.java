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
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.A6Record;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Address;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;
import org.xbill.DNS.Update;
import org.xbill.DNS.ZoneTransferException;
import org.xbill.DNS.ZoneTransferIn;

import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;

import eu.europa.ec.cipa.peppol.sml.CSMLDefault;
import eu.europa.ec.cipa.peppol.uri.BusdoxURLUtils;
import eu.europa.ec.cipa.sml.server.exceptions.DNSErrorException;
import eu.europa.ec.cipa.sml.server.exceptions.IllegalHostnameException;
import eu.europa.ec.cipa.sml.server.exceptions.IllegalIdentifierSchemeException;

/**
 * DNSClient for dynamic updating DNS Server from ServiceMetadataLocator.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public class DNSClientImpl implements IDNSClient {
  // Time to live
  public static final int DEFAULT_TTL_SECS = 60;
  private static final Logger log = LoggerFactory.getLogger (DNSClientImpl.class);

  private final String m_sServerName;
  private String m_sDNSZoneName;
  private String m_sSMLZoneName;
  private final int m_nTTLSecs;

  public DNSClientImpl (@Nonnull final String sServerName,
                        @Nonnull final String sDNSZoneName,
                        @Nonnull final String sSMLZoneName,
                        @Nonnegative final int nTTL) {
    if (StringHelper.hasNoText (sServerName))
      throw new IllegalArgumentException ("serverName may not be empty");
    if (StringHelper.hasNoText (sDNSZoneName))
      throw new IllegalArgumentException ("DNSZoneName may not be empty");
    if (StringHelper.hasNoText (sSMLZoneName))
      throw new IllegalArgumentException ("SMLZoneName may not be empty");
    if (nTTL < 0)
      throw new IllegalArgumentException ("TTL is invalid: " + nTTL);

    log.info ("DnsClientImpl init : " + sServerName + " : " + sDNSZoneName + " : " + sSMLZoneName + " : " + nTTL);
    m_sServerName = sServerName;
    m_sDNSZoneName = sDNSZoneName.toLowerCase ();
    if (!m_sDNSZoneName.endsWith (".")) {
      m_sDNSZoneName += ".";
    }

    final String sSMLZoneNameLC = sSMLZoneName.toLowerCase ();
    if (sSMLZoneNameLC.equals (m_sDNSZoneName))
      m_sSMLZoneName = sSMLZoneNameLC;
    else
      if (sSMLZoneNameLC.endsWith ("." + m_sDNSZoneName))
        m_sSMLZoneName = sSMLZoneNameLC;
      else
        if (sSMLZoneNameLC.endsWith ("."))
          m_sSMLZoneName = sSMLZoneNameLC + m_sDNSZoneName;
        else
          m_sSMLZoneName = sSMLZoneNameLC + "." + m_sDNSZoneName;

    m_nTTLSecs = nTTL;

    log.info ("DnsClientImpl init done - DNS Zone : " + m_sDNSZoneName + " - SML Zone : " + m_sSMLZoneName);
  }

  @Nonnull
  protected Resolver getResolver () throws IOException {
    return new SimpleResolver (m_sServerName);
  }

  @Nonnull
  public String getDNSZoneName () {
    return m_sDNSZoneName;
  }

  @Nonnull
  public String getSMLZoneName () {
    return m_sSMLZoneName;
  }

  @Nonnull
  public String getServer () {
    return m_sServerName;
  }

  public void createIdentifier (final ParticipantIdentifierType aParticipantIdentifier, final String sSMPID) throws IOException,
                                                                                                            IllegalIdentifierSchemeException,
                                                                                                            IllegalHostnameException {
    if (log.isDebugEnabled ())
      log.debug ("Create Identifier : " +
                 aParticipantIdentifier.getScheme () +
                 "::" +
                 aParticipantIdentifier.getValue () +
                 " -> " +
                 sSMPID);

    // check that host name is ok
    DNSUtils.verifyHostname (sSMPID);

    final String sDNSAnchor = sSMPID + ".publisher." + m_sSMLZoneName;
    String sPIDNSName;
    try {
      sPIDNSName = getDNSNameOfParticipant (aParticipantIdentifier);
    }
    catch (final IllegalArgumentException ex) {
      throw new IllegalIdentifierSchemeException (ex.getMessage ());
    }
    final Resolver aDNSResolver = getResolver ();

    //
    final Name aDNSZone = Name.fromString (m_sDNSZoneName);
    final Name aPIHost = Name.fromString (sPIDNSName);

    final Update aDNSUpdate = new Update (aDNSZone);
    final Record aRecord = new CNAMERecord (aPIHost, Type.A, m_nTTLSecs, new Name (sDNSAnchor));
    aDNSUpdate.add (aRecord);

    final Message response = aDNSResolver.send (aDNSUpdate);
    validateDNSResponse (response);
  }

  public void createIdentifiers (final List <ParticipantIdentifierType> aParticipantIdentifiers, final String sSMPID) throws IOException,
                                                                                                                     IllegalIdentifierSchemeException,
                                                                                                                     IllegalHostnameException {
    if (log.isDebugEnabled ())
      log.debug ("Create List of Identifier for : " + sSMPID);

    // check that host name is ok
    DNSUtils.verifyHostname (sSMPID);

    final Resolver aDNSResolver = getResolver ();

    //
    final Name aDNSZone = Name.fromString (m_sDNSZoneName);
    final Name aSMLAnchor = new Name (sSMPID + ".publisher." + m_sSMLZoneName);
    final Update aDNSUpdate = new Update (aDNSZone);

    for (final ParticipantIdentifierType aParticipantIdentifier : aParticipantIdentifiers) {
      final String sPIName = getDNSNameOfParticipant (aParticipantIdentifier);
      final Name aPIHost = Name.fromString (sPIName);
      final Record aPIRecord = new CNAMERecord (aPIHost, Type.A, m_nTTLSecs, aSMLAnchor);
      aDNSUpdate.add (aPIRecord);
    }

    final Message response = aDNSResolver.send (aDNSUpdate);
    validateDNSResponse (response);
  }

  public void deleteIdentifier (final ParticipantIdentifierType pi) throws IllegalIdentifierSchemeException,
                                                                   IOException {
    final String sPIDNSName = getDNSNameOfParticipant (pi);
    if (log.isDebugEnabled ())
      log.debug ("Delete Identifier : " + sPIDNSName);

    deleteZoneRecord (sPIDNSName);
  }

  public void deleteIdentifiers (final List <ParticipantIdentifierType> aParticipantIdentifiers) throws IOException,
                                                                                                IllegalIdentifierSchemeException {
    final Name aDNSZone = Name.fromString (m_sDNSZoneName);

    final Update aDNSUpdate = new Update (aDNSZone);
    final Resolver aDNSResolver = getResolver ();

    for (final ParticipantIdentifierType aParticipantIdentifier : aParticipantIdentifiers) {
      final String sPIName = getDNSNameOfParticipant (aParticipantIdentifier);
      final Name aPIHost = Name.fromString (sPIName, aDNSZone);
      aDNSUpdate.delete (aPIHost);
    }

    final Message response = aDNSResolver.send (aDNSUpdate);
    validateDNSResponse (response);
  }

  public void createPublisherAnchor (final String sSMPID, final String sEndpoint) throws IOException,
                                                                                 IllegalHostnameException {
    if (log.isDebugEnabled ())
      log.debug ("Create Publisher Anchor : " + sSMPID + " in zone : " + m_sDNSZoneName + " -> " + sEndpoint);

    // check that host name is ok
    DNSUtils.verifyHostname (sSMPID);

    final String sSMPHostName = sSMPID + ".publisher." + m_sSMLZoneName;

    //
    final Name aDNSZone = Name.fromString (m_sDNSZoneName);
    final Name aSMPHost = Name.fromString (sSMPHostName);

    final Update aDNSUpdate = new Update (aDNSZone);
    final Resolver aDNSResolver = getResolver ();

    // Delete old host - if exists!
    aDNSUpdate.delete (aSMPHost);

    Record aRecord = null;
    byte [] ipAddressBytes = Address.toByteArray (sEndpoint, Address.IPv4);
    if (ipAddressBytes != null) {
      if (log.isDebugEnabled ())
        log.debug (" - IPV4");
      final InetAddress ipAddress = InetAddress.getByAddress (ipAddressBytes);
      aRecord = new ARecord (aSMPHost, Type.A, m_nTTLSecs, ipAddress);
    }
    else {
      ipAddressBytes = Address.toByteArray (sEndpoint, Address.IPv6);
      // NO IPv6 yet
      if (false && ipAddressBytes != null) {
        if (log.isDebugEnabled ())
          log.debug (" - IPV6");
        final InetAddress ipAddress = InetAddress.getByAddress (ipAddressBytes);
        // FIXME
        final int nPrefixBits = 0;
        final Name aPrefix = null;
        aRecord = new A6Record (aSMPHost, Type.A, m_nTTLSecs, nPrefixBits, ipAddress, aPrefix);
      }
      else
        if ((sEndpoint + ".").endsWith (m_sDNSZoneName)) {
          if (log.isDebugEnabled ())
            log.debug (" - in Local Zone");
          // FOR NOW WE CAN ONLY RESOLVE LOCAL ADDRESSES...
          // - CNAME TO LOCAL
          aRecord = new CNAMERecord (aSMPHost, Type.A, m_nTTLSecs, new Name (sEndpoint + "."));
        }
        else {
          if (log.isDebugEnabled ())
            log.debug (" - Other Zone");
          // NOT VALID ANY MORE
          // FOR NOW WE CAN ONLY RESOLVE LOCAL ADDRESSES...
          // - if NOT local - resolve and create A RECORD
          // - in real setup - change to CNAME -> endpoint

          // InetAddress resolvedAddress = InetAddress.getByName(endpoint);
          // r = new ARecord(host, Type.A, ttl, resolvedAddress);

          // THIS IS
          aRecord = new CNAMERecord (aSMPHost, Type.A, m_nTTLSecs, new Name (sEndpoint + "."));
        }
    }
    aDNSUpdate.add (aRecord);

    final Message response = aDNSResolver.send (aDNSUpdate);
    validateDNSResponse (response);
  }

  public void deletePublisherAnchor (final String sSMPID) throws IOException {
    final String aSMPAnchor = sSMPID + ".publisher." + m_sSMLZoneName;
    if (log.isDebugEnabled ())
      log.debug ("Delete Publisher Anchor : " + aSMPAnchor);

    deleteZoneRecord (aSMPAnchor);
  }

  @SuppressWarnings ("unused")
  private void createZoneRecord (final Record aRecord) throws IOException {
    if (log.isDebugEnabled ())
      log.debug ("Insert Zone Record : " + aRecord);

    final Name aDNSZone = Name.fromString (m_sDNSZoneName);
    final Update aDNSUpdate = new Update (aDNSZone);
    final Resolver aDNSResolver = getResolver ();

    aDNSUpdate.add (aRecord);

    final Message response = aDNSResolver.send (aDNSUpdate);
    validateDNSResponse (response);
  }

  public List <Record> getAllRecords () throws IOException, ZoneTransferException {
    // do zone transfer to get complete list..
    final ZoneTransferIn xfr = ZoneTransferIn.newAXFR (new Name (m_sDNSZoneName), m_sServerName, null);
    @SuppressWarnings ("unchecked")
    final List <Record> records = xfr.run ();
    return records;
  }

  public String lookupPeppolPublisherById (final String sSMPID) throws IOException {
    if (log.isDebugEnabled ())
      log.debug ("Lookup Publisher By ID : " + sSMPID);
    final String name = sSMPID + "." + CSMLDefault.DNS_PUBLISHER_SUBZONE + m_sSMLZoneName;

    return lookupDNSRecord (name);
  }

  @Nullable
  public String lookupDNSRecord (final String name) throws IOException {
    if (log.isDebugEnabled ())
      log.debug ("Lookup Publisher : " + name);

    final Resolver res = getResolver ();
    Name host;
    if (name.endsWith (".")) {
      host = Name.fromString (name);
    }
    else {
      final Name zone = Name.fromString (m_sDNSZoneName);
      host = Name.fromString (name, zone);
    }
    final Lookup lookup = new Lookup (host, Type.ANY); // , Type.CNAME);
    lookup.setResolver (res);
    lookup.setCache (null);

    if (log.isDebugEnabled ())
      log.debug ("Run Lookup for Host : " + host);
    final Record [] records = lookup.run ();
    if (log.isDebugEnabled ())
      log.debug ("Run Lookup for Host - DONE : " + Arrays.toString (records));

    if (records == null)
      return null;

    if (records[0] instanceof CNAMERecord) {
      return ((CNAMERecord) records[0]).getAlias ().toString ();
    }
    if (records[0] instanceof ARecord) {
      // ?? is this to validate ???
      final InetAddress inetAddress = ((ARecord) records[0]).getAddress ();
      return inetAddress.getHostAddress ();
    }

    return records[0].toString ();
  }

  /**
   * Helper for deleting records.
   * 
   * @param name
   * @throws IOException
   */
  private void deleteZoneRecord (final String name) throws IOException {
    if (log.isDebugEnabled ())
      log.debug ("Delete Zone Record : " + name);

    final Resolver aDNSResolver = getResolver ();

    final Name aDNSZone = Name.fromString (m_sDNSZoneName);
    Name host;
    if (name.endsWith ("."))
      host = Name.fromString (name);
    else
      host = Name.fromString (name, aDNSZone);

    final Update aDNSUpdate = new Update (aDNSZone);
    aDNSUpdate.delete (host);

    final Message response = aDNSResolver.send (aDNSUpdate);
    validateDNSResponse (response);
  }

  /**
   * Helper for deleting records.
   * 
   * @param aDNSRecord
   * @throws IOException
   */
  @SuppressWarnings ("unused")
  private void deleteZoneRecord (final Record aDNSRecord) throws IOException {
    if (log.isDebugEnabled ())
      log.debug ("Delete Zone Record : " + aDNSRecord);

    final Resolver aDNSResolver = getResolver ();

    final Name aDNSZone = Name.fromString (m_sDNSZoneName);
    final Update aDNSUpdate = new Update (aDNSZone);

    aDNSUpdate.delete (aDNSRecord);

    final Message response = aDNSResolver.send (aDNSUpdate);
    validateDNSResponse (response);
  }

  /**
   * Common method for validating Responses from DNS
   * 
   * @param aResponse
   */
  private static void validateDNSResponse (final Message aResponse) {
    final int nRetCode = aResponse.getRcode ();
    final String sRetCode = Rcode.string (nRetCode);
    if (log.isDebugEnabled ())
      log.debug ("validateDNSResponse : " + sRetCode);
    if (nRetCode != Rcode.NOERROR) {
      // Error - not handling special cases yet
      log.error ("Error performing DNS request : " + sRetCode + "\n" + aResponse);
      throw new DNSErrorException ("Error performing DNS request : " + sRetCode);
    }
  }

  public ParticipantIdentifierType getIdentifierFromDnsName (final String sDnsName) {
    return DNSUtils.getIdentiferFromDnsName (sDnsName, m_sSMLZoneName);
  }

  public String getDNSNameOfParticipant (@Nonnull final ParticipantIdentifierType aParticipantIdentifier) throws IllegalIdentifierSchemeException {
    return BusdoxURLUtils.getDNSNameOfParticipant (aParticipantIdentifier, m_sSMLZoneName);
  }

  public String getPublisherAnchorFromDnsName (@Nonnull final String sDnsName) {
    return DNSUtils.getPublisherAnchorFromDnsName (sDnsName, m_sSMLZoneName);
  }

  public boolean isHandledZone (@Nonnull final String sDnsName) {
    return DNSUtils.isHandledZone (sDnsName, m_sSMLZoneName);
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("serverName", m_sServerName)
                                       .append ("dnsZone", m_sDNSZoneName)
                                       .append ("smlDnsZone", m_sSMLZoneName)
                                       .append ("ttl", m_nTTLSecs)
                                       .toString ();
  }
}
