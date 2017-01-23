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
package eu.europa.ec.cipa.smp.server.services;

import eu.europa.ec.cipa.smp.server.conversion.ServiceMetadataConverter;
import eu.europa.ec.cipa.smp.server.data.DataManagerFactory;
import eu.europa.ec.cipa.smp.server.data.IDataManager;
import eu.europa.ec.cipa.smp.server.errors.exceptions.NotFoundException;
import eu.europa.ec.smp.api.Identifiers;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.UriInfo;

/**
 * This class implements the read-only methods for the REST
 * SignedServiceMetadata interface. It is used in the read-only interface and in
 * the writable interface.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class BaseServiceMetadataInterfaceImpl {
  private static final Logger s_aLogger = LoggerFactory.getLogger (BaseServiceMetadataInterfaceImpl.class);

  private BaseServiceMetadataInterfaceImpl () {}

  @Nonnull
  public static Document getServiceRegistration (@Nonnull final UriInfo uriInfo,
                                                                                @Nullable final String sServiceGroupID,
                                                                                @Nullable final String sDocumentTypeID) throws Throwable {
    s_aLogger.info (String.format("GET /%s/services/%s", sServiceGroupID, sDocumentTypeID));

    final ParticipantIdentifierType aServiceGroupID = Identifiers.asParticipantId(sServiceGroupID);
    final DocumentIdentifier aDocTypeID = Identifiers.asDocumentId (sDocumentTypeID);

    final IDataManager aDataManager = DataManagerFactory.getInstance ();
    String sServiceMetadata = aDataManager.getService (aServiceGroupID, aDocTypeID);
    if(sServiceMetadata == null) {
      throw new NotFoundException(String.format("Service '%s/services/%s' was not found", sServiceGroupID, sDocumentTypeID));
    }

    Document aSignedServiceMetadata = ServiceMetadataConverter.toSignedServiceMetadatadaDocument(sServiceMetadata);

    s_aLogger.info (String.format("Finished getServiceRegistration(%s,%s)", sServiceGroupID, sDocumentTypeID));
    return aSignedServiceMetadata;
  }
}
