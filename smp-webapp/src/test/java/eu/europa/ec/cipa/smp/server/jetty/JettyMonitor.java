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
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class JettyMonitor extends Thread {
  public static final int STOP_PORT = 8078;
  public static final String STOP_KEY = "secret";

  private static final Logger s_aLogger = LoggerFactory.getLogger (JettyMonitor.class);
  private final int m_nPort;
  private final String m_sKey;
  private final ServerSocket m_aServerSocket;

  public JettyMonitor () throws IOException {
    this (STOP_PORT, STOP_KEY);
  }

  private JettyMonitor (final int nPort, final String sKey) throws IOException {
    m_nPort = nPort;
    m_sKey = sKey;
    setDaemon (true);
    setName ("JettyStopMonitor");
    m_aServerSocket = new ServerSocket (m_nPort, 1, InetAddress.getByName (null));
    if (m_aServerSocket == null)
      s_aLogger.error ("WARN: Not listening on monitor port: " + m_nPort);
  }

  @Override
  public void run () {
    while (true) {
      Socket aSocket = null;
      try {
        aSocket = m_aServerSocket.accept ();

        final LineNumberReader lin = new LineNumberReader (new InputStreamReader (aSocket.getInputStream ()));
        final String sKey = lin.readLine ();
        if (!m_sKey.equals (sKey))
          continue;

        final String sCmd = lin.readLine ();
        if ("stop".equals (sCmd)) {
          try {
            aSocket.close ();
            m_aServerSocket.close ();
          }
          catch (final Exception e) {
            s_aLogger.error ("Failed to close socket", e);
          }
          System.exit (0);
        }
      }
      catch (final Exception e) {
        s_aLogger.error ("Error reading from socket", e);
      }
      finally {
        if (aSocket != null)
          try {
            aSocket.close ();
          }
          catch (final IOException e) {}
        aSocket = null;
      }
    }
  }
}
