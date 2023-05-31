package eu.europa.ec.edelivery.smp.services.resource;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;

public class ResolvedData {
    boolean resolved;
    DBDomain domain;
    DBGroup group;
    DBResourceDef resourceDef;
    DBSubresourceDef subResourceDef;
    DBResource resource;
    DBSubresource subresource;

    public DBDomain getDomain() {
        return domain;
    }

    public void setDomain(DBDomain domain) {
        this.domain = domain;
    }

    public DBResourceDef getResourceDef() {
        return resourceDef;
    }

    public void setResourceDef(DBResourceDef resourceDef) {
        this.resourceDef = resourceDef;
    }

    public DBSubresourceDef getSubResourceDef() {
        return subResourceDef;
    }

    public DBResource getResource() {
        return resource;
    }

    public void setResource(DBResource resource) {
        this.resource = resource;
    }

    public DBSubresource getSubresource() {
        return subresource;
    }

    public void setSubResourceDef(DBSubresourceDef subResourceDef) {
        this.subResourceDef = subResourceDef;
    }

    public void setSubresource(DBSubresource subresource) {
        this.subresource = subresource;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public DBGroup getGroup() {
        return group;
    }

    public void setGroup(DBGroup group) {
        this.group = group;
    }
}
