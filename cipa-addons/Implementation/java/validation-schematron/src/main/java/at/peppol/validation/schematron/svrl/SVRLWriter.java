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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;

import org.oclc.purl.dsdl.svrl.ObjectFactory;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.io.streams.NonBlockingStringWriter;
import com.phloc.commons.jaxb.JAXBContextCache;
import com.phloc.commons.jaxb.validation.LoggingValidationEventHandler;
import com.phloc.commons.state.ESuccess;
import com.phloc.commons.xml.XMLFactory;
import com.phloc.commons.xml.schema.XMLSchemaCache;

/**
 * This is the XML writer for Schematron SVRL documents. It reads
 * {@link SchematronOutputType} elements and converts them to W3C nodes. The
 * writing itself is done with JAXB.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class SVRLWriter {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SVRLWriter.class);

  private SVRLWriter () {}

  /**
   * @return The JAXB marshaller
   * @throws JAXBException
   *         In case of an error
   */
  @Nonnull
  private static Marshaller _createMarshaller () throws JAXBException {
    // Get the relevant JAXB context
    final JAXBContext aJAXBContext = JAXBContextCache.getInstance ().getFromCache (SchematronOutputType.class);

    // create a marshaller
    final Marshaller aMarshaller = aJAXBContext.createMarshaller ();
    aMarshaller.setEventHandler (new LoggingValidationEventHandler (aMarshaller.getEventHandler ()));

    // Get Schema for validation
    aMarshaller.setSchema (XMLSchemaCache.getInstance ().getSchema (new ClassPathResource (CSVRL.SVRL_XSD_PATH)));
    return aMarshaller;
  }

  /**
   * Convert the passed schematron output element into an W3C Document node.
   * 
   * @param aSchematronOutput
   *        The schematron output to be converted. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  public static ESuccess writeSVRL (@Nonnull final SchematronOutputType aSchematronOutput, @Nonnull final Result aResult) {
    if (aSchematronOutput == null)
      throw new NullPointerException ("schematronOutput");
    if (aResult == null)
      throw new NullPointerException ("result");

    try {
      final Marshaller aMarshaller = _createMarshaller ();

      // start marshalling
      final JAXBElement <SchematronOutputType> aElement = new ObjectFactory ().createSchematronOutput (aSchematronOutput);
      aMarshaller.marshal (aElement, aResult);
      return ESuccess.SUCCESS;
    }
    catch (final JAXBException ex) {
      s_aLogger.error ("Exception writing SVRL document", ex);
      return ESuccess.FAILURE;
    }
  }

  /**
   * Convert the passed schematron output element into an W3C Document node.
   * 
   * @param aSchematronOutput
   *        The schematron output to be converted. May not be <code>null</code>.
   * @return <code>null</code> if conversion failed.
   */
  @Nullable
  public static Document createXML (@Nonnull final SchematronOutputType aSchematronOutput) {
    final Document aDoc = XMLFactory.newDocument ();
    return writeSVRL (aSchematronOutput, new DOMResult (aDoc)).isSuccess () ? aDoc : null;
  }

  /**
   * Utility method to directly convert the passed SVRL domain object to an XML
   * string.
   * 
   * @param aSchematronOutput
   *        The SVRL domain object to be converted. May not be null.
   * @return <code>null</code> if the passed domain object could not be
   *         converted because of validation errors.
   */
  @Nullable
  public static String createXMLString (@Nonnull final SchematronOutputType aSchematronOutput) {
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    return writeSVRL (aSchematronOutput, new StreamResult (aSW)).isSuccess () ? aSW.getAsString () : null;
  }
}
