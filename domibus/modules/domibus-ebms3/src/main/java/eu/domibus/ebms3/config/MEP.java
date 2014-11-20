package eu.domibus.ebms3.config;

import eu.domibus.common.exceptions.ConfigurationException;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "MEP", strict = false)
public class MEP implements Serializable {
    private static final long serialVersionUID = -5593310192832220737L;

    public static final String ONE_WAY_PUSH = "One-Way/Push";
    public static final String ONE_WAY_PULL = "One-Way/Pull";
    public static final String TWO_WAY_SYNC = "Two-Way/Sync";
    public static final String TWO_WAY_PUSH_AND_PUSH = "Two-Way/Push-and-Push";
    public static final String TWO_WAY_PUSH_AND_PULL = "Two-Way/Push-and-Pull";
    public static final String TWO_WAY_PULL_AND_PUSH = "Two-Way/Pull-and-Push";
    public static final String TWO_WAY_PULL_AND_PULL = "Two-Way/Pull-and-Pull";

    @Attribute(required = false)
    protected String name;

    @ElementList(inline = true)
    protected List<Leg> legs = new ArrayList<Leg>();

    public MEP() {
    }

    public MEP(final String name, final List<Leg> legs) {
        this.name = name;
        this.legs = legs;
    }

    public void addLeg(final int number, final String us, final String mpc, final String producer,
                       final Endpoint endpoint) {
        final Leg p = new Leg(number, us, mpc, producer, endpoint);
        this.legs.add(p);
    }

    public void addLeg(final Leg leg) {
        this.legs.add(leg);
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<Leg> getLegs() {
        return this.legs;
    }

    public void setLegs(final List<Leg> legs) {
        this.legs = legs;
    }

    public void setPmode(final PMode pmode) {
        if (pmode == null) {
            return;
        }
        if ((this.legs != null) && !this.legs.isEmpty()) {
            for (final Leg leg : this.legs) {
                leg.setPmode(pmode);
            }
        }
    }

    /* To serialize objects to Flex UI */
    public Leg[] getLegsArray() {
        if (this.legs == null) {
            return null;
        }
        final Leg[] res = new Leg[this.legs.size()];
        int i = 0;
        for (final Leg leg : this.legs) {
            res[i] = leg;
            i++;
        }
        return res;
    }

    public void setLegsArray(final Leg[] list) {
        if ((list == null) || (list.length == 0)) {
            if ((this.legs != null) && !this.legs.isEmpty()) {
                this.legs.clear();
            }
            return;
        }
        if (this.legs == null) {
            this.legs = new ArrayList<Leg>();
        }
        if (!this.legs.isEmpty()) {
            this.legs.clear();
        }
        for (final Leg leg : list) {
            this.addLeg(leg);
        }
    }

    public Leg getLegByNumber(final int i) {
        for (final Leg leg : this.legs) {
            if (leg.getNumber() == i) {
                return leg;
            }
        }
        throw new ConfigurationException("Missing leg number=\"" + i + "\" of MEP " + this.getName());

    }
}