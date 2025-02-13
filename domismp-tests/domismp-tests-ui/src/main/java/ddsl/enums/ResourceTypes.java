package ddsl.enums;

public enum ResourceTypes {
    OASIS1("edelivery-oasis-smp-1.0-servicegroup"),
    OASIS2("edelivery-oasis-smp-2.0-servicegroup"),
    OASIS3("edelivery-oasis-cppa-3.0-cpp");


    public String getName() {
        return name;
    }

    private final String name;


    ResourceTypes(String name) {
        this.name = name;
    }


}
