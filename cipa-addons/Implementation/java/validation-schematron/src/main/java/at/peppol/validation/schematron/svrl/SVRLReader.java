/**
 * Copyright (C) 2010 Bundesrechenzentrum GmbH
 * http://www.brz.gv.at
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.peppol.validation.schematron.svrl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.jaxb.JAXBContextCache;
import com.phloc.commons.jaxb.validation.LoggingValidationEventHandler;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.xml.schema.XMLSchemaCache;
import com.phloc.commons.xml.transform.ResourceStreamSource;

/**
 * This is the XML reader for Schematron SVRL documents. It reads XML DOM
 * documents and returns {@link SchematronOutputType} elements. The reading
 * itself is done with JAXB.<br>
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class SVRLReader {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SVRLReader.class);

  private SVRLReader () {}

  @Nonnull
  private static Unmarshaller _createUnmarshaller () throws JAXBException {
    // Get the relevant JAXB context
    final JAXBContext aJAXBContext = JAXBContextCache.getInstance ().getFromCache (SchematronOutputType.class);
    // create an Unmarshaller
    final Unmarshaller aUnmarshaller = aJAXBContext.createUnmarshaller ();
    aUnmarshaller.setEventHandler (new LoggingValidationEventHandler (aUnmarshaller.getEventHandler ()));

    // Get Schema for validation
    aUnmarshaller.setSchema (XMLSchemaCache.getInstance ().getSchema (new ClassPathResource (CSVRL.SVRL_XSD_PATH)));
    return aUnmarshaller;
  }

  @Nullable
  public static SchematronOutputType readXML (@Nonnull final IReadableResource aRes) {
    if (aRes == null)
      throw new NullPointerException ("res");

    return readXML (new ResourceStreamSource (aRes));
  }

  /**
   * Convert the passed W3C node into a SVRL domain object
   * 
   * @param aNode
   *        The node to be converted. May not be <code>null</code>.
   * @return <code>null</code> if the passed object could not be interpreted as
   *         SVRL.
   */
  @Nullable
  public static SchematronOutputType readXML (@Nonnull final Node aNode) {
    if (aNode == null)
      throw new NullPointerException ("node");

    return readXML (new DOMSource (aNode));
  }

  @Nullable
  public static SchematronOutputType readXML (@Nonnull final Source aSource) {
    if (aSource == null)
      throw new NullPointerException ("source");

    SchematronOutputType aSchematronOutput = null;
    try {
      final Unmarshaller aUnmarshaller = _createUnmarshaller ();

      // start unmarshalling
      aSchematronOutput = aUnmarshaller.unmarshal (aSource, SchematronOutputType.class).getValue ();
      if (aSchematronOutput == null)
        throw new IllegalStateException ("Failed to unmarshal SVRL - no exception!");
      return aSchematronOutput;
    }
    catch (final UnmarshalException ex) {
      // The JAXB specification does not mandate how the JAXB provider
      // must behave when attempting to unmarshal invalid XML data. In
      // those cases, the JAXB provider is allowed to terminate the
      // call to unmarshal with an UnmarshalException.
      String sMsg = ex.getMessage ();
      if (StringHelper.hasNoText (sMsg) && ex.getCause () != null)
        sMsg = ex.getCause ().getMessage ();

      if (StringHelper.hasText (sMsg))
        s_aLogger.warn ("Exception unmarshaling SVRL document: " + sMsg);
      else
        s_aLogger.warn ("Exception unmarshaling SVRL document", ex);
    }
    catch (final JAXBException ex) {
      s_aLogger.error ("Exception reading SVRL document", ex);
    }
    return null;
  }
}
