/*
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * 
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the Licence is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the Licence for the specific language governing permissions and limitations
 * under the Licence.
 */
package eu.domibus.discovery.locatorClient;

import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.NAPTRRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import eu.domibus.discovery.DiscoveryException;
import eu.domibus.discovery.Metadata;
import eu.domibus.discovery.names.ECodexNamingScheme;
import eu.domibus.discovery.names.NamingScheme;

/**
 * This service metadata locator client looks up a service metadata publisher (SMP) URL using DNS.
 * 
 * <p>It connects to a DNS server and looks up an NAPTR-U record.</p>
 * 
 * <p>The SMP URI is the second entry in the regular expression field of the NAPTR-U record.</p> 
 *
 * <p>Built upon the proof of concept (Python implementation) by Pim van der Eijk.</p>
 * 
 * @author Thorsten Niedzwetzki
 * @see #resolvePublisher(String)
 * @see NAPTRRecord
 */
public class DNSLocatorClient implements LocatorClient {
	private static final Logger log = Logger.getLogger(DNSLocatorClient.class);

	/**
	 * Do not query the local system's default resolvers first.
	 * Query dedicated SML resolvers only.
	 */
	private static final Resolver[] NO_DEFAULT_RESOLVERS = { };

	/**
	 * Regular expression to extract the publisher URI from the NAPTR-U record.
	 *
	 * @see #extractSMPAddressPatterns(NAPTRRecord)
	 */
	private static final Pattern URI_FROM_REGEXP = Pattern.compile("^(.)(.*)\\1(.*)\\1$");
	// TODO: Instead of using ! as seperator, any seperator can be used.
	// The first character in the pattern is used as a seperator -- whatever character it is.
	
	/**
	 * Sort {@link NAPTRRecord} collection by DNS record order and preference (ascending).
	 * 
	 * @see #NAPTROrderAndPreferenceComparator
	 */
	private static final Comparator<NAPTRRecord> NAPTR_BY_ORDER_AND_PREFERENCE =
			new NAPTROrderAndPreferenceComparator();
	
	/**
	 * One DNS resolver or a list of DNS resolvers.
	 * If this is {@code null} (by default),
	 * this SML client will query the system's default resolvers.
	 * 
	 * @see SimpleResolver
	 * @see ExtendedResolver
	 */
	private Resolver resolver;
	private String[] resolvers;
	
	/**
	 * Specify no community, no environment, no normalisation algorithm
	 * and use the system's default DNS servers.
	 */
	public DNSLocatorClient() { }
	
	
	/**
	 * Set up a locator client with default DNS resolver names.
	 * 
	 * @param resolver DNS server address(es) or {@code null} to use the system's default resolvers list.
	 * @throws DiscoveryException if at least one DNS resolver host name cannot be found
	 * @see #setResolvers(String...)
	 */
	public DNSLocatorClient(final String... resolvers) throws DiscoveryException {
		setResolvers(resolvers);
	}
	

