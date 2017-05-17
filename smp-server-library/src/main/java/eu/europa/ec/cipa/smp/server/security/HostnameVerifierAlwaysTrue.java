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
package eu.europa.ec.cipa.smp.server.security;

import com.helger.commons.GlobalDebug;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Implementation of HostnameVerifier always returning <code>true</code>.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Deprecated
public final class HostnameVerifierAlwaysTrue implements HostnameVerifier {
  private static final Logger s_aLogger = LoggerFactory.getLogger (HostnameVerifierAlwaysTrue.class);

  private final boolean m_bDebug;

  public HostnameVerifierAlwaysTrue() {
    this (GlobalDebug.isDebugMode ());
  }

  public HostnameVerifierAlwaysTrue(final boolean bDebug) {
    m_bDebug = bDebug;
  }

  public boolean isDebug () {
    return m_bDebug;
  }

  public boolean verify (final String sURLHostname, final SSLSession aSession) {
    if (m_bDebug)
      s_aLogger.debug ("Hostname '" + sURLHostname + "' is accepted by default in SSL session " + aSession + "!");
    return true;
  }
}
