package eu.europa.ec.edelivery.smp.data.ui;

import java.util.Date;

public class PropertyRO extends BaseRO{
    private static final long serialVersionUID = -49713386560325302L;

    String property;
    String value;
    String type;
    String desc;
    boolean isEncrypted;
    Date updateDate;
    String newValue;
    boolean mandatory;
    boolean restartNeeded;

    public PropertyRO() {
    }

    public PropertyRO(String property, String value, String type, String desc) {
        this.property = property;
        this.value = value;
        this.type = type;
        this.desc = desc;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isRestartNeeded() {
        return restartNeeded;
    }

    public void setRestartNeeded(boolean restartNeeded) {
        this.restartNeeded = restartNeeded;
    }
}
