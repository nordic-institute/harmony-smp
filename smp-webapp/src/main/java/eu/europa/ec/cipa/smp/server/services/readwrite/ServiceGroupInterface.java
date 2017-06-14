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

import eu.europa.ec.cipa.smp.server.conversion.ServiceGroupConverter;
import eu.europa.ec.cipa.smp.server.data.DataManagerFactory;
import eu.europa.ec.cipa.smp.server.data.IDataManager;
import eu.europa.ec.cipa.smp.server.errors.exceptions.BadRequestException;
import eu.europa.ec.cipa.smp.server.errors.exceptions.UnauthorizedException;
import eu.europa.ec.cipa.smp.server.services.BaseServiceGroupInterfaceImpl;
import eu.europa.ec.cipa.smp.server.util.ConfigFile;
import eu.europa.ec.cipa.smp.server.util.IdentifierUtils;
import eu.europa.ec.cipa.smp.server.util.RequestHelper;
import eu.europa.ec.edelivery.security.PreAuthenticatedCertificatePrincipal;
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
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

  private static final String CONFIG_SERVICE_GROUP_SCHEME_REGEXP = "identifiersBehaviour.ParticipantIdentifierScheme.validationRegex";
  private Pattern schemaPattern;

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

    // Validations
    verifySMPAdminCredentials();
    BdxSmpOasisValidator.validateXSD(body);
    final ServiceGroup aServiceGroup = ServiceGroupConverter.unmarshal(body);
    validateIds(sServiceGroupID, aServiceGroup);

    // Service action
    final IDataManager aDataManager = DataManagerFactory.getInstance ();
    boolean bServiceGroupCreated = aDataManager.saveServiceGroup(aServiceGroup, RequestHelper.getAuth(headers, true));

    s_aLogger.info (String.format("Finished saveServiceGroup(%s,%s)", sServiceGroupID, aServiceGroup));

    return bServiceGroupCreated ? Response.created(uriInfo.getRequestUri()).build() : Response.ok ().build ();
  }

  private void validateIds(@PathParam("ServiceGroupId") String sServiceGroupID, ServiceGroup aServiceGroup) {

    final ParticipantIdentifierType aServiceGroupID = Identifiers.asParticipantId(sServiceGroupID);
    if (!IdentifierUtils.areIdentifiersEqual (aServiceGroupID, aServiceGroup.getParticipantIdentifier ())) {
      // Business identifier must equal path
      throw new BadRequestException(WRONG_FIELD, "Service Group Ids don't match between URL parameter and XML body");
    }

    String scheme = aServiceGroup.getParticipantIdentifier().getScheme();
    if(!getSchemaPattern().matcher(scheme).matches()){
      throw new BadRequestException(WRONG_FIELD, "Service Group scheme does not match allowed pattern: " + getSchemaPattern().pattern());
    }
  }

  @DELETE
  public Response deleteServiceGroup (@PathParam ("ServiceGroupId") final String sServiceGroupID) throws Throwable {
    s_aLogger.info ("DELETE /" + sServiceGroupID);
    verifySMPAdminCredentials();

    final ParticipantIdentifierType aServiceGroupID = Identifiers.asParticipantId(sServiceGroupID);

    final IDataManager aDataManager = DataManagerFactory.getInstance ();
    aDataManager.deleteServiceGroup (aServiceGroupID, RequestHelper.getAuth (headers, false));

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
    if( auth.getDetails() instanceof PreAuthenticatedCertificatePrincipal){
      //TODO: In SMP 4.0 authentication must be split from Authorization.
      //TODO: Thus it will be possible ( at least by configuration ! ) to be an "SMP Admin" authenticated by certificate.
      throw new UnauthorizedException("User authenticated with Certificate does not have 'SMP Admin' permission.");
    }
    boolean isSmpAdmin = false;
    for (GrantedAuthority authority : auth.getAuthorities()) {
      isSmpAdmin |= ROLE_SMP_ADMIN.name().equals(authority.getAuthority());
    }
    if (!isSmpAdmin) {
      throw new UnauthorizedException("Authenticated user does not have 'SMP Admin' permission.");
    }
  }

  private Pattern getSchemaPattern(){
    if(schemaPattern == null){
      synchronized (this){
        WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        ConfigFile configFile = ctx.getBean(ConfigFile.class);
        String regex = configFile.getString(CONFIG_SERVICE_GROUP_SCHEME_REGEXP);
        try {
          schemaPattern = Pattern.compile(regex);
        } catch (PatternSyntaxException | NullPointerException e){
          throw new IllegalStateException("Contact Administrator. ServiceGroup schema pattern is wrongly configured: " + regex);
        }
      }
    }

    return schemaPattern;
  }

}
