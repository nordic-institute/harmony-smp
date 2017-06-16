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
package eu.europa.ec.cipa.smp.server.data.dbms;

import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.annotations.UsedViaReflection;
import com.helger.db.jpa.AbstractGlobalEntityManagerFactory;
import eu.europa.ec.cipa.smp.server.util.ConfigFile;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Specific SMP JPA entity manager factory
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class SMPEntityManagerFactory extends AbstractGlobalEntityManagerFactory {

    private static ConfigFile configFile;

    static {
        /* TODO : This is a quick and dirty hack to allow the use of a configuration file with an other name if it's
        in the classpath (like smp.config.properties or sml.config.properties).
        If the configuration file defined in applicationContext.xml couldn't be found, then the config.properties inside the war is used as a fallback.
        Needs to be properly refactored */
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath:applicationContext.xml"});
        configFile = (ConfigFile) context.getBean("configFile");
    }

  @Nonnull
  @ReturnsMutableCopy
  private static Map <String, Object> _createPropertiesMap () {
    final Map <String, Object> ret = new HashMap <String, Object> ();
    // Read all properties from the standard configuration file
    // Connection pooling
    ret.put (PersistenceUnitProperties.CONNECTION_POOL_MAX,
            configFile.getString (SMPJPAConfiguration.CONFIG_JDBC_READ_CONNECTIONS_MAX));

    // EclipseLink should create the database schema automatically
    // Values: Values: none/create-tables/drop-and-create-tables
    ret.put (PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
    ret.put (PersistenceUnitProperties.DDL_GENERATION_MODE,
            configFile.getString (SMPJPAConfiguration.CONFIG_DDL_GENERATION_MODE,
                            SMPJPAConfiguration.getDefaultDDLGenerationMode ()));
    ret.put (PersistenceUnitProperties.CREATE_JDBC_DDL_FILE, "db-create-smp.sql");
    ret.put (PersistenceUnitProperties.DROP_JDBC_DDL_FILE, "db-drop-smp.sql");

    // Use an isolated cache
    // (http://code.google.com/p/peppol-silicone/issues/detail?id=6)
    ret.put (PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");

    //Non-default name causes that WebLogic does not touch and especially does not load it. Hopefully.
    ret.put(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/smp-persistence.xml");

    return ret;
  }

  @Deprecated
  @UsedViaReflection
  public SMPEntityManagerFactory () {
    super (configFile.getString (SMPJPAConfiguration.CONFIG_JDBC_DRIVER),
           configFile.getString (SMPJPAConfiguration.CONFIG_JDBC_URL),
           configFile.getString (SMPJPAConfiguration.CONFIG_JDBC_USER),
           configFile.getString (SMPJPAConfiguration.CONFIG_JDBC_PASSWORD),
           configFile.getString (SMPJPAConfiguration.CONFIG_TARGET_DATABASE),
           "peppol-smp",
           _createPropertiesMap ());
  }

  @Nonnull
  public static SMPEntityManagerFactory getInstance () {
    return getGlobalSingleton (SMPEntityManagerFactory.class);
  }
}
