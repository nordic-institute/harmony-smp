/*
 * Copyright 2018 European Commission | CEF eDelivery
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

package eu.europa.ec.edelivery.smp.conversion;

import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.junit.Test;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by gutowpa on 08/01/2018.
 */
public class SmlIdentifierConverterTest {

    public static final String SMP_ID = "SMP-ID";
    public static final String ID_VALUE = "sample:value";
    public static final String ID_SCHEME = "sample:scheme";

    @Test
    public void positiveCase() {
        //given
        ParticipantIdentifierType participantId = new ParticipantIdentifierType(ID_VALUE, ID_SCHEME);

        //when
        ServiceMetadataPublisherServiceForParticipantType result = SmlIdentifierConverter.toBusdoxParticipantId(participantId, SMP_ID);

        //then
        assertEquals(SMP_ID, result.getServiceMetadataPublisherID());
        assertEquals(ID_SCHEME, result.getParticipantIdentifier().getScheme());
        assertEquals(ID_VALUE, result.getParticipantIdentifier().getValue());
    }

    @Test(expected = IllegalStateException.class)
    public void negativeCaseMissingSmpId() {
        //given
        ParticipantIdentifierType participantId = new ParticipantIdentifierType(ID_VALUE, ID_SCHEME);

        //when
        SmlIdentifierConverter.toBusdoxParticipantId(participantId, null);
    }

    @Test
    public void positiveCaseWithNullScheme() {
        //given
        ParticipantIdentifierType participantId = new ParticipantIdentifierType(ID_VALUE, null);

        //when
        ServiceMetadataPublisherServiceForParticipantType result = SmlIdentifierConverter.toBusdoxParticipantId(participantId, SMP_ID);
        //then
        assertEquals(SMP_ID, result.getServiceMetadataPublisherID());
        assertNull(result.getParticipantIdentifier().getScheme());
        assertEquals(ID_VALUE, result.getParticipantIdentifier().getValue());
    }

    @Test(expected = IllegalStateException.class)
    public void negativeCaseMissingValue() {
        //given
        ParticipantIdentifierType participantId = new ParticipantIdentifierType(null, ID_SCHEME);

        //when
        SmlIdentifierConverter.toBusdoxParticipantId(participantId, SMP_ID);
    }
}
