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
package eu.domibus.discovery.handlers.smp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.xml.bind.DatatypeConverter;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.domibus.discovery.DiscoveryException;
import eu.domibus.discovery.Metadata;
import eu.domibus.discovery.handlers.DefaultMetadataHandler;
import eu.domibus.discovery.handlers.ReceiverLocatorHandler;
import eu.domibus.discovery.util.DefaultNamespaceContext;
import eu.domibus.discovery.util.FileUtils;

public class SMPHandler extends DefaultMetadataHandler {

	private static final Logger log = Logger.getLogger(SMPHandler.class);
	
	protected static final String SMP_NAMESPACE = "http://busdox.org/serviceMetadata/publishing/1.0/";
	protected static final String SMP_SERVICE_ELEMENT = "ServiceMetadata";
	protected static final String SMP_SIGNED_SERVICE_ELEMENT = "SignedServiceMetadata";
	protected static final String SMP_GROUP_ELEMENT = "ServiceGroup";
	protected static final String SMP_SIGNATURE_ELEMENT = "Signature";
	
	public static final String SERVICE_INFORMATION = "serviceInformation";
	public static final String PROCESS = "process";
	public static final String ENDPOINT = "endpoint";
	public static final String ENDPOINT_ADDRESS = "endpointAddress";
	
	@Override
	public void configureNamespaces(final DefaultNamespaceContext namespaces) {
		super.configureNamespaces(namespaces);
		namespaces.addNamespace("smp", SMP_NAMESPACE);
		namespaces.addNamespace("ids", "http://busdox.org/transport/identifiers/1.0/");
		namespaces.addNamespace("wsa", "http://www.w3.org/2005/08/addressing");
		namespaces.addNamespace("ds", "http://www.w3.org/2000/09/xmldsig#");
	}

	@Override
	public boolean canHandle(final Map<String,Node> nodes, final Map<String,Object> metadata) {
		final Node receiverMetadataRoot = nodes.get(ReceiverLocatorHandler.NODE_RECEIVER_METADATA_ROOT);
		return receiverMetadataRoot != null &&
				SMP_NAMESPACE.equals(receiverMetadataRoot.getNamespaceURI()) &&
				(SMP_SERVICE_ELEMENT.equals(receiverMetadataRoot.getLocalName()) ||
						SMP_SIGNED_SERVICE_ELEMENT.equals(receiverMetadataRoot.getLocalName()) ||
						SMP_GROUP_ELEMENT.equals(receiverMetadataRoot.getLocalName())) &&
				metadata.containsKey(Metadata.PROCESS_ID) &&
				metadata.containsKey(Metadata.DOCUMENT_OR_ACTION_ID) &&
				nodes.containsKey(ReceiverLocatorHandler.NODE_RECEIVER_METADATA_ROOT);
	}

