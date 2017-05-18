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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used for logging startup and shutdown
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class LogStartupListener implements ServletContextListener {
  private static final Logger s_aLogger = LoggerFactory.getLogger (LogStartupListener.class);

  public void contextInitialized (@Nonnull final ServletContextEvent aServletContextEvent) {
    s_aLogger.info ("SMP context started");
  }

  public void contextDestroyed (@Nonnull final ServletContextEvent aServletContextEvent) {
    s_aLogger.info ("SMP context stopped");
  }
}
