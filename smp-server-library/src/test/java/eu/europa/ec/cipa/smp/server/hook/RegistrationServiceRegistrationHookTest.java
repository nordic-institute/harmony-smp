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
package eu.europa.ec.cipa.smp.server.hook;

import org.junit.Ignore;
import org.junit.Test;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;

/**
 * Test class for class {@link RegistrationServiceRegistrationHook}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class RegistrationServiceRegistrationHookTest {
  @Test
  @Ignore ("Potentially modifies the DNS!")
  public void testCreateAndDelete () {
    final RegistrationServiceRegistrationHook aHook = new RegistrationServiceRegistrationHook ();
    final ParticipantIdentifierType aPI = new ParticipantIdentifierType("0088:12345test", "iso6523-actorid-upis");
    aHook.create (aPI);
    aHook.delete (aPI);
    // Throws ExceptionInInitializerError:
    // Happens when no keystore is present!
  }
}
