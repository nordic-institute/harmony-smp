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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.busdox.servicemetadata.managebusinessidentifierservice._1.BadRequestFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.InternalErrorFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.NotFoundFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.UnauthorizedFault;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;

import eu.europa.ec.cipa.peppol.sml.CSMLDefault;
import eu.europa.ec.cipa.peppol.sml.ISMLInfo;
import eu.europa.ec.cipa.sml.client.support.ESMLCommand;
import eu.europa.ec.cipa.sml.client.support.ESMLObjectType;

public final class Main {
  private static ManageSMPClient m_aManageServiceMetadataClient;
  private static ManageParticipantsClient m_aManageParticipantIdentifierClient;
  private static URL m_aManageParticipantIdentifierEndpointAddress;
  private static URL m_aManageServiceMetadataEndpointAddress;
  private static String m_sSMPID;

  private static ManageSMPClient _getSMClient () {
    if (m_aManageServiceMetadataClient == null)
      m_aManageServiceMetadataClient = new ManageSMPClient (m_aManageServiceMetadataEndpointAddress);
    return m_aManageServiceMetadataClient;
  }

  private static ManageParticipantsClient _getMPClient () {
    if (m_aManageParticipantIdentifierClient == null)
      m_aManageParticipantIdentifierClient = new ManageParticipantsClient (m_aManageParticipantIdentifierEndpointAddress);
    return m_aManageParticipantIdentifierClient;
  }

  /**
   * @param args
   *        Commandline arguments
   */
  public static void main (final String [] args) {
    try {
      // TODO: set property
      // System.setProperty("javax.net.ssl.trustStore",
      // "conf/servIdentity.jks");
      // System.setProperty("javax.net.ssl.trustStorePassword", "Test1234");
      int index = 0;
      while (hasSpecial (args, index)) {
        handleSpecial (args, index);
        index += 2;
      }

      handleCommand (args, index);
    }
    catch (final Exception ex) {
      ex.printStackTrace ();
      System.exit (-1);
    }
  }

  private static boolean hasSpecial (final String [] args, final int index) {
    if (args.length == index)
      return false;
    if (args[index].equals ("-i"))
      return true;
    if (args[index].equals ("-h"))
      return true;
    return false;
  }

  private static void handleSpecial (final String [] args, final int index) throws MalformedURLException {
    final String sParam = args[index];
    if (sParam.equals ("-i"))
      setSMPID (args, index + 1);
    else
      if (sParam.equals ("-h"))
        setHost (args, index + 1);
      else
        System.err.println ("Illegal parameter " + sParam);
  }

  private static void setSMPID (final String [] args, final int index) {
    if (args.length == index) {
      System.err.println ("No id is given.");
      printHelp ();
      System.exit (-1);
    }
    setSMPID (args[index]);
  }

  public static void setSMPID (final String sSMPID) {
    m_sSMPID = sSMPID;
  }

  public static String getSMPID () {
    return m_sSMPID;
  }

  private static void setHost (final String [] args, final int index) throws MalformedURLException {
    if (args.length == index) {
      System.err.println ("No host is given.");
      printHelp ();
      System.exit (-1);
    }
    setHost (args[index]);
  }

  public static void setHost (final String sHost) throws MalformedURLException {
    m_aManageParticipantIdentifierEndpointAddress = new URL (sHost +
                                                             CSMLDefault.MANAGEMENT_SERVICE_PARTICIPANTIDENTIFIER);
    m_aManageServiceMetadataEndpointAddress = new URL (sHost + CSMLDefault.MANAGEMENT_SERVICE_METADATA);
  }

  public static void setHost (@Nonnull final ISMLInfo aSMLInfo) {
    m_aManageParticipantIdentifierEndpointAddress = aSMLInfo.getManageParticipantIdentifierEndpointAddress ();
    m_aManageServiceMetadataEndpointAddress = aSMLInfo.getManageServiceMetaDataEndpointAddress ();
  }

