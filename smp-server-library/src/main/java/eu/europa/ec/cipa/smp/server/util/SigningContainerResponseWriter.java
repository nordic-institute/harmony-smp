/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.cipa.smp.server.util;

import com.helger.commons.io.streams.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.streams.StreamUtils;
import com.helger.commons.xml.EXMLIncorrectCharacterHandling;
import com.helger.commons.xml.serialize.*;
import com.helger.commons.xml.transform.XMLTransformerFactory;
import eu.europa.ec.cipa.smp.server.security.Signer;
import org.glassfish.jersey.server.ContainerException;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;
import org.w3c.dom.Document;

import javax.annotation.Nonnull;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

final class SigningContainerResponseWriter implements ContainerResponseWriter {

  private final ContainerResponseWriter m_aCRW;
  private NonBlockingByteArrayOutputStream m_aBAOS;
  private ContainerResponse m_aResponse;
  private final Signer signatureSigner;

  SigningContainerResponseWriter (@Nonnull final ContainerResponseWriter crw,
                                  @Nonnull final PrivateKeyEntry aKeyEntry,
                                  @Nonnull final X509Certificate aCert) {
    m_aCRW = crw;
    if (aKeyEntry == null) {
      throw new InvalidParameterException("Private key must be not null.");
    }
    signatureSigner = new Signer(aKeyEntry.getPrivateKey(),aCert);
  }

  @Override
  public OutputStream writeResponseStatusAndHeaders(long nContentLength, ContainerResponse aResponse) throws ContainerException {
    m_aResponse = aResponse;
    return m_aBAOS = new NonBlockingByteArrayOutputStream ();
  }

  @Override
  public void commit () {
    final byte [] aContent = m_aBAOS.toByteArray ();
    final OutputStream aOS = m_aCRW.writeResponseStatusAndHeaders (-1, m_aResponse);

    // Do security work here wrapping content and writing out XMLDSIG stuff to
    // out

    // Parse current response to XML
    Document aDoc;
    try {
      aDoc = DOMReader.readXMLDOM (aContent);
    }
    catch (final Exception e) {
      throw new RuntimeException ("Error in parsing xml", e);
    }

    // Sign the document
    try {
        signatureSigner.signXML(aDoc.getDocumentElement ());
    }
    catch (final Exception e) {
      throw new RuntimeException ("Error in signing xml", e);
    }

    if (false) {
      // And write the result to the main output stream
      // IMPORTANT: no indent and no align!
      final IXMLWriterSettings aSettings = new XMLWriterSettings ().setIncorrectCharacterHandling (EXMLIncorrectCharacterHandling.THROW_EXCEPTION)
                                                                   .setIndent (EXMLSerializeIndent.NONE);
      if (XMLWriter.writeToStream (aDoc, aOS, aSettings).isFailure ())
        throw new RuntimeException ("Failed to serialize node!");
    }
    else {
      // Use this because it correctly serializes &#13; which is important for
      // validating the signature!
      try {
        XMLTransformerFactory.newTransformer ().transform (new DOMSource (aDoc), new StreamResult (aOS));
      }
      catch (final TransformerException ex) {
        throw new IllegalStateException ("Failed to save to XML", ex);
      }
      finally {
        StreamUtils.close (aOS);
      }
    }
  }


    @Override
    public boolean suspend(long timeOut, TimeUnit timeUnit, TimeoutHandler timeoutHandler) {
        return m_aCRW.suspend(timeOut, timeUnit, timeoutHandler);
    }

    @Override
    public void setSuspendTimeout(long timeOut, TimeUnit timeUnit) throws IllegalStateException {
      m_aCRW.setSuspendTimeout(timeOut, timeUnit);
    }

    @Override
    public void failure(Throwable error) {
      m_aCRW.failure(error);
    }

    @Override
    public boolean enableResponseBuffering() {
        return m_aCRW.enableResponseBuffering();
    }
}