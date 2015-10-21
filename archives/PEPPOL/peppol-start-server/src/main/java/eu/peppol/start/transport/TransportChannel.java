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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 *
 * @author Jose Gorvenia Narvaez(jose@alfa1lab.com)
 */
public class TransportChannel {

    /**
     * Extension of the Metadata.
     */
    public static final String EXT_METADATA = ".metadata";

    /**
     * Extension of the Payload.
     */
    public static final String EXT_PAYLOAD = ".payload";

    /**
     * Directory of the Inbox.
     */
    public static final String INBOX_DIR = "inbox";

    /**
     * Time limit for Messages.
     */
    public static final long MESSAGE_INVALID_TIME_IN_MILLIS = 1000L * 60L * 60L * 2L;

    /**
     * Path of the Store.
     */
    protected String storePath;

     /**
     * Indicates if the document was saved.
     */
    public boolean isSaved = false;

     /**
     * Indicates if the document was deleted.
     */
    public boolean isMetadataRemoved = false;

     /**
     * Indicates if the document was deleted.
     */
    public boolean isPayloadRemoved = false;

    /**
     * Set the path of the Store.
     *
     * @param storePath Path of the store.
     */
    public TransportChannel(final String storePath) {
        this.storePath = storePath;
    }

    /**
     * Logger to follow this class behavior.
     */
    private static org.apache.log4j.Logger logger =
            org.apache.log4j.Logger.getLogger(TransportChannel.class);

    /**
     * Save a Document.
     * @param channelID
     *                  ID for channel.
     * @param messageID
     *                  ID for message.
     * @param metadataDocument
     *                  XML Document for Metadata.
     * @param payloadDocument
     *                  XML Document for Payload.
     * @throws Exception
     *                  Exception if document cannot be saved.
     */
    public final void saveDocument(String channelID,
                             String messageID,
                             Document metadataDocument,
                             Document payloadDocument) throws Exception{
        isSaved = false;

        File channelInboxDir = getChannelInboxDir(channelID);

        File metadataFile = getMetadataFile(channelInboxDir, messageID);
        File payloadFile = getPayloadFile(channelInboxDir, messageID);

        if (!metadataFile.createNewFile()) {
            Logger.getLogger(TransportChannel.class.getName()).log(Level.SEVERE,
                                "Cannot create new metadata file for message ID "
                                + messageID
                                + " in inbox for channel "
                                + channelID);
            logger.error("Cannot create new metadata file for message ID "
                                + messageID
                                + " in inbox for channel "
                                + channelID);
            throw new Exception(
                                "Cannot create new metadata file for message ID "
                                + messageID
                                + " in inbox for channel "
                                + channelID);
        }
        if (!payloadFile.createNewFile()) {
            Logger.getLogger(TransportChannel.class.getName()).log(Level.SEVERE,
                                "Cannot create new payload file for message ID "
                                + messageID
                                + " in inbox for channel "
                                + channelID);
            logger.error("Cannot create new payload file for message ID "
                                + messageID
                                + " in inbox for channel "
                                + channelID);
            if (!metadataFile.delete()) {
                logger.debug("Cannot delete metadata file for message ID "
                                + messageID
                                + " in inbox for channel "
                                + channelID);
            } else {
                logger.debug("Metadata file deleted: " + metadataFile.getAbsolutePath());
            }
            throw new Exception(
                                "Cannot create new payload file for message ID "
                                + messageID
                                + " in inbox for channel "
                                + channelID);
        }

        try {

            writeDocumentToFile(metadataFile, metadataDocument);
            logger.info("Metadata created: " + metadataFile.getName());
            writeDocumentToFile(payloadFile, payloadDocument);
            logger.info("Payload created: " + payloadFile.getName());

            isSaved = true;
        } catch (Exception ex) {
            if (metadataFile.delete()) {
                    logger.debug("Metadata file deleted: " + metadataFile.getAbsolutePath());
                } else {
                    logger.debug("Cannot delete Metadata file: " + metadataFile.getAbsolutePath());
                }
             if (payloadFile.delete()) {
                    logger.debug("Payload file deleted: " + payloadFile.getAbsolutePath());
                } else {
                    logger.debug("Cannot delete Payload file: " + payloadFile.getAbsolutePath());
                }

            Logger.getLogger(TransportChannel.class.getName()).log(Level.SEVERE, "Error saving a document.", ex);
            logger.error("Error saving a document.", ex);

            throw ex;
        }
    }

