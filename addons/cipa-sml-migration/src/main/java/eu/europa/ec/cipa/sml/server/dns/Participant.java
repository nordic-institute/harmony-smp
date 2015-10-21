package eu.europa.ec.cipa.sml.server.dns;

/**
 * Created by feriaad on 20/10/2015.
 */
public class Participant {
    private String recValue;

    private String scheme;

    private String smpId;

    public String getRecValue() {
        return recValue;
    }

    public void setRecValue(String recValue) {
        this.recValue = recValue;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getSmpId() {
        return smpId;
    }

    public void setSmpId(String smpId) {
        this.smpId = smpId;
    }
}
