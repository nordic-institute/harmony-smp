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
package at.peppol.webgui.app;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.webgui.app.login.UserDirManager;
import at.peppol.webgui.app.login.UserFolderManager;

import com.phloc.appbasics.security.AccessManager;
import com.phloc.appbasics.security.login.ELoginResult;
import com.phloc.appbasics.security.login.LoggedInUserManager;
import com.phloc.appbasics.security.user.IUser;
import com.phloc.scopes.web.mgr.WebScopeManager;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Window;

public class PawgApp extends Application implements HttpServletRequestListener {
  private static final ThreadLocal <PawgApp> threadLocal = new ThreadLocal <PawgApp> ();
  private static final Logger LOGGER = LoggerFactory.getLogger (PawgApp.class);
  private IUser user;
  private UserDirManager um;
  private final LoggedInUserManager lum = LoggedInUserManager.getInstance ();

  @Override
  public void init () {
    // Ensure that no user is logged in
    setInstance (this);
    setTheme ("peppol");
    try {
    	if (false) {
    		if (!lum.isUserLoggedInInCurrentSession()) {
       			showLoginWindow ();
        	}
    		else {
    			showMainAppWindow ();
    		}
    		/*logout();
    		lum.logoutCurrentUser ();
    		showLoginWindow ();
    		startWithMainWindow ();*/
    	}
    	else {
    		authenticate ("user@peppol.eu", "user");
    	}
    }
    catch (final Exception ex) {
      LOGGER.error (null, ex);
    }
  }
  
  public UserFolderManager<File> getUserSpaceManager() {
	  return um;
  }

  public void showLoginWindow () {
    final LoginWindow win = new LoginWindow ();
    setMainWindow (win);
  }

  private void showMainAppWindow () {
    final MainWindow mainWin = new MainWindow ();
    if (getMainWindow () != null) {
      removeWindow (getMainWindow ());
    }

    setMainWindow (mainWin);
    // getMainWindow().open(new ExternalResource(mainWin.getURL()));
    LOGGER.debug ("Called Showmain");
  }

  public static PawgApp getInstance () {
    return threadLocal.get ();
  }

  // Set the current application instance
  public static void setInstance (final PawgApp application) {
    threadLocal.set (application);
  }

  @Override
  public void onRequestStart (final HttpServletRequest request, final HttpServletResponse response) {
    PawgApp.setInstance (this);
    WebScopeManager.onRequestBegin ("pawg", request, response);
  }

  @Override
  public void onRequestEnd (final HttpServletRequest request, final HttpServletResponse response) {
    WebScopeManager.onRequestEnd ();
    threadLocal.remove ();
  }

  public void authenticate (final String username, final String password) throws Exception {

    final ELoginResult res = lum.loginUser (username, password);

    if (res.isSuccess ()) {
      user = AccessManager.getInstance ().getUserOfID (lum.getCurrentUserID ());
      setUser (user);
      um = new UserDirManager(user, "invoice");
      um.createUserFolders();
      showMainAppWindow ();

    }
    else {
      throw new Exception (res.toString ());
    }

  }

  public void logout () {
    lum.logoutCurrentUser ();
    close ();
  }
  
  private void startWithMainWindow () throws Exception {
    user = AccessManager.getInstance ().getUserOfLoginName ("user@peppol.eu");
    setUser (user);
    showMainAppWindow ();
  }

  @Override
  public Window getWindow (final String name) {
    Window w = super.getWindow (name);
    if (w == null) {
      w = new MainWindow (); // it's best to have separate classes for your
      // windows
      w.setName (name);
      addWindow (w);
    }
    return w;
  }
}
