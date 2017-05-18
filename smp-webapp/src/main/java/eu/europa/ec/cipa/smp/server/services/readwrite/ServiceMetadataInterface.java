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
package eu.europa.ec.cipa.smp.server.services.readwrite;

import eu.europa.ec.cipa.smp.server.conversion.ServiceMetadataConverter;
import eu.europa.ec.cipa.smp.server.data.DataManagerFactory;
import eu.europa.ec.cipa.smp.server.data.IDataManager;
import eu.europa.ec.cipa.smp.server.errors.exceptions.BadRequestException;
import eu.europa.ec.cipa.smp.server.services.BaseServiceMetadataInterfaceImpl;
import eu.europa.ec.cipa.smp.server.util.IdentifierUtils;
import eu.europa.ec.cipa.smp.server.util.RequestHelper;
import eu.europa.ec.smp.api.Identifiers;
import eu.europa.ec.smp.api.validators.BdxSmpOasisValidator;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static eu.europa.ec.cipa.smp.server.errors.ErrorBusinessCode.OUT_OF_RANGE;
import static eu.europa.ec.cipa.smp.server.errors.ErrorBusinessCode.WRONG_FIELD;

/**
 * This class implements the REST interface for getting SignedServiceMetadata's.
 * PUT and DELETE are also implemented.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Path ("/{ServiceGroupId}/services/{DocumentTypeId}")
public final class ServiceMetadataInterface {
  private static final Logger s_aLogger = LoggerFactory.getLogger (ServiceMetadataInterface.class);

  @Context
  private HttpHeaders headers;
  @Context
  private UriInfo uriInfo;
  @Context
  private MessageBodyWorkers bodyWorkers;

  public ServiceMetadataInterface () {}

  @GET
  // changed Produced media type to match the smp specification.
  @Produces (MediaType.TEXT_XML)
  public Document getServiceRegistration (@PathParam ("ServiceGroupId") final String sServiceGroupID,
                                          @PathParam ("DocumentTypeId") final String sDocumentTypeID) throws Throwable {
    // Delegate to common implementation
    return BaseServiceMetadataInterfaceImpl.getServiceRegistration(uriInfo, sServiceGroupID, sDocumentTypeID);
  }

  @PUT
  public Response saveServiceRegistration (@PathParam ("ServiceGroupId") final String sServiceGroupID,
                                           @PathParam ("DocumentTypeId") final String sDocumentTypeID,
                                           final String body) throws Throwable {

    s_aLogger.info (String.format("PUT /%s/services/%s ==> %s", sServiceGroupID, sDocumentTypeID, body));

    BdxSmpOasisValidator.validateXSD(body);

    final ParticipantIdentifierType aServiceGroupID = Identifiers.asParticipantId(sServiceGroupID);
    final DocumentIdentifier aDocTypeID = Identifiers.asDocumentId(sDocumentTypeID);
    final ServiceMetadata aServiceMetadata = ServiceMetadataConverter.unmarshal(body);

    validateErrors(aServiceGroupID, aDocTypeID, aServiceMetadata);

    // Main save
    final IDataManager aDataManager = DataManagerFactory.getInstance ();
    boolean bServiceCreated = aDataManager.saveService (aServiceGroupID, aDocTypeID, body, RequestHelper.getAuth (headers, false));

    s_aLogger.info (String.format("Finished saveServiceRegistration(%s,%s,%s)", sServiceGroupID, sDocumentTypeID, aServiceMetadata));

    return bServiceCreated ? Response.created(uriInfo.getRequestUri()).build() : Response.ok ().build ();
  }

  private void validateErrors(final ParticipantIdentifierType aServiceGroupID,
                              final DocumentIdentifier aDocTypeID,
                              final ServiceMetadata aServiceMetadata) {

    final ServiceInformationType aServiceInformationType = aServiceMetadata.getServiceInformation();

    if (aServiceInformationType != null) {
      // Business identifiers from path (ServiceGroupID) and from service
      // metadata (body) must equal path
      if (!IdentifierUtils.areIdentifiersEqual(aServiceInformationType.getParticipantIdentifier(), aServiceGroupID)) {
        String errorMessage = String.format("Save service metadata was called with bad parameters. serviceInfo: %s param: %s",
                IdentifierUtils.getIdentifierURIEncoded(aServiceInformationType.getParticipantIdentifier()),
                aServiceGroupID);
        s_aLogger.info(errorMessage);
        throw new BadRequestException(WRONG_FIELD, errorMessage);
      }

      if (!IdentifierUtils.areIdentifiersEqual(aServiceInformationType.getDocumentIdentifier(), aDocTypeID)) {
        String errorMessage = String.format("Save service metadata was called with bad parameters. serviceInfo: %s param: %s",
                IdentifierUtils.getIdentifierURIEncoded(aServiceInformationType.getDocumentIdentifier()),
                aDocTypeID);
        s_aLogger.info(errorMessage);
        // Document type must equal path
        throw new BadRequestException(WRONG_FIELD, errorMessage);
      }
      validateServiceInformationData(aServiceInformationType);
    }
  }

  private void validateServiceInformationData(ServiceInformationType aServiceInformationType) {
    ProcessListType processList = aServiceInformationType.getProcessList();
    if (processList == null) {
      return;
    }

    for(ProcessType process : processList.getProcesses()) {
      ServiceEndpointList serviceEndpointList = process.getServiceEndpointList();
      if(serviceEndpointList == null) {
        return;
      }

      Set<String> transportProfiles = new HashSet<>();
      for(EndpointType endpoint : serviceEndpointList.getEndpoints()) {
        if(!transportProfiles.add(endpoint.getTransportProfile())) {
          throw new BadRequestException(WRONG_FIELD, "Transport Profile already exists for the same process");
        }

        Date activationDate = endpoint.getServiceActivationDate();
        Date expirationDate = endpoint.getServiceExpirationDate();

        if(activationDate != null && expirationDate != null && activationDate.after(expirationDate)) {
          throw new BadRequestException(OUT_OF_RANGE, "Expiration date is before Activation date");
        }

        if(expirationDate != null && expirationDate.before(new Date())) {
          throw new BadRequestException(OUT_OF_RANGE, "Expiration date has passed");
        }
      }
    }
  }

  @DELETE
  public Response deleteServiceRegistration (@PathParam ("ServiceGroupId") final String sServiceGroupID,
                                             @PathParam ("DocumentTypeId") final String sDocumentTypeID) throws Throwable {
    s_aLogger.info (String.format("DELETE /%s/services/%s", sServiceGroupID, sDocumentTypeID));

    final ParticipantIdentifierType aServiceGroupID = Identifiers.asParticipantId(sServiceGroupID);

    final DocumentIdentifier aDocTypeID = Identifiers.asDocumentId(sDocumentTypeID);

    final IDataManager aDataManager = DataManagerFactory.getInstance ();
    aDataManager.deleteService (aServiceGroupID, aDocTypeID, RequestHelper.getAuth (headers, false));

    s_aLogger.info (String.format("Finished deleteServiceRegistration(%s,%s)", sServiceGroupID, sDocumentTypeID));

    return Response.ok ().build ();
  }

  public void setHeaders(HttpHeaders headers) {
    this.headers = headers;
  }
}