    /**
     * Delete a Document.
     * @param channelID
     *        ChannelID directory.
     * @param messageID
     *        ID of the Message.
     * @throws Exception
     *         Throw the exception.
     */
    public final void deleteDocument(final String channelID,
                final String messageID) throws Exception {

        if (channelID != null && messageID != null) {
            File channelInboxDir = getChannelInboxDir(channelID);
            File metadataFile = getMetadataFile(channelInboxDir, messageID);
            File payloadFile = getPayloadFile(channelInboxDir, messageID);

            if (metadataFile.exists()) {
                if (metadataFile.delete()) {
                    isMetadataRemoved = true;
                    logger.debug("Metadata file deleted: " + metadataFile.getAbsolutePath());
                } else {
                    logger.debug("Cannot delete Metadata file: " + metadataFile.getAbsolutePath());
                }
            }
            if (payloadFile.exists()) {
                if (payloadFile.delete()) {
                    isPayloadRemoved = true;
                    logger.debug("Payload file deleted: " + payloadFile.getAbsolutePath());
                } else {
                    logger.debug("Cannot delete Payload file: " + payloadFile.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Get MessagesID from a Channel.
     * @param channelID
     *        ID of the Channel.
     * @return  Array of MessagesID.
     * @throws Exception
     *         Throws an exception.
     */
    public final String[] getMessageIDs(final String channelID) throws Exception {

        File dir = getChannelInboxDir(channelID);
        File[] files = dir.listFiles(new FilenameFilter() {

            public boolean accept(final File dir, final String name) {
                return (name.endsWith(EXT_PAYLOAD));
            }
        });

        String[] messageIDs = new String[files.length];
        int i = 0;
        for (File payloadFile : files) {
            String curMessageId = getMessageIDFromPayloadFile(payloadFile);

            if ((System.currentTimeMillis() - payloadFile.lastModified())
                 > MESSAGE_INVALID_TIME_IN_MILLIS) {
                deleteDocument(channelID, curMessageId);
            } else {
                messageIDs[i] = curMessageId;
                i++;
            }
        }
        return messageIDs;
    }

    /**
     * Get Metadata of a Document.
     * @param channelID
     *        ID of the Channel.
     * @param messageID
     *        ID of the Message.
     * @return  Metadata Document
     * @throws Exception
     *         throws an exception.
     */
    public final Document getDocumentMetadata(final String channelID,
                final String messageID) throws Exception {

        File channelInboxDir = getChannelInboxDir(channelID);
        File metadataFile = getMetadataFile(channelInboxDir, messageID);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(false);
        DocumentBuilder documentBuilder =
                                    documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(metadataFile);
    }

    /**
     * Get the document data from a binary file.
     * @param channelID
     *              Represents the channel identifier.
     * @param messageID
     *              Represents the message identifier.
     * @return
     *              Document complex type which contains xml data.
     * @throws Exception
     *              Generic exception.
     */
    public final Document getDocument(final String channelID,
                final String messageID) throws Exception {

        File channelInboxDir = getChannelInboxDir(channelID);
        File payloadFile = getPayloadFile(channelInboxDir, messageID);
        DocumentBuilderFactory documentBuilderFactory
                                            = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder =
                                    documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(payloadFile);
    }

    /**
     * Returns the size of a file.
     * @param channelID
     *               Represents the channel identifier.
     * @param messageID
     *               Represents the message identifier.
     * @return
     *          long primitive type representing the size of file.
     * @throws Exception
     *          generic exception.
     */
    public final long getSize(final String channelID,
                              final String messageID) throws Exception {

        File channelInboxDir = getChannelInboxDir(channelID);
        File payloadFile = getPayloadFile(channelInboxDir, messageID);
        long fileLength = payloadFile.length();
        //calculate length in Kilobytes and round up
        final int kb = 1023;
        final int size = 1024;
        long fileLenghtInKB = (fileLength + kb) / size;
        return fileLenghtInKB;
    }

    /**
     * Returns the date in which a file was created.
     * @param channelID
     *              Represents the channel identifier.
     * @param messageID
     *              Represents the message identifier.
     * @return
     *          Date complex type with data of the creation time.
     * @throws Exception
     *          Generic exception.
     */
    public final Date getCreationTime(final String channelID,
                                      final String messageID) throws Exception {

        File channelInboxDir = getChannelInboxDir(channelID);
        File payloadFile = getPayloadFile(channelInboxDir, messageID);
        return new Date(payloadFile.lastModified());
    }

    /**
     * Returns the message ID of a payload file.
     * @param payloadFile
     *              File containing the document data.
     * @return
     *          String data type containing the message id.
     */
    private String getMessageIDFromPayloadFile(final File payloadFile) {

        String str = payloadFile.getName();

        String messageID =
                str.substring(
                0, str.length()
                - EXT_PAYLOAD.length());

        messageID = messageID.replace('_', ':');
        return messageID;
    }

    /**
     * Returns a file in binary format containing data of soap header.
     * @param channelInboxDir
     *              Represents the path in which message will be saved.
     * @param messageID
     *              Represents identifier of the message.
     * @return
     *          File complex type containing information of SOAP Header.
     */
    private File getMetadataFile(final File channelInboxDir, String messageID) {
        messageID = removeSpecialChars(messageID);
        return new File(channelInboxDir, messageID + EXT_METADATA);
    }

    /**
     * Returns a file in binary format containing data of document.
     * @param channelInboxDir
     *              Represents the path in which message will be saved.
     * @param messageID
     *              Represents identifier of the message.
     * @return
     *          File complex type containing information of xml document.
     */
    private File getPayloadFile(final File channelInboxDir, String messageID) {
        messageID = removeSpecialChars(messageID);
        File file = new File(channelInboxDir, messageID + EXT_PAYLOAD);

        return file;
    }

    /**
     * Returns the path in which messages will be stored.
     * @param channelID
     *              Represents the channel identifier.
     * @return
     *              File representing storage path.
     * @throws Exception
     *              Generic exception.
     */
    private File getChannelInboxDir(String channelID) throws Exception {

        File inboxDir = new File(storePath, INBOX_DIR);
        if (!inboxDir.exists()) {
            if (!inboxDir.mkdir()) {
                logger.debug("Cannot create the inbox directory: "
                            + storePath + "/" + inboxDir);
            }
        }
        channelID = removeSpecialChars(channelID);

        File channelDir = new File(inboxDir, channelID);
        if (!channelDir.exists()) {
            if (!channelDir.mkdir()) {
                logger.debug("Cannot create the channel directory: "
                            + inboxDir + "/" + channelID);
            }
        }
        if (!channelDir.exists()) {

            Logger.getLogger(TransportChannel.class.getName()).log(Level.SEVERE,
                    "Inbox for channel \""
                    + channelID
                    + "\" could not be found or created: "
                    + channelDir.getAbsolutePath());

            logger.error("Inbox for channel \""
                    + channelID
                    + "\" could not be found or created: "
                    + channelDir.getAbsolutePath());

            throw new Exception("Inbox for channel \""
                    + channelID
                    + "\" could not be found or created: "
                    + channelDir.getAbsolutePath());
        }
        return channelDir;
    }

    /**
     * Remove characters that are not allowed for folder creation.
     * @param fileOrDirName
     *              Name of the directory or path to be created.
     * @return
     *              String with special characters removed.
     */
    private String removeSpecialChars(String fileOrDirName) {
        fileOrDirName = fileOrDirName.replace(':', '_');
        return fileOrDirName;
    }

    private void writeDocumentToFile(File messageFile, Document document)
                                     throws TransformerException, IOException {

        BufferedOutputStream bos =
                new BufferedOutputStream(new FileOutputStream(messageFile));

        TransformerFactory transformerFactory =
                                            TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(bos);
        transformer.transform(source, result);
        bos.close();
    }
}