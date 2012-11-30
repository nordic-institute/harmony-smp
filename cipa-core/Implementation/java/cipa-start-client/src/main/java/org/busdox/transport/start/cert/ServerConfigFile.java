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
package org.busdox.transport.start.cert;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import eu.europa.ec.cipa.peppol.utils.ConfigFile;

/**
 * Wrapper around the server/client AP configuration file.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class ServerConfigFile {
  private static final String CONFIG_SERVER_KEYSTORE_PATH = "server.keystore.path";
  private static final String CONFIG_SERVER_KEYSTORE_PASSWORD = "server.keystore.password";
  private static final String CONFIG_SERVER_KEYSTORE_ALIAS = "server.keystore.alias";
  private static final String CONFIG_SERVER_KEYSTORE_ALIASPASSWORD = "server.keystore.aliaspassword";
  private static final String CONFIG_SERVER_TRUSTSTORE_PATH = "server.truststore.path";
  private static final String CONFIG_SERVER_TRUSTSTORE_PASSWORD = "server.truststore.password";
  private static final String CONFIG_SERVER_TRUSTSTORE_ALIAS = "server.truststore.alias";
  private static final String CONFIG_SERVER_TRUSTSTORE_ALIASPASSWORD = "server.truststore.aliaspassword";
  private static final String CONFIG_SERVER_ENDPOINT_URL = "server.endpoint.url";
  private static final String CONFIG_SERVER_MODE = "server.mode";
  private static final String CONFIG_SERVER_SMP_URL = "server.smp.url";
  private static final String CONFIG_SERVER_RECEIVER_CLASSPATH = "server.receiver.classpath";

  private static final ConfigFile s_aConfigFile = new ConfigFile ("private-configServer.properties",
                                                                  "configServer.properties");

  private ServerConfigFile () {}

  /**
   * @return The underlying {@link ConfigFile} object. Never <code>null</code>.
   */
  public static ConfigFile getConfigFile () {
    return s_aConfigFile;
  }
  @Nullable
  public static String getServerMode () {
    return s_aConfigFile.getString (CONFIG_SERVER_MODE);
  }
  @Nullable
  public static String getReceiverClassPath () {
    return s_aConfigFile.getString (CONFIG_SERVER_RECEIVER_CLASSPATH);
  }
  @Nullable
  public static String getServerSMPUrl () {
    return s_aConfigFile.getString (CONFIG_SERVER_SMP_URL);
  }
  @Nullable
  public static String getKeyStorePath () {
    return s_aConfigFile.getString (CONFIG_SERVER_KEYSTORE_PATH);
  }

  @Nullable
  public static String getKeyStorePassword () {
    return s_aConfigFile.getString (CONFIG_SERVER_KEYSTORE_PASSWORD);
  }

  @Nullable
  public static String getKeyStoreAlias () {
    return s_aConfigFile.getString (CONFIG_SERVER_KEYSTORE_ALIAS);
  }

  @Nullable
  public static char [] getKeyStoreAliasPassword () {
    return s_aConfigFile.getCharArray (CONFIG_SERVER_KEYSTORE_ALIASPASSWORD);
  }

  @Nullable
  public static String getTrustStorePath () {
    return s_aConfigFile.getString (CONFIG_SERVER_TRUSTSTORE_PATH);
  }

  @Nullable
  public static String getTrustStorePassword () {
    return s_aConfigFile.getString (CONFIG_SERVER_TRUSTSTORE_PASSWORD);
  }

  @Nullable
  public static String getTrustStoreAlias () {
    return s_aConfigFile.getString (CONFIG_SERVER_TRUSTSTORE_ALIAS);
  }

  @Nullable
  public static char [] getTrustStoreAliasPassword () {
    return s_aConfigFile.getCharArray (CONFIG_SERVER_TRUSTSTORE_ALIASPASSWORD);
  }

  @Nullable
  public static String getOwnAPURL () {
    return s_aConfigFile.getString (CONFIG_SERVER_ENDPOINT_URL);
  }
}
