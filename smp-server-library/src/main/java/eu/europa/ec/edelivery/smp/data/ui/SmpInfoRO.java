package eu.europa.ec.edelivery.smp.data.ui;

import java.io.Serializable;

public class SmpInfoRO implements Serializable {
    private static final long serialVersionUID = -49712226560325302L;
    String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
