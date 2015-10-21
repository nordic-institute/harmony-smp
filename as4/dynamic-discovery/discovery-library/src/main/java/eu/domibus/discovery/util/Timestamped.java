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
 * A data object with a timestamp.
 * 
 * @author Thorsten Niedzwetzki
 */
public class Timestamped<T> {
	private final T data;
	private final long timestamp;

	/**
	 * Create a timestamp for a data object.
	 * 
	 * @param data the data to timestamp
	 */
	public Timestamped(final T data) {
		this.data = data;
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * Get the data object.
	 */
	public T getData() {
		return data;
	}

	/**
	 * Get the timestamp of the data object.
	 * 
	 * @return timestamp in milliseconds
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
}
