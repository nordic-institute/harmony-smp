/*
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.peppol.start.sml;

import eu.peppol.start.exception.DocumentTypeNotAcceptedException;
import eu.peppol.start.exception.LookupException;
import eu.peppol.start.exception.UnknownEndpointException;
import eu.peppol.start.util.Configuration;
import eu.peppol.start.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.busdox.servicemetadata.types.EndpointType;
import org.busdox.servicemetadata.types.ProcessType;
import org.busdox.servicemetadata.types.ServiceMetadataType;
import org.busdox.servicemetadata.types.SignedServiceMetadataType;
import org.w3c.dom.Document;

/**
 * The SMLLookup aims to hold the entire processes required for getting
 * the information of a business identifier using the SML service.
 *
 * @author  Jose Gorvenia Narvaez(jose@alfa1lab.com)
 *          Marcelo Tataje Salinas(marcelo@alfa1lab.com)
 *          George Reátegui Ravina (jorge@alfa1lab.com)
 */
public class SMLLookup {

    /**
     * Encoding GZIP.
     */
    private static final String ENCODING_GZIP = "gzip";

    /**
     * Encoding DEFLATE.
     */
    private static final String ENCODING_DEFLATE = "deflate";

    /**
     * Configuration instance used to get the application properties.
     */
    private static Configuration config;

    /**
     * Logger to follow this class behavior.
     */
    private static org.apache.log4j.Logger logger4J =
            org.apache.log4j.Logger.getLogger(SMLLookup.class);
    private static SMLLookup instance;

    /*
     * Constructor Method.
     */
    private SMLLookup() {
        config = Configuration.getInstance();
    }

    /**
     * Return the instance of the class.
     * @return Instance of SMLLookup.
     */
    public synchronized static SMLLookup getInstance() {
        if (instance == null) {
            instance = new SMLLookup();
        }
        return instance;
    }

