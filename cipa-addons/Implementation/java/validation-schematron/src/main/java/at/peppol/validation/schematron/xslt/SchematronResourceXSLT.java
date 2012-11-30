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
import javax.xml.transform.URIResolver;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.io.resource.FileSystemResource;

/**
 * A Schematron resource that is based on an existing, pre-compiled XSLT script.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class SchematronResourceXSLT extends AbstractSchematronXSLTResource {
  public SchematronResourceXSLT (@Nonnull final IReadableResource aXSLTResource) {
    this (aXSLTResource, null, null);
  }

  public SchematronResourceXSLT (@Nonnull final IReadableResource aXSLTResource,
                                 @Nullable final ErrorListener aCustomErrorListener,
                                 @Nullable final URIResolver aCustomURIResolver) {
    super (aXSLTResource,
           aCustomErrorListener,
           aCustomURIResolver,
           SchematronResourceXSLTCache.getSchematronXSLTProvider (aXSLTResource));
  }

  @Nonnull
  public static SchematronResourceXSLT fromClassPath (@Nonnull @Nonempty final String sXSLTPath) {
    return new SchematronResourceXSLT (new ClassPathResource (sXSLTPath));
  }

  @Nonnull
  public static SchematronResourceXSLT fromClassPath (@Nonnull @Nonempty final String sXSLTPath,
                                                      @Nullable final ErrorListener aCustomErrorListener,
                                                      @Nullable final URIResolver aCustomURIResolver) {
    return new SchematronResourceXSLT (new ClassPathResource (sXSLTPath), aCustomErrorListener, aCustomURIResolver);
  }

  @Nonnull
  public static SchematronResourceXSLT fromFile (@Nonnull @Nonempty final String sXSLTPath) {
    return new SchematronResourceXSLT (new FileSystemResource (sXSLTPath));
  }

  @Nonnull
  public static SchematronResourceXSLT fromFile (@Nonnull @Nonempty final String sXSLTPath,
                                                 @Nullable final ErrorListener aCustomErrorListener,
                                                 @Nullable final URIResolver aCustomURIResolver) {
    return new SchematronResourceXSLT (new FileSystemResource (sXSLTPath), aCustomErrorListener, aCustomURIResolver);
  }
}
