/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.axis2.webapp;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.context.ServiceGroupContext;
import org.apache.axis2.deployment.util.PhasesInfo;
import org.apache.axis2.description.*;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.transport.http.AbstractAgent;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Provides methods to process axis2 admin requests.
 */
public class AdminAgent extends AbstractAgent {
    private static final Logger log = Logger.getLogger(AbstractAgent.class);
    /**
     * Field LIST_MULTIPLE_SERVICE_JSP_NAME
     */
    private static final String LIST_SERVICE_GROUP_JSP = "ListServiceGroup.jsp";
    private static final String LIST_SERVICES_JSP_NAME = "listService.jsp";
    private static final String LIST_SINGLE_SERVICES_JSP_NAME = "listSingleService.jsp";
    private static final String SELECT_SERVICE_JSP_NAME = "SelectService.jsp";
    private static final String IN_ACTIVATE_SERVICE_JSP_NAME = "InActivateService.jsp";
    private static final String ACTIVATE_SERVICE_JSP_NAME = "ActivateService.jsp";

    /**
     * Field LIST_SINGLE_SERVICE_JSP_NAME
     */
    private static final String LIST_PHASES_JSP_NAME = "viewphases.jsp";
    private static final String LIST_GLOABLLY_ENGAGED_MODULES_JSP_NAME = "globalModules.jsp";
    private static final String LIST_AVAILABLE_MODULES_JSP_NAME = "listModules.jsp";
    private static final String ENGAGING_MODULE_TO_SERVICE_JSP_NAME = "engagingtoaservice.jsp";
    private static final String ENGAGING_MODULE_TO_SERVICE_GROUP_JSP_NAME = "EngageToServiceGroup.jsp";
    private static final String ENGAGING_MODULE_GLOBALLY_JSP_NAME = "engagingglobally.jsp";
    public static final String ADMIN_JSP_NAME = "admin.jsp";
    private static final String VIEW_GLOBAL_HANDLERS_JSP_NAME = "ViewGlobalHandlers.jsp";
    private static final String VIEW_SERVICE_HANDLERS_JSP_NAME = "ViewServiceHandlers.jsp";
    private static final String SERVICE_PARA_EDIT_JSP_NAME = "ServiceParaEdit.jsp";
    private static final String ENGAGE_TO_OPERATION_JSP_NAME = "engagingtoanoperation.jsp";
    private static final String LOGIN_JSP_NAME = "Login.jsp";

    private File serviceDir;

