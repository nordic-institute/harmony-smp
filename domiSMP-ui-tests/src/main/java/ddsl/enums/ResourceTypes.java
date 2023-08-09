package ddsl.enums;

import java.util.Random;

public enum ResourceTypes {
    OASIS1("edelivery-oasis-smp-1.0-servicegroup (smp-1)"),
    OASIS2("edelivery-oasis-smp-2.0-servicegroup (oasis-bdxr-smp-2)"),
    OASIS3("edelivery-oasis-cppa-3.0-cpp (cpp)");


    public final String name;


    ResourceTypes(String name) {
        this.name = name;

    }

    public static String getRandomResourceType() {
        ResourceTypes[] resourceTypes = values();
        int size = resourceTypes.length;
        Random random = new Random();
        int index = random.nextInt(size);
        return resourceTypes[index].name;
    }

}
