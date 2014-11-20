package eu.domibus.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "PMode")
public class PMode implements Serializable {
    private static final long serialVersionUID = -5593318273642220737L;

    @Attribute
    protected String name;

    @Attribute(name = "binding", required = false)
    protected String bindingName;

    @ElementList(entry = "Producer", inline = true, required = false)
    protected List<Producer> producers = new ArrayList<Producer>();

    @ElementList(entry = "UserService", inline = true, required = false)
    protected List<UserService> userServices = new ArrayList<UserService>();

    @ElementList(entry = "Binding", inline = true, required = false)
    protected List<Binding> bindings = new ArrayList<Binding>();

    @Attribute(required = false)
    protected boolean explicit = true;

    /* This is not to be serialized in XML */
    protected Binding binding;
    protected PModePool pool;
    protected boolean initialized;

    public PMode() {
    }

    public PMode(final String name, final String bindingName) {
        this.name = name;
        this.bindingName = bindingName;
    }

    public PMode(final String name, final Binding binding) {
        this.name = name;
        if (binding == null) {
            return;
        }
        if (this.bindings == null) {
            this.bindings = new ArrayList<Binding>();
        }
        this.bindings.add(binding);
        this.bindingName = binding.getName();
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean getExplicit() {
        return this.explicit;
    }

    public void setExplicit(final boolean explicit) {
        this.explicit = explicit;
    }

    public String getBindingName() {
        if ((this.bindingName != null) && !"".equals(this.bindingName.trim())) {
            return this.bindingName;
        }
        if ((this.bindings == null) || this.bindings.isEmpty()) {
            return null;
        }
        this.bindingName = this.bindings.get(0).getName();
        return this.bindingName;
    }

    public void setBindingName(final String bindingName) {
        this.bindingName = bindingName;
    }

    public List<Producer> getProducers() {
        return this.producers;
    }

    public void setProducers(final List<Producer> producers) {
        this.producers = producers;
    }

    public List<UserService> getUserServices() {
        return this.userServices;
    }

    public void setUserServices(final List<UserService> userServices) {
        this.userServices = userServices;
    }

    public List<Binding> getBindings() {
        return this.bindings;
    }

    public void setBindings(final List<Binding> bindings) {
        this.bindings = bindings;
    }

    public Binding getBinding() {
        if (this.binding != null) {
            return this.binding;
        }
        if ((this.bindings != null) && !this.bindings.isEmpty()) {
            if ((this.getBindingName() == null) || "".equals(getBindingName().trim())) {
                if (!this.initialized) {
                    this.setBinding(this.bindings.get(0));
                }
                return this.bindings.get(0);
            } else {
                for (final Binding b : this.bindings) {
                    if (b.getName().equalsIgnoreCase(this.bindingName)) {
                        if (!this.initialized) {
                            this.setBinding(b);
                        }
                        return b;
                    }
                }
            }
        }
        if (this.pool != null) {
            final Binding b = this.pool.getBinding(this.getBindingName());
            if ((b != null) && !this.initialized) {
                this.setBinding(b);
            }
            return b;
        }
        return null;
    }

    public void setBinding(final Binding binding) {
        this.binding = binding;
        if (binding != null) {
            this.bindingName = binding.getName();
            this.binding.setPmode(this);
            this.initialized = true;
        }
    }

    public PModePool getPool() {
        return this.pool;
    }

    public void setPool(final PModePool pool) {
        this.pool = pool;
        this.getBinding();
    }

    /* Utility method */
    public Producer getProducer(final String producerName) {
        if ((this.producers != null) && !this.producers.isEmpty()) {
            for (final Producer p : this.producers) {
                if (p.getName().equalsIgnoreCase(producerName)) {
                    return p;
                }
            }
        }
        if ((this.pool != null) && (this.pool.getProducers() != null) &&
            !this.pool.getProducers().isEmpty()) {
            for (final Producer p : this.pool.getProducers()) {
                if (p.getName().equalsIgnoreCase(producerName)) {
                    return p;
                }
            }
        }
        return null;
    }

    public UserService getUserService(final String userServiceName) {
        if ((this.userServices != null) && !this.userServices.isEmpty()) {
            for (final UserService us : this.userServices) {
                if (us.getName().equalsIgnoreCase(userServiceName)) {
                    return us;
                }
            }
        }
        if ((this.pool != null) && (this.pool.getUserServices() != null) &&
            !this.pool.getUserServices().isEmpty()) {
            for (final UserService us : this.pool.getUserServices()) {
                if (us.getName().equalsIgnoreCase(userServiceName)) {
                    return us;
                }
            }
        }
        return null;
    }

    public Leg getLeg(final String mep, final String mpc, final String address) {
        if (this.getBinding() == null) {
            return null;
        }
        final MEP m = this.getBinding().getMep();
        if (m == null) {
            return null;
        }
        final List<Leg> legs = m.getLegs();
        if ((legs == null) || legs.isEmpty()) {
            return null;
        }
        for (final Leg leg : legs) {
            if (leg.getEndpoint() == null) {
                if (this.same(leg.getMpc(), mpc) &&
                    this.same(mep, m.getName()) &&
                    ((address == null) || "".equals(address.trim()))) {
                    return leg;
                }
            } else if (this.same(leg.getMpc(), mpc) && this.same(mep, m.getName()) &&
                       this.same(address, leg.getEndpoint().getAddress())) {
                return leg;
            }
        }
        return null;
    }

    public Leg getLeg(final String mep, final String mpc) {
        if (this.getBinding() == null) {
            return null;
        }
        final MEP m = this.getBinding().getMep();
        if (m == null) {
            return null;
        }
        final List<Leg> legs = m.getLegs();
        if ((legs == null) || legs.isEmpty()) {
            return null;
        }
        for (final Leg leg : legs) {
            if (this.same(leg.getMpc(), mpc) && this.same(mep, m.getName())) {
                return leg;
            }
        }
        return null;
    }

    public Leg getLeg(final int legNumber, final String mep, final String mpc) {
        if (this.getBinding() == null) {
            return null;
        }
        final MEP m = this.getBinding().getMep();
        if (m == null) {
            return null;
        }
        final List<Leg> legs = m.getLegs();
        if ((legs == null) || legs.isEmpty()) {
            return null;
        }
        for (final Leg leg : legs) {
            if ((leg.getNumber() == legNumber) && this.same(leg.getMpc(), mpc) &&
                this.same(mep, m.getName())) {
                return leg;
            }
        }
        return null;
    }

    public String getMep() {
        if (this.getBinding() == null) {
            return null;
        }
        final MEP mep = this.getBinding().getMep();
        if (mep == null) {
            return null;
        }
        return mep.getName();
    }

    /**
     * Gets the configuration of the <code>legNumber</code>-th leg of the MEP that
     * this P-Mode uses.
     *
     * @param legNumber The number of the leg to get the configuration of
     * @return The Leg configuration of the given leg if it exists
     * null otherwise
     */
    public Leg getLeg(final int legNumber) {
        final Leg[] legs = this.getBinding().getMep().getLegsArray();

        if ((legNumber <= 0) || (legNumber > legs.length)) {
            return null;
        }

        return legs[legNumber - 1];
    }

    private boolean same(final String v1, final String v2) {
        if (v1 != null) {
            return v1.equals(v2);
        }
        return (v2 == null) || v2.equals(v1);
    }

  /* To serialize objects to Flex UI, add below array getter/setter methods */
}