	@Override
	public boolean resolveMetadata(Map<String, Node> nodes,
			Map<String, Object> metadata) throws DiscoveryException {
		
		Node receiverMetadataRoot = nodes.get(ReceiverLocatorHandler.NODE_RECEIVER_METADATA_ROOT);
		
		if(SMP_GROUP_ELEMENT.equals(receiverMetadataRoot.getLocalName())) {
			
			try {
				final NodeList serviceMetadataReferences = safeXPathNodeList(receiverMetadataRoot,
						"/smp:ServiceGroup" +
						"/smp:ServiceMetadataReferenceCollection" +
						"/smp:ServiceMetadataReference/@href");
				
				for(int counter = 0; counter < serviceMetadataReferences.getLength(); counter++) {
					if(!metadata.containsKey(Metadata.ENDPOINT_ADDRESS)) {
						String referencedMetadataURI = serviceMetadataReferences.item(counter).getTextContent();
						log.trace(MessageFormat.format(
								"Getting SignedServiceMetadata for {0} from {1}",
								metadata.get(Metadata.DOCUMENT_OR_ACTION_ID), referencedMetadataURI));
						
						Node currentReferencedMetadataRoot = documentBuilder.parse(
								FileUtils.openStream(referencedMetadataURI)).getDocumentElement();
						log.trace("Referenced metadata format: " + currentReferencedMetadataRoot.getLocalName());
						
						Map<String, Node> currentNode = new HashMap<String, Node>();
						currentNode.put(ReceiverLocatorHandler.NODE_RECEIVER_METADATA_ROOT, currentReferencedMetadataRoot);
						
						resolveMetadata(currentNode, metadata);
						
						if(metadata.containsKey(Metadata.ENDPOINT_ADDRESS)) {
							currentNode.remove(ReceiverLocatorHandler.NODE_RECEIVER_METADATA_ROOT);
							nodes.putAll(currentNode);
						}
					}
				}
				
			} catch (final IOException e) {
				throw new DiscoveryException("Cannot download SMP service group metadata file: " + e.getMessage(), e);
			} catch (final ClassNotFoundException e) {
				throw new DiscoveryException("Cannot find class of resource" + e.getMessage(), e);
			} catch (final SAXException e) {
				throw new DiscoveryException("Cannot parse SMP service group metadata file: " + e.getMessage(), e);
			} catch (final XPathExpressionException e) {
				throw new DiscoveryException("Invalid XPath expression: " + e.getMessage(), e);
			}
			
		} else if(SMP_SERVICE_ELEMENT.equals(receiverMetadataRoot.getLocalName())) {
			
			try {
				handleServiceElement(nodes, metadata);
			} catch (XPathExpressionException e) {
				throw new DiscoveryException("Invalid XPath expression: " + e.getMessage(), e);
			}
			
		} else if(SMP_SIGNED_SERVICE_ELEMENT.equals(receiverMetadataRoot.getLocalName())) {
			try{
				// Validate Signature
				final Node signature = unsafeXPath(receiverMetadataRoot, "descendant::ds:Signature");
				metadata.put(Metadata.SIGNATURE_VALIDITY, false);
				
				if (signature != null) {
					metadata.put(Metadata.SIGNATURE_VALIDITY, isSignatureValid(receiverMetadataRoot, signature));
				}
			} catch (XPathExpressionException e) {
				throw new DiscoveryException("Invalid XPath expression at the time of signature verification: " + e.getMessage(), e);
			}
			
			try {
				handleServiceElement(nodes, metadata);
			} catch (XPathExpressionException e) {
				throw new DiscoveryException("Invalid XPath expression: " + e.getMessage(), e);
			}
			
			if((Boolean) metadata.get(Metadata.SIGNATURE_VALIDITY) != true) {
				nodes.put(SERVICE_INFORMATION, null);
				nodes.put(PROCESS, null);
				nodes.put(ENDPOINT, null);
				nodes.put(ENDPOINT_ADDRESS, null);
		        metadata.put(Metadata.SERVICE_ACTIVATION_DATE, null);
		        metadata.put(Metadata.SERVICE_EXPIRATION_DATE, null);
				metadata.put(Metadata.ENDPOINT_ADDRESS, null);				
			}
			
		} else {
			throw new DiscoveryException("Invalid Entry within the received data of the SMP: Neither " + 
					SMP_GROUP_ELEMENT + " nor " + SMP_SERVICE_ELEMENT + " or " + SMP_SIGNED_SERVICE_ELEMENT + " were present!");
		}
		
		return true;
	}
	
	private void handleServiceElement(Map<String, Node> nodes,
			Map<String, Object> metadata) throws XPathExpressionException, DiscoveryException {
		
		Node documentRoot = nodes.get(ReceiverLocatorHandler.NODE_RECEIVER_METADATA_ROOT);
		
		// Handle SMP redirect feature
		final Node redirect = unsafeXPath(documentRoot, "descendant::smp:Redirect");
		if (redirect != null) {
			documentRoot = handleRedirect(redirect);
		}
		
		boolean lookupEndpointAddress = true;
		
		while(lookupEndpointAddress){

			flexibleResolveMetadata(nodes, metadata, false, false, false);
			
			if(metadata.get(ENDPOINT_ADDRESS) != null) { 
				log.trace("Endpoint address discovered, all data matched.");
				break;
			}
			
			flexibleResolveMetadata(nodes, metadata, false, false, true);
			if(metadata.get(ENDPOINT_ADDRESS) != null) { 
				log.trace("Endpoint address discovered, used wildcard entry for toPartyId.");
				break;
			}

			flexibleResolveMetadata(nodes, metadata, true, false, true);
			if(metadata.get(ENDPOINT_ADDRESS) != null) { 
				log.trace("Endpoint address discovered, used wildcard entry for toPartyId and action.");
				break;
			}
			
			flexibleResolveMetadata(nodes, metadata, true, true, true);
			if(metadata.get(ENDPOINT_ADDRESS) != null) { 
				log.trace("Endpoint address discovered, used wildcard entry for toPartyId, service and action.");
				break;
			}

			lookupEndpointAddress = false;
		}		
	}

