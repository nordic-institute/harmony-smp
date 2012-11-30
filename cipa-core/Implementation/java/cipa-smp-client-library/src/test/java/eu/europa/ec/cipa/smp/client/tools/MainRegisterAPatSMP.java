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
package eu.europa.ec.cipa.smp.client.tools;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.Nonnull;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.servicemetadata.publishing._1.ObjectFactory;
import org.busdox.servicemetadata.publishing._1.ProcessListType;
import org.busdox.servicemetadata.publishing._1.ProcessType;
import org.busdox.servicemetadata.publishing._1.ServiceEndpointList;
import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.busdox.servicemetadata.publishing._1.ServiceInformationType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.phloc.commons.state.ESuccess;

import eu.europa.ec.cipa.peppol.identifier.doctype.EPredefinedDocumentTypeIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.process.EPredefinedProcessIdentifier;
import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.peppol.sml.ISMLInfo;
import eu.europa.ec.cipa.peppol.utils.IReadonlyUsernamePWCredentials;
import eu.europa.ec.cipa.peppol.utils.UsernamePWCredentials;
import eu.europa.ec.cipa.smp.client.CSMPIdentifier;
import eu.europa.ec.cipa.smp.client.SMPServiceCaller;

/**
 * This tool lets you register a single AP at an SMP. All the constants must be
 * modified before the tool can be executed!
 * 
 * @author philip
 */
public final class MainRegisterAPatSMP {
  public static final Logger s_aLogger = LoggerFactory.getLogger (MainRegisterAPatSMP.class);

  // Modify the following constants to fit your needs:

  // SMP user name and password (as found in the smp_user table)
  private static final String SMP_USERID = "mySMPUserID";
  private static final String SMP_PASSWORD = "mySMPPassword";

  // The participant you want to register
  private static final String PARTICIPANT_ID = "0088:myGLNNumber";

  // What is the URL of the START service (without any ?wsdl!)
  private static final String AP_ENDPOINTREF = "https://myap.example.com/accessPointService";

  // The Base64 encoded, DER encoded AP certificate (public key only)
  private static final String AP_CERT_STRING = null;

  // Descriptive string
  private static final String AP_SERVICE_DESCRIPTION = "What does my service do?";

  // Contact email
  private static final String AP_CONTACT_URL = "info@mycompany.com";

  // Contact website
  private static final String AP_INFO_URL = "http://company.url";

  // Is a business level signature required?
  private static final boolean AP_REQUIRE_BUSSINES_LEVEL_SIGNATURE = false;

  // Document type to be registered (e.g. invoice T10)
  private static final EPredefinedDocumentTypeIdentifier DOCTYPE = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A;

  // Process type to be registered (e.g. BIS4A)
  private static final EPredefinedProcessIdentifier PROCTYPE = EPredefinedProcessIdentifier.BIS4A;

  // Validity start date (may be present!)
  private static final Date START_DATE = new GregorianCalendar (2012, Calendar.JANUARY, 1).getTime ();

  // Validity end date (may be present!)
  private static final Date END_DATE = new GregorianCalendar (2029, Calendar.DECEMBER, 31).getTime ();

  @Nonnull
  private static ESuccess _registerRecipient (@Nonnull final ISMLInfo aSMLInfo,
                                              @Nonnull final ParticipantIdentifierType aParticipantID,
                                              @Nonnull final EPredefinedDocumentTypeIdentifier eDocumentID,
                                              @Nonnull final EPredefinedProcessIdentifier eProcessID,
                                              @Nonnull final Date aStartDate,
                                              @Nonnull final Date aEndDate,
                                              @Nonnull final IReadonlyUsernamePWCredentials aAuth) {
    if (aParticipantID == null)
      return ESuccess.FAILURE;
    if (eDocumentID == null)
      return ESuccess.FAILURE;
    if (eProcessID == null)
      return ESuccess.FAILURE;
    if (aStartDate == null || aEndDate == null || aStartDate.getTime () > aEndDate.getTime ())
      return ESuccess.FAILURE;

    try {
      // Create object for ServiceGroup registration
      final ObjectFactory aObjFactory = new ObjectFactory ();
      final ServiceGroupType aServiceGroup = aObjFactory.createServiceGroupType ();
      aServiceGroup.setParticipantIdentifier (aParticipantID);

      // 1. create the service group
      final SMPServiceCaller aSMPCaller = new SMPServiceCaller (aParticipantID, aSMLInfo);
      aSMPCaller.saveServiceGroup (aServiceGroup, aAuth);

      // 2. create the service registration
      final ServiceMetadataType aServiceMetadata = aObjFactory.createServiceMetadataType ();
      {
        final ServiceInformationType aServiceInformation = aObjFactory.createServiceInformationType ();
        {
          final ProcessListType aProcessList = aObjFactory.createProcessListType ();
          {
            final ProcessType aProcess = aObjFactory.createProcessType ();
            {
              final ServiceEndpointList aServiceEndpointList = aObjFactory.createServiceEndpointList ();
              {
                final EndpointType aEndpoint = aObjFactory.createEndpointType ();
                aEndpoint.setEndpointReference (new W3CEndpointReferenceBuilder ().address (AP_ENDPOINTREF).build ());
                aEndpoint.setTransportProfile (CSMPIdentifier.TRANSPORT_PROFILE_START);
                aEndpoint.setCertificate (AP_CERT_STRING);
                aEndpoint.setServiceActivationDate (aStartDate);
                aEndpoint.setServiceExpirationDate (aEndDate);
                aEndpoint.setServiceDescription (AP_SERVICE_DESCRIPTION);
                aEndpoint.setTechnicalContactUrl (AP_CONTACT_URL);
                aEndpoint.setTechnicalInformationUrl (AP_INFO_URL);
                aEndpoint.setMinimumAuthenticationLevel ("1");
                aEndpoint.setRequireBusinessLevelSignature (AP_REQUIRE_BUSSINES_LEVEL_SIGNATURE);
                aServiceEndpointList.getEndpoint ().add (aEndpoint);
              }
              aProcess.setProcessIdentifier (eProcessID.getAsProcessIdentifier ());
              aProcess.setServiceEndpointList (aServiceEndpointList);
            }
            aProcessList.getProcess ().add (aProcess);
          }
          aServiceInformation.setDocumentIdentifier (eDocumentID.getAsDocumentTypeIdentifier ());
          aServiceInformation.setParticipantIdentifier (aParticipantID);
          aServiceInformation.setProcessList (aProcessList);
        }
        aServiceMetadata.setServiceInformation (aServiceInformation);
      }
      aSMPCaller.saveServiceRegistration (aServiceMetadata, aAuth);

      return ESuccess.SUCCESS;
    }
    catch (final Exception ex) {
      s_aLogger.error ("Error saving service registration for " + aParticipantID, ex);
      return ESuccess.FAILURE;
    }
  }

  public static void main (final String [] args) {
    _registerRecipient (ESML.PRODUCTION,
                        SimpleParticipantIdentifier.createWithDefaultScheme (PARTICIPANT_ID),
                        DOCTYPE,
                        PROCTYPE,
                        START_DATE,
                        END_DATE,
                        new UsernamePWCredentials (SMP_USERID, SMP_PASSWORD));
  }
}
