package eu.europa.ec.edelivery.smp.data.ui;

public class ServiceResultProperties extends ServiceResult<PropertyRO> {
    boolean serverRestartNeeded;

    public boolean isServerRestartNeeded() {
        return serverRestartNeeded;
    }

    public void setServerRestartNeeded(boolean serverRestartNeeded) {
        this.serverRestartNeeded = serverRestartNeeded;
    }
}
