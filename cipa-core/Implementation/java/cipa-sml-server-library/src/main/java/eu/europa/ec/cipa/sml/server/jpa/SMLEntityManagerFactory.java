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
package eu.europa.ec.cipa.sml.server.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.db.jpa.AbstractGlobalEntityManagerFactory;

import eu.europa.ec.cipa.peppol.utils.ConfigFile;

/**
 * Specific SML JPA entity manager
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class SMLEntityManagerFactory extends AbstractGlobalEntityManagerFactory {
  @Nonnull
  @ReturnsMutableCopy
  private static Map <String, Object> _createSettingsMap () {
    // Standard configuration file
    final ConfigFile aCF = ConfigFile.getInstance ();

    final Map <String, Object> ret = new HashMap <String, Object> ();
    // Connection pooling
    ret.put (PersistenceUnitProperties.CONNECTION_POOL_MAX,
             aCF.getString (SMLJPAConfiguration.CONFIG_JDBC_READ_CONNECTIONS_MAX));

    // EclipseLink should create the database schema automatically
    // Values: Values: none/create-tables/drop-and-create-tables
    ret.put (PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
    ret.put (PersistenceUnitProperties.DDL_GENERATION_MODE,
             aCF.getString (SMLJPAConfiguration.CONFIG_DDL_GENERATION_MODE,
                            SMLJPAConfiguration.getDefaultDDLGenerationMode ()));
    ret.put (PersistenceUnitProperties.CREATE_JDBC_DDL_FILE, "db-create-sml.sql");
    ret.put (PersistenceUnitProperties.DROP_JDBC_DDL_FILE, "db-drop-sml.sql");

    // Use an isolated cache
    // (http://code.google.com/p/peppol-silicone/issues/detail?id=6)
    ret.put (PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");

    return ret;
  }

  @Deprecated
  @UsedViaReflection
  public SMLEntityManagerFactory () {
    super (ConfigFile.getInstance ().getString (SMLJPAConfiguration.CONFIG_JDBC_DRIVER),
           ConfigFile.getInstance ().getString (SMLJPAConfiguration.CONFIG_JDBC_URL),
           ConfigFile.getInstance ().getString (SMLJPAConfiguration.CONFIG_JDBC_USER),
           ConfigFile.getInstance ().getString (SMLJPAConfiguration.CONFIG_JDBC_PASSWORD),
           ConfigFile.getInstance ().getString (SMLJPAConfiguration.CONFIG_TARGET_DATABASE),
           "peppol-sml",
           _createSettingsMap ());
  }

  @Nonnull
  public static SMLEntityManagerFactory getInstance () {
    return getGlobalSingleton (SMLEntityManagerFactory.class);
  }
}
