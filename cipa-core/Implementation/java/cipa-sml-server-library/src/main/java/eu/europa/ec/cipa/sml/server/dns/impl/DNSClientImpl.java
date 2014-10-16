package eu.europa.ec.cipa.sml.server.dns.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.DNSSEC.DNSSECException;
import org.xbill.DNS.KEYRecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Options;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SIG0;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TSIG;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import org.xbill.DNS.Update;
import org.xbill.DNS.ZoneTransferException;
import org.xbill.DNS.ZoneTransferIn;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.OverrideOnDemand;
import com.helger.commons.exceptions.InitializationException;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

import eu.europa.ec.cipa.peppol.uri.BusdoxURLUtils;
import eu.europa.ec.cipa.sml.server.dns.DNSClientConfiguration;
import eu.europa.ec.cipa.sml.server.dns.DNSUtils;
import eu.europa.ec.cipa.sml.server.dns.IDNSClient;
import eu.europa.ec.cipa.sml.server.exceptions.DNSErrorException;
import eu.europa.ec.cipa.sml.server.security.SIG0KeyProvider;

public class DNSClientImpl implements IDNSClient {

	static final Logger s_aLogger = LoggerFactory.getLogger(DNSClientImpl.class);
	public final int DEFAULT_TTL_SECS = 60;
	protected String m_sServerName;
	protected String m_sDNSZoneName;
	protected Name m_aDNSZoneName;
	protected String m_sPublisherZoneName;
	protected int m_nTTLSecs;
	private KEYRecord SIG0Rec;

	public DNSClientImpl() {
		super();
	}

	public DNSClientImpl(@Nonnull @Nonempty String sServerName, @Nonnull @Nonempty String sDNSZoneName, @Nonnull @Nonempty String sSMLZoneName,
			@Nonnegative int nTTLSecs) {
		if (StringHelper.hasNoText(sServerName))
			throw new IllegalArgumentException("serverName may not be empty");

		if (StringHelper.hasNoText(sDNSZoneName))
			throw new IllegalArgumentException("DNSZoneName may not be empty");

		if (StringHelper.hasNoText(sSMLZoneName))
			throw new IllegalArgumentException("SMLZoneName may not be empty");

		if (nTTLSecs < 0)
			throw new IllegalArgumentException("TTL is invalid: " + nTTLSecs);

		s_aLogger.info("DnsClientImpl init: " + sServerName + " : " + sDNSZoneName + " : " + sSMLZoneName + " : " + nTTLSecs);
		m_sServerName = sServerName;

		String sRealDNSZoneName = sDNSZoneName.toLowerCase(BusdoxURLUtils.URL_LOCALE);
		if (!sRealDNSZoneName.endsWith("."))
			sRealDNSZoneName += '.';
		m_sDNSZoneName = sRealDNSZoneName;
		try {
			m_aDNSZoneName = Name.fromString(m_sDNSZoneName);
		} catch (final TextParseException ex) {
			throw new InitializationException("Failed to build DNS Name from '" + m_sDNSZoneName + "'", ex);
		}

		final String sSMLZoneNameLC = sSMLZoneName.toLowerCase(BusdoxURLUtils.URL_LOCALE);
		if (sSMLZoneNameLC.equals(m_sDNSZoneName) || sSMLZoneNameLC.endsWith("." + m_sDNSZoneName)) {
			// Fully qualified DNS zone provided
			m_sPublisherZoneName = sSMLZoneNameLC;
		} else {
			// Only the sub zone provided - append the main DNS zone
			if (sSMLZoneNameLC.endsWith("."))
				m_sPublisherZoneName = sSMLZoneNameLC + m_sDNSZoneName;
			else
				m_sPublisherZoneName = sSMLZoneNameLC + "." + m_sDNSZoneName;
		}

		m_nTTLSecs = nTTLSecs;
		s_aLogger.info("DnsClientImpl init done. DNS Zone=" + m_sDNSZoneName + "; SML Zone=" + m_sPublisherZoneName);
	}

	public void addRecords(List<Record> records) {
		s_aLogger.info("Add list of  records. " + records.size() + " will be added.");
		if (records.isEmpty())
			return;
		final Update aDNSUpdate = new Update(m_aDNSZoneName);
		// Delete old host - if exists!
		Record[] t = new Record[records.size()];
		records.toArray(t);
		aDNSUpdate.add(t);
		final Message response = sendMessgeToDnsServer(aDNSUpdate);
		_validateDNSResponse(response);
	}

	@Override
	public void deleteList(List<Record> records) {
		s_aLogger.info("Delete list of  records. " + records.size() + " will be deleted.");
		if (records.isEmpty())
			return;
		final Update aDNSUpdate = new Update(m_aDNSZoneName);
		// Delete old host - if exists!
		Record[] t = new Record[records.size()];
		records.toArray(t);
		aDNSUpdate.delete(t);
		final Message response = sendMessgeToDnsServer(aDNSUpdate);
		_validateDNSResponse(response);
	}

	/**
	 * Common method for validating Responses from DNS
	 * 
	 * @param aResponse
	 */
	protected static void _validateDNSResponse(@Nonnull final Message aResponse) {
		final int nRetCode = aResponse.getRcode();
		final String sRetCode = Rcode.string(nRetCode);
		if (s_aLogger.isDebugEnabled())
			s_aLogger.debug("validateDNSResponse '" + sRetCode + "'");

		if (nRetCode != Rcode.NOERROR) {
			// Error - not handling special cases yet
			s_aLogger.error("Error performing DNS request : " + sRetCode + "\n" + aResponse);
			throw new DNSErrorException("Error performing DNS request : " + sRetCode);
		}
	}

