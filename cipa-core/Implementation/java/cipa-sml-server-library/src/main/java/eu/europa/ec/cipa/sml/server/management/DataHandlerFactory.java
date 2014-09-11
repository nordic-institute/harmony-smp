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
package eu.europa.ec.cipa.sml.server.management;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.string.StringHelper;

import eu.europa.ec.cipa.peppol.utils.ConfigFile;
import eu.europa.ec.cipa.sml.server.IGenericDataHandler;
import eu.europa.ec.cipa.sml.server.IParticipantDataHandler;
import eu.europa.ec.cipa.sml.server.IParticipantDataHandlerCallback;
import eu.europa.ec.cipa.sml.server.ISMPDataHandler;
import eu.europa.ec.cipa.sml.server.ISMPDataHandlerCallback;

/**
 * A factory used for constructing new instances of the
 * {@link IGenericDataHandler} interface.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class DataHandlerFactory {
  @Immutable
  private static final class SingletonHolder {
    private static final Logger s_aLogger = LoggerFactory.getLogger (SingletonHolder.class);
    private static final String CONFIG_SML_DATAHANDLER_SMP_CLASS = "sml.datahandler.smp.class";
    private static final String CONFIG_SML_DATAHANDLER_SMP_CALLBACK = "sml.datahandler.smp.callback";
    private static final String CONFIG_SML_DATAHANDLER_PARTICIPANT_CLASS = "sml.datahandler.participant.class";
    private static final String CONFIG_SML_DATAHANDLER_PARTICIPANT_CALLBACK = "sml.datahandler.participant.callback";
    private static final String CONFIG_SML_DATAHANDLER_GENERIC_CLASS = "sml.datahandler.generic.class";

    static final ISMPDataHandler s_aSMPInstance;
    static final IParticipantDataHandler s_aParticipantInstance;
    static final IGenericDataHandler s_aGenericInstance;

    static {
      final ConfigFile aConfig = ConfigFile.getInstance ();

      // SMP stuff
      {
        // Instantiate the SMP data handler
        final String sSMPHandlerClass = aConfig.getString (CONFIG_SML_DATAHANDLER_SMP_CLASS);
        s_aSMPInstance = GenericReflection.newInstance (sSMPHandlerClass, ISMPDataHandler.class);
        if (s_aSMPInstance == null)
          throw new IllegalStateException ("Failed to instantiate SMP data handler class '" + sSMPHandlerClass + "'");

        // Check for the callback
        final String sSMPCallbackClass = aConfig.getString (CONFIG_SML_DATAHANDLER_SMP_CALLBACK);
        if (StringHelper.hasText (sSMPCallbackClass)) {
          // An optional data change callback is present
          final ISMPDataHandlerCallback aCallback = GenericReflection.newInstance (sSMPCallbackClass,
                                                                                   ISMPDataHandlerCallback.class);
          if (aCallback != null)
            s_aSMPInstance.setCallback (aCallback);
          else
            s_aLogger.warn ("Failed to instantiate SMP data handler callback class '" + sSMPCallbackClass + "'");
        }
      }

      // participant stuff
      {
        final String sParticipantHandlerClass = aConfig.getString (CONFIG_SML_DATAHANDLER_PARTICIPANT_CLASS);

        // Instantiate the main data handler
        s_aParticipantInstance = GenericReflection.newInstance (sParticipantHandlerClass, IParticipantDataHandler.class);
        if (s_aParticipantInstance == null)
          throw new IllegalStateException ("Failed to instantiate participant data handler class '" +
                                           sParticipantHandlerClass +
                                           "'");

        final String sParticipantCallbackClass = aConfig.getString (CONFIG_SML_DATAHANDLER_PARTICIPANT_CALLBACK);
        if (StringHelper.hasText (sParticipantCallbackClass)) {
          // An optional data change callback is present
          final IParticipantDataHandlerCallback aCallback = GenericReflection.newInstance (sParticipantCallbackClass,
                                                                                           IParticipantDataHandlerCallback.class);
          if (aCallback != null)
            s_aParticipantInstance.setCallback (aCallback);
          else
            s_aLogger.warn ("Failed to instantiate participant data handler callback class '" +
                            sParticipantCallbackClass +
                            "'");
        }
      }

      // Generic stuff
      {
        final String sGenericHandlerClass = aConfig.getString (CONFIG_SML_DATAHANDLER_GENERIC_CLASS);

        // Instantiate the main data handler
        s_aGenericInstance = GenericReflection.newInstance (sGenericHandlerClass, IGenericDataHandler.class);
        if (s_aGenericInstance == null)
          throw new IllegalStateException ("Failed to instantiate generic data handler class '" +
                                           sGenericHandlerClass +
                                           "'");
      }
    }
  }

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final DataHandlerFactory s_aInstance = new DataHandlerFactory ();

  private DataHandlerFactory () {}

  @Nonnull
  public static ISMPDataHandler getSMPDataHandler () {
    return SingletonHolder.s_aSMPInstance;
  }

  @Nonnull
  public static IParticipantDataHandler getParticipantDataHandler () {
    return SingletonHolder.s_aParticipantInstance;
  }

  @Nonnull
  public static IGenericDataHandler getGenericDataHandler () {
    return SingletonHolder.s_aGenericInstance;
  }
}
