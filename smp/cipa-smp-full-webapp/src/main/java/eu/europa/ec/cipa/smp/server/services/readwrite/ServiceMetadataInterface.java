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
import eu.europa.ec.cipa.smp.server.conversion.ServiceMetadataConverter;
import eu.europa.ec.cipa.smp.server.data.DataManagerFactory;
import eu.europa.ec.cipa.smp.server.data.IDataManager;
import eu.europa.ec.cipa.smp.server.errors.exceptions.BadRequestException;
import eu.europa.ec.cipa.smp.server.services.BaseServiceMetadataInterfaceImpl;
import eu.europa.ec.cipa.smp.server.util.IdentifierUtils;
import eu.europa.ec.cipa.smp.server.util.RequestHelper;
import eu.europa.ec.smp.api.Identifiers;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.EndpointType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ProcessListType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ProcessType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceEndpointList;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceInformationType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Calendar;

import static eu.europa.ec.cipa.smp.server.errors.ErrorBusinessCode.OUT_OF_RANGE;
import static eu.europa.ec.cipa.smp.server.errors.ErrorBusinessCode.WRONG_FIELD;
import static eu.europa.ec.cipa.smp.server.errors.ErrorBusinessCode.XSD_INVALID;

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

    final ServiceMetadata aServiceMetadata = ServiceMetadataConverter.unmarshal(body);

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

    final ServiceMetadata aServiceMetadata = ServiceMetadataConverter.unmarshal(body);
    final ParticipantIdentifierType aServiceGroupID = Identifiers.asParticipantId(sServiceGroupID);
    if (aServiceGroupID == null) {
      // Invalid identifier
      throw new BadRequestException(XSD_INVALID, "Failed to parse participant identifier '" + sServiceGroupID + "'");
    }

    final DocumentIdentifier aDocTypeID =  Identifiers.asDocumentId(sDocumentTypeID);
    if (aDocTypeID == null) {
      // Invalid identifier
      throw new BadRequestException(XSD_INVALID, "Failed to parse participant identifier '" + sServiceGroupID + "'");
    }

    final ServiceInformationType aServiceInformationType = aServiceMetadata.getServiceInformation();

      // Business identifiers from path (ServiceGroupID) and from service
      // metadata (body) must equal path
      if (!IdentifierUtils.areIdentifiersEqual (aServiceInformationType.getParticipantIdentifier (), aServiceGroupID)) {
        s_aLogger.info ("Save service metadata was called with bad parameters. serviceInfo:" +
                        IdentifierUtils.getIdentifierURIEncoded (aServiceInformationType.getParticipantIdentifier ()) +
                        " param:" +
                        aServiceGroupID);
        throw new BadRequestException(WRONG_FIELD, "Save service metadata was called with bad parameters. serviceInfo:" +
                                                      IdentifierUtils.getIdentifierURIEncoded (aServiceInformationType.getParticipantIdentifier ()) +
                                                      " param:" +
                                                      aServiceGroupID.getValue());

      }

      if (!IdentifierUtils.areIdentifiersEqual (aServiceInformationType.getDocumentIdentifier (), aDocTypeID)) {
        s_aLogger.info ("Save service metadata was called with bad parameters. serviceInfo:" +
                        IdentifierUtils.getIdentifierURIEncoded (aServiceInformationType.getDocumentIdentifier ()) +
                        " param:" +
                        aDocTypeID);
        // Document type must equal path
        throw new BadRequestException(WRONG_FIELD, "Save service metadata was called with bad parameters. serviceInfo:" +
                                                      IdentifierUtils.getIdentifierURIEncoded (aServiceInformationType.getDocumentIdentifier ()) +
                                                      " param:" +
                                                      aDocTypeID.getValue());
      }

      validateData(aServiceMetadata);
  }

  private void validateData(ServiceMetadata aServiceMetadata) throws Exception {
    if(aServiceMetadata.getServiceInformation() == null) {
      return;
    }
    ProcessListType processList = aServiceMetadata.getServiceInformation().getProcessList();
    if (processList == null) {
      return;
    }

    ProcessType process;
    for(int i = 0; i < processList.getProcesses().size(); i++) {
      process = processList.getProcesses().get(i);
      if(process == null) {
        return;
      }
      ServiceEndpointList serviceEndpointList = process.getServiceEndpointList();
      if(serviceEndpointList == null) {
        return;
      }

      EndpointType endpoint;
      for(int j = 0; j < serviceEndpointList.getEndpoints().size(); j++) {
        endpoint = serviceEndpointList.getEndpoints().get(j);

        if(endpoint == null) {
          return;
        }

        Calendar activationDate = endpoint.getServiceActivationDate();
        Calendar expirationDate = endpoint.getServiceExpirationDate();

        if(activationDate != null && expirationDate != null && activationDate.after(expirationDate)) {
          throw new BadRequestException(OUT_OF_RANGE, "Expiration date is before Activation date");
        }
      }
    }
  }

  @DELETE
  public Response deleteServiceRegistration (@PathParam ("ServiceGroupId") final String sServiceGroupID,
                                             @PathParam ("DocumentTypeId") final String sDocumentTypeID) throws Throwable {
    s_aLogger.info ("DELETE /" + sServiceGroupID + "/services/" + sDocumentTypeID);

    final ParticipantIdentifierType aServiceGroupID = Identifiers.asParticipantId(sServiceGroupID);
    if (aServiceGroupID == null) {
      // Invalid identifier
      throw new BadRequestException(XSD_INVALID, "Failed to parse participant identifier '" + sServiceGroupID + "'");
    }

    final DocumentIdentifier aDocTypeID = Identifiers.asDocumentId(sDocumentTypeID);
    if (aDocTypeID == null) {
      // Invalid identifier
      throw new BadRequestException(XSD_INVALID, "Failed to parse document type identifier '" + sDocumentTypeID + "'");
    }

    final IDataManager aDataManager = DataManagerFactory.getInstance ();
    aDataManager.deleteService (aServiceGroupID, aDocTypeID, RequestHelper.getAuth (headers));

    s_aLogger.info ("Finished deleteServiceRegistration(" + sServiceGroupID + "," + sDocumentTypeID);

    return Response.ok ().build ();
  }
}
