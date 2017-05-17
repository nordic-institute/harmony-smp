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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import com.helger.commons.GlobalDebug;
import com.helger.commons.annotations.PresentForCodeCoverage;

/**
 * Default JPA configuration file properties
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class SMPJPAConfiguration {
  public static final String CONFIG_JDBC_DRIVER = "jdbc.driver";
  public static final String CONFIG_JDBC_URL = "jdbc.url";
  public static final String CONFIG_JDBC_USER = "jdbc.user";
  public static final String CONFIG_JDBC_PASSWORD = "jdbc.password";
  public static final String CONFIG_TARGET_DATABASE = "target-database";
  public static final String CONFIG_JDBC_READ_CONNECTIONS_MAX = "jdbc.read-connections.max";
  public static final String CONFIG_DDL_GENERATION_MODE = PersistenceUnitProperties.DDL_GENERATION_MODE;

  @PresentForCodeCoverage
  private static final SMPJPAConfiguration s_aInstance = new SMPJPAConfiguration ();

  private SMPJPAConfiguration () {}

  // Write SQL file only in debug mode, so that the production version can be
  // read-only!
  @Nonnull
  public static String getDefaultDDLGenerationMode () {
    return GlobalDebug.isDebugMode () ? PersistenceUnitProperties.DDL_SQL_SCRIPT_GENERATION
                                     : PersistenceUnitProperties.NONE;
  }
}