    /**
     * Generate the url to get the service metadata
     * group of the Business Identifier.
     * @param smlURL            Service Metadata Locator url address.
     * @param businessIdScheme  Schema of the Business Identifier.
     * @param businessIdValue   Business Identifier.
     * @return URL generated.
     */
    private String generateBusinessIdURL(String smlURL,
            String businessIdScheme, String businessIdValue) {

        String businessIdURL = null;

        try {
            String dns = "B-" + Util.calculateMD5(businessIdValue.toLowerCase())
                    + "." + businessIdScheme
                    + "." + smlURL;

            businessIdURL = "http://" + dns + "/"
                    + URLEncoder.encode(businessIdScheme + "::" + businessIdValue, "UTF-8");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SMLLookup.class.getName()).log(Level.SEVERE, ex.getMessage());
            logger4J.error(ex.getMessage(), ex);
            throw new LookupException();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SMLLookup.class.getName()).log(Level.SEVERE, ex.getMessage());
            logger4J.error(ex.getMessage(), ex);
            throw new LookupException();
        }
        return businessIdURL;
    }

    /**
     * Generate the url to get the metadata of a Document type
     * supported for a Business Identifier.
     * @param businessIdURL     Url of Business Identifier.
     * @param documentIdScheme  Schema of the Document.
     * @param documentIdValue   Document Identifier
     * @return URL generated.
     */
    private String generateServiceURL(String businessIdURL,
            String documentIdScheme, String documentIdValue) {

        String serviceURL = null;

        try {
            serviceURL = businessIdURL
                    + "/services/"
                    + URLEncoder.encode(documentIdScheme + "::" + documentIdValue, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SMLLookup.class.getName()).log(Level.SEVERE, ex.getMessage());
            logger4J.error(ex.getMessage(), ex);
            throw new LookupException();
        }
        return serviceURL;
    }

    /**
     * Open a connection from an url.
     * @param address   Url address to open the connection.
     * @return opened connection.
     */
    private HttpURLConnection openConnection(String address) {

        HttpURLConnection httpConn = null;

        try {
            URL url = new URL(address);

            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.connect();
        } catch (IOException ex) {
            Logger.getLogger(SMLLookup.class.getName()).log(Level.SEVERE, ex.getMessage());
            logger4J.error(ex.getMessage(), ex);
            throw new LookupException();
        }
        return httpConn;
    }

    /**
     * Check the status of an url address.
     * @param address   URL address to check.
     * @return boolean true if the url is enable.
     */
    private boolean checkAddressStatus(String address) {
        int code = 404;

        HttpURLConnection conn = null;

        try {
            URL url = new URL(address);

            conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            code = conn.getResponseCode();
        } catch (IOException ex) {
            Logger.getLogger(SMLLookup.class.getName()).log(Level.SEVERE, ex.getMessage());
            logger4J.error(ex.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return (code != 200) ? false : true;
    }

    /**
     * Close the connection.
     * @param conn  Connection to close.
     */
    private void closeConnection(HttpURLConnection conn) {
        if (conn != null) {
            conn.disconnect();
        }
    }

    /**
     * Get the InputStream from the connection and read it.
     * @param conn  Opened connection.
     * @return The contend of the InputStream as String.
     */
    private String getMetadata(HttpURLConnection conn) {

        InputStream in = null;
        InputStream result = null;
        BufferedReader buffReader = null;
        StringBuilder strBuffer = null;

        try {
            String encoding = conn.getContentEncoding();
            in = conn.getInputStream();

            if (encoding != null && encoding.equalsIgnoreCase(ENCODING_GZIP)) {
                result = new GZIPInputStream(in);
            } else if (encoding != null && encoding.equalsIgnoreCase(ENCODING_DEFLATE)) {
                result = new InflaterInputStream(in);
            } else {
                result = in;
            }

            buffReader = new BufferedReader(new InputStreamReader(result));

            String line = null;
            strBuffer = new StringBuilder();
            while ((line = buffReader.readLine()) != null) {
                strBuffer.append(line).append("\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(SMLLookup.class.getName()).log(Level.SEVERE, ex.getMessage());
            logger4J.error(ex.getMessage(), ex);
            throw new LookupException();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(SMLLookup.class.getName()).log(Level.SEVERE, ex.getMessage());
                    logger4J.error(ex.getMessage(), ex);
                }
            }
            if (result != null) {
                try {
                    result.close();
                } catch (IOException ex) {
                    Logger.getLogger(SMLLookup.class.getName()).log(Level.SEVERE, ex.getMessage());
                    logger4J.error(ex.getMessage(), ex);
                }
            }
            if (buffReader != null) {
                try {
                    buffReader.close();
                } catch (IOException ex) {
                    Logger.getLogger(SMLLookup.class.getName()).log(Level.SEVERE, ex.getMessage());
                    logger4J.error(ex.getMessage(), ex);
                }
            }
        }
        return (strBuffer != null) ? strBuffer.toString() : null;
    }

    /**
     * Parse the metadata document with jaxb.
     * @param document  Metadata document.
     * @return ServiceMetadataType object instantiated.
     */
    private ServiceMetadataType getServiceMetadata(Document document) {

        ServiceMetadataType metaType = null;
        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(SignedServiceMetadataType.class).createUnmarshaller();
            JAXBElement<SignedServiceMetadataType> root = unmarshaller.unmarshal(document, SignedServiceMetadataType.class);
            metaType = root.getValue().getServiceMetadata();
        } catch (JAXBException ex) {
            Logger.getLogger(SMLLookup.class.getName()).log(Level.SEVERE, ex.getMessage());
            logger4J.error(ex.getMessage(), ex);
            throw new LookupException();
        }
        return metaType;
    }

    /**
     * Get the endpoint url Address from a Business Identifier.
     * @param smlUrl                Service Metadata Locator url address.
     * @param businesssIdScheme     Scheme of the Business Identifier.
     * @param businesssIdValue      Business Identifier.
     * @param documentIdScheme      Scheme of the Document Identifier.
     * @param documentIdValue       Document Identifier.
     * @return Endpoint url address.
     */
    public String getEndpointAddress(String smlUrl,
            String businesssIdScheme, String businesssIdValue,
            String documentIdScheme, String documentIdValue) {

        String address = null;

        String businessIdURL = generateBusinessIdURL(smlUrl,
                businesssIdScheme, businesssIdValue);
        String documentURL = generateServiceURL(businessIdURL,
                documentIdScheme, documentIdValue);

        if (checkAddressStatus(businessIdURL)) {
            if (checkAddressStatus(documentURL)) {
                HttpURLConnection smlConn = openConnection(documentURL);
                String metadata = getMetadata(smlConn);
                closeConnection(smlConn);
                Document document = Util.parseStringtoDocument(metadata);

                ServiceMetadataType serviceMetadata = getServiceMetadata(document);

                address = serviceMetadata.getServiceInformation().getProcessList().getProcess().get(0).getServiceEndpointList().getEndpoint().get(0).getEndpointReference().getAddress().getValue();
            } else {
                Logger.getLogger(SMLLookup.class.getName()).log(Level.SEVERE, "DocumentTypeNotAccepted");
                logger4J.debug("DocumentTypeNotAccepted");
                throw new DocumentTypeNotAcceptedException();
            }
        } else {
            Logger.getLogger(SMLLookup.class.getName()).log(Level.SEVERE, "UnknownEndpointException");
            logger4J.debug("UnknownEndpointException");
            throw new UnknownEndpointException();
        }
        return address;
    }

    /**
     * Get the certificate from a Business Identifier.
     * @param smlUrl                Service Metadata Locator url address.
     * @param businesssIdScheme     Scheme of the Business Identifier.
     * @param businesssIdValue      Business Identifier.
     * @param documentIdScheme      Scheme of the Document Identifier.
     * @param documentIdValue       Document Identifier.
     * @param processIdScheme       Scheme of the Process Identifier.
     * @param processIdValue        Process Identifier.
     * @return certificate as a String.
     */
    public String getEndpointCertificate(String smlUrl,
            String businesssIdScheme, String businesssIdValue,
            String documentIdScheme, String documentIdValue,
            String processIdScheme, String processIdValue) {

        String certificate = null;

        String businessIdURL = generateBusinessIdURL(smlUrl,
                businesssIdScheme, businesssIdValue);
        String documentURL = generateServiceURL(businessIdURL,
                documentIdScheme, documentIdValue);

        if (checkAddressStatus(businessIdURL)) {
            if (checkAddressStatus(documentURL)) {
                HttpURLConnection smlConn = openConnection(documentURL);
                String metadata = getMetadata(smlConn);
                closeConnection(smlConn);
                Document document = Util.parseStringtoDocument(metadata);

                ServiceMetadataType serviceMetadata = getServiceMetadata(document);

                List<ProcessType> processes = serviceMetadata.getServiceInformation().getProcessList().getProcess();

                for (ProcessType process : processes) {
                    if (processIdScheme.equals(process.getProcessIdentifier().getScheme())
                            && processIdValue.equals(process.getProcessIdentifier().getValue())) {
                        EndpointType enpointType = process.getServiceEndpointList().getEndpoint().get(0);
                        certificate = enpointType.getCertificate();
                        break;
                    }
                }
            } else {
                Logger.getLogger(SMLLookup.class.getName()).log(Level.INFO, "DocumentTypeNotAccepted");
                logger4J.debug("DocumentTypeNotAccepted");
                throw new DocumentTypeNotAcceptedException();
            }
        } else {
            Logger.getLogger(SMLLookup.class.getName()).log(Level.SEVERE, "UnknownEndpointException");
            logger4J.debug("UnknownEndpointException");
            throw new UnknownEndpointException();
        }
        return certificate;
    }    
}
