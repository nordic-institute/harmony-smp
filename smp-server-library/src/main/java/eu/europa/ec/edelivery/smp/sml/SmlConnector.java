/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.sml;

import eu.europa.ec.bdmsl.ws.soap.IManageParticipantIdentifierWS;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import static eu.europa.ec.edelivery.smp.conversion.SmlIdentifierConverter.toBusdoxParticipantId;
import static eu.europa.ec.smp.api.Identifiers.asString;

/**
 * Created by gutowpa on 22/12/2017.
 */
@Component
public class SmlConnector implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(SmlConnector.class);

    @Value("${bdmsl.integration.enabled}")
    boolean smlIntegrationEnabled;

    @Value("${bdmsl.integration.smp.id}")
    private String smpId;

    private ApplicationContext ctx;

    public void registerInDns(ParticipantIdentifierType normalizedParticipantId) {
        if (!smlIntegrationEnabled) {
            return;
        }
        log.info("Registering new Participant in BDMSL: " + asString(normalizedParticipantId));
        try {
            ServiceMetadataPublisherServiceForParticipantType smlRequest = toBusdoxParticipantId(normalizedParticipantId, smpId);
            buildClient().create(smlRequest);
        } catch (Exception e) {
            throw new SmlIntegrationException("Could not create new DNS entry through SML", e);
        }
    }

    public void unregisterFromDns(ParticipantIdentifierType normalizedParticipantId) {
        if (!smlIntegrationEnabled) {
            return;
        }
        log.info("Removing Participant from BDMSL: " + asString(normalizedParticipantId));
        try {
            ServiceMetadataPublisherServiceForParticipantType smlRequest = toBusdoxParticipantId(normalizedParticipantId, smpId);
            buildClient().delete(smlRequest);
        } catch (Exception e) {
            throw new SmlIntegrationException("Could not remove DNS entry through SML", e);
        }
    }

    private IManageParticipantIdentifierWS buildClient() {
        return ctx.getBean(IManageParticipantIdentifierWS.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }
}