	@Nonnull
	@OverrideOnDemand
	protected Resolver createResolver() throws IOException {
		SimpleResolver res = new SimpleResolver(m_sServerName);
		res.setTCP(true);
		if (getTSIG() != null)
			res.setTSIGKey(getTSIG());
		return res;
	}

	private TSIG getTSIG() {
		if (DNSClientConfiguration.isEnabled()) {
			String secret = DNSClientConfiguration.getSecret();
			if (secret != null && !secret.isEmpty()) {
				s_aLogger.info("DNS Pre-shared secret returned");
				return new TSIG("hmac-sha256", "key-ddns.peppol.tech.ec.europa.eu.", secret);
			}
		}
		return null;
	}

	private KEYRecord getSIG0Record() throws IOException {
		if (SIG0Rec == null) {
			if (DNSClientConfiguration.isEnabled()) {
				String KeyName = DNSClientConfiguration.getSIG0PublicKeyName();
				if (KeyName != null) {
					Lookup aLookup = new Lookup(KeyName, Type.KEY);
					aLookup.setResolver(createResolver());
					aLookup.setCache(null);
					Record[] aRecords = aLookup.run();
					for (Record rec : aRecords) {
						if (rec.getType() == Type.KEY) {
							SIG0Rec = (KEYRecord) rec;
						}
					}
				}
			}
		}
		return SIG0Rec;
	}
	
	protected Message sendMessgeToDnsServer(Message m) {
		boolean SIG0Enabled = false;
		try {
			Options.set("sig0validity", "6000");
			if (DNSClientConfiguration.isEnabled()) {
				SIG0Enabled = DNSClientConfiguration.getSIG0();
			}
			if (SIG0Enabled) {
				SIG0KeyProvider prov = new SIG0KeyProvider();
				SIG0.signMessage(m, getSIG0Record(), prov.getPrivateSIG0Key(), null);
				Thread.sleep(2000);
			}
			return createResolver().send(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (DNSSECException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Nonnull
	public List<Record> getAllRecords() throws IOException, ZoneTransferException {
		// do zone transfer to get complete list..
		final ZoneTransferIn xfr = ZoneTransferIn.newAXFR(m_aDNSZoneName, m_sServerName, null);
		@SuppressWarnings("unchecked")
		final List<Record> records = xfr.run();
		return records;
	}

	public Record[] getRecordFromName(String name) {
		Record[] recs = null;
		try {
			Lookup look = new Lookup(name);
			look.setResolver(createResolver());
			recs = look.run();
		} catch (TextParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return recs;
	}

	@Nullable
	public String lookupDNSRecord(@Nonnull final String sName) throws IOException {
		if (s_aLogger.isDebugEnabled())
			s_aLogger.debug("Lookup Publisher: " + sName);

		Name aHost;
		if (sName.endsWith("."))
			aHost = Name.fromString(sName);
		else
			aHost = Name.fromString(sName, m_aDNSZoneName);

		final Lookup aLookup = new Lookup(aHost, Type.ANY); // , Type.CNAME);
		aLookup.setResolver(createResolver());
		aLookup.setCache(null);

		if (s_aLogger.isDebugEnabled())
			s_aLogger.debug("Run Lookup for Host : " + aHost);
		final Record[] aRecords = aLookup.run();
		if (s_aLogger.isDebugEnabled())
			s_aLogger.debug("Run Lookup for Host - DONE : " + Arrays.toString(aRecords));

		if (aRecords == null)
			return null;

		if (aRecords[0] instanceof CNAMERecord) {
			return ((CNAMERecord) aRecords[0]).getAlias().toString();
		}
		if (aRecords[0] instanceof ARecord) {
			// ?? is this to validate ???
			final InetAddress aInetAddress = ((ARecord) aRecords[0]).getAddress();
			return aInetAddress.getHostAddress();
		}

		return aRecords[0].toString();
	}

	/* getters and setters */
	@Nonnull
	@Nonempty
	public String getDNSZoneName() {
		return m_sDNSZoneName;
	}

	@Nonnull
	@Nonempty
	public String getSMLZoneName() {
		return m_sPublisherZoneName;
	}

	@Nonnull
	@Nonempty
	public String getServer() {
		return m_sServerName;
	}

	public int getTTLSecs() {
		return m_nTTLSecs;
	}

	/**
	 * Helper for deleting records.
	 * 
	 * @param name
	 * @throws IOException
	 */
	protected void _deleteZoneRecord(final String name) throws IOException {
		if (s_aLogger.isDebugEnabled())
			s_aLogger.debug("Delete Zone Record : " + name);

		final Update aDNSUpdate = new Update(m_aDNSZoneName);

		Name aHost;
		if (name.endsWith("."))
			aHost = Name.fromString(name);
		else
			aHost = Name.fromString(name, m_aDNSZoneName);
		aDNSUpdate.delete(aHost);

		// Execute
		final Message response = sendMessgeToDnsServer(aDNSUpdate);
		_validateDNSResponse(response);
	}

	public boolean isHandledZone(@Nonnull final String sDnsName) {
		return DNSUtils.isHandledZone(sDnsName, m_sPublisherZoneName);
	}

	@Override
	public String toString() {
		return new ToStringGenerator(this).append("serverName", m_sServerName).append("dnsZoneName", m_sDNSZoneName).append("smlDnsZoneName", m_sPublisherZoneName)
				.append("ttlSecs", m_nTTLSecs).toString();
	}

}