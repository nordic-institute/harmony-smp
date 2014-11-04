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
package eu.europa.ec.cipa.peppol.ipmapper;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.string.StringHelper;

import eu.europa.ec.cipa.peppol.utils.ConfigFile;

/**
 * This class is no longer needed and will be removed in the next major release.
 *
 * @author PEPPOL.AT, BRZ, Andreas Haberl
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Deprecated
public class ConfiguredDNSMapper {
  /**
   * The name of the configuration property representing a mapping of external
   * IPs that should be translated into internal ones. the string must conform
   * to the following pattern
   * <code>externalip1,externalip2=internalip1:port1 externalip3=internalip2:port2</code>
   * etc.
   */
  public static final String CONFIG_HOSTNAME_MAPPING = "busdox.net.dns.map.hostname_mapping";

  private static final Logger s_aLogger = LoggerFactory.getLogger (ConfiguredDNSMapper.class);

  private final Map <String, String> m_aNameMapping = new HashMap <String, String> ();

  public ConfiguredDNSMapper (@Nonnull final ConfigFile aConfigFile) {
    ValueEnforcer.notNull (aConfigFile, "ConfigFile");

    // Init mapping
    final String sMappings = StringHelper.trim (aConfigFile.getString (CONFIG_HOSTNAME_MAPPING));
    if (StringHelper.hasText (sMappings)) {
      // For all contained mappings
      final String [] aMappings = RegExHelper.getSplitToArray (sMappings, "[ \t]+");
      for (final String sMapping : aMappings) {
        // Mapping is e.g. 1.1.1.1,2.2.2.2=4.4.4.4:8080
        // Separate external from internal
        final String [] aParts = StringHelper.getExplodedArray ('=', sMapping, 2);
        if (aParts.length != 2) {
          s_aLogger.warn ("Mapping '" + sMapping + "' is missing the '=' separator");
          continue;
        }
        final String sExternals = aParts[0].trim ();
        final String sInternal = aParts[1].trim ();
        if (StringHelper.hasNoText (sExternals)) {
          s_aLogger.warn ("Mapping '" + sMapping + "' has an empty external part");
          continue;
        }
        if (StringHelper.hasNoText (sInternal)) {
          s_aLogger.warn ("Mapping '" + sMapping + "' has an empty internal part");
          continue;
        }

        // Check if multiple externals are defined
        final String [] aExternals = StringHelper.getExplodedArray (',', sExternals);
        for (final String sExternal : aExternals) {
          final String sRealExternal = sExternal.trim ();
          if (StringHelper.hasNoText (sRealExternal)) {
            s_aLogger.warn ("Mapping '" + sMapping + "' has at lease on empty external address");
            continue;
          }
          m_aNameMapping.put (sRealExternal, sInternal);
        }
      }
    }
  }

  @Nonnull
  public MappedDNSHost getMappedDNSHost (@Nonnull final InetAddress aInetAddress) {
    ValueEnforcer.notNull (aInetAddress, "InetAddress");

    final String sHostAddr = aInetAddress.getHostAddress ();

    final String sInternalMapping = m_aNameMapping.get (sHostAddr);
    if (sInternalMapping == null)
      return MappedDNSHost.create (aInetAddress.getHostName ());

    s_aLogger.info ("Found mapping of external IP '" + sHostAddr + "' to internal IP '" + sInternalMapping + "'");
    return MappedDNSHost.create (sInternalMapping);
  }

  /**
   * @return <code>true</code> if at least one mapping is contained,
   *         <code>false</code> otherwise.
   */
  public boolean containsAnyMapping () {
    return !m_aNameMapping.isEmpty ();
  }
}
