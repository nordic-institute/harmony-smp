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
package eu.europa.ec.cipa.sml.server.dns;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import eu.europa.ec.cipa.peppol.utils.ConfigFile;

/**
 * Get configuration for DNSClient.<br>
 * This class only uses the {@link ConfigFile} with fixed keys and fixed types.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class DNSClientConfiguration {
  private static final String CONFIG_ENABLED = "dnsClient.enabled";
  private static final String CONFIG_ZONE = "dnsClient.zone";
  private static final String CONFIG_SML_ZONE_NAME = "dnsClient.smlzonename";
  private static final String CONFIG_SERVER = "dnsClient.server";
  private static final String CONFIG_TTL = "dnsClient.ttl";
  private static final String CONFIG_TSIG_SECRET = "dnsClient.secret";
  private static final String CONFIG_SIG0 = "dnsClient.SIG0Enabled";
  private static final String CONFIG_SIG0PublicKeyName = "dnsClient.SIG0PublicKeyName";

  // The config file instance to be used for reading
  private static final ConfigFile s_aConfigFile = ConfigFile.getInstance ();

  private DNSClientConfiguration () {}

  /**
   * Property "dnsClient.enabled" from "config.properties".
   *
   * @return enabled or not : default false
   */
  public static boolean isEnabled () {
    return s_aConfigFile.getBoolean (CONFIG_ENABLED, false);
  }

  /**
   * Property "dnsClient.zone" from "config.properties". This is the Domain
   * name.
   *
   * @return zone
   */
  @Nullable
  public static String getZone () {
    return s_aConfigFile.getString (CONFIG_ZONE);
  }

  /**
   * Property "dnsClient.smlzonename" from "config.properties". This zone is
   * prefixed on Zone (Domain).
   *
   * @return zone
   */
  @Nullable
  public static String getSMLZoneName () {
    return s_aConfigFile.getString (CONFIG_SML_ZONE_NAME);
  }

  /**
   * Property "dnsClient.server" from "config.properties".
   *
   * @return server
   */
  @Nullable
  public static String getServer () {
    return s_aConfigFile.getString (CONFIG_SERVER);
  }

  /**
   * Property "dnsClient.ttl" from "config.properties".
   *
   * @return ttl : default 60 seconds
   */
  public static int getTTL () {
    return s_aConfigFile.getInt (CONFIG_TTL, 60);
  }

  /**
   * Property "dnsClient.secret" from "config.properties".
   *
   * @return server
   */
  @Nullable
  public static String getSecret () {
    return s_aConfigFile.getString (CONFIG_TSIG_SECRET);
  }

  /**
   * Property "dnsClient.SIG0Enabled" from "config.properties".
   *
   * @return server
   */
  public static boolean getSIG0 () {
    return Boolean.parseBoolean (s_aConfigFile.getString (CONFIG_SIG0));
  }

  /**
   * Property "dnsClient.SIG0PublicKeyName" from "config.properties".
   *
   * @return server
   */
  @Nullable
  public static String getSIG0PublicKeyName () {
    return s_aConfigFile.getString (CONFIG_SIG0PublicKeyName);
  }
}
