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
package eu.europa.ec.cipa.webgui.app;

import java.io.File;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.phloc.appbasics.app.io.WebFileIO;
import com.phloc.appbasics.app.io.WebIO;
import com.phloc.appbasics.app.io.WebIOResourceProviderChain;
import com.phloc.appbasics.security.AccessManager;
import com.phloc.appbasics.security.role.RoleManager;
import com.phloc.appbasics.security.user.UserManager;
import com.phloc.appbasics.security.usergroup.UserGroupManager;
import com.phloc.commons.GlobalDebug;
import com.phloc.commons.idfactory.FileIntIDFactory;
import com.phloc.commons.idfactory.GlobalIDFactory;
import com.phloc.commons.string.StringHelper;
import com.phloc.webscopes.MetaWebScopeFactory;
import com.phloc.webscopes.domain.IRequestWebScope;
import com.phloc.webscopes.factory.DefaultWebScopeFactory;
import com.phloc.webscopes.impl.RequestWebScopeNoMultipart;
import com.phloc.webscopes.mgr.WebScopeManager;

import eu.europa.ec.cipa.webgui.security.CSecurity;

/**
 * A special scope listener, that correctly manages global and session scopes
 * 
 * @author philip
 */
public final class ScopeListener implements ServletContextListener, HttpSessionListener {
  public static final String INIT_PARAMETER_TRACE = "trace";
  // like Vaadin:
  public static final String INIT_PARAMETER_DEBUG = "Debug";
  // like Vaadin:
  public static final String INIT_PARAMETER_PRODUCTION = "productionMode";
  public static final String INIT_PARAMETER_STORAGE_BASE = "storage-base";

  private static final Logger s_aLogger = LoggerFactory.getLogger (ScopeListener.class);

  private static void _initAccessManager () {
    // Call before accessing AccessManager!
    RoleManager.setCreateDefaults (false);
    UserManager.setCreateDefaults (false);
    UserGroupManager.setCreateDefaults (false);

    final AccessManager aAM = AccessManager.getInstance ();

    // Users
    if (!aAM.containsUserWithID (CSecurity.USER_ADMINISTRATOR_ID))
      aAM.createPredefinedUser (CSecurity.USER_ADMINISTRATOR_ID,
                                CSecurity.USER_ADMINISTRATOR_EMAIL,
                                CSecurity.USER_ADMINISTRATOR_EMAIL,
                                CSecurity.USER_ADMINISTRATOR_PASSWORD,
                                CSecurity.USER_ADMINISTRATOR_NAME,
                                null,
                                null,
                                null,
                                false);
    if (!aAM.containsUserWithID (CSecurity.USER_USER_ID))
      aAM.createPredefinedUser (CSecurity.USER_USER_ID,
                                CSecurity.USER_USER_EMAIL,
                                CSecurity.USER_USER_EMAIL,
                                CSecurity.USER_USER_PASSWORD,
                                CSecurity.USER_USER_NAME,
                                null,
                                null,
                                null,
                                false);
    if (!aAM.containsUserWithID (CSecurity.USER_GUEST_ID))
      aAM.createPredefinedUser (CSecurity.USER_GUEST_ID,
                                CSecurity.USER_GUEST_EMAIL,
                                CSecurity.USER_GUEST_EMAIL,
                                CSecurity.USER_GUEST_PASSWORD,
                                CSecurity.USER_GUEST_NAME,
                                null,
                                null,
                                null,
                                false);

    // Roles
    if (!aAM.containsRoleWithID (CSecurity.ROLE_ADMINISTRATOR_ID))
      aAM.createPredefinedRole (CSecurity.ROLE_ADMINISTRATOR_ID, CSecurity.ROLE_ADMINISTRATOR_NAME);
    if (!aAM.containsRoleWithID (CSecurity.ROLE_USER_ID))
      aAM.createPredefinedRole (CSecurity.ROLE_USER_ID, CSecurity.ROLE_USER_NAME);

    // User groups
    if (!aAM.containsUserGroupWithID (CSecurity.USERGROUP_ADMINISTRATORS_ID)) {
      aAM.createPredefinedUserGroup (CSecurity.USERGROUP_ADMINISTRATORS_ID, CSecurity.USERGROUP_ADMINISTRATORS_NAME);
      aAM.assignUserToUserGroup (CSecurity.USERGROUP_ADMINISTRATORS_ID, CSecurity.USER_ADMINISTRATOR_ID);
      aAM.assignRoleToUserGroup (CSecurity.USERGROUP_ADMINISTRATORS_ID, CSecurity.ROLE_ADMINISTRATOR_ID);
    }
    if (!aAM.containsUserGroupWithID (CSecurity.USERGROUP_USERS_ID)) {
      aAM.createPredefinedUserGroup (CSecurity.USERGROUP_USERS_ID, CSecurity.USERGROUP_USERS_NAME);
      aAM.assignRoleToUserGroup (CSecurity.USERGROUP_USERS_ID, CSecurity.ROLE_USER_ID);
    }
    if (!aAM.containsUserGroupWithID (CSecurity.USERGROUP_GUESTS_ID)) {
      aAM.createPredefinedUserGroup (CSecurity.USERGROUP_GUESTS_ID, CSecurity.USERGROUP_GUESTS_NAME);
    }
  }