    public AdminAgent(final ConfigurationContext aConfigContext) {
        super(aConfigContext);
        try {
            if (this.configContext.getAxisConfiguration().getRepository() != null) {
                final File repoDir = new File(this.configContext.getAxisConfiguration().getRepository().getFile());
                this.serviceDir = new File(repoDir, "services");
                if (!this.serviceDir.exists()) {
                    this.serviceDir.mkdirs();
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void handle(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
            throws IOException, ServletException {

        // We forward to login page if axis2 security is enabled
        // and the user is not authorized
        // TODO Fix workaround for login test
        if (axisSecurityEnabled() && authorizationRequired(httpServletRequest)) {
            renderView(LOGIN_JSP_NAME, httpServletRequest, httpServletResponse);
        } else {
            super.handle(httpServletRequest, httpServletResponse);
        }
    }

    @Override
    public void processIndex(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        renderView(ADMIN_JSP_NAME, req, res);
    }

    // supported web operations

    public void processUpload(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        final String hasHotDeployment =
                (String) this.configContext.getAxisConfiguration().getParameterValue("hotdeployment");
        final String hasHotUpdate = (String) this.configContext.getAxisConfiguration().getParameterValue("hotupdate");
        req.setAttribute("hotDeployment", ("true".equals(hasHotDeployment)) ? "enabled" : "disabled");
        req.setAttribute("hotUpdate", ("true".equals(hasHotUpdate)) ? "enabled" : "disabled");
        final RequestContext reqContext = new ServletRequestContext(req);

        final boolean isMultipart = ServletFileUpload.isMultipartContent(reqContext);
        if (isMultipart) {

            try {
                //Create a factory for disk-based file items
                final FileItemFactory factory = new DiskFileItemFactory();
                //Create a new file upload handler
                final ServletFileUpload upload = new ServletFileUpload(factory);
                final List<?> items = upload.parseRequest(req);
                // Process the uploaded items
                final Iterator<?> iter = items.iterator();
                while (iter.hasNext()) {
                    final FileItem item = (FileItem) iter.next();
                    if (!item.isFormField()) {

                        final String fileName = item.getName();
                        String fileExtesion = fileName;
                        fileExtesion = fileExtesion.toLowerCase();
                        if (!(fileExtesion.endsWith(".jar") || fileExtesion.endsWith(".aar"))) {
                            req.setAttribute("status", "failure");
                            req.setAttribute("cause", "Unsupported file type " + fileExtesion);
                        } else {

                            final String fileNameOnly;
                            if (fileName.indexOf("\\") < 0) {
                                fileNameOnly = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
                            } else {
                                fileNameOnly = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length());
                            }

                            final File uploadedFile = new File(this.serviceDir, fileNameOnly);
                            item.write(uploadedFile);
                            req.setAttribute("status", "success");
                            req.setAttribute("filename", fileNameOnly);
                        }
                    }
                }
            } catch (Exception e) {
                req.setAttribute("status", "failure");
                req.setAttribute("cause", e.getMessage());

            }
        }
        renderView("upload.jsp", req, res);
    }


    public void processLogin(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        final String username = req.getParameter("userName");
        final String password = req.getParameter("password");

        if ((username == null) || (password == null) || username.trim().length() == 0 ||
            password.trim().length() == 0) {
            req.setAttribute("errorMessage", "Invalid auth credentials!");
            renderView(LOGIN_JSP_NAME, req, res);
            return;
        }

        final String adminUserName =
                (String) this.configContext.getAxisConfiguration().getParameter(Constants.USER_NAME).getValue();
        final String adminPassword =
                (String) this.configContext.getAxisConfiguration().getParameter(Constants.PASSWORD).getValue();

        if (username.equals(adminUserName) && password.equals(adminPassword)) {
            req.getSession().setAttribute(Constants.LOGGED, "Yes");
            renderView(ADMIN_JSP_NAME, req, res);
        } else {
            req.setAttribute("errorMessage", "Invalid auth credentials!");
            renderView(LOGIN_JSP_NAME, req, res);
        }
    }

    public void processEditServicePara(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        final String serviceName = req.getParameter("axisService");
        if (req.getParameter("changePara") != null) {
            final AxisService service = this.configContext.getAxisConfiguration().getService(serviceName);
            if (service != null) {
                for (final Parameter parameter : service.getParameters()) {
                    final String para = req.getParameter(serviceName + "_" + parameter.getName());
                    service.addParameter(new Parameter(parameter.getName(), para));
                }

                for (Iterator<AxisOperation> iterator = service.getOperations(); iterator.hasNext(); ) {
                    final AxisOperation axisOperation = iterator.next();
                    final String op_name = axisOperation.getName().getLocalPart();

                    for (final Parameter parameter : axisOperation.getParameters()) {
                        final String para = req.getParameter(op_name + "_" + parameter.getName());

                        axisOperation.addParameter(new Parameter(parameter.getName(), para));
                    }
                }
            }
            res.setContentType("text/html");
            req.setAttribute("status", "Parameters Changed Successfully.");
            req.getSession().removeAttribute(Constants.SERVICE);
        } else {
            final AxisService serviceTemp =
                    this.configContext.getAxisConfiguration().getServiceForActivation(serviceName);
            if (serviceTemp.isActive()) {

                if (serviceName != null) {
                    req.getSession().setAttribute(Constants.SERVICE,
                                                  this.configContext.getAxisConfiguration().getService(serviceName));
                }
            } else {
                req.setAttribute("status", "Service " + serviceName + " is not an active service" +
                                           ". \n Only parameters of active services can be edited.");
            }
        }
        renderView(SERVICE_PARA_EDIT_JSP_NAME, req, res);
    }

    public void processEngagingGlobally(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        final Map<String, AxisModule> modules = this.configContext.getAxisConfiguration().getModules();

        req.getSession().setAttribute(Constants.MODULE_MAP, modules);

        final String moduleName = req.getParameter("modules");

        req.getSession().setAttribute(Constants.ENGAGE_STATUS, null);

        if (moduleName != null) {
            try {
                this.configContext.getAxisConfiguration().engageModule(moduleName);
                req.getSession()
                   .setAttribute(Constants.ENGAGE_STATUS, moduleName + " module engaged globally successfully");
            } catch (AxisFault axisFault) {
                req.getSession().setAttribute(Constants.ENGAGE_STATUS, axisFault.getMessage());
            }
        }

        req.getSession().setAttribute("modules", null);
        renderView(ENGAGING_MODULE_GLOBALLY_JSP_NAME, req, res);

    }

    public void processListOperations(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        final Map<String, AxisModule> modules = this.configContext.getAxisConfiguration().getModules();

        req.getSession().setAttribute(Constants.MODULE_MAP, modules);

        final String moduleName = req.getParameter("modules");

        req.getSession().setAttribute(Constants.ENGAGE_STATUS, null);
        req.getSession().setAttribute("modules", null);

        String serviceName = req.getParameter("axisService");

        if (serviceName != null) {
            req.getSession().setAttribute("service", serviceName);
        } else {
            serviceName = (String) req.getSession().getAttribute("service");
        }

        req.getSession().setAttribute(Constants.OPERATION_MAP,
                                      this.configContext.getAxisConfiguration().getService(serviceName)
                                                        .getOperations());
        req.getSession().setAttribute(Constants.ENGAGE_STATUS, null);

        final String operationName = req.getParameter("axisOperation");

        if ((serviceName != null) && (moduleName != null) && (operationName != null)) {
            try {
                final AxisOperation od = this.configContext.getAxisConfiguration().getService(serviceName)
                                                           .getOperation(new QName(operationName));

                od.engageModule(this.configContext.getAxisConfiguration().getModule(moduleName));
                req.getSession()
                   .setAttribute(Constants.ENGAGE_STATUS, moduleName + " module engaged to the operation successfully");
            } catch (AxisFault axisFault) {
                req.getSession().setAttribute(Constants.ENGAGE_STATUS, axisFault.getMessage());
            }
        }

        req.getSession().setAttribute("operation", null);
        renderView(ENGAGE_TO_OPERATION_JSP_NAME, req, res);
    }

    public void processEngageToService(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        final Map<String, AxisModule> modules = this.configContext.getAxisConfiguration().getModules();

        req.getSession().setAttribute(Constants.MODULE_MAP, modules);
        populateSessionInformation(req);

        final String moduleName = req.getParameter("modules");

        req.getSession().setAttribute(Constants.ENGAGE_STATUS, null);
        req.getSession().setAttribute("modules", null);

        final String serviceName = req.getParameter("axisService");

        req.getSession().setAttribute(Constants.ENGAGE_STATUS, null);

        if ((serviceName != null) && (moduleName != null)) {
            try {
                this.configContext.getAxisConfiguration().getService(serviceName)
                                  .engageModule(this.configContext.getAxisConfiguration().getModule(moduleName));
                req.getSession()
                   .setAttribute(Constants.ENGAGE_STATUS, moduleName + " module engaged to the service successfully");
            } catch (AxisFault axisFault) {
                req.getSession().setAttribute(Constants.ENGAGE_STATUS, axisFault.getMessage());
            }
        }

        req.getSession().setAttribute("axisService", null);
        renderView(ENGAGING_MODULE_TO_SERVICE_JSP_NAME, req, res);
    }

    public void processEngageToServiceGroup(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        final Map<String, AxisModule> modules = this.configContext.getAxisConfiguration().getModules();

        req.getSession().setAttribute(Constants.MODULE_MAP, modules);

        final Iterator<AxisServiceGroup> services = this.configContext.getAxisConfiguration().getServiceGroups();

        req.getSession().setAttribute(Constants.SERVICE_GROUP_MAP, services);

        final String moduleName = req.getParameter("modules");

        req.getSession().setAttribute(Constants.ENGAGE_STATUS, null);
        req.getSession().setAttribute("modules", null);

        final String serviceName = req.getParameter("axisService");

        req.getSession().setAttribute(Constants.ENGAGE_STATUS, null);

        if ((serviceName != null) && (moduleName != null)) {
            this.configContext.getAxisConfiguration().getServiceGroup(serviceName)
                              .engageModule(this.configContext.getAxisConfiguration().getModule(moduleName));
            req.getSession()
               .setAttribute(Constants.ENGAGE_STATUS, moduleName + " module engaged to the service group successfully");
        }

        req.getSession().setAttribute("axisService", null);
        renderView(ENGAGING_MODULE_TO_SERVICE_GROUP_JSP_NAME, req, res);
    }


    public void processLogout(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        req.getSession().invalidate();
        renderView("index.jsp", req, res);
    }

    public void processviewServiceGroupConetxt(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        final String type = req.getParameter("TYPE");
        final String sgID = req.getParameter("ID");
        final ServiceGroupContext sgContext = this.configContext.getServiceGroupContext(sgID);
        req.getSession().setAttribute("ServiceGroupContext", sgContext);
        req.getSession().setAttribute("TYPE", type);
        req.getSession().setAttribute("ConfigurationContext", this.configContext);
        renderView("viewServiceGroupContext.jsp", req, res);
    }

    public void processviewServiceContext(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        final String type = req.getParameter("TYPE");
        final String sgID = req.getParameter("PID");
        final String ID = req.getParameter("ID");
        final ServiceGroupContext sgContext = this.configContext.getServiceGroupContext(sgID);
        if (sgContext != null) {
            final AxisService service = sgContext.getDescription().getService(ID);
            final ServiceContext serviceContext = sgContext.getServiceContext(service);
            req.setAttribute("ServiceContext", serviceContext);
            req.setAttribute("TYPE", type);
        } else {
            req.setAttribute("ServiceContext", null);
            req.setAttribute("TYPE", type);
        }
        renderView("viewServiceContext.jsp", req, res);
    }

    public void processSelectServiceParaEdit(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        populateSessionInformation(req);
        req.getSession().setAttribute(Constants.SELECT_SERVICE_TYPE, "SERVICE_PARAMETER");
        renderView(SELECT_SERVICE_JSP_NAME, req, res);
    }

    public void processListOperation(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        populateSessionInformation(req);
        req.getSession().setAttribute(Constants.SELECT_SERVICE_TYPE, "MODULE");

        renderView(SELECT_SERVICE_JSP_NAME, req, res);
    }

    public void processActivateService(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        if (req.getParameter("submit") != null) {
            final String serviceName = req.getParameter("axisService");
            final String turnon = req.getParameter("turnon");
            if (serviceName != null) {
                if (turnon != null) {
                    this.configContext.getAxisConfiguration().startService(serviceName);
                }
            }
        }
        populateSessionInformation(req);
        renderView(ACTIVATE_SERVICE_JSP_NAME, req, res);
    }

    public void processDeactivateService(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        if (req.getParameter("submit") != null) {
            final String serviceName = req.getParameter("axisService");
            final String turnoff = req.getParameter("turnoff");
            if (serviceName != null) {
                if (turnoff != null) {
                    this.configContext.getAxisConfiguration().stopService(serviceName);
                }
                populateSessionInformation(req);
            }
        } else {
            populateSessionInformation(req);
        }

        renderView(IN_ACTIVATE_SERVICE_JSP_NAME, req, res);
    }


    public void processViewGlobalHandlers(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        req.getSession().setAttribute(Constants.GLOBAL_HANDLERS, this.configContext.getAxisConfiguration());

        renderView(VIEW_GLOBAL_HANDLERS_JSP_NAME, req, res);
    }

    public void processViewServiceHandlers(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        final String service = req.getParameter("axisService");

        if (service != null) {
            req.getSession()
               .setAttribute(Constants.SERVICE_HANDLERS, this.configContext.getAxisConfiguration().getService(service));
        }

        renderView(VIEW_SERVICE_HANDLERS_JSP_NAME, req, res);
    }


    public void processListPhases(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        final PhasesInfo info = this.configContext.getAxisConfiguration().getPhasesInfo();
        req.getSession().setAttribute(Constants.PHASE_LIST, info);
        renderView(LIST_PHASES_JSP_NAME, req, res);
    }

    public void processListServiceGroups(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        final Iterator<AxisServiceGroup> serviceGroups = this.configContext.getAxisConfiguration().getServiceGroups();
        populateSessionInformation(req);
        req.getSession().setAttribute(Constants.SERVICE_GROUP_MAP, serviceGroups);

        renderView(LIST_SERVICE_GROUP_JSP, req, res);
    }

    public void processListService(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        populateSessionInformation(req);
        req.getSession()
           .setAttribute(Constants.ERROR_SERVICE_MAP, this.configContext.getAxisConfiguration().getFaultyServices());

        renderView(LIST_SERVICES_JSP_NAME, req, res);
    }

    public void processListSingleService(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        req.getSession().setAttribute(Constants.IS_FAULTY, ""); //Clearing out any old values.
        final String serviceName = req.getParameter("serviceName");
        if (serviceName != null) {
            final AxisService service = this.configContext.getAxisConfiguration().getService(serviceName);
            req.getSession().setAttribute(Constants.SINGLE_SERVICE, service);
        }
        renderView(LIST_SINGLE_SERVICES_JSP_NAME, req, res);
    }


    public void processListContexts(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        req.getSession().setAttribute(Constants.CONFIG_CONTEXT, this.configContext);
        renderView("ViewContexts.jsp", req, res);
    }

    public void processglobalModules(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        final Collection<AxisModule> modules = this.configContext.getAxisConfiguration().getEngagedModules();

        req.getSession().setAttribute(Constants.MODULE_MAP, modules);

        renderView(LIST_GLOABLLY_ENGAGED_MODULES_JSP_NAME, req, res);
    }

    public void processListModules(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        final Map<String, AxisModule> modules = this.configContext.getAxisConfiguration().getModules();

        req.getSession().setAttribute(Constants.MODULE_MAP, modules);
        req.getSession()
           .setAttribute(Constants.ERROR_MODULE_MAP, this.configContext.getAxisConfiguration().getFaultyModules());

        renderView(LIST_AVAILABLE_MODULES_JSP_NAME, req, res);
    }

    public void processdisengageModule(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        final String type = req.getParameter("type");
        final String serviceName = req.getParameter("serviceName");
        final String moduleName = req.getParameter("module");
        final AxisConfiguration axisConfiguration = this.configContext.getAxisConfiguration();
        final AxisService service = axisConfiguration.getService(serviceName);
        final AxisModule module = axisConfiguration.getModule(moduleName);
        if ("operation".equals(type)) {
            if (service.isEngaged(module.getName()) || axisConfiguration.isEngaged(module.getName())) {
                req.getSession().setAttribute("status", "Can not disengage module " + moduleName +
                                                        ". This module is engaged at a higher level.");
            } else {
                final String opName = req.getParameter("operation");
                final AxisOperation op = service.getOperation(new QName(opName));
                op.disengageModule(module);
                req.getSession().setAttribute("status", "Module " + moduleName + " was disengaged from " +
                                                        "operation " + opName + " in service " + serviceName + ".");
            }
        } else {
            if (axisConfiguration.isEngaged(module.getName())) {
                req.getSession().setAttribute("status", "Can not disengage module " + moduleName + ". " +
                                                        "This module is engaged at a higher level.");
            } else {
                service.disengageModule(axisConfiguration.getModule(moduleName));
                req.getSession().setAttribute("status", "Module " + moduleName + " was disengaged from" +
                                                        " service " + serviceName + ".");
            }
        }
        renderView("disengage.jsp", req, res);
    }

    public void processdeleteService(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        final String serviceName = req.getParameter("serviceName");
        final AxisConfiguration axisConfiguration = this.configContext.getAxisConfiguration();
        if (axisConfiguration.getService(serviceName) != null) {
            axisConfiguration.removeService(serviceName);
            req.getSession().setAttribute("status", "Service '" + serviceName + "' has been successfully removed.");
        } else {
            req.getSession()
               .setAttribute("status", "Failed to delete service '" + serviceName + "'. Service doesn't exist.");
        }

        renderView("deleteService.jsp", req, res);
    }

    public void processSelectService(final HttpServletRequest req, final HttpServletResponse res)
            throws IOException, ServletException {
        populateSessionInformation(req);
        req.getSession().setAttribute(Constants.SELECT_SERVICE_TYPE, "VIEW");

        renderView(SELECT_SERVICE_JSP_NAME, req, res);
    }


    private boolean authorizationRequired(final HttpServletRequest httpServletRequest) {
        return httpServletRequest.getSession().getAttribute(Constants.LOGGED) == null &&
               !httpServletRequest.getRequestURI().endsWith("login");
    }

    private boolean axisSecurityEnabled() {
        final Parameter parameter =
                this.configContext.getAxisConfiguration().getParameter(Constants.ADMIN_SECURITY_DISABLED);
        return parameter == null || !"true".equals(parameter.getValue());
    }

}