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
import eu.domibus.discovery.handlers.cpp3.CPP3BasicConfiguration;
import eu.domibus.discovery.util.DefaultNamespaceContext;

/**
 * This handler extracts sender and receiver (corner 2 and 3) certificates from CPP metadata if any.
 * 
 * @see CertificateHandler#RECEIVER_CERTIFICATE
 * @see CertificateHandler#SENDER_CERTIFICATE
 * @author Thorsten Niedzwetzki
 */
public class CPPCertificateHandler extends CPP3BasicConfiguration {

	private final CertificateFactory certificateFactory;

	
	/**
	 * Initializes a X.509 certificate factory.
	 * 
	 * @throws RuntimeException that encapsulates a CertificateException on any error
	 */
	public CPPCertificateHandler() {
		try {
			certificateFactory = CertificateFactory.getInstance("X.509");
		} catch (final CertificateException e) {
			throw new RuntimeException("Cannot get X.509 certificate factory: " + e.getMessage(), e);
		}
	}


	/**
	 * Add XMLDSig namespace with prefix "ds".
	 */
	@Override
	public void configureNamespaces(final DefaultNamespaceContext namespaces) {
		super.configureNamespaces(namespaces);
		namespaces.addNamespace("ds", "http://www.w3.org/2000/09/xmldsig#");
	}


	/**
	 * Checks whether the smp:Endpoint element is available.
	 * 
	 * <p>The smp:Certificate element is a child element of the smp:Endpoint element.</p>
	 */
	@Override
	public boolean canHandle(final Map<String, Node> nodes, final Map<String, Object> metadata) {
		return super.canHandle(nodes, metadata) && nodes.containsKey(NODE_RECEIVER_TRANSPORT);
	}


	/**
	 * Reads and extracts information from the //smp:Endpoint/smp:Certificate element.
	 * 
	 * <p>The extracted certificates are available as RECEIVER_CERTIFICATE and SENDER_CERTIFICATE
	 * in the metadata map -- or {@code null}, if not provided.</p>
	 */
	@Override
	public boolean resolveMetadata(
			final Map<String,Node> nodes,
			final Map<String,Object> metadata) throws DiscoveryException {

		final X509Certificate receiverCertificate = readCertificateIfAvailable(
				nodes.get(NODE_RECEIVER_TRANSPORT),
				nodes.get(NODE_RECEIVER_ACTION_BINDING));
		if (receiverCertificate != null) {
			metadata.put(CertificateHandler.RECEIVER_CERTIFICATE, receiverCertificate);	
		}

		final X509Certificate senderCertificate = readCertificateIfAvailable(
				nodes.get(NODE_SENDER_TRANSPORT),
				nodes.get(NODE_SENDER_ACTION_BINDING));
		if (senderCertificate  != null) {
			metadata.put(CertificateHandler.SENDER_CERTIFICATE, senderCertificate);	
		}

		return true;
	}


	private X509Certificate readCertificateIfAvailable(
			final Node receiverTransportNode,
			final Node resolvedReceiverActionBindingNode) throws DiscoveryException {
		try {
			final Node certIdNode = unsafeXPath(receiverTransportNode,
					"child::cpp3:TransportReceiver/cpp3:TransportServerSecurity/cpp3:ServerCertificateRef/@certId");
			if (certIdNode == null) {
				return null;  // no certificate reference found
			}
			final String certId = certIdNode.getTextContent();

			final Node certificateNode = safeXPath(resolvedReceiverActionBindingNode,
					"../../../cpp3:Certificate[@certId=\"{0}\"]", certId);
			final Node x509certificateNode = unsafeXPath(certificateNode,
					"ds:KeyInfo/ds:X509Data/ds:X509Certificate/text()");
			if (x509certificateNode == null) {
				return null;  // no embedded certificate found
			}
			
			final String certificateBase64String = x509certificateNode.getTextContent();

			final byte[] certificateBytes = DatatypeConverter.parseBase64Binary(certificateBase64String);
			final InputStream certificateInputStream = new ByteArrayInputStream(certificateBytes);

			X509Certificate certificate;
			certificate = (X509Certificate) certificateFactory.generateCertificate(certificateInputStream);

			// check subjectName
			final Node x509SubjectName = safeXPath(certificateNode,
					"ds:KeyInfo/ds:X509Data/ds:X509SubjectName/text()");

			final String certificateSubjectName = certificate.getSubjectX500Principal().getName();
			
			if (!certificateSubjectName.trim().equals(x509SubjectName.getTextContent().trim())) {
				throw new DiscoveryException("Certificate subject name and //ds:X509SubjectName do not match: " +
						certificateSubjectName + " vs. " + x509SubjectName.getTextContent());
			}

			
			return certificate;
		} catch (final XPathExpressionException e) {
			throw new DiscoveryException("Invalid XPath expression: " + e.getMessage(), e);
		} catch (final CertificateException e) {
			throw new DiscoveryException("Invalid //cpp:Certificate/ds:KeyInfo/ds:X509Data/ds:X509Certificate: " + e.getMessage(), e);
		} catch (final XPathNullResultException e) {
			throw new DiscoveryException("Invalid XPath expression: " + e.getMessage(), e);
		}
	}

}
