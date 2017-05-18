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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test class for class {@link BusdoxURLUtils}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class BusdoxURLUtilsTest {
  @Test
  public void testCreatePercentEncodedURL () {
    assertNull (BusdoxURLUtils.createPercentEncodedURL (null));
    assertEquals ("", BusdoxURLUtils.createPercentEncodedURL (""));
    assertEquals ("abc", BusdoxURLUtils.createPercentEncodedURL ("abc"));
    assertEquals ("a%25b", BusdoxURLUtils.createPercentEncodedURL ("a%b"));
    assertEquals ("a%25%25b", BusdoxURLUtils.createPercentEncodedURL ("a%%b"));
    assertEquals ("a%2Fb", BusdoxURLUtils.createPercentEncodedURL ("a/b"));
  }

}
