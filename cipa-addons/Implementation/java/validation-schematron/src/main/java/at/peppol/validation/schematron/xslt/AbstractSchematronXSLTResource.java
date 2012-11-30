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
package at.peppol.validation.schematron.xslt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Document;

import at.peppol.validation.schematron.AbstractSchematronResource;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.commons.xml.XMLFactory;

/**
 * Abstract implementation of a Schematron resource that is based on XSLT
 * transformations.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public abstract class AbstractSchematronXSLTResource extends AbstractSchematronResource {
  private final ErrorListener m_aCustomErrorListener;
  private final URIResolver m_aCustomURIResolver;
  private final ISchematronXSLTProvider m_aXSLTProvider;

  public AbstractSchematronXSLTResource (@Nonnull final IReadableResource aSCHResource,
                                         @Nullable final ErrorListener aCustomErrorListener,
                                         @Nullable final URIResolver aCustomURIResolver,
                                         @Nullable final ISchematronXSLTProvider aXSLTProvider) {
    super (aSCHResource);
    m_aCustomErrorListener = aCustomErrorListener;
    m_aCustomURIResolver = aCustomURIResolver;
    m_aXSLTProvider = aXSLTProvider;
  }

  public final boolean isValidSchematron () {
    return m_aXSLTProvider != null && m_aXSLTProvider.isValidSchematron ();
  }

  @Nullable
  public final Document applySchematronValidation (@Nonnull final Source aXMLSource) throws Exception {
    if (!isValidSchematron ())
      return null;

    // Create result document
    final Document ret = XMLFactory.newDocument ();

    // Create the transformer object from the templates specified in the
    // constructor
    final Transformer aTransformer = m_aXSLTProvider.getXSLTTemplates ().newTransformer ();
    if (m_aCustomErrorListener != null)
      aTransformer.setErrorListener (m_aCustomErrorListener);
    if (m_aCustomURIResolver != null)
      aTransformer.setURIResolver (m_aCustomURIResolver);

    // Do the main transformation
    aTransformer.transform (aXMLSource, new DOMResult (ret));
    return ret;
  }

  @Override
  public String toString () {
    return ToStringGenerator.getDerived (super.toString ())
                            .appendIfNotNull ("customErrListener", m_aCustomErrorListener)
                            .appendIfNotNull ("customURIResolver", m_aCustomURIResolver)
                            .append ("XSLTProvider", m_aXSLTProvider)
                            .toString ();
  }
}
