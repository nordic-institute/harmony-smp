/**
 * Copyright (C) 2015 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
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
package com.helger.peppol.smpserver.restapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.state.ESuccess;
import com.helger.commons.statistics.IMutableStatisticsHandlerKeyedCounter;
import com.helger.commons.statistics.IStatisticsHandlerKeyedCounter;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.peppol.identifier.DocumentIdentifierType;
import com.helger.peppol.identifier.IDocumentTypeIdentifier;
import com.helger.peppol.identifier.IdentifierHelper;
import com.helger.peppol.identifier.ParticipantIdentifierType;
import com.helger.peppol.identifier.doctype.SimpleDocumentTypeIdentifier;
import com.helger.peppol.identifier.participant.SimpleParticipantIdentifier;
import com.helger.peppol.smp.CompleteServiceGroupType;
import com.helger.peppol.smp.EndpointType;
import com.helger.peppol.smp.ProcessListType;
import com.helger.peppol.smp.ProcessType;
import com.helger.peppol.smp.SMPExtensionConverter;
import com.helger.peppol.smp.ServiceGroupReferenceListType;
import com.helger.peppol.smp.ServiceGroupReferenceType;
import com.helger.peppol.smp.ServiceGroupType;
import com.helger.peppol.smp.ServiceInformationType;
import com.helger.peppol.smp.ServiceMetadataReferenceCollectionType;
import com.helger.peppol.smp.ServiceMetadataReferenceType;
import com.helger.peppol.smp.ServiceMetadataType;
import com.helger.peppol.smp.SignedServiceMetadataType;
import com.helger.peppol.smpserver.domain.SMPMetaManager;
import com.helger.peppol.smpserver.domain.redirect.ISMPRedirect;
import com.helger.peppol.smpserver.domain.redirect.ISMPRedirectManager;
import com.helger.peppol.smpserver.domain.servicegroup.ISMPServiceGroup;
import com.helger.peppol.smpserver.domain.servicegroup.ISMPServiceGroupManager;
import com.helger.peppol.smpserver.domain.serviceinfo.ISMPServiceInformation;
import com.helger.peppol.smpserver.domain.serviceinfo.ISMPServiceInformationManager;
import com.helger.peppol.smpserver.domain.serviceinfo.SMPEndpoint;
import com.helger.peppol.smpserver.domain.serviceinfo.SMPProcess;
import com.helger.peppol.smpserver.domain.serviceinfo.SMPServiceInformation;
import com.helger.peppol.smpserver.domain.user.ISMPUser;
import com.helger.peppol.smpserver.domain.user.ISMPUserManager;
import com.helger.peppol.smpserver.exception.SMPNotFoundException;
import com.helger.peppol.smpserver.exception.SMPUnauthorizedException;
import com.helger.peppol.utils.W3CEndpointReferenceHelper;
import com.helger.web.http.basicauth.BasicAuthClientCredentials;

/**
 * This class implements all the service methods, that must be provided by the
 * REST service.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class SMPServerAPI
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SMPServerAPI.class);
  private static final IMutableStatisticsHandlerKeyedCounter s_aStatsCounterInvocation = StatisticsManager.getKeyedCounterHandler (SMPServerAPI.class.getName () +
                                                                                                                                   "$call");
  private static final IMutableStatisticsHandlerKeyedCounter s_aStatsCounterSuccess = StatisticsManager.getKeyedCounterHandler (SMPServerAPI.class.getName () +
                                                                                                                                "$success");
  private final ISMPServerAPIDataProvider m_aAPIProvider;

  public SMPServerAPI (@Nonnull final ISMPServerAPIDataProvider aDataProvider)
  {
    m_aAPIProvider = ValueEnforcer.notNull (aDataProvider, "DataProvider");
  }

  @Nonnull
  public CompleteServiceGroupType getCompleteServiceGroup (final String sServiceGroupID) throws Throwable
  {
    s_aLogger.info ("getCompleteServiceGroup - GET /complete/" + sServiceGroupID);
    s_aStatsCounterInvocation.increment ("getCompleteServiceGroup");

    final SimpleParticipantIdentifier aServiceGroupID = SimpleParticipantIdentifier.createFromURIPartOrNull (sServiceGroupID);
    if (aServiceGroupID == null)
    {
      // Invalid identifier
      throw new SMPNotFoundException ("Failed to parse serviceGroup '" +
                                      sServiceGroupID +
                                      "'",
                                      m_aAPIProvider.getCurrentURI ());
    }

    final ISMPServiceGroupManager aServiceGroupMgr = SMPMetaManager.getServiceGroupMgr ();
    final ISMPServiceInformationManager aServiceInfoMgr = SMPMetaManager.getServiceInformationMgr ();

    final ISMPServiceGroup aServiceGroup = aServiceGroupMgr.getSMPServiceGroupOfID (aServiceGroupID);
    if (aServiceGroup == null)
    {
      // No such service group
      throw new SMPNotFoundException ("Unknown serviceGroup '" +
                                      sServiceGroupID +
                                      "'",
                                      m_aAPIProvider.getCurrentURI ());
    }

    /*
     * Then add the service metadata references
     */
    final ServiceMetadataReferenceCollectionType aRefCollection = new ServiceMetadataReferenceCollectionType ();
    final List <ServiceMetadataReferenceType> aMetadataReferences = aRefCollection.getServiceMetadataReference ();

    for (final IDocumentTypeIdentifier aDocTypeID : aServiceInfoMgr.getAllSMPDocumentTypesOfServiceGroup (aServiceGroup))
    {
      final ServiceMetadataReferenceType aMetadataReference = new ServiceMetadataReferenceType ();
      aMetadataReference.setHref (m_aAPIProvider.getServiceMetadataReferenceHref (aServiceGroupID, aDocTypeID));
      aMetadataReferences.add (aMetadataReference);
    }

    final ServiceGroupType aSG = aServiceGroup.getAsJAXBObject ();
    aSG.setServiceMetadataReferenceCollection (aRefCollection);

    final CompleteServiceGroupType aCompleteServiceGroup = new CompleteServiceGroupType ();
    aCompleteServiceGroup.setServiceGroup (aSG);

    for (final ISMPServiceInformation aService : aServiceInfoMgr.getAllSMPServiceInformationsOfServiceGroup (aServiceGroup))
      aCompleteServiceGroup.addServiceMetadata (aService.getAsJAXBObject ());

    s_aLogger.info ("Finished getCompleteServiceGroup(" + sServiceGroupID + ")");
    s_aStatsCounterSuccess.increment ("getCompleteServiceGroup");
    return aCompleteServiceGroup;
  }

  @Nonnull
  public ServiceGroupReferenceListType getServiceGroupReferenceList (@Nonnull final String sUserID,
                                                                     @Nonnull final BasicAuthClientCredentials aCredentials) throws Throwable
  {
    s_aLogger.info ("getServiceGroupReferenceList - GET /list/" + sUserID);
    s_aStatsCounterInvocation.increment ("getServiceGroupReferenceList");

    if (!aCredentials.getUserName ().equals (sUserID))
    {
      throw new SMPUnauthorizedException ("URL user name '" +
                                          sUserID +
                                          "' does not match HTTP Basic Auth user name '" +
                                          aCredentials.getUserName () +
                                          "'");
    }

    final ISMPUserManager aUserMgr = SMPMetaManager.getUserMgr ();
    final ISMPUser aSMPUser = aUserMgr.validateUserCredentials (aCredentials);
    final Collection <? extends ISMPServiceGroup> aServiceGroups = SMPMetaManager.getServiceGroupMgr ()
                                                                                 .getAllSMPServiceGroupsOfOwner (aSMPUser.getID ());

    final ServiceGroupReferenceListType aRefList = new ServiceGroupReferenceListType ();
    final List <ServiceGroupReferenceType> aReferenceTypes = aRefList.getServiceGroupReference ();
    for (final ISMPServiceGroup aServiceGroup : aServiceGroups)
    {
      final String sHref = m_aAPIProvider.getServiceGroupHref (aServiceGroup.getParticpantIdentifier ());

      final ServiceGroupReferenceType aServGroupRefType = new ServiceGroupReferenceType ();
      aServGroupRefType.setHref (sHref);
      aReferenceTypes.add (aServGroupRefType);
    }

    s_aLogger.info ("Finished getServiceGroupReferenceList(" + sUserID + ")");
    s_aStatsCounterSuccess.increment ("getServiceGroupReferenceList");
    return aRefList;
  }

  @Nonnull
  public ServiceGroupType getServiceGroup (final String sServiceGroupID) throws Throwable
  {
    s_aLogger.info ("getServiceGroup - GET /" + sServiceGroupID);
    s_aStatsCounterInvocation.increment ("getServiceGroup");

    final SimpleParticipantIdentifier aServiceGroupID = SimpleParticipantIdentifier.createFromURIPartOrNull (sServiceGroupID);
    if (aServiceGroupID == null)
    {
      // Invalid identifier
      throw new SMPNotFoundException ("Failed to parse serviceGroup '" +
                                      sServiceGroupID +
                                      "'",
                                      m_aAPIProvider.getCurrentURI ());
    }

    final ISMPServiceGroupManager aServiceGroupMgr = SMPMetaManager.getServiceGroupMgr ();
    final ISMPServiceInformationManager aServiceInfoMgr = SMPMetaManager.getServiceInformationMgr ();

    // Retrieve the service group
    final ISMPServiceGroup aServiceGroup = aServiceGroupMgr.getSMPServiceGroupOfID (aServiceGroupID);
    if (aServiceGroup == null)
    {
      // No such service group
      throw new SMPNotFoundException ("Unknown serviceGroup '" +
                                      sServiceGroupID +
                                      "'",
                                      m_aAPIProvider.getCurrentURI ());
    }

    // Then add the service metadata references
    final ServiceGroupType aSG = aServiceGroup.getAsJAXBObject ();
    final ServiceMetadataReferenceCollectionType aCollectionType = new ServiceMetadataReferenceCollectionType ();
    final List <ServiceMetadataReferenceType> aMetadataReferences = aCollectionType.getServiceMetadataReference ();
    for (final IDocumentTypeIdentifier aDocTypeID : aServiceInfoMgr.getAllSMPDocumentTypesOfServiceGroup (aServiceGroup))
    {
      final ServiceMetadataReferenceType aMetadataReference = new ServiceMetadataReferenceType ();
      aMetadataReference.setHref (m_aAPIProvider.getServiceMetadataReferenceHref (aServiceGroupID, aDocTypeID));
      aMetadataReferences.add (aMetadataReference);
    }
    aSG.setServiceMetadataReferenceCollection (aCollectionType);

    s_aLogger.info ("Finished getServiceGroup(" + sServiceGroupID + ")");
    s_aStatsCounterSuccess.increment ("getServiceGroup");
    return aSG;
  }

  @Nonnull
  public ESuccess saveServiceGroup (@Nonnull final String sServiceGroupID,
                                    @Nonnull final ServiceGroupType aServiceGroup,
                                    @Nonnull final BasicAuthClientCredentials aCredentials) throws Throwable
  {
    s_aLogger.info ("saveServiceGroup - PUT /" + sServiceGroupID + " ==> " + aServiceGroup);
    s_aStatsCounterInvocation.increment ("saveServiceGroup");

    final ParticipantIdentifierType aServiceGroupID = SimpleParticipantIdentifier.createFromURIPartOrNull (sServiceGroupID);
    if (aServiceGroupID == null)
    {
      // Invalid identifier
      throw new SMPNotFoundException ("Failed to parse serviceGroup '" +
                                      sServiceGroupID +
                                      "'",
                                      m_aAPIProvider.getCurrentURI ());
    }

    if (!IdentifierHelper.areParticipantIdentifiersEqual (aServiceGroupID, aServiceGroup.getParticipantIdentifier ()))
    {
      // Business identifiers must be equal
      throw new SMPNotFoundException ("ServiceGroup inconsistency", m_aAPIProvider.getCurrentURI ());
    }

    final ISMPUserManager aUserMgr = SMPMetaManager.getUserMgr ();
    final ISMPUser aSMPUser = aUserMgr.validateUserCredentials (aCredentials);

    final ISMPServiceGroupManager aServiceGroupMgr = SMPMetaManager.getServiceGroupMgr ();
    final String sExtension = SMPExtensionConverter.convertToString (aServiceGroup.getExtension ());
    if (aServiceGroupMgr.containsSMPServiceGroupWithID (aServiceGroupID))
      aServiceGroupMgr.updateSMPServiceGroup (sServiceGroupID, aSMPUser.getID (), sExtension);
    else
      aServiceGroupMgr.createSMPServiceGroup (aSMPUser.getID (), aServiceGroupID, sExtension);

    s_aLogger.info ("Finished saveServiceGroup(" + sServiceGroupID + "," + aServiceGroup + ")");
    s_aStatsCounterSuccess.increment ("saveServiceGroup");
    return ESuccess.SUCCESS;
  }

  @Nonnull
  public ESuccess deleteServiceGroup (@Nonnull final String sServiceGroupID,
                                      @Nonnull final BasicAuthClientCredentials aCredentials) throws Throwable
  {
    s_aLogger.info ("deleteServiceGroup - DELETE /" + sServiceGroupID);
    s_aStatsCounterInvocation.increment ("deleteServiceGroup");

    final ParticipantIdentifierType aServiceGroupID = SimpleParticipantIdentifier.createFromURIPartOrNull (sServiceGroupID);
    if (aServiceGroupID == null)
    {
      // Invalid identifier
      s_aLogger.info ("Failed to parse participant identifier '" + sServiceGroupID + "'");
      return ESuccess.FAILURE;
    }

    final ISMPUserManager aUserMgr = SMPMetaManager.getUserMgr ();
    final ISMPUser aSMPUser = aUserMgr.validateUserCredentials (aCredentials);
    aUserMgr.verifyOwnership (aServiceGroupID, aSMPUser);

    final ISMPServiceGroupManager aServiceGroupMgr = SMPMetaManager.getServiceGroupMgr ();
    aServiceGroupMgr.deleteSMPServiceGroup (aServiceGroupID);

    s_aLogger.info ("Finished deleteServiceGroup(" + sServiceGroupID + ")");
    s_aStatsCounterSuccess.increment ("deleteServiceGroup");
    return ESuccess.SUCCESS;
  }

  @Nonnull
  public SignedServiceMetadataType getServiceRegistration (@Nonnull final String sServiceGroupID,
                                                           @Nonnull final String sDocumentTypeID) throws Throwable
  {
    s_aLogger.info ("getServiceRegistration - GET /" + sServiceGroupID + "/services/" + sDocumentTypeID);
    s_aStatsCounterInvocation.increment ("getServiceRegistration");

    final ParticipantIdentifierType aServiceGroupID = SimpleParticipantIdentifier.createFromURIPartOrNull (sServiceGroupID);
    if (aServiceGroupID == null)
    {
      throw new SMPNotFoundException ("Failed to parse serviceGroup '" +
                                      sServiceGroupID +
                                      "'",
                                      m_aAPIProvider.getCurrentURI ());
    }

    final ISMPServiceGroup aServiceGroup = SMPMetaManager.getServiceGroupMgr ()
                                                         .getSMPServiceGroupOfID (aServiceGroupID);
    if (aServiceGroup == null)
    {
      throw new SMPNotFoundException ("No such serviceGroup '" +
                                      sServiceGroupID +
                                      "'",
                                      m_aAPIProvider.getCurrentURI ());
    }

    final DocumentIdentifierType aDocTypeID = IdentifierHelper.createDocumentTypeIdentifierFromURIPartOrNull (sDocumentTypeID);
    if (aDocTypeID == null)
    {
      throw new SMPNotFoundException ("Failed to parse documentTypeID '" +
                                      sServiceGroupID +
                                      "'",
                                      m_aAPIProvider.getCurrentURI ());
    }

    // First check for redirection, then for actual service
    final ISMPRedirectManager aRedirectMgr = SMPMetaManager.getRedirectMgr ();
    final ISMPRedirect aRedirect = aRedirectMgr.getSMPRedirectOfServiceGroupAndDocumentType (aServiceGroup, aDocTypeID);

    final SignedServiceMetadataType aSignedServiceMetadata = new SignedServiceMetadataType ();
    if (aRedirect != null)
    {
      aSignedServiceMetadata.setServiceMetadata (aRedirect.getAsJAXBObject ());
    }
    else
    {
      // Get as regular service information
      final ISMPServiceInformationManager aServiceInfoMgr = SMPMetaManager.getServiceInformationMgr ();
      final ISMPServiceInformation aServiceInfo = aServiceInfoMgr.getSMPServiceInformationOfServiceGroupAndDocumentType (aServiceGroup,
                                                                                                                         aDocTypeID);
      if (aServiceInfo != null)
      {
        aSignedServiceMetadata.setServiceMetadata (aServiceInfo.getAsJAXBObject ());
      }
      else
      {
        // Neither nor is present
        throw new SMPNotFoundException ("service(" +
                                        sServiceGroupID +
                                        "," +
                                        sDocumentTypeID +
                                        ")",
                                        m_aAPIProvider.getCurrentURI ());
      }
    }

    // Signature must be added by the rest service

    s_aLogger.info ("Finished getServiceRegistration(" + sServiceGroupID + "," + sDocumentTypeID + ")");
    s_aStatsCounterSuccess.increment ("getServiceRegistration");
    return aSignedServiceMetadata;
  }

  @Nonnull
  public ESuccess saveServiceRegistration (@Nonnull final String sServiceGroupID,
                                           @Nonnull final String sDocumentTypeID,
                                           @Nonnull final ServiceMetadataType aServiceMetadata,
                                           @Nonnull final BasicAuthClientCredentials aCredentials) throws Throwable
  {
    s_aLogger.info ("saveServiceRegistration - PUT /" +
                    sServiceGroupID +
                    "/services/" +
                    sDocumentTypeID +
                    " ==> " +
                    aServiceMetadata);
    s_aStatsCounterInvocation.increment ("saveServiceRegistration");

    final SimpleParticipantIdentifier aServiceGroupID = SimpleParticipantIdentifier.createFromURIPartOrNull (sServiceGroupID);
    if (aServiceGroupID == null)
    {
      // Invalid identifier
      s_aLogger.info ("Failed to parse participant identifier '" + sServiceGroupID + "'");
      return ESuccess.FAILURE;
    }

    final SimpleDocumentTypeIdentifier aDocTypeID = SimpleDocumentTypeIdentifier.createFromURIPartOrNull (sDocumentTypeID);
    if (aDocTypeID == null)
    {
      // Invalid identifier
      s_aLogger.info ("Failed to parse document type identifier '" + sDocumentTypeID + "'");
      return ESuccess.FAILURE;
    }

    // May be null for a Redirect!
    final ServiceInformationType aServiceInformation = aServiceMetadata.getServiceInformation ();
    if (aServiceInformation != null)
    {
      // Business identifiers from path (ServiceGroupID) and from service
      // metadata (body) must equal path
      if (!IdentifierHelper.areParticipantIdentifiersEqual (aServiceInformation.getParticipantIdentifier (),
                                                            aServiceGroupID))
      {
        s_aLogger.info ("Save service metadata was called with bad parameters. serviceInfo:" +
                        IdentifierHelper.getIdentifierURIEncoded (aServiceInformation.getParticipantIdentifier ()) +
                        " param:" +
                        aServiceGroupID);
        return ESuccess.FAILURE;
      }

      if (!IdentifierHelper.areDocumentTypeIdentifiersEqual (aServiceInformation.getDocumentIdentifier (), aDocTypeID))
      {
        s_aLogger.info ("Save service metadata was called with bad parameters. serviceInfo:" +
                        IdentifierHelper.getIdentifierURIEncoded (aServiceInformation.getDocumentIdentifier ()) +
                        " param:" +
                        aDocTypeID);
        // Document type must equal path
        return ESuccess.FAILURE;
      }
    }

    // Main save
    final ISMPUserManager aUserMgr = SMPMetaManager.getUserMgr ();
    final ISMPUser aDataUser = aUserMgr.validateUserCredentials (aCredentials);
    aUserMgr.verifyOwnership (aServiceGroupID, aDataUser);

    final ISMPServiceGroupManager aServiceGroupMgr = SMPMetaManager.getServiceGroupMgr ();
    final ISMPServiceGroup aServiceGroup = aServiceGroupMgr.getSMPServiceGroupOfID (aServiceGroupID);
    if (aServiceGroup == null)
    {
      // Service group not found
      s_aLogger.info ("ServiceGroup not found: " + sServiceGroupID);
      return ESuccess.FAILURE;
    }

    if (aServiceMetadata.getRedirect () != null)
    {
      final ISMPRedirectManager aRedirectMgr = SMPMetaManager.getRedirectMgr ();
      aRedirectMgr.createOrUpdateSMPRedirect (aServiceGroup,
                                              aDocTypeID,
                                              aServiceMetadata.getRedirect ().getHref (),
                                              aServiceMetadata.getRedirect ().getCertificateUID (),
                                              SMPExtensionConverter.convertToString (aServiceMetadata.getRedirect ()
                                                                                                     .getExtension ()));
    }
    else
    {
      final ProcessListType aJAXBProcesses = aServiceMetadata.getServiceInformation ().getProcessList ();
      final List <SMPProcess> aProcesses = new ArrayList <> ();
      for (final ProcessType aJAXBProcess : aJAXBProcesses.getProcess ())
      {
        final List <SMPEndpoint> aEndpoints = new ArrayList <> ();
        for (final EndpointType aJAXBEndpoint : aJAXBProcess.getServiceEndpointList ().getEndpoint ())
        {
          final SMPEndpoint aEndpoint = new SMPEndpoint (aJAXBEndpoint.getTransportProfile (),
                                                         W3CEndpointReferenceHelper.getAddress (aJAXBEndpoint.getEndpointReference ()),
                                                         aJAXBEndpoint.isRequireBusinessLevelSignature (),
                                                         aJAXBEndpoint.getMinimumAuthenticationLevel (),
                                                         aJAXBEndpoint.getServiceActivationDate (),
                                                         aJAXBEndpoint.getServiceExpirationDate (),
                                                         aJAXBEndpoint.getCertificate (),
                                                         aJAXBEndpoint.getServiceDescription (),
                                                         aJAXBEndpoint.getTechnicalContactUrl (),
                                                         aJAXBEndpoint.getTechnicalInformationUrl (),
                                                         SMPExtensionConverter.convertToString (aJAXBEndpoint.getExtension ()));
          aEndpoints.add (aEndpoint);
        }
        final SMPProcess aProcess = new SMPProcess (aJAXBProcess.getProcessIdentifier (),
                                                    aEndpoints,
                                                    SMPExtensionConverter.convertToString (aJAXBProcess.getExtension ()));
        aProcesses.add (aProcess);
      }

      final ISMPServiceInformationManager aServiceInfoMgr = SMPMetaManager.getServiceInformationMgr ();
      aServiceInfoMgr.mergeSMPServiceInformation (new SMPServiceInformation (aServiceGroup,
                                                                             aDocTypeID,
                                                                             aProcesses,
                                                                             SMPExtensionConverter.convertToString (aServiceMetadata.getServiceInformation ()
                                                                                                                                    .getExtension ())));
    }

    s_aLogger.info ("Finished saveServiceRegistration(" +
                    sServiceGroupID +
                    "," +
                    sDocumentTypeID +
                    "," +
                    aServiceMetadata +
                    ")");
    s_aStatsCounterSuccess.increment ("saveServiceRegistration");
    return ESuccess.SUCCESS;
  }

  @Nonnull
  public ESuccess deleteServiceRegistration (@Nonnull final String sServiceGroupID,
                                             @Nonnull final String sDocumentTypeID,
                                             @Nonnull final BasicAuthClientCredentials aCredentials) throws Throwable
  {
    s_aLogger.info ("deleteServiceRegistration - DELETE /" + sServiceGroupID + "/services/" + sDocumentTypeID);
    s_aStatsCounterInvocation.increment ("deleteServiceRegistration");

    final SimpleParticipantIdentifier aServiceGroupID = SimpleParticipantIdentifier.createFromURIPartOrNull (sServiceGroupID);
    if (aServiceGroupID == null)
    {
      // Invalid identifier
      s_aLogger.info ("Failed to parse participant identifier '" + sServiceGroupID + "'");
      return ESuccess.FAILURE;
    }

    final SimpleDocumentTypeIdentifier aDocTypeID = SimpleDocumentTypeIdentifier.createFromURIPartOrNull (sDocumentTypeID);
    if (aDocTypeID == null)
    {
      // Invalid identifier
      s_aLogger.info ("Failed to parse document type identifier '" + sDocumentTypeID + "'");
      return ESuccess.FAILURE;
    }

    final ISMPUserManager aUserMgr = SMPMetaManager.getUserMgr ();
    final ISMPUser aSMPUser = aUserMgr.validateUserCredentials (aCredentials);
    aUserMgr.verifyOwnership (aServiceGroupID, aSMPUser);

    final ISMPServiceGroupManager aServiceGroupMgr = SMPMetaManager.getServiceGroupMgr ();
    final ISMPServiceGroup aServiceGroup = aServiceGroupMgr.getSMPServiceGroupOfID (aServiceGroupID);
    if (aServiceGroup == null)
    {
      s_aLogger.info ("Service group '" + sServiceGroupID + "' not on this SMP");
      return ESuccess.FAILURE;
    }

    final ISMPServiceInformationManager aServiceInfoMgr = SMPMetaManager.getServiceInformationMgr ();
    final ISMPServiceInformation aServiceInfo = aServiceInfoMgr.getSMPServiceInformationOfServiceGroupAndDocumentType (aServiceGroup,
                                                                                                                       aDocTypeID);
    if (aServiceInfo == null)
    {
      s_aLogger.info ("Service group '" +
                      sServiceGroupID +
                      "' has no document type '" +
                      sDocumentTypeID +
                      "' on this SMP!");
      return ESuccess.FAILURE;
    }
    aServiceInfoMgr.deleteSMPServiceInformation (aServiceInfo);

    s_aLogger.info ("Finished deleteServiceRegistration(" + sServiceGroupID + "," + sDocumentTypeID + ")");
    s_aStatsCounterSuccess.increment ("deleteServiceRegistration");
    return ESuccess.SUCCESS;
  }

  /**
   * @return The statistics data with the invocation counter.
   */
  @Nonnull
  public static IStatisticsHandlerKeyedCounter getInvocationCounter ()
  {
    return s_aStatsCounterInvocation;
  }

  /**
   * @return The statistics data with the successful invocation counter.
   */
  @Nonnull
  public static IStatisticsHandlerKeyedCounter getSuccessCounter ()
  {
    return s_aStatsCounterSuccess;
  }
}