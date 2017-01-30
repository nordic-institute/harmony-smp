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

import eu.europa.ec.cipa.smp.server.conversion.ServiceGroupConverter;
import eu.europa.ec.cipa.smp.server.data.DataManagerFactory;
import eu.europa.ec.cipa.smp.server.data.IDataManager;
import eu.europa.ec.cipa.smp.server.errors.exceptions.BadRequestException;
import eu.europa.ec.cipa.smp.server.errors.exceptions.UnauthorizedException;
import eu.europa.ec.cipa.smp.server.services.BaseServiceGroupInterfaceImpl;
import eu.europa.ec.cipa.smp.server.util.IdentifierUtils;
import eu.europa.ec.cipa.smp.server.util.RequestHelper;
import eu.europa.ec.smp.api.Identifiers;
import eu.europa.ec.smp.api.validators.BdxSmpOasisValidator;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import static eu.europa.ec.cipa.smp.server.errors.ErrorBusinessCode.WRONG_FIELD;
import static eu.europa.ec.cipa.smp.server.security.UserRole.ROLE_SMP_ADMIN;

/**
 * This class implements the REST interface for getting ServiceGroup's. PUT and
 * DELETE are also implemented.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Path ("/{ServiceGroupId}")
public final class ServiceGroupInterface {
  private static final Logger s_aLogger = LoggerFactory.getLogger (ServiceGroupInterface.class);

  @Context
  private HttpHeaders headers;
  @Context
  private UriInfo uriInfo;

  public ServiceGroupInterface () {}

  @GET
  @Produces (MediaType.TEXT_XML)
  public ServiceGroup getServiceGroup (@PathParam ("ServiceGroupId") final String sServiceGroupId) throws Throwable {
    // Delegate to common implementation
    return BaseServiceGroupInterfaceImpl.getServiceGroup (uriInfo, headers, sServiceGroupId, ServiceMetadataInterface.class);
  }

  @PUT
  public Response saveServiceGroup (@PathParam ("ServiceGroupId") final String sServiceGroupID,
                                    final String body) throws Throwable{

    s_aLogger.info (String.format("PUT /%s ==> %s", sServiceGroupID, body));
    verifySMPAdminCredentials();
    BdxSmpOasisValidator.validateXSD(body);

    final ServiceGroup aServiceGroup = ServiceGroupConverter.unmarshal(body);

    final ParticipantIdentifierType aServiceGroupID = Identifiers.asParticipantId(sServiceGroupID);
    if (!IdentifierUtils.areIdentifiersEqual (aServiceGroupID, aServiceGroup.getParticipantIdentifier ())) {
      // Business identifier must equal path
      throw new BadRequestException(WRONG_FIELD, "Service Group Ids don't match between URL parameter and XML body");
    }

    final IDataManager aDataManager = DataManagerFactory.getInstance ();
    boolean bServiceGroupCreated = aDataManager.saveServiceGroup(aServiceGroup, RequestHelper.getAuth(headers));

    s_aLogger.info (String.format("Finished saveServiceGroup(%s,%s)", sServiceGroupID, aServiceGroup));

    return bServiceGroupCreated ? Response.created(uriInfo.getRequestUri()).build() : Response.ok ().build ();
  }

  @DELETE
  public Response deleteServiceGroup (@PathParam ("ServiceGroupId") final String sServiceGroupID) throws Throwable {
    s_aLogger.info ("DELETE /" + sServiceGroupID);
    verifySMPAdminCredentials();

    final ParticipantIdentifierType aServiceGroupID = Identifiers.asParticipantId(sServiceGroupID);

    final IDataManager aDataManager = DataManagerFactory.getInstance ();
    aDataManager.deleteServiceGroup (aServiceGroupID, RequestHelper.getAuth (headers));

    s_aLogger.info (String.format("Finished deleteServiceGroup(%s)", sServiceGroupID));

    return Response.ok ().build ();
  }

  //TODO: Use @Secured annotation instead of this garbage after the application is migrated to Spring
  @Deprecated
  private void verifySMPAdminCredentials() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth instanceof AnonymousAuthenticationToken) {
      throw new UnauthorizedException("User is not authenticated");
    }
    boolean isSmpAdin = false;
    for (GrantedAuthority authority : auth.getAuthorities()) {
      isSmpAdin |= ROLE_SMP_ADMIN.name().equals(authority.getAuthority());
    }
    if (!isSmpAdin) {
      throw new UnauthorizedException("Authenticated user does not have 'SMP Admin' permission.");
    }
  }

}
