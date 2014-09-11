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
package eu.europa.ec.cipa.transport.lime.username;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.mock.PHTestUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link UsernamePWCredentials}.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class UsernamePWCredentialsTest {
  @Test
  @SuppressFBWarnings ("NP_NONNULL_PARAM_VIOLATION")
  public void testDefault () {
    final UsernamePWCredentials uc = new UsernamePWCredentials ();
    assertNull (uc.getUsername ());
    assertNull (uc.getPassword ());

    uc.setUsername ("abc");
    assertEquals ("abc", uc.getUsername ());
    try {
      uc.setUsername (null);
      fail ();
    }
    catch (final IllegalArgumentException ex) {}
    assertEquals ("abc", uc.getUsername ());

    // Logged warning only:
    uc.setUsername ("a:b");
    assertEquals ("a:b", uc.getUsername ());
  }

  @Test
  public void testAll () {
    final UsernamePWCredentials uc = new UsernamePWCredentials ("name", "pw");
    assertEquals ("name", uc.getUsername ());
    assertEquals ("pw", uc.getPassword ());
    uc.setUsername ("name2");
    assertEquals ("name2", uc.getUsername ());
    assertEquals ("pw", uc.getPassword ());
    uc.setPassword ("pw2");
    assertEquals ("name2", uc.getUsername ());
    assertEquals ("pw2", uc.getPassword ());
    uc.setPassword (null);
    assertEquals ("name2", uc.getUsername ());
    assertNull (uc.getPassword ());

    // Test equals/hashcode etc.
    PHTestUtils.testDefaultImplementationWithEqualContentObject (new UsernamePWCredentials ("name", "pw"),
                                                                 new UsernamePWCredentials ("name", "pw"));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (new UsernamePWCredentials ("name", "pw"),
                                                                     new UsernamePWCredentials ("name2", "pw"));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (new UsernamePWCredentials ("name", "pw"),
                                                                     new UsernamePWCredentials ("name", "pww"));
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (new UsernamePWCredentials ("name", "pw"),
                                                                     new UsernamePWCredentials ("name", null));
  }
}
