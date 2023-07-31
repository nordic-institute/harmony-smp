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
