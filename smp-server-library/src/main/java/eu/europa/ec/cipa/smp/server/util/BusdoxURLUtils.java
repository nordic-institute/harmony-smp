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

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Utility methods for assembling URLs and URL elements required for BusDox.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
@Deprecated
//TODO: Remove me
public final class BusdoxURLUtils {
  //public static final Charset URL_CHARSET = Charset.forName("UTF-8");
  //public static final Locale URL_LOCALE = Locale.US;


  private static final BusdoxURLUtils s_aInstance = new BusdoxURLUtils ();

  private BusdoxURLUtils() {}

  /**
   * Escape the passed URL to use the percentage maskings.
   *
   * @param sURL
   *        The input URL or URL part. May be <code>null</code>.
   * @return <code>null</code> if the input string was <code>null</code>.
   */
  @Nullable
  public static String createPercentEncodedURL (@Nullable final String sURL) {
    if (sURL != null)
      //return new URLCodec ().encodeText (sURL);
      try {
        return URLEncoder.encode(sURL, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    return null;
  }

}
