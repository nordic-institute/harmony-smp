package eu.europa.ec.edelivery.smp.data.ui.enums;


/**
 * Enumeraton of Resourceobject statuse .
 * @author Joze Rihtarsic
 * @since 4.1
 */
public enum SMLAction {
    REGISTER(0),
    UNREGISTER(1);

    int actionNumber;

    SMLAction(int actionNumber) {
        this.actionNumber = actionNumber;
    }

    public int getAction() {
        return actionNumber;
    }
}