	@Override
	public void setResolvers(final String... resolvers) throws DiscoveryException {
		if (Arrays.equals(this.resolvers, resolvers)) { return; }
		this.resolvers = resolvers;

		switch (resolvers.length) {
		case 0:
			this.resolver = null;
			break;
		case 1:
			final String[] resolverConfigStrings = resolvers[0].split("[,;\\s]");
			switch (resolverConfigStrings.length) {
			case 0:
				this.resolver = null;
				break;
			case 1:
				this.resolver = createSimpleResolver(resolvers[0]);
				break;
			default:
				this.resolver = createExtendedResolver(resolvers);
			}
			break;
		default:
			this.resolver = createExtendedResolver(resolvers);
		}
		// Clear cache if using new resolver(s) because their lookup results may differ.
		// The DiscoveryClient class manages its own cache.
		Lookup.getDefaultCache(DClass.IN).clearCache();
	}
	
	
	/**
	 * Create a simple DNS resolver using the given configuration String.
	 * 
	 * <p>The syntax is: Hostname or IP number [ : Port [ : Timeout in seconds ] ]</p>
	 * 
	 * <p>Examples for "Port":</p>
	 * <ul>
	 * 	<li>"53": Default DNS port</p></li>
	 * 	<li>"tcp53": Use TCP instead of UDP</li>
	 * 	<li>"udp5353": Use a different port number</li>
	 * 	<li>"tcp5353": Use TCP instead of UDP and a different port number</li>
	 * </ul>
	 * 
	 * <ul>
	 * 	<li>Port defaults to 53, the well known DNS port number</li>
	 * 	<li>Timeout in seconds, defaults to 10</li>
	 * </ul>
	 * 
	 * @param resolverConfigString simple DNS resolver configuration string
	 * @return a simple resolver
	 * @throws DiscoveryException unknown DNS host name or configuration string format error
	 * @see SimpleResolver
	 */
	private static SimpleResolver createSimpleResolver(final String resolverConfigString)
			throws DiscoveryException {
		final String[] resolverConfig = resolverConfigString.split("\\:");
		if (resolverConfig.length == 0) {
			throw new DiscoveryException("Empty DNS resolver configuration");
		}
		final SimpleResolver simpleResolver;
		try {
			simpleResolver = new SimpleResolver(resolverConfig[0]);
		} catch (final UnknownHostException e) {
			throw new DiscoveryException("Unknown DNS host name: " + resolverConfig[0], e);
		}
		
		// Optional argument #1: Port number (defaults to DNS = 53)
		if (resolverConfig.length > 1 && resolverConfig[1].length() > 0) {
			try {
				if (resolverConfig[1].startsWith("tcp")) {
					simpleResolver.setTCP(true);
					final int port = Integer.parseInt(resolverConfig[1].substring(3), 10);
					simpleResolver.setPort(port);
					log.debug("Setting Port to TCP " + port);
				} else
					if (resolverConfig[1].startsWith("udp")) {
						simpleResolver.setTCP(false);
						final int port = Integer.parseInt(resolverConfig[1].substring(3), 10);
						simpleResolver.setPort(port);
						log.debug("Setting Port to UDP " + port);
					} else {
						final int port = Integer.parseInt(resolverConfig[1], 10);
						simpleResolver.setPort(port);
						log.debug("Setting Port to " + port);
					}
			} catch (final NumberFormatException e) {
				throw new DiscoveryException("Invalid port number: " + resolverConfig[1] +
						" in DNS resolver configuration: " + resolverConfigString);
			} catch (final IllegalArgumentException e) {
				throw new DiscoveryException("Invalid port number for DNS host: " + resolverConfig[1] +
						" in DNS resolver configuration: " + resolverConfigString);
			}
		}
		
		// Optional argument #2: Timeout in seconds (defaults to 10 seconds)
		if (resolverConfig.length > 2 && resolverConfig[2].length() > 0) {
			try {
				simpleResolver.setTimeout(Integer.parseInt(resolverConfig[2], 10));
			} catch (final NumberFormatException e) {
				throw new DiscoveryException("Invalid timeout in seconds: " + resolverConfig[2] +
						" in DNS resolver configuration: " + resolverConfigString);
			}
		}
		return simpleResolver;
	}
	
	
	private static Resolver createExtendedResolver(final String[] resolverConfigStrings)
			throws DiscoveryException {

		final ExtendedResolver extendedResolvers;
		try {
			extendedResolvers = new ExtendedResolver(NO_DEFAULT_RESOLVERS);
		} catch (final UnknownHostException e) {
			throw new DiscoveryException("Cannot initialise extended DNS resolver" +
					" with empty list of default resolvers", e);
		}
		
		for (final String resolverConfigString : resolverConfigStrings) {
			for (final String simpleResolverConfigString : resolverConfigString.split("[,;\\s]")) {
				try {
					extendedResolvers.addResolver(createSimpleResolver(simpleResolverConfigString));
				} catch (final DiscoveryException e) {
					log.error("Skipping SML resolver due to invalid configuration: " +
							simpleResolverConfigString, e);
				}
			}
		}
		
		if (extendedResolvers.getResolvers().length > 0) {
			return extendedResolvers;
		} else {
			throw new DiscoveryException("Invalid DNS configuration: " +
					Arrays.toString(resolverConfigStrings));
		}
	}


