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
package eu.europa.ec.cipa.smp.server.jetty;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class JettyStopSMPFull {
  private static final Logger s_aLogger = LoggerFactory.getLogger (JettyStopSMPFull.class);

  public static void main (final String [] args) throws IOException {
    try {
      final Socket s = new Socket (InetAddress.getByName (null), JettyMonitor.STOP_PORT);
      s.setSoLinger (false, 0);

      final OutputStream out = s.getOutputStream ();
      s_aLogger.info ("Sending jetty stop request");
      out.write ((JettyMonitor.STOP_KEY + "\r\nstop\r\n").getBytes ());
      out.flush ();
      s.close ();
    }
    catch (final ConnectException ex) {
      s_aLogger.warn ("Jetty is not running");
    }
  }
}
