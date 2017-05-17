/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.cipa.smp.server.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for class {@link ConfigFile}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class ConfigFileTest {
  @Test
  public void testAll () {
    final ConfigFile aCF = ConfigFile.getInstance ();
    assertTrue (aCF.isRead ());
    // Existing elements
    assertEquals ("string", aCF.getString ("element1"));
    assertEquals (6, aCF.getCharArray ("element1").length);
    assertEquals (2, aCF.getInt ("element2", 5));
    assertFalse (aCF.getBoolean ("element3", true));
    assertEquals ("abc", aCF.getString ("element4"));

    List<String> listElements = aCF.getStringList("aList");
    Assert.assertArrayEquals(new String [] {"elem1", "elem2", "elem3"}, listElements.toArray());

    // Non-existing elements
    assertNull (aCF.getString ("element1a"));
    assertNull (aCF.getCharArray ("element1a"));
    assertEquals (5, aCF.getInt ("element2a", 5));
    assertTrue (aCF.getBoolean ("element3a", true));
    assertTrue (aCF.getStringList("anEmptyList").isEmpty());

    // All keys
    assertEquals (26, aCF.getAllKeys ().size ());

    assertNotNull (aCF.toString ());
  }

  @Test
  public void testNonExisting () {
    final ConfigFile aCF = new ConfigFile (new String[] {"non-existent-file.xml"});
    assertFalse (aCF.isRead ());
    assertNull (aCF.getString ("any"));
    assertEquals (0, aCF.getAllKeys ().size ());

    assertNotNull (aCF.toString ());
  }
}
