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

package eu.europa.ec.edelivery.smp.conversion;

import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by gutowpa on 18/12/2017.
 */
public class SmlIdentifierConverter {

    public static ServiceMetadataPublisherServiceForParticipantType toBusdoxParticipantId(ParticipantIdentifierType participantId, String smpId) {
        if (isBlank(smpId)) {
            throw new IllegalStateException("SMP ID is null or empty");
        }
        if (participantId == null || isBlank(participantId.getValue())) {
            throw new IllegalStateException("Participant Scheme or Id is null or empty");
        }

        ServiceMetadataPublisherServiceForParticipantType busdoxIdentifier = new ServiceMetadataPublisherServiceForParticipantType();
        busdoxIdentifier.setServiceMetadataPublisherID(smpId);
        org.busdox.transport.identifiers._1.ParticipantIdentifierType parId = new org.busdox.transport.identifiers._1.ParticipantIdentifierType();
        parId.setScheme(participantId.getScheme());
        parId.setValue(participantId.getValue());
        busdoxIdentifier.setParticipantIdentifier(parId);
        return busdoxIdentifier;
    }
}
