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

import eu.europa.ec.cipa.smp.server.util.to_be_removed.ESuccess;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import javax.annotation.concurrent.Immutable;

/**
 * An extension of the RegistrationHook class that does nothing.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
@Component
@Conditional(SMLHookConditionOff.class)
public final class DoNothingRegistrationHook extends AbstractRegistrationHook {
  public void create (final ParticipantIdentifierType aPI) {}

  public void delete (final ParticipantIdentifierType aPI) {}

  public void postUpdate (final ESuccess eSuccess) {}
}
