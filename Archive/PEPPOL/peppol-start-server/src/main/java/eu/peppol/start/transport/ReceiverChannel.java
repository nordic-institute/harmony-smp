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
package eu.peppol.start.transport;

import eu.peppol.start.exception.TransportException;
import eu.peppol.start.metadata.MessageMetadata;
import eu.peppol.start.soap.SOAPHeaderObject;
import eu.peppol.start.util.Configuration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

import org.w3c.dom.Document;

/**
 *
 * @author Jose Gorvenia Narvaez(jose@alfa1lab.com)
 */
public class ReceiverChannel {

    /**
     * Logger to follow this class behavior.
     */
    private static org.apache.log4j.Logger logger4J =
            org.apache.log4j.Logger.getLogger(ReceiverChannel.class);

    /**
     * Context parameter name;
     */
    private static final String STORAGE_PATH = "userfolder";

    /**
     * Stores the message.
     * @param context           Servlet context.
     * @param metadata          Object that represent the metadata of the message.
     * @param businessDocument  Business Document from the Body.
     */
    public void deliverMessage(ServletContext context,
                MessageMetadata metadata, Document businessDocument) {

        Document metadataDocument = SOAPHeaderObject.getDocument(metadata.getSoapHeader());
        String storagePath = context.getInitParameter(STORAGE_PATH);
        try {
            new TransportChannel(storagePath).saveDocument(metadata.getChannelId(),
                    metadata.getMessageId(), metadataDocument, businessDocument);

            logger4J.debug("Documetn stored : " + metadata.getDocumentIdValue());
        } catch (Exception ex) {
            logger4J.error(ex.getMessage(),ex);
            Logger.getLogger(ReceiverChannel.class.getName()).log(Level.SEVERE, ex.getMessage());

            Configuration conf = Configuration.getInstance();
            String errorMSG = conf.getProperty("error.message.transport") + metadata.getDocumentIdValue();
            throw new TransportException(errorMSG, ex);
        }
    }
}
