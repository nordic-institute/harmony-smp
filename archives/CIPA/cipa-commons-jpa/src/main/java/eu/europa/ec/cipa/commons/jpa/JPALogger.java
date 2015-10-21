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
package eu.europa.ec.cipa.commons.jpa;

import javax.annotation.Nonnull;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.CGlobal;
import com.phloc.commons.regex.RegExHelper;

/**
 * A logging adapter that can be hooked into JPA and forwards all logging
 * requests to phloc logging.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class JPALogger extends AbstractSessionLog {
  private static final Logger s_aLogger = LoggerFactory.getLogger (JPALogger.class);

  @Override
  public void log (@Nonnull final SessionLogEntry aSessionLogEntry) {
    final int nLogLevel = aSessionLogEntry.getLevel ();
    // JPA uses the System property for adding line breaks
    final String [] aMsgLines = RegExHelper.getSplitToArray (aSessionLogEntry.getMessage (), CGlobal.LINE_SEPARATOR);
    for (int i = 0; i < aMsgLines.length; ++i) {
      final String sMsg = aMsgLines[i];
      final Throwable t = i == aMsgLines.length - 1 ? aSessionLogEntry.getException () : null;
      if (nLogLevel >= SessionLog.SEVERE)
        s_aLogger.error (sMsg, t);
      else
        if (nLogLevel >= SessionLog.WARNING)
          s_aLogger.warn (sMsg, t);
        else
          s_aLogger.info (sMsg, t);
    }
  }
}
