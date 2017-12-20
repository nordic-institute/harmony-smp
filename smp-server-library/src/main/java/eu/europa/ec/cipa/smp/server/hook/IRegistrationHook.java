/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.cipa.smp.server.hook;

import eu.europa.ec.cipa.smp.server.util.to_be_removed.ESuccess;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;

import javax.annotation.Nonnull;

/**
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public interface IRegistrationHook {
  /**
   * Create a participant in the SML.
   * 
   * @param aPI
   *        The participant to be created
   * @throws HookException
   *         If something goes wrong.
   */
  void create (@Nonnull ParticipantIdentifierType aPI) throws HookException;

  /**
   * Delete a participant in the SML.
   * 
   * @param aPI
   *        The participant to be deleted
   * @throws HookException
   *         If something goes wrong.
   */
  void delete (@Nonnull ParticipantIdentifierType aPI) throws HookException;

  /**
   * In case of failure, this method is meant to rollback the previously done
   * changes. It is call for success and failures.
   * 
   * @param eSuccess
   *        The success state.
   * @throws HookException
   *         If something goes wrong.
   */
  void postUpdate (@Nonnull ESuccess eSuccess) throws HookException;
}
