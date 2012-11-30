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
package at.peppol.visualization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.file.FileOperationManager;
import com.phloc.commons.io.file.FileUtils;
import com.phloc.commons.io.file.FilenameHelper;
import com.phloc.commons.io.file.LoggingFileOperationCallback;
import com.phloc.commons.io.streams.StreamUtils;
import com.phloc.commons.state.ESuccess;
import com.phloc.commons.xml.XMLFactory;
import com.phloc.commons.xml.serialize.EXMLSerializeFormat;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.commons.xml.serialize.XMLWriterSettings;
import com.phloc.commons.xml.transform.ResourceStreamSource;
import com.phloc.commons.xml.transform.XMLTransformerFactory;

/**
 * The main visualization manager. This is the main class for performing
 * visualization.
 * 
 * @author philip
 */
@ThreadSafe
public final class VisualizationManager {
  private static final Logger s_aLogger = LoggerFactory.getLogger (VisualizationManager.class);
  private static final FileOperationManager s_aFOM = new FileOperationManager (new LoggingFileOperationCallback ());
  private static final Map <String, Templates> s_aTemplatesCache = new HashMap <String, Templates> ();
  private static final Lock s_aLock = new ReentrantLock ();

  private VisualizationManager () {}

  @Nonnull
  public static ESuccess visualize (@Nonnull final EVisualizationArtefact eArtefact,
                                    @Nonnull final Source aSource,
                                    @Nonnull final Result aResult) {
    if (eArtefact == null)
      throw new NullPointerException ("artefact");

    // Get cached XSL templates
    Templates aTemplates;
    s_aLock.lock ();
    try {
      final String sArtefactID = eArtefact.getID ();
      aTemplates = s_aTemplatesCache.get (sArtefactID);
      if (aTemplates == null) {
        // Not in the cache - create a new one and put in the cache
        aTemplates = XMLTransformerFactory.newTemplates (eArtefact.getStylesheetResource ());
        if (aTemplates == null) {
          s_aLogger.error ("Failed to parse XSLT file for artefact " + eArtefact);
          return ESuccess.FAILURE;
        }
        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Compiled XSLT template " + eArtefact.getStylesheetResource ());
        s_aTemplatesCache.put (sArtefactID, aTemplates);
      }
    }
    finally {
      s_aLock.unlock ();
    }

    // Start the main transformation
    try {
      final Transformer aTransformer = aTemplates.newTransformer ();
      aTransformer.transform (aSource, aResult);
      return ESuccess.SUCCESS;
    }
    catch (final TransformerException ex) {
      s_aLogger.error ("Failed to apply transformation with artefact " + eArtefact, ex);
      return ESuccess.FAILURE;
    }
  }

  @Nullable
  public static Document visualizeToDOMDocument (@Nonnull final EVisualizationArtefact eArtefact,
                                                 @Nonnull final Source aSource) {
    final Document aDoc = XMLFactory.newDocument ();
    return visualize (eArtefact, aSource, new DOMResult (aDoc)).isSuccess () ? aDoc : null;
  }

  @Nullable
  public static Document visualizeToDOMDocument (@Nonnull final EVisualizationArtefact eArtefact,
                                                 @Nonnull final IReadableResource aResource) {
    return visualizeToDOMDocument (eArtefact, new ResourceStreamSource (aResource));
  }

  @Nullable
  public static ESuccess visualizeToFile (@Nonnull final EVisualizationArtefact eArtefact,
                                          @Nonnull final Source aSource,
                                          @Nonnull final File aDestinationFile,
                                          final boolean bCopyResources) {
    if (eArtefact == null)
      throw new NullPointerException ("artefact");
    if (aDestinationFile == null)
      throw new NullPointerException ("file");
    if (aDestinationFile.isDirectory ())
      throw new IllegalArgumentException ("Passed destination is a directory!");

    // Ensure the parent directory is present
    s_aFOM.createDirRecursiveIfNotExisting (aDestinationFile.getParentFile ());

    // Main conversion
    final Document aDoc = visualizeToDOMDocument (eArtefact, aSource);
    if (aDoc == null)
      return ESuccess.FAILURE;

    if (aDoc.getDocumentElement () == null)
      s_aLogger.warn ("Visualized document with artefact " + eArtefact + " is empty!");

    // If destination file exists, manually delete it
    if (aDestinationFile.exists ())
      s_aFOM.deleteFile (aDestinationFile);

    // Write the resulting HTML document to the file system
    final XMLWriterSettings aXWS = new XMLWriterSettings ().setFormat (EXMLSerializeFormat.HTML)
                                                           .setCharset (eArtefact.getCharset ());
    if (XMLWriter.writeToStream (aDoc, FileUtils.getOutputStream (aDestinationFile), aXWS).isFailure ())
      return ESuccess.FAILURE;

    if (bCopyResources) {
      // Copy all referenced resources of this visualization artefact to the
      // same directory where the destination file is created
      for (final IReadableResource aArtefactResource : eArtefact.getAllResources ()) {
        // Assuming, that the destination resources don't have a directory
        // structure:
        final File aDestFile = new File (aDestinationFile.getParentFile (),
                                         FilenameHelper.getWithoutPath (aArtefactResource.getPath ()));
        try {
          StreamUtils.copyInputStreamToOutputStreamAndCloseOS (aArtefactResource.getInputStream (),
                                                               new FileOutputStream (aDestFile));
        }
        catch (final FileNotFoundException ex) {
          // Should never happen - and in case this is not a showstopper
          s_aLogger.error ("Failed to write resource document " + aDestFile + " - file not found");
        }
      }
    }

    return ESuccess.SUCCESS;
  }

  @Nullable
  public static ESuccess visualizeToFile (@Nonnull final EVisualizationArtefact eArtefact,
                                          @Nonnull final IReadableResource aResource,
                                          @Nonnull final File aDestinationFile,
                                          final boolean bCopyResources) {
    return visualizeToFile (eArtefact, new ResourceStreamSource (aResource), aDestinationFile, bCopyResources);
  }
}