  private static void handleCommand (final String [] args, final int nStartIndex) throws Exception {
    int index = nStartIndex;
    if (args.length == index) {
      System.err.println ("No command was given");
      printHelp ();
      System.exit (-1);
    }
    final String command = args[index++];
    final ESMLCommand eClientCommand = ESMLCommand.getFromNameOrNull (command);
    switch (eClientCommand) {
      case CREATE:
        create (args, index);
        return;
      case UPDATE:
        update (args, index);
        return;
      case DELETE:
        delete (args, index);
        return;
      case READ:
        read (args, index);
        return;
      case LIST:
        list (args, index);
        return;
      case PREPARETOMIGRATE:
        prepareToMigrate (args, index);
        return;
      case MIGRATE:
        migrate (args, index);
        return;
    }
    System.err.println ("Unknown command " + command);
    printHelp ();
    System.exit (-1);
  }

  private static void create (final String [] args, final int index) throws Exception {
    final String sObject = args[index];
    final ESMLObjectType eObject = ESMLObjectType.getFromNameOrNull (sObject);
    if (eObject != null)
      switch (eObject) {
        case PARTICIPANT:
          _getMPClient ().create (m_sSMPID, args, index + 1);
          return;
        case METADATA:
          _getSMClient ().create (m_sSMPID, args, index + 1);
          return;
      }
    System.err.println ("CANNOT DO CREATE ON " + sObject);
    printHelp ();
    System.exit (-1);
  }

  private static void update (final String [] args, final int index) throws Exception {
    final String sObject = args[index];
    final ESMLObjectType eObject = ESMLObjectType.getFromNameOrNull (sObject);
    if (eObject != null)
      switch (eObject) {
        case PARTICIPANT:
          // not supported
          break;
        case METADATA:
          _getSMClient ().update (m_sSMPID, args, index + 1);
          return;
      }
    System.err.println ("CANNOT DO UPDATE ON " + sObject);
    printHelp ();
    System.exit (-1);
  }

  private static void delete (final String [] args, final int index) throws Exception {
    final String sObject = args[index];
    final ESMLObjectType eObject = ESMLObjectType.getFromNameOrNull (sObject);
    if (eObject != null)
      switch (eObject) {
        case PARTICIPANT:
          _getMPClient ().delete (args, index + 1);
          return;
        case METADATA:
          _getSMClient ().delete (m_sSMPID, args, index + 1);
          return;
      }
    System.err.println ("CANNOT DO DELETE ON " + sObject);
    printHelp ();
    System.exit (-1);
  }

  static void deleteParticipant (final String sBusinessIdentifier, final String sBusinessIdentifierScheme) throws BadRequestFault,
                                                                                                          InternalErrorFault,
                                                                                                          NotFoundFault,
                                                                                                          UnauthorizedFault {
    _getMPClient ().delete (sBusinessIdentifier, sBusinessIdentifierScheme);
  }

  static void deleteServiceMetadata () throws org.busdox.servicemetadata.manageservicemetadataservice._1.InternalErrorFault,
                                      org.busdox.servicemetadata.manageservicemetadataservice._1.NotFoundFault,
                                      org.busdox.servicemetadata.manageservicemetadataservice._1.UnauthorizedFault,
                                      org.busdox.servicemetadata.manageservicemetadataservice._1.BadRequestFault {
    deleteServiceMetadata (m_sSMPID);
  }

  public static void deleteServiceMetadata (final String sSMPID) throws org.busdox.servicemetadata.manageservicemetadataservice._1.InternalErrorFault,
                                                                org.busdox.servicemetadata.manageservicemetadataservice._1.NotFoundFault,
                                                                org.busdox.servicemetadata.manageservicemetadataservice._1.UnauthorizedFault,
                                                                org.busdox.servicemetadata.manageservicemetadataservice._1.BadRequestFault {
    _getSMClient ().delete (sSMPID);
  }

  static UUID prepateToMigrate (final ParticipantIdentifierType aIdentifier) throws BadRequestFault,
                                                                            InternalErrorFault,
                                                                            NotFoundFault,
                                                                            UnauthorizedFault {
    return _getMPClient ().prepareToMigrate (m_sSMPID, aIdentifier);
  }

  static void migrate (final ParticipantIdentifierType aIdentifier, final UUID aMigrationCode) throws BadRequestFault,
                                                                                              InternalErrorFault,
                                                                                              NotFoundFault,
                                                                                              UnauthorizedFault {
    _getMPClient ().migrate (m_sSMPID, aIdentifier, aMigrationCode);
  }

