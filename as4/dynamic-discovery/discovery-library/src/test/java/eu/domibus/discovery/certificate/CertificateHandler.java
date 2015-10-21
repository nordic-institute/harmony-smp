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

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.w3c.dom.Node;

import eu.domibus.discovery.DiscoveryException;
import eu.domibus.discovery.handlers.DefaultMetadataHandler;

/**
 * This handler extracts the resolved certificate if any.
 * 
 * <p>Extracts information from already extracted certificates.</p>
 * 
 * @see SMPCertificateHandler
 * @see CPPCertificateHandler
 * @see #RECEIVER_CERTIFICATE_SUBJECT_CN
 * @see #SENDER_CERTIFICATE_SUBJECT_CN
 * @author Thorsten Niedzwetzki
 */
public class CertificateHandler extends DefaultMetadataHandler {

	public static final String SENDER_CERTIFICATE = "senderCertificate";
	public static final String SENDER_CERTIFICATE_SUBJECT_CN = "senderCertificateSubjectCommonName";

	public static final String RECEIVER_CERTIFICATE = "receiverCertificate";
	public static final String RECEIVER_CERTIFICATE_SUBJECT_CN = "receiverCertificateSubjectCommonName";

	final Map<String,String> receiverSubjectFieldsToExtract;
	final Map<String,String> senderSubjectFieldsToExtract;


	/**
	 * Defines which certificate fields should be extracted.
	 */
	public CertificateHandler() {
		receiverSubjectFieldsToExtract = new HashMap<String,String>();
		receiverSubjectFieldsToExtract.put("CN", RECEIVER_CERTIFICATE_SUBJECT_CN);
		
		senderSubjectFieldsToExtract = new HashMap<String,String>();
		senderSubjectFieldsToExtract.put("CN", SENDER_CERTIFICATE_SUBJECT_CN);
	}


	/**
	 * Checks whether any certificates have been extracted.
	 */
	@Override
	public boolean canHandle(final Map<String, Node> nodes, final Map<String, Object> metadata) {
		return metadata.containsKey(RECEIVER_CERTIFICATE) || metadata.containsKey(SENDER_CERTIFICATE);
	}


	/**
	 * Reads and extracts information from the //smp:Endpoint/smp:Certificate element.
	 */
	@Override
	public boolean resolveMetadata(
			final Map<String,Node> nodes,
			final Map<String,Object> metadata) throws DiscoveryException {
		
		if (metadata.containsKey(RECEIVER_CERTIFICATE)) {
			extractCertificateFields(metadata, RECEIVER_CERTIFICATE, receiverSubjectFieldsToExtract);
		}
		
		if (metadata.containsKey(SENDER_CERTIFICATE)) {
			extractCertificateFields(metadata, SENDER_CERTIFICATE, senderSubjectFieldsToExtract);
		}

		return true;
	}


	/**
	 * Extracts specific fields from the certificate's name.
	 * 
	 * @param metadata with the certificate.  Will receive extracted fields.
	 * @param certificateKey Metadata key where the certificate is being stored
	 * @param certificateFields Names of fields to extract and metadata keys where to store fields
	 * @throws DiscoveryException on any receiver's certificate name error.
	 */
	private void extractCertificateFields(
			final Map<String, Object> metadata,
			final String certificateKey,
			final Map<String,String> certificateFields) throws DiscoveryException {

		final X509Certificate certificate = (X509Certificate) metadata.get(certificateKey);
		if (certificate != null)
		try {
			final String certificateName = certificate.getSubjectX500Principal().getName();

			final LdapName ldapName = new LdapName(certificateName);
			for(final Rdn rdn: ldapName.getRdns()) {
				if (certificateFields.containsKey(rdn.getType())) {
					metadata.put(certificateFields.get(rdn.getType()), rdn.getValue());	
				}
			}
		} catch (final InvalidNameException e) {
			throw new DiscoveryException("Invalid certificate name: " + e.getMessage(), e);
		}
	}

}
