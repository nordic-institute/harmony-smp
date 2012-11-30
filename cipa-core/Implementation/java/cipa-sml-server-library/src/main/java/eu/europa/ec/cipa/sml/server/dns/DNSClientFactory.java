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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory to init DNSClient. If property "dnsClient.enabled=true" from
 * "config.properties" a real implementation is returned, otherwise Dummy.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@ThreadSafe
public final class DNSClientFactory {
  private static final class SingletonHolder {
    private static final Logger log = LoggerFactory.getLogger (DNSClientFactory.class);
    static final IDNSClient s_aInstance;
    static {
      if (DNSClientConfiguration.isEnabled ()) {
        // DNS enabled
        final String sServer = DNSClientConfiguration.getServer ();
        final String sZone = DNSClientConfiguration.getZone ();
        final String sSMLZoneName = DNSClientConfiguration.getSMLZoneName ();
        final int nTTL = DNSClientConfiguration.getTTL ();
        log.info ("Init DnsClientFactory : " +
                  sServer +
                  " - " +
                  sZone +
                  " - " +
                  sSMLZoneName +
                  "  (" +
                  sSMLZoneName +
                  "." +
                  sZone +
                  ")  - " +
                  nTTL);

        s_aInstance = new DNSClientImpl (sServer, sZone, sSMLZoneName, nTTL);
      }
      else {
        // DNS disabled
        log.warn ("DNS is disabled - no DNS operations are available!");
        s_aInstance = new DoNothingDNSClient ();
      }
    }
  }

  private DNSClientFactory () {}

  /**
   * Return instance.
   * 
   * @return non null
   */
  @Nonnull
  public static IDNSClient getInstance () {
    return SingletonHolder.s_aInstance;
  }
}
