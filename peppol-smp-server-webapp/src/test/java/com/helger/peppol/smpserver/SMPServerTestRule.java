/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.peppol.smpserver;

import java.util.concurrent.atomic.AtomicBoolean;

import com.helger.peppol.smpserver.servlet.SMPWebAppListener;
import com.helger.photon.basic.mock.PhotonBasicWebTestRule;

/**
 * Special SMP server JUnit test rule.
 *
 * @author Philip Helger
 */
public class SMPServerTestRule extends PhotonBasicWebTestRule
{
  private static final AtomicBoolean s_aInitBackend = new AtomicBoolean (false);

  @Override
  public void before ()
  {
    super.before ();

    // Set it only once
    if (s_aInitBackend.compareAndSet (false, true))
      SMPWebAppListener.initBackendFromConfiguration ();
  }

  @Override
  public void after ()
  {
    super.after ();
  }
}