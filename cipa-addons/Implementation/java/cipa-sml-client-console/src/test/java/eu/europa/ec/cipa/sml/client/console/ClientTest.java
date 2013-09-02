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
package eu.europa.ec.cipa.sml.client.console;

import java.util.UUID;

import org.busdox.servicemetadata.managebusinessidentifierservice._1.BadRequestFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.InternalErrorFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.NotFoundFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.UnauthorizedFault;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.phloc.commons.annotations.DevelopersNote;

import eu.europa.ec.cipa.peppol.identifier.CIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.peppol.sml.ISMLInfo;

/**
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Ignore
@DevelopersNote ("You need a running test SML for this test")
public final class ClientTest {
  // SML specific data
  private static ISMLInfo SML_INFO = ESML.PRODUCTION;

  // SML unspecific data
  private static final String PI_SCHEMA = CIdentifier.DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME;
  private static final String PI_ID_CRUD = "9915:tmp";
  private static final ParticipantIdentifierType PI = SimpleParticipantIdentifier.createWithDefaultScheme (PI_ID_CRUD);
  private static final String SMP_ID_CRUD = "SMP-CLIENTTEST";
  private static final String SMP_ID_MIGRATETO = "SMP-CLIENTTEST2";

  @Before
  public void initiate () {
    System.setProperty ("http.proxyHost", "172.30.9.12");
    System.setProperty ("http.proxyPort", "8080");
    System.setProperty ("https.proxyHost", "172.30.9.12");
    System.setProperty ("https.proxyPort", "8080");

    // Where is the SML located?
    Main.setHost (SML_INFO);

    // The dummy SMP we're working on
    Main.setSMPID (SMP_ID_CRUD);

    // Initial cleanup!
    try {
      // In case a previous test failed, ensure that no SMP data is contained!
      Main.deleteServiceMetadata (SMP_ID_CRUD);
      Main.deleteServiceMetadata (SMP_ID_MIGRATETO);
    }
    catch (final org.busdox.servicemetadata.manageservicemetadataservice._1.NotFoundFault ex) {
      // okay
    }
    catch (final Exception ex) {
      ex.printStackTrace ();
    }
    try {
      // In case a previous test failed, ensure that no participant data is
      // contained (is unique across all SMPs)
      Main.deleteParticipant (PI_ID_CRUD, PI_SCHEMA);
    }
    catch (final org.busdox.servicemetadata.managebusinessidentifierservice._1.NotFoundFault ex) {
      // okay
    }
    catch (final Exception ex) {
      ex.printStackTrace ();
    }

    // Main.print();
  }

  /**
   * This unit test creates, updates and deletes a SMP on the configured SML.
   * After creation and deletion the data is read from the SML and output
   * generated on standard out.
   */
  @Test
  public void testMetadataCRUD () {
    Main.main (new String [] { "create", "metadata", "127.0.0.1", "http://mySMP" });
    Main.main (new String [] { "read", "metadata" });
    Main.main (new String [] { "update", "metadata", "127.0.0.2", "http://anotherSMP" });
    Main.main (new String [] { "read", "metadata" });
    Main.main (new String [] { "delete", "metadata" });
    Main.main (new String [] { "read", "metadata" });
  }

  /**
   * This unit test creates a new participant on the SML (not on the SMP!),
   * lists all participants registered for the SMP (on the SML, no lookup on the
   * SMP performed!) and deletes the participant.
   */
  @Test
  public void testIdentifierCRUD () {
    Main.main (new String [] { "create", "metadata", "127.0.0.1", "http://mySMP" });
    Main.main (new String [] { "create", "participant", PI_ID_CRUD, PI_SCHEMA });
    Main.main (new String [] { "list", "participant" });
    Main.main (new String [] { "delete", "participant", PI_ID_CRUD, PI_SCHEMA });
    Main.main (new String [] { "list", "participant" });
    Main.main (new String [] { "delete", "metadata" });
  }

  /**
   * This unit test creates a new participant on the SML (not on the SMP!),
   * lists all participants registered for the SMP (on the SML, no lookup on the
   * SMP performed!) and deletes the participant.
   */
  @Test
  public void testIdentifierCRUD_ParticipantOnly () {
    Main.main (new String [] { "create", "participant", PI_ID_CRUD, PI_SCHEMA });
    Main.main (new String [] { "list", "participant" });
    Main.main (new String [] { "delete", "participant", PI_ID_CRUD, PI_SCHEMA });
    Main.main (new String [] { "list", "participant" });
  }

  @Test
  public void testIdentifierMigrate () throws BadRequestFault, InternalErrorFault, NotFoundFault, UnauthorizedFault {
    // Create source and target SMPs
    Main.setSMPID (SMP_ID_CRUD);
    Main.main (new String [] { "create", "metadata", "127.0.0.1", "http://mySMP" });
    Main.setSMPID (SMP_ID_MIGRATETO);
    Main.main (new String [] { "create", "metadata", "127.0.0.2", "http://myOtherSMP" });
    // Register participant in source SMP
    Main.setSMPID (SMP_ID_CRUD);
    Main.main (new String [] { "create", "participant", PI_ID_CRUD, PI_SCHEMA });
    System.out.println ("Listing all participants in SMP " + Main.getSMPID ());
    Main.main (new String [] { "list", "participant" });

    // Get migration code
    final UUID aMigrationCode = Main.prepateToMigrate (PI);

    // Do migrate
    Main.setSMPID (SMP_ID_MIGRATETO);
    Main.main (new String [] { "migrate", "participant", PI_ID_CRUD, PI_SCHEMA, aMigrationCode.toString () });

    // Check results
    System.out.println ("Listing all participants in SMP " + Main.getSMPID ());
    Main.main (new String [] { "list", "participant" });
    Main.main (new String [] { "delete", "metadata" });

    Main.setSMPID (SMP_ID_CRUD);
    System.out.println ("Listing all participants in SMP " + Main.getSMPID ());
    Main.main (new String [] { "list", "participant" });
    Main.main (new String [] { "delete", "metadata" });

    // End of list marker
    System.out.println ("Done");
  }
}