	@Override
	public String resolvePublisher(final Map<String, Object> metadata, final Target target) throws DiscoveryException {
		if (!metadata.containsKey(Metadata.NAMING_SCHEME)) {
			metadata.put(Metadata.NAMING_SCHEME, new ECodexNamingScheme());
			throw new DiscoveryException("Cannot resolve publisher: Missing " + Metadata.NAMING_SCHEME);
		}
		final NamingScheme namingScheme = (NamingScheme) metadata.get(Metadata.NAMING_SCHEME);
		final String fullName = namingScheme.getFullName(metadata, target);
		
		final Record[] records = lookupDNSRecord(fullName + ".");

		// Try NAPTR-U records (BDXR Service Metadata Location style) first
		final NAPTRRecord[] naptrRecords = selectNAPTRRecordsByFlag(records, "U");
		if (naptrRecords.length > 0) {
			final NAPTRRecord naptrRecord = findPreferredRecord(naptrRecords);
			final String[] addressPatterns = extractSMPAddressPatterns(naptrRecord);
			log.trace("Found preferred NAPTR-U for " + fullName + ": " + Arrays.toString(addressPatterns));
			if (".*".equals(addressPatterns[0])) {
				return addressPatterns[1];
			} else {
				return fullName.replaceAll(addressPatterns[0], addressPatterns[1].replace("\\\\", "$"));
			}
		}
		
		// Try CNAME records (BusDoX style) if no matching NAPTR record exists
		final CNAMERecord[] cnameRecords = selectCNAMERecordsByType(records);
		if (cnameRecords.length > 0) {
			return cnameRecords[0].getTarget().toString();
		}
		
		throw new DiscoveryException("No CNAME or NAPTR record found for " + fullName + " " + metadata);
	}


	/**
	 * Looks up any DNS record for the given identifier.
	 * It uses the given resolver(s) or uses the standard system DNS resolver.
	 * 
	 * <p>This is one single step in the lookup process.</p>
	 * 
	 * @param fullName identifer to look up
	 * @return found DNS records (not null)
	 * @throws DiscoveryException on any error
	 * @see #resolvePublisher(String)
	 * @see #setResolver(String)
	 * @see #setResolvers(String...)
	 */
	private Record[] lookupDNSRecord(final String fullName) throws DiscoveryException {
		final Lookup lookup;
		try {
			lookup = new Lookup(fullName, Type.ANY, DClass.IN);
		} catch (final TextParseException e) {
			throw new DiscoveryException("Cannot parse destination name: " + fullName, e);
		}

		if (resolver != null) {
			lookup.setResolver(resolver);
		}
		
		final Record[] records = lookup.run();
		if (lookup.getResult() != Lookup.SUCCESSFUL) {
			throw new DiscoveryException(
					MessageFormat.format("Cannot lookup {0}: {1}",
							fullName, lookup.getErrorString()));
		}
		
		if (records == null || records.length == 0) {
			throw new DiscoveryException(
					MessageFormat.format("Cannot find any records for {0}",
							fullName));
		}

		for (final Record record : records) {
			if (!(record instanceof NAPTRRecord) && !(record instanceof CNAMERecord)) {
				throw new DiscoveryException(
						MessageFormat.format("Record type mismatch for {0}: {1}",
								fullName, record.getType()));
			}
		}
		
		return records;
	}


