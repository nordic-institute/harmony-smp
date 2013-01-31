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
package eu.europa.ec.cipa.transport.lime.server.storage;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.phloc.commons.CGlobal;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.io.file.FileOperations;
import com.phloc.commons.io.file.FileUtils;
import com.phloc.commons.io.file.filter.FilenameFilterEndsWith;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.xml.serialize.XMLReader;
import com.phloc.commons.xml.serialize.XMLWriter;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class LimeStorage {
  private static final String EXT_METADATA = ".metadata";
  private static final String EXT_PAYLOAD = ".payload";
  private static final String INBOX_DIR = "inbox";
  private static final long MESSAGE_INVALID_TIME_IN_MILLIS = CGlobal.MILLISECONDS_PER_HOUR * 2;
  private static final Logger s_aLogger = LoggerFactory.getLogger (LimeStorage.class);

  private final String m_sStorePath;

  public LimeStorage (@Nonnull @Nonempty final String sStorePath) {
    if (StringHelper.hasNoText (sStorePath))
      throw new IllegalArgumentException ("storePath");
    m_sStorePath = sStorePath;
  }

  public void saveDocument (@Nonnull final String sChannelID,
                            @Nonnull final String sMessageID,
                            @Nonnull final Document aMetadataDocument,
                            @Nonnull final Document aPayloadDocument) throws IOException {
    final File aChannelInboxDir = _getChannelInboxDir (sChannelID);
    final File aMetadataFile = _getMetadataFile (aChannelInboxDir, sMessageID);
    final File aPayloadFile = _getPayloadFile (aChannelInboxDir, sMessageID);

    if (!aMetadataFile.createNewFile ()) {
      s_aLogger.info ("Metadata filename: " + aMetadataFile.getAbsolutePath ());
      throw new IllegalStateException ("Cannot create new metadata file for message ID " +
                                       sMessageID +
                                       " in inbox for channel " +
                                       sChannelID);

    }
    if (!aPayloadFile.createNewFile ()) {
      s_aLogger.info ("Payload filename: " + aPayloadFile.getAbsolutePath ());
      aMetadataFile.delete ();
      throw new IllegalStateException ("Cannot create new document file for message ID " +
                                       sMessageID +
                                       " in inbox for channel " +
                                       sChannelID);
    }

    try {
      _writeDocumentToFile (aMetadataDocument, aMetadataFile);
      _writeDocumentToFile (aPayloadDocument, aPayloadFile);
    }
    catch (final RuntimeException ex) {
      aMetadataFile.delete ();
      aPayloadFile.delete ();
      throw ex;
    }
  }

  public void deleteDocument (@Nullable final String sChannelID, @Nullable final String sMessageID) {
    if (sChannelID != null && sMessageID != null) {
      final File aChannelInboxDir = _getChannelInboxDir (sChannelID);
      final File aMetadataFile = _getMetadataFile (aChannelInboxDir, sMessageID);
      final File aPayloadFile = _getPayloadFile (aChannelInboxDir, sMessageID);

      final boolean bMetadataFileExists = aMetadataFile.exists ();
      final boolean bPayloadFileExists = aPayloadFile.exists ();
      if (bMetadataFileExists && bPayloadFileExists) {
        aMetadataFile.delete ();
        aPayloadFile.delete ();
      }
      else
        if (bMetadataFileExists) {
          s_aLogger.warn ("Only the metadata file exists. Payload file " + aPayloadFile + " is missing");
          aMetadataFile.delete ();
        }
        else
          if (bPayloadFileExists) {
            s_aLogger.warn ("Only the payload file exists. Metadata file " + aMetadataFile + " is missing");
            aPayloadFile.delete ();
          }
      // else none of the files exist
    }
  }

  @Nonnull
  public String [] getMessageIDs (@Nonnull final String sChannelID) {
    final File aChannnelDir = _getChannelInboxDir (sChannelID);
    final List <File> aPayloadFiles = FileUtils.getDirectoryContent (aChannnelDir,
                                                                     new FilenameFilterEndsWith (EXT_PAYLOAD));

    final String [] aMessageIDs = new String [aPayloadFiles.size ()];
    int nMsgIdx = 0;
    for (final File aPayloadFile : aPayloadFiles) {
      final String sMsgID = _getMessageIDFromPayloadFile (aPayloadFile);

      if ((System.currentTimeMillis () - aPayloadFile.lastModified ()) > MESSAGE_INVALID_TIME_IN_MILLIS)
        deleteDocument (sChannelID, sMsgID);
      else
        aMessageIDs[nMsgIdx++] = sMsgID;
    }
    return aMessageIDs;
  }

  @Nullable
  public Document getDocumentMetadata (@Nonnull final String sChannelID, @Nonnull final String sMessageID) throws SAXException {
    final File aChannelInboxDir = _getChannelInboxDir (sChannelID);
    final File aMetadataFile = _getMetadataFile (aChannelInboxDir, sMessageID);
    return XMLReader.readXMLDOM (FileUtils.getInputStream (aMetadataFile));
  }

  @Nullable
  public Document getDocument (@Nonnull final String sChannelID, final String sMessageID) throws SAXException {
    final File aChannelInboxDir = _getChannelInboxDir (sChannelID);
    final File aPayloadFile = _getPayloadFile (aChannelInboxDir, sMessageID);
    return XMLReader.readXMLDOM (FileUtils.getInputStream (aPayloadFile));
  }

  public long getSize (@Nonnull final String sChannelID, final String sMessageID) {
    final File aChannelInboxDir = _getChannelInboxDir (sChannelID);
    final File aPayloadFile = _getPayloadFile (aChannelInboxDir, sMessageID);
    final long nFileLength = aPayloadFile.length ();
    // calculate length in Kilobytes and round up
    final long nFileLengthInKB = (nFileLength + CGlobal.BYTES_PER_KILOBYTE_LONG - 1) / CGlobal.BYTES_PER_KILOBYTE_LONG;
    return nFileLengthInKB;
  }

  @Nonnull
  public Date getCreationTime (@Nonnull final String sChannelID, final String sMessageID) {
    final File aChannelInboxDir = _getChannelInboxDir (sChannelID);
    final File aPayloadFile = _getPayloadFile (aChannelInboxDir, sMessageID);
    return new Date (aPayloadFile.lastModified ());
  }

  @Nonnull
  private static String _getMessageIDFromPayloadFile (@Nonnull final File aPayloadFile) {
    final String sFilename = aPayloadFile.getName ();
    String sMessageID = sFilename.substring (0, sFilename.length () - EXT_PAYLOAD.length ());
    sMessageID = sMessageID.replace ('_', ':');
    return sMessageID;
  }

  @Nonnull
  private static File _getMetadataFile (@Nonnull final File aChannelInboxDir, @Nonnull final String sMessageID) {
    final String sRealMessageID = _removeSpecialChars (sMessageID);
    return new File (aChannelInboxDir, sRealMessageID + EXT_METADATA);
  }

  @Nonnull
  private static File _getPayloadFile (@Nonnull final File aChannelInboxDir, @Nonnull final String sMessageID) {
    final String sRealMessageID = _removeSpecialChars (sMessageID);
    final File aFile = new File (aChannelInboxDir, sRealMessageID + EXT_PAYLOAD);
    s_aLogger.info ("Getting payload file: " + aFile.getAbsolutePath ());
    return aFile;
  }

  @Nonnull
  private File _getChannelInboxDir (@Nonnull final String sChannelID) {
    final File aInboxDir = new File (m_sStorePath, INBOX_DIR);
    FileOperations.createDirIfNotExisting (aInboxDir);

    final String sRealChannelID = _removeSpecialChars (sChannelID);
    final File aChannelDir = new File (aInboxDir, sRealChannelID);
    FileOperations.createDirIfNotExisting (aChannelDir);
    if (!aChannelDir.exists ())
      throw new IllegalStateException ("Inbox for channel \"" +
                                       sRealChannelID +
                                       "\" could not be found or created: " +
                                       aChannelDir.getAbsolutePath ());
    return aChannelDir;
  }

  @Nonnull
  private static String _removeSpecialChars (@Nonnull final String sFileOrDirName) {
    return sFileOrDirName.replace (':', '_');
  }

  private static void _writeDocumentToFile (@Nonnull final Document aDoc, @Nonnull final File aMessageFile) {
    XMLWriter.writeToStream (aDoc, FileUtils.getOutputStream (aMessageFile));
  }
}
