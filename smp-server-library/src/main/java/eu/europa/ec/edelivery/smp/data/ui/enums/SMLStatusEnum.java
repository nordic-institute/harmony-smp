package eu.europa.ec.edelivery.smp.data.ui.enums;


/**
 * Enumeration of the SML source group status.
 * @author Joze Rihtarsic
 * @since 4.1
 */
public enum SMLStatusEnum {
    REGISTER(0),
    UNREGISTER(1);

    int actionNumber;

    SMLStatusEnum(int actionNumber) {
        this.actionNumber = actionNumber;
    }

    public int getAction() {
        return actionNumber;
    }
}
