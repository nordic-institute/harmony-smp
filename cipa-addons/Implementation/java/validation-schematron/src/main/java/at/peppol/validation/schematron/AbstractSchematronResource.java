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
package at.peppol.validation.schematron;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.commons.xml.transform.ResourceStreamSource;

/**
 * Abstract implementation of the {@link ISchematronResource} interface handling
 * the underlying resource and wrapping one method.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public abstract class AbstractSchematronResource implements ISchematronResource {
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractSchematronResource.class);
  private final IReadableResource m_aRes;

  public AbstractSchematronResource (@Nonnull final IReadableResource aRes) {
    if (aRes == null)
      throw new NullPointerException ("resource");
    m_aRes = aRes;
  }

  @Nonnull
  public final String getID () {
    return m_aRes.getResourceID ();
  }

  @Nonnull
  public final IReadableResource getResource () {
    return m_aRes;
  }

  @Nullable
  public final Document applySchematronValidation (@Nonnull final IReadableResource aXMLResource) throws Exception {
    if (!aXMLResource.exists ()) {
      // Resource not found
      s_aLogger.warn ("XML resource " + aXMLResource + " does not exist!");
      return null;
    }
    return applySchematronValidation (new ResourceStreamSource (aXMLResource));
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("resource", m_aRes).toString ();
  }
}
