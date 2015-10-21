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
package eu.domibus.discovery.certificate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

import eu.domibus.discovery.DiscoveryException;
import eu.domibus.discovery.handlers.smp.SMPHandler;

/**
 * This handler extracts a receiver certificate from SMP metadata.
 * 
 * <p>For SMP Service Metadata only</p>
 * 
 * @see CertificateHandler#RECEIVER_CERTIFICATE
 * @author Thorsten Niedzwetzki
 */
public class SMPCertificateHandler extends SMPHandler {

	private final CertificateFactory certificateFactory;

	
	/**
	 * Initializes a X.509 certificate factory.
	 * 
	 * @throws RuntimeException that encapsulates a CertificateException on any error
	 */
	public SMPCertificateHandler() {
		try {
			certificateFactory = CertificateFactory.getInstance("X.509");
		} catch (final CertificateException e) {
			throw new RuntimeException("Cannot get X.509 certificate factory: " + e.getMessage(), e);
		}
	}


	/**
	 * Checks whether the smp:Endpoint element is available.
	 * 
	 * <p>The smp:Certificate element is a child element of the smp:Endpoint element.</p>
	 */
	@Override
	public boolean canHandle(final Map<String, Node> nodes, final Map<String, Object> metadata) {
		return super.canHandle(nodes, metadata) && nodes.containsKey(ENDPOINT);
	}


	/**
	 * Reads and extracts information from the //smp:Endpoint/smp:Certificate element.
	 * 
	 * <p>The extracted certificate is available as RECEIVER_CERTIFICATE
	 * in the metadata map -- or {@code null}, if not provided.</p>
	 * 
	 * @see CertificateHandler#RECEIVER_CERTIFICATE
	 */
	@Override
	public boolean resolveMetadata(
			final Map<String,Node> nodes,
			final Map<String,Object> metadata) throws DiscoveryException {
		
		final Node endpoint = nodes.get(ENDPOINT);

		try {
			final Node certificateNode = unsafeXPath(endpoint, "child::smp:Certificate/text()");
			if (certificateNode == null) {
				return true;  // no certificate found
			}
			final String certificateBase64String = certificateNode.getTextContent();
			
			final byte[] certificateBytes = DatatypeConverter.parseBase64Binary(certificateBase64String);
			final InputStream certificateInputStream = new ByteArrayInputStream(certificateBytes);

			X509Certificate certificate;
			certificate = (X509Certificate) certificateFactory.generateCertificate(certificateInputStream);
			metadata.put(CertificateHandler.RECEIVER_CERTIFICATE, certificate);

			return true;
		} catch (final XPathExpressionException e) {
			throw new DiscoveryException("Invalid XPath expression: " + e.getMessage(), e);
		} catch (final CertificateException e) {
			throw new DiscoveryException("Invalid //smp:Endpoint/smp:Certificate: " + e.getMessage(), e);
		}
	}

}
