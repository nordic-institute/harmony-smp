/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.test.testutils;

import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.GroupRO;

import java.util.UUID;

public class TestROUtils {

    public static GroupRO createGroup() {
        return createGroup(anyString());
    }


    public static GroupRO createGroup(String name) {
        GroupRO groupRO = new GroupRO();
        groupRO.setGroupName(name);
        groupRO.setGroupDescription(anyString());
        groupRO.setVisibility(VisibilityType.PRIVATE);
        return groupRO;
    }

    public static DomainRO createDomain() {
        return createDomain(anyString());
    }

    public static DomainRO createDomain(String name) {
        DomainRO domainRO = new DomainRO();
        domainRO.setDomainCode(name);
        domainRO.setVisibility(VisibilityType.PRIVATE);
        return domainRO;
    }

    public static String anyString() {
        return UUID.randomUUID().toString();
    }


    public static String createSMP10ServiceGroupPayload(String id, String sch) {

        return "<ServiceGroup xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\">" +
                "<ParticipantIdentifier scheme=\"" + sch + "\">" + id + "</ParticipantIdentifier>" +
                "<ServiceMetadataReferenceCollection />" +
                "</ServiceGroup>";
    }
}
