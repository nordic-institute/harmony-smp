package eu.domibus.ebms3.config;

import org.apache.log4j.Logger;
import org.simpleframework.xml.*;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "PModes")
@Namespace(reference = "http://www.e-codex.eu/domibus/pmodes/0.1")
public class PModePool implements Serializable {
    private static final long serialVersionUID = -5593318201928374737L;

    private static final Logger log = Logger.getLogger(PModePool.class);

    @Attribute(required = false)
    private String schemaLocation;

    @ElementList(entry = "Producer", inline = true, required = false)
    protected List<Producer> producers = new ArrayList<Producer>();

    @ElementList(entry = "UserService", inline = true, required = false)
    protected List<UserService> userServices = new ArrayList<UserService>();

    @ElementList(entry = "Binding", inline = true, required = false)
    protected List<Binding> bindings = new ArrayList<Binding>();

    @ElementList(entry = "PMode", inline = true, required = false)
    protected List<PMode> pmodes = new ArrayList<PMode>();

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

    public List<PMode> getPmodes() {
        return this.pmodes;
    }

    public void setPmodes(final List<PMode> pmodes) {
        this.pmodes = pmodes;
    }

    /* Utility method */
    public Binding getBinding(final String bindingName) {
        if ((this.bindings == null) || this.bindings.isEmpty()) {
            return null;
        }
        for (final Binding b : this.bindings) {
            if (b.getName().equalsIgnoreCase(bindingName)) {
                return b;
            }
        }
        return null;
    }

    public static PModePool load(final String pmodesFileName) {
        if ((pmodesFileName == null) || "".equals(pmodesFileName.trim())) {
            return null;
        }
        return PModePool.load(new File(pmodesFileName));
    }

    public static PModePool load(final File source) {
        if ((source == null) || !source.exists()) {
            return null;
        }
        PModePool pool = null;
        try {
            final Serializer serializer = new Persister(new AnnotationStrategy());
            pool = serializer.read(PModePool.class, source);
            if (pool != null) {
                pool.init();
            }
        } catch (Exception ex) {
            PModePool.log.error("Error during serilization of PModeFile on load", ex);
        }
        return pool;
    }

    protected void init() {
        if ((this.pmodes != null) && !this.pmodes.isEmpty()) {
            for (final PMode pm : this.pmodes) {
                pm.setPool(this);
            }
        }
    }

    public void reload(final File source) {
        if ((source == null) || !source.exists()) {
            return;
        }
        final PModePool pool;
        try {
            final Serializer serializer = new Persister(new AnnotationStrategy());
            pool = serializer.read(PModePool.class, source);
            if (pool != null) {
                pool.init();
                this.setPmodes(pool.getPmodes());
            }
        } catch (Exception ex) {
            PModePool.log.error("Error during serilization of PModeFile on reload", ex);
        }
    }
}