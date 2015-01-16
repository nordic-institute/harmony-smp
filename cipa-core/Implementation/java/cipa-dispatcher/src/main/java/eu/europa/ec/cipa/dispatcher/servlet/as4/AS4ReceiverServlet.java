package eu.europa.ec.cipa.dispatcher.servlet.as4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.net.ssl.HostnameVerifier;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.cipa.dispatcher.endpoint_interface.as4.service.AS4PModeService;
import eu.europa.ec.cipa.dispatcher.handler.AS4Handler;
import eu.europa.ec.cipa.dispatcher.servlet.AbstractReceiverServlet;
import eu.europa.ec.cipa.dispatcher.util.KeystoreUtil;
import eu.europa.ec.cipa.dispatcher.util.PropertiesUtil;
import org.xml.sax.SAXException;

public class AS4ReceiverServlet extends AbstractReceiverServlet

{
    public static final Logger s_aLogger = LoggerFactory.getLogger(AS4ReceiverServlet.class);

    public void init() throws UnavailableException {
        classType = "AS4";
        super.init();
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        if (isIncomingAS4Message(req))
            handleIncomingAS4Message(req, resp);
        else {
            try {
                s_aLogger.error(HttpServletResponse.SC_BAD_REQUEST + " Missing necessary AS4 headers");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing necessary AS4 headers");
            } catch (Exception e) {
            }
            return;
        }
    }

    private boolean isIncomingAS4Message(HttpServletRequest req) {
        // TODO: what are the necessary fields in AS4?
        // if (req.getHeader("as2-from")!=null && req.getHeader("as2-to")!=null
        // && req.getHeader("message-id")!=null && /*
        // req.getHeader("recipient-address")!=null && */
        // req.getHeader("disposition-notification-to")!=null)
        return true;
        // else
        // return false;
    }

    private void handleIncomingAS4Message(HttpServletRequest req, HttpServletResponse resp) {

        try {
            // we wrap the inputstream into a ByteArrayInputStream so we can
            // read it multiple times (the AS2 endpoint will need to read it
            // later)
            ByteArrayOutputStream buffer = copyToMarkableBuffer(req.getInputStream());
            ByteArrayInputStream input = new ByteArrayInputStream(buffer.toByteArray());

            // parse and extract input info
            Map<String, String> inputMap = parseInput(input, resp);

            String senderIdentifier = (String) inputMap.get("senderIdentifier");
            String processIdentifier = (String) inputMap.get("processIdentifier");
            String documentIdentifier = (String) inputMap.get("documentIdentifier");
            String receiverIdentifier = (String) inputMap.get("receiverIdentifier");

            // req.

            // check certificate trust
            byte[] binaryCert = Base64.decode((String) inputMap.get("certificate"));
            X509Certificate senderCert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(binaryCert));
            if (!validateCertificate(resp, senderCert)) return;

            Properties properties = PropertiesUtil.getProperties();
            String as4TruststorePath = properties.getProperty(PropertiesUtil.AS4_TRUSTSTORE_PATH);
            String as4TruststorePassword = properties.getProperty(PropertiesUtil.AS4_TRUSTSTORE_PASSWORD);
            KeystoreUtil util = new KeystoreUtil(as4TruststorePath, as4TruststorePassword);
            //

            // find the certificate's CN
            String senderCN = KeystoreUtil.extractCN(senderCert);

            if (!senderCN.equalsIgnoreCase(((String) inputMap.get("senderIdentifier")))) {
                s_aLogger.error(HttpServletResponse.SC_BAD_REQUEST + " The Sender Identifier value in your message doesn't match your certificate's Common Name");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "The Sender Identifier value in your message doesn't match your certificate's Common Name");
            }

            // install the sender public key inside the truststore of the AS4 implementation
            util.installNewPartnerCertificate(senderCert, senderCN);
            // make the request's inputstream available to be read again
            AS4PModeService service = new AS4PModeService();
            // create Pmode for receiving the message
            s_aLogger.debug("creating/ updating PMODE for Sender :" + senderIdentifier + "Receiver : " + receiverIdentifier + "Process : " + processIdentifier + "Document Identifier " + documentIdentifier);
            service.createPartner(senderIdentifier, receiverIdentifier, processIdentifier, documentIdentifier, "");

