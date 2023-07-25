package eu.europa.ec.edelivery.smp.test.testutils;

import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
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

    public static String anyString() {
        return UUID.randomUUID().toString();
    }
}
