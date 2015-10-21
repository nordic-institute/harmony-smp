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
package eu.domibus.discovery.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * An in-memory cache with automatic removal of timed out entries.
 * 
 * @author Thorsten Niedzwetzki
 * @param <K> key type
 * @param <V> element type
 */
public class InMemoryTimeoutCache<K,V> implements TimeoutCache<K, V> {
	private static final Logger log = Logger.getLogger(InMemoryTimeoutCache.class);

	/**
	 * The default in-memory cache timeout duration is one hour.
	 */
	private static final int DEFAULT_CACHE_TIMEOUT_DURATION_IN_MILLIS = 3600 * 1000;
	

	private final Map<K,Timestamped<V>> cache;
	
	private long cacheTimeoutDurationInMillis;
	private long cacheCleanupDurationInMillis;
	private long lastCleanupTimestamp;


	public InMemoryTimeoutCache() {
		this.cache = new HashMap<K,Timestamped<V>>();
		setCacheEntriesTimeout(DEFAULT_CACHE_TIMEOUT_DURATION_IN_MILLIS);
		this.lastCleanupTimestamp = System.currentTimeMillis();
	}


	/* (non-Javadoc)
	 * @see eu.ecodex.discovery.util._TimeoutCache#put(K, V)
	 */
	@Override
	public Timestamped<V> put(final K key, final V data) {
		log.trace("Put into cache: " + key + "=" + data);
		return cache.put(key, new Timestamped<V>(data));
	}
	
	
	/* (non-Javadoc)
	 * @see eu.ecodex.discovery.util._TimeoutCache#get(K)
	 */
	@Override
	public V get(final K key) {
		removeTimedOutEntries();
		final Timestamped<V> element = cache.get(key);
		if (element != null) {
			// Remove timed out cache entries
			if (element.getTimestamp() + cacheTimeoutDurationInMillis < System.currentTimeMillis()) {
				cache.remove(key);
			} else {
				final V data = element.getData();
				log.trace("Retrieved from cache: " + key + "=" + data);
				return data;
			}
		}
		return null;  // not found or timed out
	}


	/**
	 * Remove timed out cache entries if the timeout for this cleanup operation has exceeded.
	 */
	private void removeTimedOutEntries() {
		final long now = System.currentTimeMillis();
		final List<K> keysOfExpiredElements = new LinkedList<K>();
		if (lastCleanupTimestamp + cacheCleanupDurationInMillis < now) {
			for (final Entry<K,Timestamped<V>> entry : cache.entrySet()) {
				if (entry.getValue().getTimestamp() + cacheTimeoutDurationInMillis < now) {
					keysOfExpiredElements.add(entry.getKey());
				}
			}
			for (final K key : keysOfExpiredElements) {
				log.trace("Removing cached data for key: " + key);
				cache.remove(key);
			}
			lastCleanupTimestamp = now;
		}
	}


	/* (non-Javadoc)
	 * @see eu.ecodex.discovery.util._TimeoutCache#setCacheEntriesTimeout(long)
	 */
	@Override
	public void setCacheEntriesTimeout(final long cacheTimeoutDurationInMillis) {
		this.cacheTimeoutDurationInMillis = cacheTimeoutDurationInMillis;
		this.cacheCleanupDurationInMillis = cacheTimeoutDurationInMillis;
		if (log.isTraceEnabled()) {
			if (cacheTimeoutDurationInMillis > 0) {
				log.trace("Set cache entries time-out to " + cacheTimeoutDurationInMillis + " ms");
			} else {
				log.trace("Switched off cache");
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see eu.ecodex.discovery.util._TimeoutCache#getCacheEntriesTimeout()
	 */
	@Override
	public long getCacheEntriesTimeout() {
		return this.cacheTimeoutDurationInMillis;
	}


	/* (non-Javadoc)
	 * @see eu.ecodex.discovery.util._TimeoutCache#clear()
	 */
	@Override
	public void clear() {
		log.trace("Removed " + cache.size() + (cache.size() == 1 ? " cache entry" : " cache entries"));
		cache.clear();
	}

}
