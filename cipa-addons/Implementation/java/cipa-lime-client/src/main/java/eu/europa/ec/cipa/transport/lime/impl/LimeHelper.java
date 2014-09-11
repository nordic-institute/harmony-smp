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
package eu.europa.ec.cipa.transport.lime.impl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Nonnull;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.xml.ws.BindingProvider;

import org.w3._2009._02.ws_tra.Resource;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.random.VerySecureRandom;
import com.helger.commons.string.StringHelper;

import eu.europa.ec.cipa.transport.cert.AccessPointX509TrustManager;
import eu.europa.ec.cipa.transport.lime.username.IReadonlyUsernamePWCredentials;
import eu.europa.ec.cipa.transport.lime.ws.LimeClientService;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class LimeHelper {
  private LimeHelper () {}

  private static void _setupSSLSocketFactory () throws NoSuchAlgorithmException, KeyManagementException {
    final TrustManager [] aTrustManagers = new TrustManager [] { new AccessPointX509TrustManager (null, null) };
    final SSLContext aSSLContext = SSLContext.getInstance ("SSL");
    aSSLContext.init (null, aTrustManagers, VerySecureRandom.getInstance ());
    HttpsURLConnection.setDefaultSSLSocketFactory (aSSLContext.getSocketFactory ());
  }

  private static void _setupHostnameVerifier () {
    final HostnameVerifier aHostVerifier = new HostnameVerifier () {
      public boolean verify (final String sUrlHostName, final SSLSession aSSLSession) {
        return sUrlHostName.equals (aSSLSession.getPeerHost ());
      }
    };
    HttpsURLConnection.setDefaultHostnameVerifier (aHostVerifier);
  }

  @Nonnull
  public static Resource createServicePort (@Nonnull @Nonempty final String sAPStr,
                                            @Nonnull final IReadonlyUsernamePWCredentials aCredentials) throws KeyManagementException,
                                                                                                       NoSuchAlgorithmException {
    if (StringHelper.hasNoTextAfterTrim (sAPStr))
      throw new IllegalArgumentException ("LIME access point url is empty");

    _setupSSLSocketFactory ();

    final LimeClientService aService = new LimeClientService ();
    final Resource aPort = aService.getResourceBindingPort ();
    final BindingProvider bp = (BindingProvider) aPort;
    bp.getRequestContext ().put (BindingProvider.USERNAME_PROPERTY, aCredentials.getUsername ());
    bp.getRequestContext ().put (BindingProvider.PASSWORD_PROPERTY, aCredentials.getPassword ());
    bp.getRequestContext ().put (BindingProvider.ENDPOINT_ADDRESS_PROPERTY, sAPStr);
    _setupHostnameVerifier ();

    return aPort;
  }
}
