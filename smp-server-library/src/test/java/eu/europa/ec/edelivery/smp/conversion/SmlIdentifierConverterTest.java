/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */

package eu.europa.ec.edelivery.smp.conversion;

import ec.services.wsdl.bdmsl.data._1.ParticipantsType;
import ec.services.wsdl.bdmsl.data._1.SMPAdvancedServiceForParticipantType;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by gutowpa on 08/01/2018.
 */
public class SmlIdentifierConverterTest {

    private static final String SMP_ID = "SMP-ID";
    private static final String ID_VALUE = "sample:value";
    private static final String ID_SCHEME = "sample:scheme";
    private static final String SERVICE_NAME = "naptrService";

    @Test
    public void toBusdoxParticipantId() {
        //given
        Identifier participantId = new Identifier(ID_VALUE, ID_SCHEME);

        //when
        ServiceMetadataPublisherServiceForParticipantType result = SmlIdentifierConverter.toBusdoxParticipantId(participantId, SMP_ID);

        //then
        assertEquals(SMP_ID, result.getServiceMetadataPublisherID());
        assertEquals(ID_SCHEME, result.getParticipantIdentifier().getScheme());
        assertEquals(ID_VALUE, result.getParticipantIdentifier().getValue());
    }

    @Test
    public void toBusdoxParticipantId_NullScheme() {
        //given
        Identifier participantId = new Identifier(ID_VALUE, null);

        //when
        ServiceMetadataPublisherServiceForParticipantType result = SmlIdentifierConverter.toBusdoxParticipantId(participantId, SMP_ID);
        //then
        assertEquals(SMP_ID, result.getServiceMetadataPublisherID());
        assertNull(result.getParticipantIdentifier().getScheme());
        assertEquals(ID_VALUE, result.getParticipantIdentifier().getValue());
    }

    @Test
    public void toBDMSLAdvancedParticipantId() {
        //given
        Identifier participantId = new Identifier(ID_VALUE, ID_SCHEME);

        //when
        SMPAdvancedServiceForParticipantType result = SmlIdentifierConverter.toBDMSLAdvancedParticipantId(participantId, SMP_ID, SERVICE_NAME);

        //then
        assertEquals(SERVICE_NAME, result.getServiceName());
        assertEquals(ID_SCHEME, result.getCreateParticipantIdentifier().getParticipantIdentifier().getScheme());
        assertEquals(ID_VALUE, result.getCreateParticipantIdentifier().getParticipantIdentifier().getValue());
    }

    @Test
    public void toBDMSLAdvancedParticipantId_NullScheme() {
        //given
        Identifier participantId = new Identifier(ID_VALUE, null);

        //when
        SMPAdvancedServiceForParticipantType result = SmlIdentifierConverter.toBDMSLAdvancedParticipantId(participantId, SMP_ID, SERVICE_NAME);
        //then
        assertEquals(SERVICE_NAME, result.getServiceName());
        assertNull(result.getCreateParticipantIdentifier().getParticipantIdentifier().getScheme());
        assertEquals(ID_VALUE, result.getCreateParticipantIdentifier().getParticipantIdentifier().getValue());
    }

    @Test
    public void toParticipantsType() {
        //given
        Identifier participantId = new Identifier(ID_VALUE, ID_SCHEME);

        //when
        ParticipantsType result = SmlIdentifierConverter.toParticipantsType(participantId, SMP_ID);

        //then
        assertEquals(SMP_ID, result.getServiceMetadataPublisherID());
        assertEquals(ID_SCHEME, result.getParticipantIdentifier().getScheme());
        assertEquals(ID_VALUE, result.getParticipantIdentifier().getValue());
    }

    @Test
    public void toParticipantsType_NullScheme() {
        //given
        Identifier participantId = new Identifier(ID_VALUE, null);

        //when
        ParticipantsType result = SmlIdentifierConverter.toParticipantsType(participantId, SMP_ID);
        //then
        assertEquals(SMP_ID, result.getServiceMetadataPublisherID());
        assertNull(result.getParticipantIdentifier().getScheme());
        assertEquals(ID_VALUE, result.getParticipantIdentifier().getValue());
    }

    @Test
    public void validate_negativeCaseMissingSmpId() {
        //given
        Identifier participantId = new Identifier(ID_VALUE, ID_SCHEME);
        //when
        IllegalStateException result = assertThrows(IllegalStateException.class,
                () -> SmlIdentifierConverter.validate(participantId, null));
        //then
        assertEquals("SMP ID is null or empty", result.getMessage());
    }

    @Test
    public void validate_negativeCaseMissingValue() {
        //given
        Identifier participantId = new Identifier(null, ID_SCHEME);
        //when
        IllegalStateException result = assertThrows(IllegalStateException.class,
                () -> SmlIdentifierConverter.validate(participantId, SMP_ID));
        //then
        assertEquals("Participant Scheme or Id is null or empty", result.getMessage());
    }
}
