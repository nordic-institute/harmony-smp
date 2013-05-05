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
package eu.europa.ec.cipa.sml.server.jpa;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.busdox.servicemetadata.locator._1.ObjectFactory;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.phloc.commons.annotations.DevelopersNote;
import com.phloc.scopes.mock.ScopeTestRule;

import eu.europa.ec.cipa.sml.server.IGenericDataHandler;
import eu.europa.ec.cipa.sml.server.exceptions.NotFoundException;

/**
 * Test class for class {@link SMLDataHandlerGeneric}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@DevelopersNote ("You need to adjust your local src/test/resources/config.properties file to run this test")
public final class SMLDataHandlerGenericTest {
  // Static for every test.
  private static final ObjectFactory s_aObjFactory = new ObjectFactory ();
  private static IGenericDataHandler s_aGenericHandler;

  private static final class SMLTestRule extends ScopeTestRule {
    @Override
    public void before () {
      super.before ();
      if (s_aGenericHandler == null) {
        // Do it only once :)
        SMLEntityManagerFactory.getInstance ();
        s_aGenericHandler = new SMLDataHandlerGeneric ();
      }
    }
  }

  @ClassRule
  public static TestRule s_aTestRule = new SMLTestRule ();

  @Test
  public void testGetAllSMPIDs () throws Throwable {
    final List <String> aAllSMPIDs = s_aGenericHandler.getAllSMPIDs ();
    assertNotNull (aAllSMPIDs);
  }

  @Test
  public void testGetSMPEndpointAddressOfSMPID () throws Throwable {
    try {
      s_aGenericHandler.getSMPEndpointAddressOfSMPID (null);
      fail ();
    }
    catch (final NotFoundException ex) {}
  }
}