	/**
	 * Handle SMP redirect feature.
	 * Load SMP document the redirect element points to.
	 * 
	 * @param redirectElement Redirect element that contains a @href attribute pointing to an SMP document
	 * @return the root element of the SMP document the Redirect points to
	 * @throws DiscoveryException if the SMP document the Redirect points to cannot be loaded and/or parsed
	 */
	private Node handleRedirect(final Node redirectElement) throws DiscoveryException, XPathExpressionException {
		final String redirectURL = safeXPath(redirectElement, "@href").getTextContent();
		try {
			return documentBuilder.parse(FileUtils.openStream(redirectURL)).getDocumentElement();
		} catch (final MalformedURLException e) {
			throw new DiscoveryException("Malformed SMP Redirect/@href URL: " + e.getMessage(), e);
		} catch (final SAXException e) {
			throw new DiscoveryException("Cannot parse SMP file: " + e.getMessage(), e);
		} catch (final IOException e) {
			throw new DiscoveryException("Cannot load SMP file: " + e.getMessage(), e);
		} catch (final ClassNotFoundException e) {
			throw new DiscoveryException("Illegal class name in Redirect/@href URL: " + e.getMessage(), e);
		}
	}

	private void flexibleResolveMetadata(final Map<String,Node> nodes, 
			final Map<String,Object> metadata,
			boolean documentOrActionIdOptional,
			boolean processIdOptional,
			boolean participantIdOptional) throws XPathExpressionException, DiscoveryException {
		
		final String processId = processIdOptional ? "*" : (String) metadata.get(Metadata.PROCESS_ID);
		final String documentOrActionId = documentOrActionIdOptional ? "*" : (String) metadata.get(Metadata.DOCUMENT_OR_ACTION_ID);
		final String participantId = participantIdOptional ? "*" : (String) metadata.get(Metadata.TO_PARTY_ID);
		
		Node documentRoot = nodes.get(ReceiverLocatorHandler.NODE_RECEIVER_METADATA_ROOT);
		
		// Handle SMP redirect feature
		final Node redirect = unsafeXPath(documentRoot, "descendant::smp:Redirect");
		if (redirect != null) {
			documentRoot = handleRedirect(redirect);
		}
		
		final Node serviceInformation;
		if(participantId != null){
			serviceInformation = unsafeXPath(documentRoot,
				"descendant::smp:ServiceInformation[ids:DocumentIdentifier/text()=\"{0}\"][ids:ParticipantIdentifier/text()=\"{1}\"]", documentOrActionId, participantId);
		} else {
			serviceInformation = unsafeXPath(documentRoot,
					"descendant::smp:ServiceInformation[ids:DocumentIdentifier/text()=\"{0}\"]", documentOrActionId);			
		}
		
		if(serviceInformation != null)
		{
			final Node process = unsafeXPath(serviceInformation,
					"descendant::smp:Process[ids:ProcessIdentifier/text()=\"{0}\"]", processId);
			
	        if(metadata.containsKey(Metadata.TRANSPORT_PROFILE_ID)){
	            final String transportProfile = (String) metadata.get(Metadata.TRANSPORT_PROFILE_ID);
	            final Node endpoint = unsafeXPath(process, "descendant::smp:Endpoint[@transportProfile=\"{0}\"]",transportProfile);
	        }
	        
	        if(process != null)
	        {
	        
		        final Node endpoint = safeXPath(process, "descendant::smp:Endpoint");
		
				final Node endpointAddress = safeXPath(endpoint, "descendant::wsa:Address/text()");
				log.trace("Found endpoint address " + endpointAddress.getTextContent());
		
		        final Node serviceActivationDate = safeXPath(endpoint,"descendant::smp:ServiceActivationDate/text()");
		        final Node serviceExpirationDate = safeXPath(endpoint,"descendant::smp:ServiceExpirationDate/text()");
		
				nodes.put(SERVICE_INFORMATION, serviceInformation);
				nodes.put(PROCESS, process);
				nodes.put(ENDPOINT, endpoint);
				nodes.put(ENDPOINT_ADDRESS, endpointAddress);
		        metadata.put(Metadata.SERVICE_ACTIVATION_DATE,serviceActivationDate);
		        metadata.put(Metadata.SERVICE_EXPIRATION_DATE,serviceExpirationDate);
				metadata.put(Metadata.ENDPOINT_ADDRESS, endpointAddress.getTextContent());
			}
		}
	}
	
