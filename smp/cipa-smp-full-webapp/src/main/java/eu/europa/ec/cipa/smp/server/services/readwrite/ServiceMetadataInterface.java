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
package eu.europa.ec.cipa.smp.server.services.readwrite;

import com.sun.jersey.spi.MessageBodyWorkers;
import eu.europa.ec.cipa.peppol.identifier.IdentifierUtils;
import eu.europa.ec.cipa.peppol.identifier.doctype.SimpleDocumentTypeIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.smp.server.conversion.ServiceMetadataConverter;
import eu.europa.ec.cipa.smp.server.data.DataManagerFactory;
import eu.europa.ec.cipa.smp.server.data.IDataManager;
import eu.europa.ec.cipa.smp.server.exception.BadRequestException;
import eu.europa.ec.cipa.smp.server.exception.ErrorResponse;
import eu.europa.ec.cipa.smp.server.services.BaseServiceMetadataInterfaceImpl;
import eu.europa.ec.cipa.smp.server.util.RequestHelper;
import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.servicemetadata.publishing._1.ProcessListType;
import org.busdox.servicemetadata.publishing._1.ProcessType;
import org.busdox.servicemetadata.publishing._1.ServiceEndpointList;
import org.busdox.servicemetadata.publishing._1.ServiceInformationType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

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

    s_aLogger.info ("PUT /" + sServiceGroupID + "/services/" + sDocumentTypeID + " ==> " + body);

    validateErrors(sServiceGroupID, sDocumentTypeID, body);

    final ServiceMetadataType aServiceMetadata = ServiceMetadataConverter.unmarshal(body);

    // Main save
    final IDataManager aDataManager = DataManagerFactory.getInstance ();
    aDataManager.saveService (aServiceMetadata, body, RequestHelper.getAuth (headers));

    s_aLogger.info ("Finished saveServiceRegistration(" +
                      sServiceGroupID +
                      "," +
                      sDocumentTypeID +
                      "," +
                      aServiceMetadata +
                      ")");

    return Response.ok ().build ();
  }

  private void validateErrors(final String sServiceGroupID,
                              final String sDocumentTypeID,
                              final String body) throws Exception {

    final ServiceMetadataType aServiceMetadata = ServiceMetadataConverter.unmarshal(body);

    final SimpleParticipantIdentifier aServiceGroupID = SimpleParticipantIdentifier.createFromURIPartOrNull(sServiceGroupID);
    if (aServiceGroupID == null) {
      // Invalid identifier
      s_aLogger.info ("Failed to parse participant identifier '" + sServiceGroupID + "'");
      throw new BadRequestException(ErrorResponse.BusinessCode.XSD_INVALID, "Failed to parse participant identifier '" + sServiceGroupID + "'");
    }

    final SimpleDocumentTypeIdentifier aDocTypeID = SimpleDocumentTypeIdentifier.createFromURIPartOrNull(sDocumentTypeID);
    if (aDocTypeID == null) {
      // Invalid identifier
      s_aLogger.info("Failed to parse document type identifier '" + sDocumentTypeID + "'");
      throw new BadRequestException(ErrorResponse.BusinessCode.XSD_INVALID, "Failed to parse participant identifier '" + sServiceGroupID + "'");
    }

    final ServiceInformationType aServiceInformationType = aServiceMetadata.getServiceInformation();

      // Business identifiers from path (ServiceGroupID) and from service
      // metadata (body) must equal path
      if (!IdentifierUtils.areIdentifiersEqual (aServiceInformationType.getParticipantIdentifier (), aServiceGroupID)) {
        s_aLogger.info ("Save service metadata was called with bad parameters. serviceInfo:" +
                        IdentifierUtils.getIdentifierURIEncoded (aServiceInformationType.getParticipantIdentifier ()) +
                        " param:" +
                        aServiceGroupID);
        throw new BadRequestException(ErrorResponse.BusinessCode.WRONG_FIELD, "Save service metadata was called with bad parameters. serviceInfo:" +
                                                      IdentifierUtils.getIdentifierURIEncoded (aServiceInformationType.getParticipantIdentifier ()) +
                                                      " param:" +
                                                      aServiceGroupID);

      }

      if (!IdentifierUtils.areIdentifiersEqual (aServiceInformationType.getDocumentIdentifier (), aDocTypeID)) {
        s_aLogger.info ("Save service metadata was called with bad parameters. serviceInfo:" +
                        IdentifierUtils.getIdentifierURIEncoded (aServiceInformationType.getDocumentIdentifier ()) +
                        " param:" +
                        aDocTypeID);
        // Document type must equal path
        throw new BadRequestException(ErrorResponse.BusinessCode.WRONG_FIELD, "Save service metadata was called with bad parameters. serviceInfo:" +
                                                      IdentifierUtils.getIdentifierURIEncoded (aServiceInformationType.getDocumentIdentifier ()) +
                                                      " param:" +
                                                      aDocTypeID);
      }

      validateData(aServiceMetadata);
  }

  private void validateData(ServiceMetadataType aServiceMetadata) throws Exception {
    ProcessListType processList;
    if(aServiceMetadata.getServiceInformation() == null) {
      return;
    }
    processList = aServiceMetadata.getServiceInformation().getProcessList();
    if (processList == null) {
      return;
    }

    ProcessType process;
    for(int i = 0; i < processList.getProcessCount(); i++) {
      process = processList.getProcessAtIndex(i);
      if(process == null) {
        return;
      }
      ServiceEndpointList serviceEndpointList = process.getServiceEndpointList();
      if(serviceEndpointList == null) {
        return;
      }

      EndpointType endpoint;
      for(int j = 0; j < serviceEndpointList.getEndpointCount(); j++) {
        endpoint = serviceEndpointList.getEndpointAtIndex(j);

        if(endpoint == null) {
          return;
        }

        if(endpoint.getEndpointReference() == null) {
          throw new BadRequestException(ErrorResponse.BusinessCode.MISSING_FIELD, "Endpoint reference is missing");
        }

        Date activationDate = endpoint.getServiceActivationDate();
        if(activationDate == null) {
          throw new BadRequestException(ErrorResponse.BusinessCode.MISSING_FIELD, "Endpoint Activation date is missing");
        }

        Date expirationDate = endpoint.getServiceExpirationDate();
        if(expirationDate == null) {
          throw new BadRequestException(ErrorResponse.BusinessCode.MISSING_FIELD, "Endpoint Expiration date is missing");
        }

        if(activationDate.after(expirationDate)) {
          throw new BadRequestException(ErrorResponse.BusinessCode.OUT_OF_RANGE, "Expiration date is before Activation date");
        }
      }
    }
  }

  @DELETE
  public Response deleteServiceRegistration (@PathParam ("ServiceGroupId") final String sServiceGroupID,
                                             @PathParam ("DocumentTypeId") final String sDocumentTypeID) throws Throwable {
    s_aLogger.info ("DELETE /" + sServiceGroupID + "/services/" + sDocumentTypeID);

    final SimpleParticipantIdentifier aServiceGroupID = SimpleParticipantIdentifier.createFromURIPartOrNull (sServiceGroupID);
    if (aServiceGroupID == null) {
      // Invalid identifier
      s_aLogger.info ("Failed to parse participant identifier '" + sServiceGroupID + "'");
      throw new BadRequestException(ErrorResponse.BusinessCode.XSD_INVALID, "Failed to parse participant identifier '" + sServiceGroupID + "'");
    }

    final SimpleDocumentTypeIdentifier aDocTypeID = SimpleDocumentTypeIdentifier.createFromURIPartOrNull (sDocumentTypeID);
    if (aDocTypeID == null) {
      // Invalid identifier
      s_aLogger.info ("Failed to parse document type identifier '" + sDocumentTypeID + "'");
      throw new BadRequestException(ErrorResponse.BusinessCode.XSD_INVALID, "Failed to parse document type identifier '" + sDocumentTypeID + "'");
    }

    final IDataManager aDataManager = DataManagerFactory.getInstance ();
    aDataManager.deleteService (aServiceGroupID, aDocTypeID, RequestHelper.getAuth (headers));

    s_aLogger.info ("Finished deleteServiceRegistration(" + sServiceGroupID + "," + sDocumentTypeID);

    return Response.ok ().build ();
  }
}
