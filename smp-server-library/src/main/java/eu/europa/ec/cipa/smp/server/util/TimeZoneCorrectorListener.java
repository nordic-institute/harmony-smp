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
package eu.europa.ec.cipa.smp.server.util;

import java.util.TimeZone;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collections.ArrayHelper;
import com.helger.commons.exceptions.InitializationException;
import com.helger.datetime.config.PDTConfig;

/**
 * This class is used for setting the timezone so that dates saved to the
 * database are always in UTC.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class TimeZoneCorrectorListener implements ServletContextListener {
  public static final String DEFAULT_TIMEZONE = "UTC";

  private static final Logger s_aLogger = LoggerFactory.getLogger (TimeZoneCorrectorListener.class);

  public void contextInitialized (@Nonnull final ServletContextEvent aServletContextEvent) {
    // CHeck if the timezone is supported
    if (!ArrayHelper.contains (TimeZone.getAvailableIDs (), DEFAULT_TIMEZONE)) {
      final String sErrorMsg = "The default time zone '" + DEFAULT_TIMEZONE + "' is not supported!";
      s_aLogger.error (sErrorMsg);
      throw new InitializationException (sErrorMsg);
    }

    // Set the default timezone both in joda as well as in java util Timezone
    if (PDTConfig.setDefaultDateTimeZoneID (DEFAULT_TIMEZONE).isFailure ()) {
      final String sErrorMsg = "Failed to set default time zone to '" + DEFAULT_TIMEZONE + "'!";
      s_aLogger.error (sErrorMsg);
      throw new InitializationException (sErrorMsg);
    }
  }

  public void contextDestroyed (@Nonnull final ServletContextEvent aServletContextEvent) {
    // empty
  }
}
