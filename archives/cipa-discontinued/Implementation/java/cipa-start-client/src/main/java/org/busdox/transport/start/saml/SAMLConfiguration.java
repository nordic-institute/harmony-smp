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
package org.busdox.transport.start.saml;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.string.StringHelper;

import eu.europa.ec.cipa.peppol.utils.ConfigFile;

/**
 * This class encapsulates the SAML configuration and allows for an easy check,
 * before the first request is sent!
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class SAMLConfiguration {
  private static final class SingletonHolder {
    static final SAMLConfiguration s_aInstance = new SAMLConfiguration ();
  }

  /** Represents the Sender ID for an operation. */
  public static final String CONFIG_SENDER_ID = "peppol.senderid";

  /** Represents a SAML Issuer. */
  public static final String CONFIG_SAML_TOKEN_ISSUER_NAME = "peppol.servicename";

  /** Represents the KeyStore path. */
  public static final String CONFIG_KEYSTORE_PATH = "peppol.keystore";

  /** Represents the KeyStore Password. */
  public static final String CONFIG_KEYSTORE_PASSWORD = "peppol.password";

  /** Represents the alias to select. */
  public static final String CONFIG_KEY_ALIAS = "peppol.key-alias";

  /** Represents the private key password to select. */
  public static final String CONFIG_KEY_PASSWORD = "peppol.key-password";

  private static final Logger s_aLogger = LoggerFactory.getLogger (SAMLConfiguration.class);

  private boolean m_bConfigurationOK;
  private final String m_sSenderID;
  private final String m_sAccesspointName;
  private final String m_sKeyStoreFilename;
  private final String m_sKeyStorePassword;
  private final String m_sKeyAlias;
  private final String m_sKeyPassword;

  private SAMLConfiguration () {
    // Read config file
    final ConfigFile aConfig = new ConfigFile ("private-configSAML.properties", "configSAML.properties");
    m_bConfigurationOK = true;

    // Get configured properties
    m_sSenderID = aConfig.getString (CONFIG_SENDER_ID);
    if (StringHelper.hasNoText (m_sSenderID)) {
      m_bConfigurationOK = false;
      s_aLogger.error ("SAML configuration file is incomplete: " + CONFIG_SENDER_ID + " is missing");
    }

    m_sAccesspointName = aConfig.getString (CONFIG_SAML_TOKEN_ISSUER_NAME);
    if (StringHelper.hasNoText (m_sAccesspointName)) {
      m_bConfigurationOK = false;
      s_aLogger.error ("SAML configuration file is incomplete: " + CONFIG_SAML_TOKEN_ISSUER_NAME + " is missing");
    }

    m_sKeyStoreFilename = aConfig.getString (CONFIG_KEYSTORE_PATH);
    if (StringHelper.hasNoText (m_sKeyStoreFilename)) {
      m_bConfigurationOK = false;
      s_aLogger.error ("SAML configuration file is incomplete: " + CONFIG_KEYSTORE_PATH + " is missing");
    }

    m_sKeyStorePassword = aConfig.getString (CONFIG_KEYSTORE_PASSWORD);
    if (StringHelper.hasNoText (m_sKeyStorePassword)) {
      m_bConfigurationOK = false;
      s_aLogger.error ("SAML configuration file is incomplete: " + CONFIG_KEYSTORE_PASSWORD + " is missing");
    }

    m_sKeyAlias = aConfig.getString (CONFIG_KEY_ALIAS);
    if (StringHelper.hasNoText (m_sKeyAlias)) {
      m_bConfigurationOK = false;
      s_aLogger.error ("SAML configuration file is incomplete: " + CONFIG_KEY_ALIAS + " is missing");
    }

    m_sKeyPassword = aConfig.getString (CONFIG_KEY_PASSWORD);
    if (StringHelper.hasNoText (m_sKeyPassword)) {
      m_bConfigurationOK = false;
      s_aLogger.error ("SAML configuration file is incomplete: " + CONFIG_KEY_PASSWORD + " is missing");
    }
  }

  @Nonnull
  public static SAMLConfiguration getInstance () {
    return SingletonHolder.s_aInstance;
  }

  /**
   * @return <code>true</code> if all SAML configuration items are set
   *         correctly.
   */
  public boolean isConfigurationOK () {
    return m_bConfigurationOK;
  }

  @Nonnull
  @Nonempty
  public String getSenderID () {
    if (!isConfigurationOK ())
      throw new IllegalStateException ("SAML configuration is incomplete!");
    return m_sSenderID;
  }

  @Nonnull
  @Nonempty
  public String getAccessPointName () {
    if (!isConfigurationOK ())
      throw new IllegalStateException ("SAML configuration is incomplete!");
    return m_sAccesspointName;
  }

  @Nonnull
  @Nonempty
  public String getKeyStorePath () {
    if (!isConfigurationOK ())
      throw new IllegalStateException ("SAML configuration is incomplete!");
    return m_sKeyStoreFilename;
  }

  @Nonnull
  @Nonempty
  public String getKeyStorePassword () {
    if (!isConfigurationOK ())
      throw new IllegalStateException ("SAML configuration is incomplete!");
    return m_sKeyStorePassword;
  }

  @Nonnull
  @Nonempty
  public String getKeyAlias () {
    if (!isConfigurationOK ())
      throw new IllegalStateException ("SAML configuration is incomplete!");
    return m_sKeyAlias;
  }

  @Nonnull
  @Nonempty
  public String getKeyPassword () {
    if (!isConfigurationOK ())
      throw new IllegalStateException ("SAML configuration is incomplete!");
    return m_sKeyPassword;
  }
}
