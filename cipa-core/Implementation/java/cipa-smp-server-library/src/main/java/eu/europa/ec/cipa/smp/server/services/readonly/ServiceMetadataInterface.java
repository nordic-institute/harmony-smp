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
package eu.europa.ec.cipa.smp.server.services.readonly;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.busdox.servicemetadata.publishing._1.ObjectFactory;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.busdox.servicemetadata.publishing._1.SignedServiceMetadataType;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.NotFoundException;

import eu.europa.ec.cipa.peppol.identifier.IdentifierUtils;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.smp.server.data.DataManagerFactory;
import eu.europa.ec.cipa.smp.server.data.IDataManager;

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

  public ServiceMetadataInterface () {}

  @GET
  //changed Produced media type to match the smp specification.
  @Produces (MediaType.TEXT_XML)
  public JAXBElement <SignedServiceMetadataType> getServiceRegistration (@PathParam ("ServiceGroupId") final String sServiceGroupID,
                                                                         @PathParam ("DocumentTypeId") final String sDocumentTypeID) {
    s_aLogger.info ("GET /" + sServiceGroupID + "/services/" + sDocumentTypeID);

    try {
      final ObjectFactory aObjFactory = new ObjectFactory ();
      final ParticipantIdentifierType aServiceGroupID = SimpleParticipantIdentifier.createFromURIPart (sServiceGroupID);
      final DocumentIdentifierType aDocTypeID = IdentifierUtils.createDocumentTypeIdentifierFromURIPart (sDocumentTypeID);
      final IDataManager aDataManager = DataManagerFactory.getInstance ();

      // First check for redirection, then for actual service
      ServiceMetadataType aService = aDataManager.getRedirection (aServiceGroupID, aDocTypeID);
      if (aService == null) {
        aService = aDataManager.getService (aServiceGroupID, aDocTypeID);
        if (aService == null)
          throw new NotFoundException ("service", uriInfo.getAbsolutePath ());
      }

      final SignedServiceMetadataType aSignedServiceMetadata = aObjFactory.createSignedServiceMetadataType ();
      aSignedServiceMetadata.setServiceMetadata (aService);
      // Signature is added by a handler

      s_aLogger.info ("Finished getServiceRegistration(" + sServiceGroupID + "," + sDocumentTypeID + ")");

      return aObjFactory.createSignedServiceMetadata (aSignedServiceMetadata);
    }
    catch (final RuntimeException ex) {
      s_aLogger.error ("Error in returning service metadata.", ex);
      throw ex;
    }
  }

}
