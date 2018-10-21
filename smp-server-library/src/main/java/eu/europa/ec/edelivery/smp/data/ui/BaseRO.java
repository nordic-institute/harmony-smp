package eu.europa.ec.edelivery.smp.data.ui;

import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;

import java.io.Serializable;

public class BaseRO  implements Serializable {

    private int status = EntityROStatus.PERSISTED.getStatusNumber();
    private int index;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
