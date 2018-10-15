package eu.europa.ec.edelivery.smp.data.ui.enums;


/**
 * Enumeraton of Resourceobject statuse .
 * @author Joze Rihtarsic
 * @since 4.1
 */
public enum EntityROStatus {
    PERSISTED(0),
    UPDATED(1),
    NEW(2),
    REMOVE(3);

    int statusNumber;

    EntityROStatus(int statusNumber) {
        this.statusNumber = statusNumber;
    }

    public int getStatusNumber() {
        return statusNumber;
    }
}
