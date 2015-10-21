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

/**
 * Encapsulates any exception that may occur during dynamic eu.ecodex.discovery.
 * 
 * The original cause can be accessed using the {@link Exception#getCause()} method. 
 * 
 * @author Thorsten Niedzwetzki
 * @see #getCause()
 */
public class DiscoveryException extends Exception {
	private static final long serialVersionUID = -2050263186618182988L;

	public DiscoveryException() {
		super();
	}

	public DiscoveryException(final String message) {
		super(message);
	}

	public DiscoveryException(final Throwable exception) {
		super(exception);
	}

	public DiscoveryException(final String message, final Throwable exception) {
		super(message, exception);
	}

}