	/**
	 * Select only NAPTR records with a specific NAPTR flag.
	 * Drop all NAPTR records with other NAPTR flags.
	 * 
	 * <p>This is one single step in the lookup process.</p>
	 * 
	 * @param records DNS records to filter by type (NAPTR) and NAPTR flag
	 * @param recordFlag flag to filter
	 * @return (possible empty) list of NATPR records with the given flag (not {@code null})
	 * @see #resolvePublisher(String)
	 * @see NAPTRRecord
	 */
	private NAPTRRecord[] selectNAPTRRecordsByFlag(final Record[] records, final String recordFlag) {
		final ArrayList<NAPTRRecord> naptrs = new ArrayList<NAPTRRecord>(records.length);
		
		for (final Record record : records) {
			if (record instanceof NAPTRRecord) {
				final NAPTRRecord naptr = (NAPTRRecord) record;
				if (recordFlag.equals(naptr.getFlags())) {
					naptrs.add(naptr);
				}
			}
		}
		
		return naptrs.toArray(new NAPTRRecord[naptrs.size()]);
	}
	
	
	
	/**
	 * Select only CNAME records.
	 * 
	 * @param records DNS records to filter by type (CNAME)
	 * @return (possible empty) list of NATPR records with the given flag (not {@code null})
	 */
	private CNAMERecord[] selectCNAMERecordsByType(final Record[] records) {
		final ArrayList<CNAMERecord> cnames = new ArrayList<CNAMERecord>(records.length);
		
		for (final Record record : records) {
			if (record instanceof CNAMERecord) {
				final CNAMERecord cname = (CNAMERecord) record;
				cnames.add(cname);
			}
		}
		
		return cnames.toArray(new CNAMERecord[cnames.size()]);
	}


	/**
	 * Sort NAPTR records.
	 * 
	 * <p>This is one single step in the lookup process.</p>
	 * 
	 * @param naptrRecords NAPTR records to sort (must not be empty)
	 * @return sorted collection of NAPTR records (not null)
	 * @throws DiscoveryException if the list of NAPTR records is empty
	 * @see #resolvePublisher(String)
	 * @see NAPTRRecord
	 */
	private NAPTRRecord findPreferredRecord(final NAPTRRecord[] naptrRecords) throws DiscoveryException {
		assert naptrRecords != null && naptrRecords.length > 0;
		Arrays.sort(naptrRecords, NAPTR_BY_ORDER_AND_PREFERENCE);
		return naptrRecords[0];
	}


	/**
	 * Apply pattern matching to the found NAPTR record to resolve the SMP address URI.
	 * 
	 * <p>This is one single step in the lookup process.</p>
	 * 
	 * @param naptrRecord NAPTR record to investigate
	 * @return exactly one input and output pattern to apply to the identifier
	 * @throws DiscoveryException on any error
	 * @see #resolvePublisher(String)
	 * @see NAPTRRecord
	 */
	private String[] extractSMPAddressPatterns(final NAPTRRecord naptrRecord) throws DiscoveryException {
		final String regexp = naptrRecord.getRegexp();
		final Matcher matcher = URI_FROM_REGEXP.matcher(regexp);
		if (!matcher.matches()) {
			throw new DiscoveryException(
					String.format("SML record's regexp does not match: %s", URI_FROM_REGEXP));
		}
		final String inputPattern = matcher.group(2);
		final String outputPattern = matcher.group(3);
		return new String[] { inputPattern, outputPattern };
	}


	/**
	 * Sort {@link NAPTRRecord} collection by DNS record order and preference (ascending).
	 * 
	 * @author Thorsten Niedzwetzki
	 */
	private static class NAPTROrderAndPreferenceComparator implements Comparator<NAPTRRecord> {

		/**
		 * Compare two {@link NAPTRRecord} elements by order and preference.
		 * 
		 * @param left element to compare to right
		 * @param right element to compare to left
		 * @return -1 if left is smaller, +1 if left is bigger, 0 if left and right are equal
		 */
		@Override
		public int compare(final NAPTRRecord left, final NAPTRRecord right) {
			if (left.getOrder() != right.getOrder()) {
				return left.getOrder() - right.getOrder();
			}

			if (left.getPreference() != right.getPreference()) {
				return left.getPreference() - right.getPreference();
			}

			return 0;
		}
		
	}

}