  private static void read (final String [] args, final int index) throws Exception {
    final String sObject = args[index];
    final ESMLObjectType eObject = ESMLObjectType.getFromNameOrNull (sObject);
    if (eObject != null)
      switch (eObject) {
        case PARTICIPANT:
          // not supported
          break;
        case METADATA:
          _getSMClient ().read (m_sSMPID, args, index + 1);
          return;
      }
    System.err.println ("CANNOT DO READ ON " + sObject);
    printHelp ();
    System.exit (-1);
  }

  private static void list (final String [] args, final int index) throws Exception {
    final String sObject = args[index];
    final ESMLObjectType eObject = ESMLObjectType.getFromNameOrNull (sObject);
    if (eObject != null)
      switch (eObject) {
        case PARTICIPANT:
          _getMPClient ().list (m_sSMPID, args, index + 1);
          return;
        case METADATA:
          // not supported
          break;
      }
    System.err.println ("CANNOT DO LIST ON " + sObject);
    printHelp ();
    System.exit (-1);
  }

  private static void prepareToMigrate (final String [] args, final int index) throws BadRequestFault,
                                                                              InternalErrorFault,
                                                                              NotFoundFault,
                                                                              UnauthorizedFault {
    final String sObject = args[index];
    final ESMLObjectType eObject = ESMLObjectType.getFromNameOrNull (sObject);
    if (eObject != null)
      switch (eObject) {
        case PARTICIPANT:
          _getMPClient ().prepareToMigrate (m_sSMPID, args, index + 1);
          return;
        case METADATA:
          // not supported
          break;
      }
    System.err.println ("CANNOT DO PREPARETOMIGRATE ON " + sObject);
    printHelp ();
    System.exit (-1);
  }

  private static void migrate (final String [] args, final int index) throws BadRequestFault,
                                                                     InternalErrorFault,
                                                                     NotFoundFault,
                                                                     UnauthorizedFault {
    final String sObject = args[index];
    final ESMLObjectType eObject = ESMLObjectType.getFromNameOrNull (sObject);
    if (eObject != null)
      switch (eObject) {
        case PARTICIPANT:
          _getMPClient ().migrate (m_sSMPID, args, index + 1);
          return;
        case METADATA:
          // not supported
          break;
      }
    System.err.println ("CANNOT DO MIGRATE ON " + sObject);
    printHelp ();
    System.exit (-1);
  }

  private static void printHelp () {
    System.out.println ("Manage the registry service and the business identifiers on the regestry service.");
    System.out.println ();
    System.out.println ("Usage: java -jar sml-client.jar [-u user -p password] [-h host] [-i id] action type params");
    System.out.println ();
    System.out.println ("user" + '\t' + '\t' + "The username on the web service.");
    System.out.println ("password" + '\t' + "The password on the web service.");
    System.out.println ("host" +
                        '\t' +
                        '\t' +
                        "The hostname of the web service (without the service name but ending with a slash).");
    System.out.println ("id" + '\t' + '\t' + "The Publisher ID (SMP ID).");
    System.out.println ("action" +
                        '\t' +
                        '\t' +
                        "The action wanted can be [" +
                        ESMLCommand.CREATE.getName () +
                        "," +
                        ESMLCommand.UPDATE.getName () +
                        "," +
                        ESMLCommand.DELETE.getName () +
                        "," +
                        ESMLCommand.READ.getName () +
                        "," +
                        ESMLCommand.LIST.getName () +
                        "," +
                        ESMLCommand.PREPARETOMIGRATE.getName () +
                        "," +
                        ESMLCommand.MIGRATE.getName () +
                        "].");
    System.out.println ("type" +
                        '\t' +
                        '\t' +
                        "The type the action should be used on can be[" +
                        ESMLObjectType.PARTICIPANT.getName () +
                        "," +
                        ESMLObjectType.METADATA.getName () +
                        "].");
    System.out.println ();
    System.out.println ("Example: java -jar peppol-sml-client-console.jar -u TestUser -p Test1234 -h http://localhost:8080/registrylocatorservice/ read metadata");
  }
}
