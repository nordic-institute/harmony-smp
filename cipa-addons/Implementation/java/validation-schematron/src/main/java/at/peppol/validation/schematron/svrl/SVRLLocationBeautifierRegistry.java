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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.GlobalDebug;
import com.phloc.commons.lang.ServiceLoaderBackport;

/**
 * A central registry for all {@link ISVRLLocationBeautifierSPI} instances.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class SVRLLocationBeautifierRegistry {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SVRLLocationBeautifierRegistry.class);
  private static final List <ISVRLLocationBeautifierSPI> s_aList = new ArrayList <ISVRLLocationBeautifierSPI> ();

  static {
    // Load all SPI implementations
    for (final ISVRLLocationBeautifierSPI aBeautifier : ServiceLoaderBackport.load (ISVRLLocationBeautifierSPI.class))
      s_aList.add (aBeautifier);
    if (!s_aList.isEmpty ())
      s_aLogger.info ("Loaded " + s_aList.size () + " SVRL location beautifier");
  }

  private SVRLLocationBeautifierRegistry () {}

  /**
   * Get the beautified location for the given namespace and local name.
   * 
   * @param sNamespaceURI
   *        The namespace URI
   * @param sLocalName
   *        The element local name
   * @return <code>null</code> if no beautification is available
   */
  @Nullable
  public static String getBeautifiedLocation (@Nonnull final String sNamespaceURI, @Nonnull final String sLocalName) {
    for (final ISVRLLocationBeautifierSPI aBeautifier : s_aList) {
      final String sBeautified = aBeautifier.getReplacementText (sNamespaceURI, sLocalName);
      if (sBeautified != null)
        return sBeautified;
    }
    if (GlobalDebug.isDebugMode ())
      s_aLogger.warn ("Unsupported elements for beautification: " + sNamespaceURI + " -- " + sLocalName);
    return null;
  }
}
