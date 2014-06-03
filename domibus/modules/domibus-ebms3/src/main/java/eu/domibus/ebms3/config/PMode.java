package eu.domibus.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "PMode")
public class PMode implements java.io.Serializable {
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
    protected boolean initialized = false;

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
        if (bindings == null) {
            bindings = new ArrayList<Binding>();
        }
        bindings.add(binding);
        bindingName = binding.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean getExplicit() {
        return explicit;
    }

    public void setExplicit(final boolean explicit) {
        this.explicit = explicit;
    }

    public String getBindingName() {
        if (bindingName != null && !bindingName.trim().equals("")) {
            return bindingName;
        }
        if (bindings == null || bindings.size() == 0) {
            return null;
        }
        bindingName = bindings.get(0).getName();
        return bindingName;
    }

    public void setBindingName(final String bindingName) {
        this.bindingName = bindingName;
    }

    public List<Producer> getProducers() {
        return producers;
    }

    public void setProducers(final List<Producer> producers) {
        this.producers = producers;
    }

    public List<UserService> getUserServices() {
        return userServices;
    }

    public void setUserServices(final List<UserService> userServices) {
        this.userServices = userServices;
    }

    public List<Binding> getBindings() {
        return bindings;
    }

    public void setBindings(final List<Binding> bindings) {
        this.bindings = bindings;
    }

    public Binding getBinding() {
        if (binding != null) {
            return binding;
        }
        if (bindings != null && bindings.size() > 0) {
            if (getBindingName() == null || getBindingName().trim().equals("")) {
                if (!initialized) {
                    setBinding(bindings.get(0));
                }
                return bindings.get(0);
            } else {
                for (final Binding b : bindings) {
                    if (b.getName().equalsIgnoreCase(bindingName)) {
                        if (!initialized) {
                            setBinding(b);
                        }
                        return b;
                    }
                }
            }
        }
        if (pool != null) {
            final Binding b = pool.getBinding(getBindingName());
            if (b != null && !initialized) {
                setBinding(b);
            }
            return b;
        }
        return null;
    }

    public void setBinding(final Binding binding) {
        this.binding = binding;
        if (binding != null) {
            bindingName = binding.getName();
            this.binding.setPmode(this);
            initialized = true;
        }
    }

    public PModePool getPool() {
        return pool;
    }

    public void setPool(final PModePool pool) {
        this.pool = pool;
        getBinding();
    }

    /* Utility method */
    public Producer getProducer(final String producerName) {
        if (producers != null && producers.size() > 0) {
            for (final Producer p : producers) {
                if (p.getName().equalsIgnoreCase(producerName)) {
                    return p;
                }
            }
        }
        if (pool != null && pool.getProducers() != null &&
            pool.getProducers().size() > 0) {
            for (final Producer p : pool.getProducers()) {
                if (p.getName().equalsIgnoreCase(producerName)) {
                    return p;
                }
            }
        }
        return null;
    }

    public UserService getUserService(final String userServiceName) {
        if (userServices != null && userServices.size() > 0) {
            for (final UserService us : userServices) {
                if (us.getName().equalsIgnoreCase(userServiceName)) {
                    return us;
                }
            }
        }
        if (pool != null && pool.getUserServices() != null &&
            pool.getUserServices().size() > 0) {
            for (final UserService us : pool.getUserServices()) {
                if (us.getName().equalsIgnoreCase(userServiceName)) {
                    return us;
                }
            }
        }
        return null;
    }

    public Leg getLeg(final String mep, final String mpc, final String address) {
        if (getBinding() == null) {
            return null;
        }
        final MEP m = getBinding().getMep();
        if (m == null) {
            return null;
        }
        final List<Leg> legs = m.getLegs();
        if (legs == null || legs.size() == 0) {
            return null;
        }
        for (final Leg leg : legs) {
            if (leg.getEndpoint() == null) {
                if (same(leg.getMpc(), mpc) &&
                    same(mep, m.getName()) &&
                    (address == null || address.trim().equals(""))) {
                    return leg;
                }
            } else if (same(leg.getMpc(), mpc) && same(mep, m.getName()) &&
                       same(address, leg.getEndpoint().getAddress())) {
                return leg;
            }
        }
        return null;
    }

    public Leg getLeg(final String mep, final String mpc) {
        if (getBinding() == null) {
            return null;
        }
        final MEP m = getBinding().getMep();
        if (m == null) {
            return null;
        }
        final List<Leg> legs = m.getLegs();
        if (legs == null || legs.size() == 0) {
            return null;
        }
        for (final Leg leg : legs) {
            if (same(leg.getMpc(), mpc) && same(mep, m.getName())) {
                return leg;
            }
        }
        return null;
    }

    public Leg getLeg(final int legNumber, final String mep, final String mpc) {
        if (getBinding() == null) {
            return null;
        }
        final MEP m = getBinding().getMep();
        if (m == null) {
            return null;
        }
        final List<Leg> legs = m.getLegs();
        if (legs == null || legs.size() == 0) {
            return null;
        }
        for (final Leg leg : legs) {
            if (leg.getNumber() == legNumber && same(leg.getMpc(), mpc) &&
                same(mep, m.getName())) {
                return leg;
            }
        }
        return null;
    }

    public String getMep() {
        if (getBinding() == null) {
            return null;
        }
        final MEP mep = getBinding().getMep();
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
     *         null otherwise
     */
    public Leg getLeg(final int legNumber) {
        final Leg[] legs = getBinding().getMep().getLegsArray();

        if (legNumber <= 0 || legNumber > legs.length) {
            return null;
        }

        return legs[legNumber - 1];
    }

    private boolean same(final String v1, final String v2) {
        if (v1 != null) {
            return v1.equals(v2);
        }
        return v2 == null || v2.equals(v1);
    }

  /* To serialize objects to Flex UI, add below array getter/setter methods */
}