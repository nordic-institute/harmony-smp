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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * A trust manager that accepts all certificates.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Deprecated
public final class DoNothingTrustManager implements X509TrustManager {
  private static final Logger s_aLogger = LoggerFactory.getLogger (DoNothingTrustManager.class);
  private final boolean m_bDebug;

  public DoNothingTrustManager() {
    this (true);
  }

  public DoNothingTrustManager(final boolean bDebug) {
    m_bDebug = bDebug;
  }

  public boolean isDebug () {
    return m_bDebug;
  }

  @Nullable
  public X509Certificate [] getAcceptedIssuers () {
    return null;
  }

  public void checkServerTrusted (final X509Certificate [] aChain, final String sAuthType) {
    if (m_bDebug)
      s_aLogger.info ("checkServerTrusted (" + Arrays.toString (aChain) + ", " + sAuthType + ")");
  }

  public void checkClientTrusted (final X509Certificate [] aChain, final String sAuthType) {
    if (m_bDebug)
      s_aLogger.info ("checkClientTrusted (" + Arrays.toString (aChain) + ", " + sAuthType + ")");
  }
}
