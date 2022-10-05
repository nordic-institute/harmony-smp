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
package eu.europa.ec.cipa.smp.server.errors.exceptions;

import javax.annotation.Nullable;

import com.helger.commons.exceptions.LoggedRuntimeException;

/**
 * This exceptions is thrown if the provided user name does not exist.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public class UnknownUserException extends LoggedRuntimeException {

  private final String m_sUserName;

  public UnknownUserException (@Nullable final String sUserName) {
    super ("Unknown user '" + sUserName + "'");
    m_sUserName = sUserName;
  }

  /**
   * @return The user name which was not found. May be <code>null</code>.
   */
  @Nullable
  public String getUserName () {
    return m_sUserName;
  }
}