	/**
	 * Handle signature within SMP.
	 * 
	 * @param documentRoot the root element of the SMP document
	 * @param signatureNode the signature element of the SMP document
	 * @return the root element of the SMP document the Redirect points to
	 * @throws DiscoveryException if the SMP document the Redirect points to cannot be loaded and/or parsed
	 * @throws XPathExpressionException if a used XPath expression ends in an exception
	 */	
	private boolean isSignatureValid(Node documentRoot, Node signatureNode) throws DiscoveryException, XPathExpressionException {
		
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
		ByteArrayInputStream bais = null;
		
		try {
			final Node keyInfo = unsafeXPath(documentRoot, "descendant::ds:KeyInfo");
			
			if(keyInfo != null) {
				final Node x509Data = unsafeXPath(documentRoot, "descendant::ds:X509Data");
				final Node certificate = unsafeXPath(x509Data, "descendant::ds:X509Certificate");
				
				if(certificate != null) {
					byte[] decodedBytes = DatatypeConverter.parseBase64Binary(certificate.getTextContent());
					
					CertificateFactory certFac = CertificateFactory.getInstance("X509");
					bais = new ByteArrayInputStream(decodedBytes);
					X509Certificate cert = (X509Certificate) certFac.generateCertificate(bais);
					
					DOMValidateContext valContext = new DOMValidateContext(cert.getPublicKey(), signatureNode);
					
					XMLSignature signature = fac.unmarshalXMLSignature(valContext);
					
					boolean coreValidity = signature.validate(valContext);
					 
			        if (coreValidity == false) {
			        	log.trace("Signature failed core validation");
			            boolean sv = signature.getSignatureValue().validate(valContext);
			            
			            // check the validation status of each Reference
			            log.trace("Signature validation status: " + sv);
			            Iterator i = signature.getSignedInfo().getReferences().iterator();
			            for (int j=0; i.hasNext(); j++) {
			                boolean refValid = ((Reference) i.next()).validate(valContext);
			                log.trace("ref["+j+"] validity status: " + refValid);
			            }
			            return false;
			        } else {
			        	log.trace("Signature on SMP is deemed to be valid.");
			            return true;
			        }
		        } else {
		        	log.trace("Signature on SMP is deemed to be invalid! Unable to find signing certificate.");
		        	return false;
		        }
			} else {
				log.trace("Signature on SMP is deemed to be invalid! Unable to find mandatory key info field within the signature.");
				return false;
			}
		} catch(XPathExpressionException e) {
			throw e;
		} catch (XMLSignatureException e) {
			throw new DiscoveryException("Unable to validate signature: " + e.getMessage());
		} catch (MarshalException e) {
			throw new DiscoveryException("Unable to unmarshal signature: " + e.getMessage());
		} catch (CertificateException e) {
			throw new DiscoveryException("Unable to parse X509 Certificate used within the signature: " + e.getMessage());
		} finally {
			if(bais != null) {
				try {
					bais.close();
				} catch (IOException e) {
					throw new DiscoveryException("Unable to close Stream for Certificate Inputstream: " + e.getMessage());
				}
			}
		}
	}
}
