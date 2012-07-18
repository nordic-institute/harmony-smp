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
package eu.peppol.start.client;

import eu.peppol.start.soap.SOAPHeaderObject;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.busdox.servicemetadata.types.DocumentIdentifierType;
import org.busdox.servicemetadata.types.ParticipantIdentifierType;
import org.busdox.servicemetadata.types.ProcessIdentifierType;
import org.w3._2009._02.ws_tra.Create;
import org.w3._2009._02.ws_tra.Resource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * The accesspointClient class aims to hold all the processes required for
 * consuming an AccessPoint.
 *
 * @author  Jose Gorvenia Narvaez(jose@alfa1lab.com)
 *          Marcelo Tataje Salinas(marcelo@alfa1lab.com)
 *          George Reátegui Ravina (jorge@alfa1lab.com)
 * 
 */
public class Main {
    
    private static String STANDARD_MESSAGE_ID_PREFIX = "uuid:";
    
    private static String XML_FILE_PATH = null;    
    private static String DOCUMENT_IDENTIFIER_TYPE_VALUE = null;
    private static String DOCUMENT_IDENTIFIER_TYPE_SCHEME = null;
    
    private static String PROCESS_IDENTIFIER_TYPE_VALUE = null;
    private static String PROCESS_IDENTIFIER_TYPE_SCHEME = null;
    
    private static String PARTICIPANT_IDENTIFIER_SCHEME = null;
    private static String SENDER_PARTICIPANT_IDENTIFIER_VALUE = null;
    private static String RECEIVER_PARTICIPANT_IDENTIFIER_VALUE = null;
    private static String ACCESSPOINT_SERVICE_URL = null;
    
    public static void main(String[] args) {
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        
        String xmlFile = XML_FILE_PATH;
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder parser = null;
        try {
            parser = domFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        }

        Document document = null;

        try {
            document = parser.parse(new File(xmlFile));
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Create body = new Create();
        List<Object> objects = body.getAny();
        objects.add(document.getDocumentElement());
        
        SOAPHeaderObject shobject = new SOAPHeaderObject();
        shobject.setChannelIdentifier(null);
        String messageID = STANDARD_MESSAGE_ID_PREFIX + UUID.randomUUID().toString();
        shobject.setMessageIdentifier(messageID);

        DocumentIdentifierType docid = new DocumentIdentifierType();
        docid.setValue(DOCUMENT_IDENTIFIER_TYPE_VALUE);
        docid.setScheme(DOCUMENT_IDENTIFIER_TYPE_SCHEME);
        
        shobject.setDocumentIdentifier(docid);

        ProcessIdentifierType proid = new ProcessIdentifierType();
        proid.setValue(PROCESS_IDENTIFIER_TYPE_VALUE);
        proid.setScheme(PROCESS_IDENTIFIER_TYPE_SCHEME);
        shobject.setProcessIdentifier(proid);

        ParticipantIdentifierType senId = new ParticipantIdentifierType();
        senId.setValue(SENDER_PARTICIPANT_IDENTIFIER_VALUE);
        senId.setScheme(PARTICIPANT_IDENTIFIER_SCHEME);
        shobject.setSenderIdentifier(senId);
        
        ParticipantIdentifierType recId = new ParticipantIdentifierType();
        recId.setValue(RECEIVER_PARTICIPANT_IDENTIFIER_VALUE);
        recId.setScheme(PARTICIPANT_IDENTIFIER_SCHEME);
        shobject.setRecipientIdentifier(recId);
        
        String url = ACCESSPOINT_SERVICE_URL;
        accesspointClient apc = accesspointClient.getInstance();
        
        //This Value is Set to null because you are going to connect to your own AP for a local Delivery.
        apc.setMetadataCertificate(null);
        Resource port = apc.getPort(url);
        
        apc.send(port, shobject, body);
    }
}
