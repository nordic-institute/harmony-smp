package eu.europa.ec.edelivery.smp.controllers;

import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.security.DomainGuard;
import eu.europa.ec.edelivery.smp.services.resource.ResourceService;
import eu.europa.ec.edelivery.smp.servlet.ResourceAction;
import eu.europa.ec.edelivery.smp.servlet.ResourceRequest;
import eu.europa.ec.edelivery.smp.servlet.ResourceResponse;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INVALID_REQUEST;
import static eu.europa.ec.edelivery.smp.servlet.WebConstants.*;
import static org.apache.commons.lang3.StringUtils.lowerCase;

/**
 * Resource controller - allows only HTTP methods
 * - GET - for reading the resource
 * - PUT for Creating (HTTP RESPONSE 201) and updating ((HTTP RESPONSE 200)) the objects
 * - DELETE for deleting the object
 */
@RestController
@RequestMapping(value = "/{parameter1:^(?!ui).*}", method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class ResourceController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ResourceController.class);
    // set them to lower case for fast comparing with the  http headers
    private static final List<String> SUPPORTED_HEADERS = Arrays.asList(lowerCase(HTTP_PARAM_DOMAIN),
            lowerCase(HTTP_PARAM_OWNER),
            lowerCase(HTTP_PARAM_RESOURCE_TYPE));
    final ResourceService resourceService;
    final DomainGuard domainGuard;


    public ResourceController(ResourceService resourceLocatorService, DomainGuard domainGuard) {
        this.resourceService = resourceLocatorService;
        this.domainGuard = domainGuard;
    }

    protected SMPUserDetails getLoggedInUser() {
        return SessionSecurityUtils.getSessionUserDetails();
    }

    @RequestMapping(produces = "text/xml; charset=UTF-8", method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
    public void getResource(HttpServletRequest httpReq, HttpServletResponse httpRes, @PathVariable String parameter1) {
        LOG.info("Resolver path segment [{}]", parameter1);

        handleRequest(httpReq, httpRes, Collections.singletonList(parameter1));
    }

    @RequestMapping(path = "{parameter2}", produces = "text/xml; charset=UTF-8", method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
    public void getResource(HttpServletRequest httpReq, HttpServletResponse httpRes, @PathVariable String parameter1, @PathVariable String parameter2) {

        LOG.info("Resolver paths [{}],[{}]", parameter1, parameter2);
        handleRequest(httpReq, httpRes, Arrays.asList(parameter1, parameter2));
    }

    @RequestMapping(path = "{parameter2}/{parameter3}", produces = "text/xml; charset=UTF-8", method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
    public void getResource(HttpServletRequest httpReq, HttpServletResponse httpRes, @PathVariable String parameter1, @PathVariable String parameter2, @PathVariable String parameter3) {
        LOG.info("Resolver paths [{}],[{}],[{}]", parameter1, parameter2, parameter3);
        handleRequest(httpReq, httpRes, Arrays.asList(parameter1, parameter2, parameter3));
    }

    @RequestMapping(path = "{parameter2}/{parameter3}/{parameter4}", produces = "text/xml; charset=UTF-8", method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
    public void getResource(HttpServletRequest httpReq, HttpServletResponse httpRes, @PathVariable String parameter1, @PathVariable String parameter2, @PathVariable String parameter3, @PathVariable String parameter4) {
        LOG.info("Resolver paths [{}],[{}],[{}],[{}]", parameter1, parameter2, parameter3, parameter4);
        handleRequest(httpReq, httpRes, Arrays.asList(parameter1, parameter2, parameter3, parameter4));

    }

    @RequestMapping(path = "{parameter2}/{parameter3}/{parameter4}/{parameter5}", produces = "text/xml; charset=UTF-8", method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
    public void getResource(HttpServletRequest httpReq, HttpServletResponse httpRes, @PathVariable String parameter1, @PathVariable String parameter2, @PathVariable String parameter3, @PathVariable String parameter4, @PathVariable String parameter5) {
        LOG.info("Resolver paths [{}],[{}],[{}],[{}],[{}]", parameter1, parameter2, parameter3, parameter4, parameter5);
        handleRequest(httpReq, httpRes, Arrays.asList(parameter1, parameter2, parameter3, parameter4, parameter5));
    }

    /**
     * Method validates the domain authorization for the requester user and action ad handles the request
     *
     * @param httpReq        a http request
     * @param httpRes        a http response to write the result
     * @param pathParameters path parameters
     */
    protected void handleRequest(HttpServletRequest httpReq, HttpServletResponse httpRes, List<String> pathParameters) {

        ResourceRequest resourceRequest = fromServletRequest(httpReq, pathParameters);
        LOG.debug("Got resource request [{}]", resourceRequest);
        SMPUserDetails user = authorizeForDomain(resourceRequest);
        ResourceResponse resourceResponse = fromServletResponse(httpRes);
        // handle the request
        resourceService.handleRequest(user, resourceRequest, resourceResponse);
    }

    /**
     * Method validates if user is authorized for the action on the domain
     *
     * @param resourceRequest a resource http request
     * @return the authorized user details
     * @throws AuthenticationException if user rejected to access the domain resources
     */
    protected SMPUserDetails authorizeForDomain(ResourceRequest resourceRequest) {

        SMPUserDetails user = getLoggedInUser();
        // guard check from generic to domain specific
        if (user == null && resourceRequest.getAction() != ResourceAction.READ) {
            throw new AuthenticationServiceException("User must be authenticated for the action: [" + resourceRequest.getAction() + "]");
        }
        // resolve domain and test authorization for the domain.
        domainGuard.resolveAndAuthorizeForDomain(resourceRequest, user);

        return user;
    }

    /**
     * Method builds resource request from the http requests and url path segment parameters
     *
     * @param httpReq        http request
     * @param pathParameters path parameters
     * @return the resource request.
     */

    protected ResourceRequest fromServletRequest(HttpServletRequest httpReq, List<String> pathParameters) {
        ResourceAction resourceAction = ResourceAction.resolveForHeader(httpReq.getMethod());
        if (resourceAction == null) {
            throw new SMPRuntimeException(INVALID_REQUEST, "Missing or invalid HTTP request method: [" + httpReq.getMethod() + "]!");
        }

        if (pathParameters.isEmpty()) {
            throw new SMPRuntimeException(INVALID_REQUEST, "At least one path parameter must be provided!");
        }
        InputStream inputStream = getInputStreamFromRequest(httpReq, resourceAction);

        // note: If there are multiple headers with the same name, this method returns the first header in the request.
        Map<String, String> headersMap = Collections.list(httpReq.getHeaderNames()).stream()
                .map(StringUtils::lowerCase)
                .filter(SUPPORTED_HEADERS::contains)
                .collect(Collectors.toMap(name -> name, httpReq::getHeader));

        return new ResourceRequest(resourceAction, headersMap, pathParameters, inputStream);
    }

    protected ResourceResponse fromServletResponse(HttpServletResponse httpRes) {
        ResourceResponse resourceResponse = new ResourceResponse(httpRes);
        // try to open the output stream
        resourceResponse.getOutputStream();
        return resourceResponse;
    }

    /**
     * The input stream can be re-read multiple times. Return re-readable input stream in case of create/update user actions.
     * In case of read/delete action return null since no input is expected!
     *
     * @param httpReq
     * @param resourceAction
     */
    public InputStream getInputStreamFromRequest(HttpServletRequest httpReq, ResourceAction resourceAction) {
        if (resourceAction != ResourceAction.CREATE_UPDATE) {
            return null;
        }
        try {
            return new BufferedInputStream(httpReq.getInputStream());
        } catch (IOException e) {
            throw new SMPRuntimeException(INVALID_REQUEST, "Can not read input stream!", e);
        }
    }


}
