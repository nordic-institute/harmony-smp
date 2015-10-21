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

import java.net.URL;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.BadRequestFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.InternalErrorFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.NotFoundFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.UnauthorizedFault;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;

import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.sml.client.ManageParticipantIdentifierServiceCaller;

public final class ManageParticipantsClient {
  private final ManageParticipantIdentifierServiceCaller m_aCaller;

  public ManageParticipantsClient (final URL aEndpointAddress) {
    m_aCaller = new ManageParticipantIdentifierServiceCaller (aEndpointAddress);
  }

  public void create (final String sSMPID, final String [] args, final int index) throws BadRequestFault,
                                                                                 InternalErrorFault,
                                                                                 UnauthorizedFault,
                                                                                 NotFoundFault {
    final int paramsLeft = args.length - index;
    if (paramsLeft < 2) {
      System.err.println ("Invalid number of args to create a new identifier.");
      System.out.println ("Use the following two parameters: identifier indentifierType");
      return;
    }
    create (sSMPID, args[index], args[index + 1]);
  }

  public void delete (final String [] args, final int index) throws BadRequestFault,
                                                            InternalErrorFault,
                                                            NotFoundFault,
                                                            UnauthorizedFault {
    final int paramsLeft = args.length - index;
    if (paramsLeft < 2) {
      System.err.println ("Invalid number of args to delete an identifier.");
      System.out.println ("Use the following two parameters: identifier indentifierType");
      return;
    }
    delete (args[index], args[index + 1]);
  }

  public void list (final String sSMPID, final String [] args, final int index) throws BadRequestFault,
                                                                               InternalErrorFault,
                                                                               NotFoundFault,
                                                                               UnauthorizedFault {
    final int paramsLeft = args.length - index;
    switch (paramsLeft) {
      case 0:
        list (sSMPID);
        break;
      default:
        list (sSMPID, args[index]);
        break;
    }
  }

  public UUID prepareToMigrate (final String sSMPID, final String [] args, final int index) throws BadRequestFault,
                                                                                           InternalErrorFault,
                                                                                           NotFoundFault,
                                                                                           UnauthorizedFault {
    final int paramsLeft = args.length - index;
    if (paramsLeft != 2) {
      System.err.println ("Invalid number of args to prepare migrate of identifier.");
      System.out.println ("Use the following two parameters: identifier indentifierType");
      return null;
    }
    final ParticipantIdentifierType aPI = new SimpleParticipantIdentifier (args[index + 1], args[index]);
    return prepareToMigrate (sSMPID, aPI);
  }

  public void migrate (final String sSMPID, final String [] args, final int index) throws BadRequestFault,
                                                                                  InternalErrorFault,
                                                                                  NotFoundFault,
                                                                                  UnauthorizedFault {
    final int paramsLeft = args.length - index;
    if (paramsLeft != 3) {
      System.err.println ("Invalid number of args to migrate an identifier.");
      System.out.println ("Use the following three parameters: identifier indentifierType migrationCode");
      return;
    }
    final ParticipantIdentifierType aPI = new SimpleParticipantIdentifier (args[index + 1], args[index]);
    final UUID aCode = UUID.fromString (args[index + 2]);
    migrate (sSMPID, aPI, aCode);
  }

  void create (final String sSMPID, final ParticipantIdentifierType aIdentifier) throws BadRequestFault,
                                                                                InternalErrorFault,
                                                                                UnauthorizedFault,
                                                                                NotFoundFault {
    m_aCaller.create (sSMPID, aIdentifier);
  }

  void create (final String sSMPID, final String sIdentifierValue, final String sIdentifierScheme) throws BadRequestFault,
                                                                                                  InternalErrorFault,
                                                                                                  UnauthorizedFault,
                                                                                                  NotFoundFault {
    m_aCaller.create (sSMPID, new SimpleParticipantIdentifier (sIdentifierScheme, sIdentifierValue));
  }

  void delete (final String sIdentifierValue, final String sIdentifierScheme) throws BadRequestFault,
                                                                             InternalErrorFault,
                                                                             NotFoundFault,
                                                                             UnauthorizedFault {
    m_aCaller.delete (new SimpleParticipantIdentifier (sIdentifierScheme, sIdentifierValue));
  }

  void list (final String sSMPID) throws BadRequestFault, InternalErrorFault, NotFoundFault, UnauthorizedFault {
    list (sSMPID, "");
  }

  void list (final String sSMPID, final String nextPageIdentifier) throws BadRequestFault,
                                                                  InternalErrorFault,
                                                                  NotFoundFault,
                                                                  UnauthorizedFault {
    final ParticipantIdentifierPageType page = m_aCaller.list (nextPageIdentifier, sSMPID);
    print (page);
  }

  @Nonnull
  UUID prepareToMigrate (final String sSMPID, final ParticipantIdentifierType aIdentifier) throws BadRequestFault,
                                                                                          InternalErrorFault,
                                                                                          NotFoundFault,
                                                                                          UnauthorizedFault {
    final UUID migrationCode = m_aCaller.prepareToMigrate (aIdentifier, sSMPID);

    System.out.println ("Migration code: " + migrationCode);
    return migrationCode;
  }

  void migrate (final String sSMPID, final ParticipantIdentifierType aIdentifier, final UUID migrationCode) throws BadRequestFault,
                                                                                                           InternalErrorFault,
                                                                                                           NotFoundFault,
                                                                                                           UnauthorizedFault {
    m_aCaller.migrate (aIdentifier, migrationCode, sSMPID);
  }

  public static void print (final ParticipantIdentifierPageType page) {
    if (page == null) {
      System.out.println ("Returned page is null");
      return;
    }

    final List <ParticipantIdentifierType> identifierList = page.getParticipantIdentifier ();
    System.out.println ("Found " + identifierList.size () + " participant identifiers:");
    for (final ParticipantIdentifierType identifier : identifierList)
      System.out.println ("  " + identifier.getScheme () + " : " + identifier.getValue ());

    final String sNextPage = page.getNextPageIdentifier ();
    if (sNextPage != null) {
      System.out.println ();
      System.out.println ("Next page: " + sNextPage);
    }
  }
}