            // create Pmode for sending receipt back (Only needed to apply
            // security.)
            s_aLogger.debug("creating/ updating PMODE for receipt for Sender :" + receiverIdentifier + "Receiver : " + senderIdentifier + "Process : " + processIdentifier + "Document Identifier " + documentIdentifier + "endpoit " + req.getRequestURI());
            service.createPartner(receiverIdentifier, senderIdentifier, processIdentifier, documentIdentifier, req.getRequestURI());
            input.reset();
            // now we finally redirect the request to the AS4 endpoint
            forwardToAS4Endpoint(req, resp, buffer);

        } catch (Exception e) {
            try {
                s_aLogger.error(e.getMessage(), e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (IOException ioE) {
                s_aLogger.error(ioE.getMessage(), ioE);
            }
        }
    }

    private Map<String, String> parseInput(ByteArrayInputStream input, HttpServletResponse resp) throws IOException {
        input.markSupported();
        input.mark(Integer.MAX_VALUE);

        // when the message is only signed and not encrypted, here it works with both "multipart/signed" and "application/pkcs7-signature"
        Map<String, String> result = parseInput(input, "multipart/signed");
        if (result == null) {
            result = parseInput(input, "multipart/related");
        }

        if (result == null) {
            s_aLogger.error("Couldn't parse the incoming AS4 message. Returning http code " + HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Couldn't parse the incoming AS4 message");
        }
        return result;
    }

    private Map<String, String> parseInput(ByteArrayInputStream input, String mime) {
        Map<String, String> result = null;
        try {
            MimeMultipart multipart = new MimeMultipart(new ByteArrayDataSource(input, mime));
            Part part_aux, part = null;
            for (int i = 0; i < multipart.getCount(); i++) {
                part_aux = multipart.getBodyPart(i);
                /* application/soap+xml is for SOAP 1.2 and text/xml is for SOAP 1.1 */
                if (part_aux.getContentType() != null && ((part_aux.getContentType().toLowerCase().contains("application/soap+xml") || part_aux.getContentType().toLowerCase().contains("text/xml"))))
                    // this is the only part we are interested in
                    part = part_aux;
            }
            if (part != null) {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                AS4Handler handler = new AS4Handler();
                saxParser.parse(part.getInputStream(), handler);
                result = handler.getResultMap();
            }
        } catch (Exception e) {
            s_aLogger.warn("The input couldn't be parsed with the mime type " + mime);
        }
        return result;
    }

    private void forwardToAS4Endpoint(HttpServletRequest req, HttpServletResponse resp, ByteArrayOutputStream buffer) {

        try {
            String url = getProperties().getProperty(PropertiesUtil.AS4_ENDPOINT_URL);
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, isDebug() ? 3000000 : 30000);
            // 3000sec for tests purposes, 30sec in production
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, isDebug() ? 3000000 : 30000);
            // 3000sec for tests purposes, 30sec in production

            // setting up SSL connection with the AS2 endpoint if specified in
            // properties
            if (url.startsWith("https")) {
                KeyStore trustStore = KeyStore.getInstance("JKS");
                FileInputStream instream = new FileInputStream(new File(getProperties().getProperty(PropertiesUtil.SSL_TRUSTSTORE)));
                try {
                    trustStore.load(instream, getProperties().getProperty(PropertiesUtil.SSL_TRUSTSTORE_PASSWORD).toCharArray());
                } finally {
                    instream.close();
                }

                SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);

                if (isDebug()) // if we are in debug, do not verify that the signer
                // of the SSL certificate is the appropiate one (so
                // we can use self signed certificates in tests)
                {
                    HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
                    socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
                }

                // we retrieve the SSL port from the URL specified by the user
                // in the properties
                String port = url.substring(url.indexOf("://") + 3);
                port = port.substring(0, port.indexOf("/"));
                port = port.split(":")[1];
                Scheme sch = new Scheme("https", Integer.parseInt(port), socketFactory);
                httpclient.getConnectionManager().getSchemeRegistry().register(sch);
            }

            Enumeration<String> headers = req.getHeaderNames();
            String header;
            while (headers.hasMoreElements()) {
                header = headers.nextElement();
                if (!header.equalsIgnoreCase("Content-Length") && !header.equalsIgnoreCase("Transfer-encoding"))
                    post.addHeader(header, req.getHeader(header));
            }

            this.getClass().getResource("/");

            ByteArrayEntity entity = new ByteArrayEntity(buffer.toByteArray());
            post.setEntity(entity);
            OutputStream out = resp.getOutputStream();
            HttpResponse response = httpclient.execute(post);
            HttpEntity resEntity = response.getEntity();

            Header[] _headers = response.getAllHeaders();
            for (Header _header : _headers) {
                if (!_header.getName().equalsIgnoreCase("Content-Length") && !_header.getName().equalsIgnoreCase("Transfer-encoding")) {
                    resp.setHeader(_header.getName(), _header.getValue());
                }
            }

            resEntity.writeTo(out);
            out.close();
        } catch (Exception e) {
            try {
                s_aLogger.error("Returninh HTTP error code " + HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (Exception f) {
            }
        }
    }

}
