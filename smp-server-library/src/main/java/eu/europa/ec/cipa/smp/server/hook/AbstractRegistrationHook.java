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
package eu.europa.ec.cipa.smp.server.hook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
abstract class AbstractRegistrationHook implements IRegistrationHook {
  private static final ThreadLocal <AbstractRegistrationHook> s_aQueue = new ThreadLocal <AbstractRegistrationHook> ();

  @Nonnull
  protected static final ThreadLocal <AbstractRegistrationHook> getQueueInstance () {
    return s_aQueue;
  }

  @Nullable
  public static final AbstractRegistrationHook getQueue () {
    return s_aQueue.get ();
  }

  public static final void resetQueue () {
    s_aQueue.set (null);
  }
}
