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

import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import eu.europa.ec.cipa.peppol.utils.ConfigFile;


/**
 * Test class for class {@link ConfiguredDNSMapperTest}.
 * 
 * @author PEPPOL.AT, BRZ, Andreas Haberl
 */
public final class ConfiguredDNSMapperTest {
  /**
   * Test method for
   * {@link eu.europa.ec.cipa.peppol.ipmapper.ConfiguredDNSMapper#getMappedDNSHost(java.net.InetAddress)}
   * .
   * 
   * @throws UnknownHostException
   */
  @Test
  public void testMapInternalInetAddress () throws UnknownHostException {
    final ConfiguredDNSMapper dnsMapper = new ConfiguredDNSMapper (ConfigFile.getInstance ());
    final InetAddress _2222 = InetAddress.getByName ("2.2.2.2");
    final InetAddress _4444 = InetAddress.getByName ("4.4.4.4");
    MappedDNSHost res = dnsMapper.getMappedDNSHost (_2222);
    assertEquals (res, MappedDNSHost.create ("3.3.3.3"));
    res = dnsMapper.getMappedDNSHost (_4444);
    assertEquals (res, MappedDNSHost.create ("5.5.5.5:8080"));
  }
}
