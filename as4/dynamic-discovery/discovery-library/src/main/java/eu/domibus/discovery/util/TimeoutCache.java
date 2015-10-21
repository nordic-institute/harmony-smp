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


/**
 * A cache with automatic removal of timed out entries.
 * 
 * @author Thorsten Niedzwetzki
 * @param <K> key type
 * @param <V> element type
 */
public interface TimeoutCache<K, V> {

	/**
	 * Put a data entry to the cache and store a timestamp to measure timeout.
	 * The element will be removed after the timeout expires.
	 * 
	 * @param key the key to the cached data
	 * @param data the value of the cached data
	 * @return the previous value associated with key, or {@code null} if there was no mapping for key
	 */
	public Timestamped<V> put(final K key, final V data);

	
	/**
	 * Returns the cached data or {@code null} if no data has been cached
	 * using the {@code key} or if the information has timed out.
	 * 
	 * @param key the key to the cached data
	 * @return the cached data or {@code null} if not available or timed out
	 */
	public V get(final K key);

	
	/**
	 * Set the timeout duration for cache entries.
	 * 
	 * <p>If a cache entry has not timed out yet, it will be taken from the cache.</p>
	 * <p>If a cache entry has timed out, it will be resolved again.</p>
	 * 
	 * @param cacheTimeoutDurationInMillis
	 * @see #DEFAULT_CACHE_TIMEOUT_DURATION_IN_MILLIS 
	 */
	public void setCacheEntriesTimeout(final long cacheTimeoutDurationInMillis);

	
	/**
	 * Returns the the timeout duration for cache entries in milliseconds.
	 * 
	 * @return the timeout duration for cache entries in milliseconds.
	 */
	public long getCacheEntriesTimeout();

	
	/**
	 * Removes all cache entries.
	 */
	public void clear();

}