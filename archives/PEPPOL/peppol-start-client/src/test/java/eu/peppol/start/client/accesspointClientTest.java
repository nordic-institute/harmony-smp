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

import eu.peppol.start.metadata.MessageMetadata;
import eu.peppol.start.soap.SOAPHeaderObject;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.busdox.servicemetadata.types.DocumentIdentifierType;
import org.busdox.servicemetadata.types.ParticipantIdentifierType;
import org.busdox.servicemetadata.types.ProcessIdentifierType;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3._2009._02.ws_tra.Create;
import org.w3._2009._02.ws_tra.Resource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author ppnon
 */
public class accesspointClientTest {

    /**
     * Test of getPort method, of class accesspointClient.
     */
    @Test
    public void testGetPort() {
        System.out.println("getPort");

        //Endpoint address URL
        String address = ""; //set value

        //Instances of the cliente
        accesspointClient instance = accesspointClient.getInstance();

        Resource result = null;
        try {
            result = instance.getPort(address);
        } catch (Exception ex) {
            Logger.getLogger(accesspointClientTest.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        assertNotNull(result);
    }

    /**
     * Test of send method, of class accesspointClient.
     */
    @Test
    public void testSend() throws ParserConfigurationException, SAXException, IOException {
        System.out.println("send");

        //Preparing Metadata object
        SOAPHeaderObject soapHeader = new SOAPHeaderObject();

        soapHeader.setChannelIdentifier(""); //set value
        soapHeader.setMessageIdentifier(""); //set value

        DocumentIdentifierType docid = new DocumentIdentifierType();
        docid.setValue(""); //set value
        docid.setScheme(""); //set value
        soapHeader.setDocumentIdentifier(docid);

        ProcessIdentifierType proid = new ProcessIdentifierType();
        proid.setValue(""); //set value
        proid.setScheme(""); //set value
        soapHeader.setProcessIdentifier(proid);

        ParticipantIdentifierType senId = new ParticipantIdentifierType();
        senId.setValue(""); //set value
        senId.setScheme(""); //set value
        soapHeader.setSenderIdentifier(senId);

        ParticipantIdentifierType recId = new ParticipantIdentifierType();
        recId.setValue(""); //set value
        recId.setScheme(""); //set value
        soapHeader.setRecipientIdentifier(recId);

        MessageMetadata metadata = new MessageMetadata(soapHeader);

        //Document to send
        DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = dFactory.newDocumentBuilder();
        Document document = parser.parse(new File("")); //set document path

        //Preparing Body
        Create body = new Create();
        List<Object> objects = body.getAny();
        objects.add(document.getDocumentElement());

        //Instance of the client
        accesspointClient instance = accesspointClient.getInstance();

        //Endpoint address url
        String address = ""; //set value

        //Setting PORT
        Resource port = instance.getPort(address);
        boolean completed = true;
        try {
            instance.send(port, soapHeader, body);
        } catch(Exception ex) {
            completed = false;
        }
        assertTrue(completed);
    }
}
