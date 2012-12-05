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
package eu.europa.ec.cipa.smp.client.functest;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.ws.wsaddressing.W3CEndpointReference;


import com.phloc.commons.base64.Base64;
import com.phloc.commons.exceptions.InitializationException;
import com.phloc.commons.io.file.SimpleFileIO;

import eu.europa.ec.cipa.peppol.identifier.doctype.SimpleDocumentTypeIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.process.SimpleProcessIdentifier;
import eu.europa.ec.cipa.peppol.utils.CertificateUtils;
import eu.europa.ec.cipa.peppol.utils.ConfigFile;
import eu.europa.ec.cipa.peppol.utils.IReadonlyUsernamePWCredentials;
import eu.europa.ec.cipa.peppol.utils.ReadonlyUsernamePWCredentials;
import eu.europa.ec.cipa.peppol.wsaddr.W3CEndpointReferenceUtils;

/**
 * Configuration for this package
 * 
 * @author philip
 */
@Immutable
public final class CFunctestConfig {
  private static final ConfigFile s_aConfig = new ConfigFile ("private-functest.properties", "functest.properties");

  // init
  static {
    // How to get the Cert String:
   // if (false)
      System.out.println (Base64.encodeBytes (SimpleFileIO.readFileBytes (new File ("E:/data/APP_PEPPOL_ACCESS_POINT_TEST_CA.cer"))));

    try {
      if (CertificateUtils.convertStringToCertficate (getAPCert ()) == null)
        throw new InitializationException ("Failed to convert certificate string to a certificate!");
    }
    catch (final CertificateException ex) {
      throw new InitializationException ("Failed to convert certificate string to a certificate!", ex);
    }
  }

  private CFunctestConfig () {}

  @Nullable
  public static String getSMPUserName () {
    return s_aConfig.getString ("smp.username");
  }

  @Nullable
  public static String getSMPPassword () {
    return s_aConfig.getString ("smp.password");
  }

  @Nonnull
  public static final IReadonlyUsernamePWCredentials getSMPCredentials () {
  	System.out.print(getSMPUserName ());
    return new ReadonlyUsernamePWCredentials (getSMPUserName (), getSMPPassword ());
  }

  @Nonnull
  public static URI getSMPURI () {
    try {
      return new URI (s_aConfig.getString ("smp.uri"));
    }
    catch (final URISyntaxException ex) {
      throw new IllegalStateException (ex);
    }
  }

  @Nonnull
  public static final SimpleParticipantIdentifier getParticipantID () {
    return SimpleParticipantIdentifier.createWithDefaultScheme (s_aConfig.getString ("participantid"));
  }

  @Nonnull
  public static final SimpleDocumentTypeIdentifier getDocumentTypeID () {
    return SimpleDocumentTypeIdentifier.createWithDefaultScheme (s_aConfig.getString ("documenttypeid"));
  }

  @Nonnull
  public static final SimpleProcessIdentifier getProcessTypeID () {
    return SimpleProcessIdentifier.createWithDefaultScheme (s_aConfig.getString ("processtypeid"));
  }

  @Nonnull
  public static final W3CEndpointReference getAPEndpointRef () {
    return W3CEndpointReferenceUtils.createEndpointReference (s_aConfig.getString ("ap.uri"));
  }

  @Nullable
  public static String getAPCert () {
    return s_aConfig.getString ("ap.cert");
  }

  @Nullable
  public static String getAPServiceDescription () {
    return s_aConfig.getString ("ap.servicedescription");
  }

  @Nullable
  public static String getAPContact () {
    return s_aConfig.getString ("ap.contact");
  }

  @Nullable
  public static String getAPInfo () {
    return s_aConfig.getString ("ap.info");
  }
}
