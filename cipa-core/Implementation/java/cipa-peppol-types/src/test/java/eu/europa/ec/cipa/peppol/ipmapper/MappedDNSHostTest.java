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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import com.phloc.commons.mock.PhlocTestUtils;

/**
 * Test class for class {@link MappedDNSHost}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class MappedDNSHostTest {
  @Test
  public void testCtor () {
    MappedDNSHost aST = new MappedDNSHost ("myhost");
    assertEquals ("myhost", aST.getHost ());
    assertNull (aST.getPort ());
    assertEquals (22, aST.getPortToUse (22));
    assertEquals ("myhost", aST.getHostString ());
    assertEquals ("myhost:22", aST.getHostString (22));

    aST = new MappedDNSHost ("myhost", 15);
    assertEquals ("myhost", aST.getHost ());
    assertEquals (15, aST.getPort ().intValue ());
    assertEquals (15, aST.getPortToUse (22));
    assertEquals ("myhost:15", aST.getHostString ());
    assertEquals ("myhost:15", aST.getHostString (22));

    aST = new MappedDNSHost ("myhost", Integer.valueOf (27));
    assertEquals ("myhost", aST.getHost ());
    assertEquals (27, aST.getPort ().intValue ());
    assertEquals (27, aST.getPortToUse (22));
    assertEquals ("myhost:27", aST.getHostString ());
    assertEquals ("myhost:27", aST.getHostString (22));

    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (aST, new MappedDNSHost ("myhost", 27));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (aST, new MappedDNSHost ("myhost", 28));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (aST, new MappedDNSHost ("myhost2", 27));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (aST, new MappedDNSHost ("myhost"));
  }

  @Test
  public void testCreate () {
    // Create with port
    MappedDNSHost aST = MappedDNSHost.create ("myhost:67");
    assertNotNull (aST);
    assertEquals ("myhost", aST.getHost ());
    assertEquals (67, aST.getPort ().intValue ());
    assertEquals ("myhost:67", aST.getHostString ());

    // Check if bidirectional conversion works
    assertEquals (aST, MappedDNSHost.create (aST.getHostString ()));

    // Create without port
    aST = MappedDNSHost.create ("myhost67");
    assertNotNull (aST);
    assertEquals ("myhost67", aST.getHost ());
    assertNull (aST.getPort ());
    assertEquals ("myhost67", aST.getHostString ());

    aST = MappedDNSHost.create ("www.chello.at:80");
    assertEquals ("www.chello.at", aST.getHost ());
    assertEquals (80, aST.getPort ().intValue ());

    // Create with weird syntax
    try {
      MappedDNSHost.create ("myhost:69:test");
      fail ();
    }
    catch (final IllegalArgumentException ex) {
      // expected
    }

    try {
      MappedDNSHost.create ("");
      fail ();
    }
    catch (final IllegalArgumentException ex) {
      // expected
    }
  }

  /**
   * Test method for
   * {@link eu.europa.ec.cipa.peppol.ipmapper.MappedDNSHost#hashCode()} .
   */
  @Test
  public void testHashCode () {
    final MappedDNSHost st1 = MappedDNSHost.create ("1.1.1.1:28080");
    MappedDNSHost st2 = MappedDNSHost.create ("1.1.1.1:28080");
    assertEquals (st1.hashCode (), st2.hashCode ());
    st2 = MappedDNSHost.create ("1.1.1.1");
    assertEquals ("1.1.1.1".hashCode (), st2.getHostString ().hashCode ());
    assertFalse (st1.hashCode () == st2.hashCode ());
    st2 = MappedDNSHost.create ("1.1.1.:28080");
    assertFalse (st1.hashCode () == st2.hashCode ());
    st2 = MappedDNSHost.create ("1.1.1.1:28080");
    assertEquals (st1.hashCode (), st2.hashCode ());
  }

  /**
   * Test method for
   * {@link eu.europa.ec.cipa.peppol.ipmapper.MappedDNSHost#create(java.lang.String)}
   * .
   */
  @Test
  public void testCreateSocketTypeWithDnsResolution () {
    try {
      final String hostName = InetAddress.getByName ("www.chello.at").getHostName ();
      final MappedDNSHost st = MappedDNSHost.create (hostName);
      assertEquals ("www.chello.at", st.getHost ());
      assertNull (st.getPort ());
    }
    catch (final UnknownHostException ex) {
      // Happens in offline more
    }
  }

  /**
   * Test method for
   * {@link eu.europa.ec.cipa.peppol.ipmapper.MappedDNSHost#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObject () {
    final MappedDNSHost st1 = MappedDNSHost.create ("1.1.1.1:28080");
    MappedDNSHost st2 = MappedDNSHost.create ("1.1.1.1:28080");
    assertEquals (st1.hashCode (), st2.hashCode ());
    st2 = MappedDNSHost.create ("1.1.1.1");
    assertFalse (st1.equals (st2));
    assertEquals ("1.1.1.1", st2.getHostString ());
    st2 = MappedDNSHost.create ("1.1.1.:28080");
    assertFalse (st1.equals (st2));
    st2 = MappedDNSHost.create ("1.1.1.1:28080");
    assertEquals (st1, st2);
  }

  @Test
  public void testSocketTypeStringInt () {
    final MappedDNSHost st = new MappedDNSHost ("www.chello.at", 80);
    final MappedDNSHost st1 = MappedDNSHost.create ("www.chello.at:80");
    assertEquals (st, st1);
  }

  /**
   * Test method for
   * {@link eu.europa.ec.cipa.peppol.ipmapper.MappedDNSHost#getHost()}.
   */
  @Test
  public void testGetHost () {
    final MappedDNSHost st = new MappedDNSHost ("www.chello.at", 80);
    final MappedDNSHost st1 = new MappedDNSHost ("www.chello.at", 80);
    final MappedDNSHost st2 = new MappedDNSHost ("www.chello.at", 1);
    assertEquals (st, st1);
    assertFalse (st.equals (st2));
    assertEquals (st.getHost (), st1.getHost ());
    assertEquals (st1.getHost (), st2.getHost ());
  }

  /**
   * Test method for
   * {@link eu.europa.ec.cipa.peppol.ipmapper.MappedDNSHost#getPort()}.
   */
  @Test
  public void testGetPort () {
    final MappedDNSHost st = new MappedDNSHost ("www.chello.at", 80);
    final MappedDNSHost st1 = new MappedDNSHost ("www.chello.at", 80);
    final MappedDNSHost st2 = new MappedDNSHost ("www.chello.at", 1);
    assertEquals (st.getPort (), st1.getPort ());
    assertFalse (st.getPort ().equals (st2.getPort ()));
    // System.out.println(String.format("st.getPort='%d', st1.getPort='%d'",
    // st.getPort(), st1.getPort()));
    assertEquals (st.getPort ().intValue (), st1.getPort ().intValue ());
  }

  /**
   * Test method for
   * {@link eu.europa.ec.cipa.peppol.ipmapper.MappedDNSHost#getHostString()}.
   */
  @Test
  public void testGetSocketString () {
    final MappedDNSHost st = new MappedDNSHost ("1.1.1.1", 10);
    final MappedDNSHost st1 = new MappedDNSHost ("1.1.1.1", 10);
    final MappedDNSHost st2 = new MappedDNSHost ("1.1.2.1", 10);
    final MappedDNSHost st3 = new MappedDNSHost ("1.1.1.1", 9);
    assertEquals (st.getHostString (), st1.getHostString ());
    assertFalse (st1.equals (st2));
    assertFalse (st.equals (st3));
  }
}
