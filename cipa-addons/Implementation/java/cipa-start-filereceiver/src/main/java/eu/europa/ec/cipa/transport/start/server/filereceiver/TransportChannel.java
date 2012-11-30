/**
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
package eu.europa.ec.cipa.transport.start.server.filereceiver;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.phloc.commons.CGlobal;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.exceptions.LoggedException;
import com.phloc.commons.io.file.FileOperations;
import com.phloc.commons.io.file.FileUtils;
import com.phloc.commons.io.file.FilenameHelper;
import com.phloc.commons.io.file.filter.FilenameFilterEndsWith;
import com.phloc.commons.state.ESuccess;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.xml.serialize.XMLReader;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.commons.xml.serialize.XMLWriterSettings;

/**
 * @author Jose Gorvenia Narvaez(jose@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
final class TransportChannel {
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
  public static final long MESSAGE_INVALID_TIME_IN_MILLIS = CGlobal.MILLISECONDS_PER_HOUR * 2L;

  /**
   * Logger to follow this class behavior.
   */
  private static final Logger s_aLogger = LoggerFactory.getLogger (TransportChannel.class);

  /**
   * Path of the Store.
   */
  protected String m_sStorePath;

  /**
   * Indicates if the document was saved.
   */
  public boolean m_bIsSaved = false;

  /**
   * Indicates if the document was deleted.
   */
  public boolean m_bIsMetadataRemoved = false;

  /**
   * Indicates if the document was deleted.
   */
  public boolean m_bIsPayloadRemoved = false;

  /**
   * Set the path of the Store.
   * 
   * @param storePath
   *        Path of the store.
   */
  public TransportChannel (final String storePath) {
    m_sStorePath = storePath;
  }

  /**
   * Save a Document.
   * 
   * @param sChannelID
   *        ID for channel.
   * @param sMessageID
   *        ID for message.
   * @param aMetadataDocument
   *        XML Document for Metadata.
   * @param aPayloadDocument
   *        XML Document for Payload.
   * @throws Exception
   *         Exception if document cannot be saved.
   */
  public final void saveDocument (final String sChannelID,
                                  final String sMessageID,
                                  final Document aMetadataDocument,
                                  final Document aPayloadDocument) throws Exception {
    m_bIsSaved = false;

    final File aChannelInboxDir = getChannelInboxDir (sChannelID);
    s_aLogger.info ("TransportChannel");

    final File aMetadataFile = getMetadataFile (aChannelInboxDir, sMessageID);
    final File aPayloadFile = getPayloadFile (aChannelInboxDir, sMessageID);

    if (!aMetadataFile.createNewFile ())
      throw new LoggedException ("Cannot create new metadata file for message ID " +
                                 sMessageID +
                                 " in inbox for channel " +
                                 sChannelID);

    if (!aPayloadFile.createNewFile ()) {
      s_aLogger.error ("Cannot create new payload file for message ID " +
                       sMessageID +
                       " in inbox for channel " +
                       sChannelID);
      if (!aMetadataFile.delete ()) {
        s_aLogger.debug ("Cannot delete metadata file for message ID " +
                         sMessageID +
                         " in inbox for channel " +
                         sChannelID);
      }
      else {
        s_aLogger.debug ("Metadata file deleted: " + aMetadataFile.getAbsolutePath ());
      }
      throw new Exception ("Cannot create new payload file for message ID " +
                           sMessageID +
                           " in inbox for channel " +
                           sChannelID);
    }

    try {

      writeDocumentToFile (aMetadataFile, aMetadataDocument);
      s_aLogger.info ("Metadata created: " + aMetadataFile.getName ());
      writeDocumentToFile (aPayloadFile, aPayloadDocument);
      s_aLogger.info ("Payload created: " + aPayloadFile.getName ());

      m_bIsSaved = true;
    }
    catch (final Exception ex) {
      if (aMetadataFile.delete ()) {
        s_aLogger.debug ("Metadata file deleted: " + aMetadataFile.getAbsolutePath ());
      }
      else {
        s_aLogger.debug ("Cannot delete Metadata file: " + aMetadataFile.getAbsolutePath ());
      }
      if (aPayloadFile.delete ()) {
        s_aLogger.debug ("Payload file deleted: " + aPayloadFile.getAbsolutePath ());
      }
      else {
        s_aLogger.debug ("Cannot delete Payload file: " + aPayloadFile.getAbsolutePath ());
      }

      s_aLogger.error ("Error saving a document.", ex);

      throw ex;
    }
  }

  /**
   * Delete a Document.
   * 
   * @param sChannelID
   *        ChannelID directory.
   * @param sMessageID
   *        ID of the Message.
   * @throws Exception
   *         Throw the exception.
   */
  public final void deleteDocument (final String sChannelID, final String sMessageID) throws Exception {

    if (sChannelID != null && sMessageID != null) {
      final File aChannelInboxDir = getChannelInboxDir (sChannelID);
      final File aMetadataFile = getMetadataFile (aChannelInboxDir, sMessageID);
      final File aPayloadFile = getPayloadFile (aChannelInboxDir, sMessageID);

      if (aMetadataFile.exists ()) {
        if (aMetadataFile.delete ()) {
          m_bIsMetadataRemoved = true;
          s_aLogger.debug ("Metadata file deleted: " + aMetadataFile.getAbsolutePath ());
        }
        else {
          s_aLogger.debug ("Cannot delete Metadata file: " + aMetadataFile.getAbsolutePath ());
        }
      }
      if (aPayloadFile.exists ()) {
        if (aPayloadFile.delete ()) {
          m_bIsPayloadRemoved = true;
          s_aLogger.debug ("Payload file deleted: " + aPayloadFile.getAbsolutePath ());
        }
        else {
          s_aLogger.debug ("Cannot delete Payload file: " + aPayloadFile.getAbsolutePath ());
        }
      }
    }
  }

  /**
   * Get MessagesID from a Channel.
   * 
   * @param sChannelID
   *        ID of the Channel.
   * @return Array of MessagesID.
   * @throws Exception
   *         Throws an exception.
   */
  public final String [] getMessageIDs (final String sChannelID) throws Exception {

    final File dir = getChannelInboxDir (sChannelID);
    final List <File> files = FileUtils.getDirectoryContent (dir, new FilenameFilterEndsWith (EXT_PAYLOAD));

    final String [] messageIDs = new String [files.size ()];
    int i = 0;
    for (final File aPayloadFile : files) {
      final String curMessageId = getMessageIDFromPayloadFile (aPayloadFile);

      if ((System.currentTimeMillis () - aPayloadFile.lastModified ()) > MESSAGE_INVALID_TIME_IN_MILLIS) {
        deleteDocument (sChannelID, curMessageId);
      }
      else {
        messageIDs[i] = curMessageId;
        i++;
      }
    }
    return messageIDs;
  }

  /**
   * Get Metadata of a Document.
   * 
   * @param sChannelID
   *        ID of the Channel.
   * @param sMessageID
   *        ID of the Message.
   * @return Metadata Document
   * @throws Exception
   *         throws an exception.
   */
  public final Document getDocumentMetadata (final String sChannelID, final String sMessageID) throws Exception {
    final File aChannelInboxDir = getChannelInboxDir (sChannelID);
    final File aMetadataFile = getMetadataFile (aChannelInboxDir, sMessageID);
    return XMLReader.readXMLDOM (aMetadataFile);
  }

  public final Document getDocument (final String sChannelID, final String sMessageID) throws Exception {
    final File aChannelInboxDir = getChannelInboxDir (sChannelID);
    final File aPayloadFile = getPayloadFile (aChannelInboxDir, sMessageID);
    return XMLReader.readXMLDOM (aPayloadFile);
  }

  public final long getSize (final String sChannelID, final String sMessageID) throws Exception {
    final File aChannelInboxDir = getChannelInboxDir (sChannelID);
    final File aPayloadFile = getPayloadFile (aChannelInboxDir, sMessageID);
    final long fileLength = aPayloadFile.length ();
    // calculate length in Kilobytes and round up
    final int kb = 1023;
    final int size = 1024;
    final long fileLenghtInKB = (fileLength + kb) / size;
    return fileLenghtInKB;
  }

  public final Date getCreationTime (final String sChannelID, final String sMessageID) throws Exception {
    final File aChannelInboxDir = getChannelInboxDir (sChannelID);
    final File aPayloadFile = getPayloadFile (aChannelInboxDir, sMessageID);
    return new Date (aPayloadFile.lastModified ());
  }

  private static String getMessageIDFromPayloadFile (@Nonnull final File aPayloadFile) {
    final String str = aPayloadFile.getName ();

    // Remove payload extension
    return StringHelper.trimEnd (str, EXT_PAYLOAD).replace ('_', ':');
  }

  private static File getMetadataFile (final File aChannelInboxDir, final String sMessageID) {
    final String sRealMessageID = _removeSpecialChars (sMessageID);
    return new File (aChannelInboxDir, sRealMessageID + EXT_METADATA);
  }

  private static File getPayloadFile (final File aChannelInboxDir, final String sMessageID) {
    final String sRealMessageID = _removeSpecialChars (sMessageID);
    final File file = new File (aChannelInboxDir, sRealMessageID + EXT_PAYLOAD);

    return file;
  }

  private File getChannelInboxDir (final String sChannelID) throws Exception {

    final File inboxDir = new File (m_sStorePath, INBOX_DIR);
    if (FileOperations.createDirIfNotExisting (inboxDir).isFailure ())
      s_aLogger.debug ("Cannot create the inbox directory: " + m_sStorePath + "/" + inboxDir);
    final String sRealChannelID = _removeSpecialChars (sChannelID);

    final File channelDir = new File (inboxDir, sRealChannelID);
    if (FileOperations.createDirRecursiveIfNotExisting (channelDir).isFailure ())
      s_aLogger.debug ("Cannot create the channel directory: " + inboxDir + "/" + sRealChannelID);
    if (!channelDir.exists ())
      throw new LoggedException ("Inbox for channel \"" +
                                 sChannelID +
                                 "\" could not be found or created: " +
                                 channelDir.getAbsolutePath ());
    return channelDir;
  }

  @Nonnull
  @Nonempty
  private static String _removeSpecialChars (@Nullable final String sFileOrDirName) {
    final String sFilename = FilenameHelper.getAsSecureValidFilename (sFileOrDirName);
    return StringHelper.hasNoText (sFilename) ? "$$$" : sFilename;
  }

  @Nonnull
  private static ESuccess writeDocumentToFile (@Nonnull final File messageFile, @Nonnull final Document document) {
    return XMLWriter.writeToStream (document,
                                    FileUtils.getOutputStream (messageFile),
                                    XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }
}
