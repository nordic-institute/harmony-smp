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
package at.peppol.webgui.security;

import javax.annotation.concurrent.Immutable;

import com.phloc.commons.messagedigest.EMessageDigestAlgorithm;

/**
 * Constants for user handling
 * 
 * @author philip
 */
@Immutable
public final class CSecurity {
  /** Hashing algorithm to use for user passwords */
  public static final EMessageDigestAlgorithm USER_PASSWORD_ALGO = EMessageDigestAlgorithm.SHA_512;

  // Default users
  public static final String USER_ADMINISTRATOR_ID = "admin";
  public static final String USER_ADMINISTRATOR_EMAIL = "admin@peppol.eu";
  public static final String USER_ADMINISTRATOR_NAME = "Administrator";
  public static final String USER_ADMINISTRATOR_PASSWORD = "password";

  public static final String USER_USER_ID = "user";
  public static final String USER_USER_EMAIL = "user@peppol.eu";
  public static final String USER_USER_NAME = "User";
  public static final String USER_USER_PASSWORD = "user";

  public static final String USER_GUEST_ID = "guest";
  public static final String USER_GUEST_EMAIL = "guest@peppol.eu";
  public static final String USER_GUEST_NAME = "Guest";
  public static final String USER_GUEST_PASSWORD = "guest";

  // Default roles
  public static final String ROLE_ADMINISTRATOR_ID = "radmin";
  public static final String ROLE_ADMINISTRATOR_NAME = "Administrator";
  public static final String ROLE_USER_ID = "ruser";
  public static final String ROLE_USER_NAME = "User";

  // Default user groups
  public static final String USERGROUP_ADMINISTRATORS_ID = "ugadmin";
  public static final String USERGROUP_ADMINISTRATORS_NAME = "Administrators";
  public static final String USERGROUP_USERS_ID = "uguser";
  public static final String USERGROUP_USERS_NAME = "Users";
  public static final String USERGROUP_GUESTS_ID = "ugguest";
  public static final String USERGROUP_GUESTS_NAME = "Guests";

  private CSecurity () {}
}
