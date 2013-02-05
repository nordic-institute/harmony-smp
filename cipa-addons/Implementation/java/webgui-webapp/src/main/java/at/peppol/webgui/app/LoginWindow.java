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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.peppol.webgui.app;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

/**
 * @author Jerouris
 */
public class LoginWindow extends Window implements Button.ClickListener {
  private PasswordField passwordField;
  private TextField usernameField;

  @Override
  public void buttonClick (final ClickEvent event) {
    // usernameField.commit();

    try {
      PawgApp.getInstance ().authenticate ((String) usernameField.getValue (), (String) passwordField.getValue ());
      // getWindow().showNotification("Welcome " + usernameField.getValue());

    }
    catch (final Exception ex) {
      Logger.getLogger (LoginWindow.class.getName ()).log (Level.SEVERE, ex.getMessage ());
      getWindow ().showNotification (ex.getMessage (), Notification.TYPE_ERROR_MESSAGE);
    }

  }

  public LoginWindow () {
    super ();
    init ();
  }

  private void init () {

    final HorizontalLayout h1 = new HorizontalLayout ();
    h1.setSizeFull ();

    final FormLayout fl = new FormLayout ();
    fl.setSizeUndefined ();
    usernameField = new TextField ("Username:");
    usernameField.setImmediate (true);
    fl.addComponent (usernameField);
    passwordField = new PasswordField ("Password:");
    passwordField.setImmediate (true);
    fl.addComponent (passwordField);

    final Button loginButton = new Button ("Login");
    loginButton.addStyleName ("default");
    loginButton.addListener (this);
    fl.addComponent (loginButton);
    loginButton.setClickShortcut (KeyCode.ENTER);

    h1.addComponent (fl);
    h1.setComponentAlignment (fl, Alignment.MIDDLE_CENTER);
    addComponent (h1);

  }
}
