package eu.domibus.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "Binding", strict = false)
public class Binding implements Serializable {
    private static final long serialVersionUID = -5593316501928370737L;

    @Attribute
    protected String name;

    @Element(name = "MEP")
    protected MEP mep;

    public Binding() {
    }

    public Binding(final String name, final MEP mep) {
        this.name = name;
        this.mep = mep;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public MEP getMep() {
        return this.mep;
    }

    public void setMep(final MEP mep) {
        this.mep = mep;
    }

    public void setPmode(final PMode pmode) {
        if (pmode == null) {
            return;
        }
        if (this.mep != null) {
            this.mep.setPmode(pmode);
        }
    }
}