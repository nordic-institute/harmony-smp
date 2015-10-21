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
package eu.domibus.discovery;

import java.util.Calendar;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import eu.domibus.discovery.locatorClient.DNSLocatorClient;
import eu.domibus.discovery.locatorClient.LocatorClient;
import eu.domibus.discovery.util.InMemoryTimeoutCache;
import eu.domibus.discovery.util.TimeoutCache;

/**
 * This MetadataResolver decorator adds a cache to reduce latency.
 * 
 * @author Thorsten Niedzwetzki
 * @see PublisherClient
 */
public class DiscoveryClient extends PublisherClient {
	
	private final TimeoutCache<String, Map<String, Object>> metadataCache;
	
	
	/**
	 * Set up a eu.ecodex.discovery client with an in-memory lookup cache.
	 * 
	 * @throws DiscoveryException on any configuration error
	 */
	public DiscoveryClient() throws DiscoveryException {
		metadataCache = new InMemoryTimeoutCache<String,Map<String,Object>>();
	}

	
	/**
	 * Set up a eu.ecodex.discovery client with a user defined cache.
	 * 
	 * @throws DiscoveryException on any configuration error
	 */
	public DiscoveryClient(final TimeoutCache<String, Map<String, Object>> metadataCache)
			throws DiscoveryException {
		this.metadataCache = metadataCache;
	}

	
	/**
	 * Set up a eu.ecodex.discovery client with a lookup cache and a default DNS locator client.
	 * 
	 * @param resolver None, one or many URLs of DNS servers that may provide the required SML (NAPTR-U) records
	 * @throws DiscoveryException if at least one DNS resolver host name cannot be found
	 */
	public DiscoveryClient(final String... resolvers) throws DiscoveryException {
		this();
		setLocatorClient(new DNSLocatorClient(resolvers));
	}
	

	/**
	 * Set up a eu.ecodex.discovery client with a lookup cache and the given locator client.
	 * 
	 * @param locatorClient the locator client to use for SML lookups
	 * @throws DiscoveryException on any configuration error
	 */
	public DiscoveryClient(final LocatorClient locatorClient) throws DiscoveryException {
		this();
		setLocatorClient(locatorClient);
	}
	

	/**
	 * Resolve the requested service metadata or get it from the cache, if available. 
	 * 
	 * This method manages an internal cache to reduce latency.
	 * 
	 * @param sendingEndEntityId sender identifier, e. g. ebMS3 FromPartyId
	 * @param receivingEndEntityId receiver identifier, e. g. ebMS3 ToPartyId
	 * @param processId name of document or action environment, e. g. ebMS3 Service
	 * @param documentOrActionId name of specific document or action to request, e. g. ebMS3 Action
	 * @return extracted service metadata
	 * @throws DiscoveryException on any error
	 * @see #configure(String, String, String, String...)
	 * @see ServiceMetadata
	 */
	@Override
	public void resolveMetadata(final SortedMap<String,Object> metadata) throws DiscoveryException {
		
		// Get the endpoint address from the cache, if available and not timed out
		if (metadataCache.getCacheEntriesTimeout() > 0) {
			final String key = metadata.toString();

			final Map<String,Object> cachedMetadata = metadataCache.get(key);
			if (cachedMetadata != null) {
				metadata.clear();
				metadata.putAll(cachedMetadata);
				return;
			}

			super.resolveMetadata(metadata);
			metadataCache.put(key, new TreeMap<String,Object>(metadata));

		} else {
			metadataCache.clear();
			super.resolveMetadata(metadata);
		}
	}


	/**
	 * Set the timeout duration for cache entries.
	 * If a cache entry has not timed out yet, it will be taken from the cache.
	 * If a cache entry has timed out, it will be resolved again.
	 * 
	 * @param cacheTimeoutDurationInMillis timeout for each cache entry in milliseconds
	 * @see #DEFAULT_CACHE_TIMEOUT_DURATION_IN_MILLIS 
	 */
	public void setCacheEntryTimeout(final long cacheTimeoutDurationInMillis) {
		metadataCache.setCacheEntriesTimeout(cacheTimeoutDurationInMillis);
	}
	
	
	/**
	 * Get the timeout duration for cache entries.
	 * 
	 * @return timeout for each cache entry in milliseconds
	 */
	public long getCacheEntryTimeout() {
		return metadataCache.getCacheEntriesTimeout();
	}
	
	
	/**
	 * Clear any cache entries right now.
	 */
	public void clearCache() {
		metadataCache.clear();
	}


	/**
	 * Set the timeout duration for cache entries.
	 * If a cache entry has not timed out yet, it will be taken from the cache.
	 * If a cache entry has timed out, it will be resolved again.
	 * 
	 * @param duration as an XML duration string, e. g. "PT10S" = 10 seconds
	 * @throws DiscoveryException if the DatatypeFactory cannot be instanciated
	 * @see #DEFAULT_CACHE_TIMEOUT_DURATION_IN_MILLIS 
	 */
	public void setCacheEntryTimeout(final String duration) throws DiscoveryException {
		final DatatypeFactory datatypeFactory;
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (final DatatypeConfigurationException e) {
			throw new DiscoveryException("Cannot get DatatypeFactory instance: " + e.getMessage(), e);
		}
		metadataCache.setCacheEntriesTimeout(
				datatypeFactory.newDuration(duration).getTimeInMillis(Calendar.getInstance()));
	}

}