  @Override
  public void contextInitialized (final ServletContextEvent sce) {
    final ServletContext aSC = sce.getServletContext ();
    // set global debug/trace mode
    final boolean bTraceMode = Boolean.parseBoolean (aSC.getInitParameter (INIT_PARAMETER_TRACE));
    final boolean bDebugMode = Boolean.parseBoolean (aSC.getInitParameter (INIT_PARAMETER_DEBUG));
    final boolean bProductionMode = Boolean.parseBoolean (aSC.getInitParameter (INIT_PARAMETER_PRODUCTION));
    GlobalDebug.setTraceModeDirect (bTraceMode);
    GlobalDebug.setDebugModeDirect (bDebugMode);
    GlobalDebug.setProductionModeDirect (bProductionMode);

    // Set the storage base
    final String sServletContextPath = aSC.getRealPath (".");
    String sBasePath = aSC.getInitParameter (INIT_PARAMETER_STORAGE_BASE);
    if (StringHelper.hasNoText (sBasePath)) {
      if (GlobalDebug.isDebugMode () && s_aLogger.isInfoEnabled ())
        s_aLogger.info ("No servlet context init-parameter '" +
                        INIT_PARAMETER_STORAGE_BASE +
                        "' found! Defaulting to " +
                        sServletContextPath);
      sBasePath = sServletContextPath;
    }
    final File aBasePath = new File (sBasePath);
    WebFileIO.initPaths (aBasePath, new File (sServletContextPath), true);
    WebIO.init (new WebIOResourceProviderChain (aBasePath));

    // Init the unique ID provider
    GlobalIDFactory.setPersistentIntIDFactory (new FileIntIDFactory (WebFileIO.getFile ("id.txt")));

    // Set the non-multipart file item request web scope
    MetaWebScopeFactory.setWebScopeFactory (new DefaultWebScopeFactory () {
      @Override
      @Nonnull
      public IRequestWebScope createRequestScope (@Nonnull final HttpServletRequest aHttpRequest,
                                                  @Nonnull final HttpServletResponse aHttpResponse) {
        return new RequestWebScopeNoMultipart (aHttpRequest, aHttpResponse);
      }
    });

    // Init the global scope
    WebScopeManager.onGlobalBegin (aSC);

    _initAccessManager ();
  }

  @Override
  public void contextDestroyed (final ServletContextEvent sce) {
    // Destroy global scope
    WebScopeManager.onGlobalEnd ();
  }

  @Override
  public void sessionCreated (final HttpSessionEvent se) {
    // Create session scope
    WebScopeManager.onSessionBegin (se.getSession ());
  }

  @Override
  public void sessionDestroyed (final HttpSessionEvent se) {
    // Destroy session scope
    WebScopeManager.onSessionEnd (se.getSession ());
  }
}
