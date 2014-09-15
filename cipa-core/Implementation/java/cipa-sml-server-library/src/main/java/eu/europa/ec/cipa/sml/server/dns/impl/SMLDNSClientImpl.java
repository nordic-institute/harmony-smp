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

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Address;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Update;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;

import eu.europa.ec.cipa.peppol.sml.CSMLDefault;
import eu.europa.ec.cipa.peppol.uri.BusdoxURLUtils;
import eu.europa.ec.cipa.sml.server.dns.DNSUtils;
import eu.europa.ec.cipa.sml.server.dns.ISMLDNSClient;
import eu.europa.ec.cipa.sml.server.exceptions.IllegalHostnameException;
import eu.europa.ec.cipa.sml.server.exceptions.IllegalIdentifierSchemeException;

/**
 * DNSClient for dynamic updating DNS Server from ServiceMetadataLocator.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public class SMLDNSClientImpl extends DNSClientImpl implements ISMLDNSClient{
	static final Logger s_aLogger = LoggerFactory.getLogger(SMLDNSClientImpl.class);

	/**
	 * Constructor
	 * 
	 * @param sServerName
	 *            Network name of the DNS server. E.g. "blixdns0".
	 * @param sDNSZoneName
	 *            The main DNS zone name to be managed. E.g.
	 *            "sml.peppolcentral.org.". A trailing dot is automatically
	 *            appended if not present.
	 * @param sSMLZoneName
	 *            The specific sub DNS zone to be managed. Must be equal or a
	 *            child of the DNS zone. E.g. "smk.peppolcentral.org."
	 * @param nTTLSecs
	 *            The time to live in seconds. Must be &gt; 0.
	 */
	public SMLDNSClientImpl(@Nonnull @Nonempty final String sServerName, @Nonnull @Nonempty final String sDNSZoneName,
			@Nonnull @Nonempty final String sSMLZoneName, @Nonnegative final int nTTLSecs) {
		super(sServerName, sDNSZoneName, sSMLZoneName, nTTLSecs);
		s_aLogger.info("DnsClientImpl init done. DNS Zone=" + m_sDNSZoneName + "; SML Zone=" + m_sSMLZoneName);
	}

	/**
	 * Get the publisher DNS name
	 * 
	 * @param sSMPID
	 *            SMP ID
	 * @return <code>SMPID</code>.publisher.<code>SML-zone-name</code>
	 * @throws IllegalHostnameException
	 *             If the SMP ID is invalid
	 */
	@Nonnull
	@Nonempty
	public String createPublisherDNSName(@Nonnull @Nonempty final String sSMPID) throws IllegalHostnameException {
		// check that host name is ok
		DNSUtils.verifyHostname(sSMPID);

		// Build name
		return sSMPID + "." + CSMLDefault.DNS_PUBLISHER_SUBZONE + m_sSMLZoneName;
	}

	@Nonnull
	private Name _createPublisherDNSNameObject(@Nonnull @Nonempty final String sSMPID) throws IllegalHostnameException {
		final String sName = createPublisherDNSName(sSMPID);
		try {
			return Name.fromString(sName);
		} catch (final TextParseException ex) {
			throw new IllegalHostnameException("Failed to convert '" + sName + "' to a DNS name", ex);
		}
	}

	public void createIdentifier(@Nonnull final ParticipantIdentifierType aParticipantIdentifier, @Nonnull final String sSMPID) throws IOException,
			IllegalIdentifierSchemeException, IllegalHostnameException {
		ValueEnforcer.notNull(aParticipantIdentifier, "ParticipantIdentifier");

		s_aLogger.info("Create Identifier " + aParticipantIdentifier.toString() + " -> " + sSMPID);

		// Start update
		final Update aDNSUpdate = new Update(m_aDNSZoneName);

		// add record
		final Name aParticipantHost = _getDNSNameObjectOfParticipant(aParticipantIdentifier);
		final Name aPublisherHost = _createPublisherDNSNameObject(sSMPID);
		aDNSUpdate.add(new CNAMERecord(aParticipantHost, DClass.IN, m_nTTLSecs, aPublisherHost));
		s_aLogger.debug("sending Dns UPDATE :" + aDNSUpdate);

		// Execute
		final Message aResponse = sendMessgeToDnsServer(aDNSUpdate);
		_validateDNSResponse(aResponse);
	}

	public void createIdentifiers(@Nonnull final List<ParticipantIdentifierType> aParticipantIdentifiers, @Nonnull final String sSMPID) throws IOException,
			IllegalIdentifierSchemeException, IllegalHostnameException {
		ValueEnforcer.notNull(aParticipantIdentifiers, "ParticipantIdentifiers");

		s_aLogger.info("Create Identifiers " + aParticipantIdentifiers.toString() + " -> " + sSMPID);

		// What to update
		final Update aDNSUpdate = new Update(m_aDNSZoneName);

		final Name aPublisherHost = _createPublisherDNSNameObject(sSMPID);

		for (final ParticipantIdentifierType aParticipantIdentifier : aParticipantIdentifiers) {
			final Name aParticipantHost = _getDNSNameObjectOfParticipant(aParticipantIdentifier);
			aDNSUpdate.add(new CNAMERecord(aParticipantHost, DClass.IN, m_nTTLSecs, aPublisherHost));
		}
		s_aLogger.debug("sending Dns UPDATE :" + aDNSUpdate);

		// Execute
		final Message response = sendMessgeToDnsServer(aDNSUpdate);
		_validateDNSResponse(response);
	}

	public void deleteIdentifier(@Nonnull final ParticipantIdentifierType aParticipantIdentifier) throws IllegalIdentifierSchemeException, IOException {
		ValueEnforcer.notNull(aParticipantIdentifier, "ParticipantIdentifier");

		s_aLogger.info("Delete Identifier " + aParticipantIdentifier.toString());

		final String sPIDNSName = getDNSNameOfParticipant(aParticipantIdentifier);
		_deleteZoneRecord(sPIDNSName);
	}

	public void deleteIdentifiers(@Nonnull final List<ParticipantIdentifierType> aParticipantIdentifiers) throws IOException, IllegalIdentifierSchemeException {
		ValueEnforcer.notNull(aParticipantIdentifiers, "ParticipantIdentifiers");

		s_aLogger.info("Deleting Identifiers " + aParticipantIdentifiers.toString());

		final Update aDNSUpdate = new Update(m_aDNSZoneName);

		for (final ParticipantIdentifierType aParticipantIdentifier : aParticipantIdentifiers) {
			final Name aParticipantHost = _getDNSNameObjectOfParticipant(aParticipantIdentifier);
			aDNSUpdate.delete(aParticipantHost);
		}

		// Execute
		final Message response = sendMessgeToDnsServer(aDNSUpdate);
		_validateDNSResponse(response);
	}

	public void createPublisherAnchor(@Nonnull final String sSMPID, @Nonnull final String sEndpoint) throws IOException, IllegalHostnameException {
		s_aLogger.info("Create Publisher Anchor " + sSMPID + " -> " + sEndpoint);

		final Update aDNSUpdate = new Update(m_aDNSZoneName);

		// Delete old host - if exists!
		final Name aPublisherHost = _createPublisherDNSNameObject(sSMPID);
		aDNSUpdate.delete(aPublisherHost);

		Record aRecord = null;
		byte[] aIPAddressBytes = Address.toByteArray(sEndpoint, Address.IPv4);
		if (aIPAddressBytes != null) {
			if (s_aLogger.isDebugEnabled())
				s_aLogger.debug(" - IPV4");

			final InetAddress aInetAddress = InetAddress.getByAddress(aIPAddressBytes);
			aRecord = new ARecord(aPublisherHost, DClass.IN, m_nTTLSecs, aInetAddress);
		} else {
			aIPAddressBytes = Address.toByteArray(sEndpoint, Address.IPv6);
			// FIXME NO IPv6 yet
			// if (false && aIPAddressBytes != null) {
			// if (s_aLogger.isDebugEnabled())
			// s_aLogger.debug(" - IPV6");
			//
			// final InetAddress ipAddress =
			// InetAddress.getByAddress(aIPAddressBytes);
			// // FIXME
			// final int nPrefixBits = 0;
			// final Name aPrefix = null;
			// aRecord = new A6Record(aPublisherHost, DClass.IN, m_nTTLSecs,
			// nPrefixBits, ipAddress, aPrefix);
			// } else
			if ((sEndpoint + ".").endsWith(m_sDNSZoneName)) {
				if (s_aLogger.isDebugEnabled())
					s_aLogger.debug(" - in Local Zone");

				// FOR NOW WE CAN ONLY RESOLVE LOCAL ADDRESSES...
				// - CNAME TO LOCAL
				aRecord = new CNAMERecord(aPublisherHost, DClass.IN, m_nTTLSecs, new Name(sEndpoint + "."));
			} else {
				if (s_aLogger.isDebugEnabled())
					s_aLogger.debug(" - Other Zone");

				// NOT VALID ANY MORE
				// FOR NOW WE CAN ONLY RESOLVE LOCAL ADDRESSES...
				// - if NOT local - resolve and create A RECORD
				// - in real setup - change to CNAME -> endpoint

				// InetAddress resolvedAddress =
				// InetAddress.getByName(endpoint);
				// r = new ARecord(host, DClass.IN, ttl, resolvedAddress);

				// THIS IS
				aRecord = new CNAMERecord(aPublisherHost, DClass.IN, m_nTTLSecs, new Name(sEndpoint + "."));
			}
		}
		aDNSUpdate.add(aRecord);

		s_aLogger.info("  Creating record: " + aRecord.toString());

		// Execute
		final Message response = sendMessgeToDnsServer(aDNSUpdate);
		_validateDNSResponse(response);
	}

	public void deletePublisherAnchor(final String sSMPID) throws IOException, IllegalHostnameException {
		s_aLogger.info("Delete Publisher Anchor " + sSMPID);

		final String aSMPAnchor = createPublisherDNSName(sSMPID);
		_deleteZoneRecord(aSMPAnchor);
	}

	@Nullable
	public String lookupPeppolPublisherById(@Nonnull final String sSMPID) throws IOException, IllegalHostnameException {
		if (s_aLogger.isDebugEnabled())
			s_aLogger.debug("Lookup Publisher By ID : " + sSMPID);

		final String sName = createPublisherDNSName(sSMPID);
		return lookupDNSRecord(sName);
	}

	@Nullable
	public ParticipantIdentifierType getIdentifierFromDnsName(final String sDnsName) {
		return DNSUtils.getIdentiferFromDnsName(sDnsName, m_sSMLZoneName);
	}

	@Nonnull
	public String getDNSNameOfParticipant(@Nonnull final ParticipantIdentifierType aParticipantIdentifier) throws IllegalIdentifierSchemeException {
		try {
			return BusdoxURLUtils.getDNSNameOfParticipant(aParticipantIdentifier, m_sSMLZoneName);
		} catch (final IllegalArgumentException ex) {
			throw new IllegalIdentifierSchemeException(String.valueOf(aParticipantIdentifier), ex);
		}
	}

	@Nonnull
	private Name _getDNSNameObjectOfParticipant(@Nonnull final ParticipantIdentifierType aParticipantIdentifier) throws IllegalIdentifierSchemeException {
		final String sDNSName = getDNSNameOfParticipant(aParticipantIdentifier);
		try {
			return Name.fromString(sDNSName, m_aDNSZoneName);
		} catch (final TextParseException ex) {
			throw new IllegalIdentifierSchemeException(sDNSName, ex);
		}
	}

	@Nullable
	public String getPublisherAnchorFromDnsName(@Nonnull final String sDnsName) {
		return DNSUtils.getPublisherAnchorFromDnsName(sDnsName, m_sSMLZoneName);
	}
}
